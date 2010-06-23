/*
Copyright 2009 S. Koulouzis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.  
 */
package proxyWS.transport;

import proxyWS.utils.Constants;
import proxyWS.utils.Misc;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author S. Koulouzis 
 */
public class HTTPTransport extends HttpServlet implements VStreamServer {

    private static PipedInputStream serverIn;
    private static PipedOutputStream serverOut;
    private static OutputStream out;
    private static InputStream in;
    private static String address;


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String query = request.getQueryString();
//        debug("doPost: New Reuest: " + query);
        int numOfargs;
        Object[] inputArgs = null;
        if (address == null) {
            address = request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/axis/" + this.getClass().getSimpleName();
        }
        
        
        System.out.println("Upload data here: " + request.getQueryString());
        String[] cntr;
        boolean end;
        String fileName;
        if (query.startsWith(Constants.WS_STREAMING)) {
            try {
                org.apache.commons.io.IOUtils.copy(request.getInputStream(), out);
            } catch (Exception ex) {
                Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (query.startsWith(Constants.CL_STREAMING) && out != null) {
            try {
                debug("Coping from request to WS");
                ServletInputStream sin = request.getInputStream();
                org.apache.commons.io.IOUtils.copy(sin, out);

                out.flush();
                out.close();

            } catch (Exception ex) {
                Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (query.startsWith(Constants.UPLOAD_FILES)) {


            String[] params = query.split("&");
            int key = Integer.valueOf(params[1]);
            fileName = params[2];

            for (int i = 0; i < params.length; i++) {
                debug("Params[" + i + "]: " + params[i]);
            }

            hgandleFileUpload(request, response, fileName, key);

        } else if (query.startsWith(Constants.IN_UPLOAD_MEM)) {
            handleUploadInputArgs(request);
        } else {
//            debug("SC_BAD_REQUEST");
            response.flushBuffer();
            response.getOutputStream().close();
            request.getInputStream().close();
            if (!query.startsWith(Constants.CL_STREAMING)) {
                in = null;
                out = null;
            }

        }

//        debug("doPost: END\n");

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String query = request.getQueryString();
//        debug("doGet: New Reuest: " + query);
       
        int numOfargs;
        Object[] inputArgs;

        try {
            if (address == null) {
                address = request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/axis/VHTTPTransport";
            }

            if (query.startsWith(Constants.WS_STREAMING) && in != null) {
//                debug("Copying from WS to response");

//                nl.uva.vlet.io.StreamUtil.copyStreams(in, response.getOutputStream());      

                org.apache.commons.io.IOUtils.copy(in, response.getOutputStream());

                response.flushBuffer();
                response.getOutputStream().flush();
                response.getOutputStream().close();

                in = null;
                out = null;

            } else if (query.startsWith(Constants.IN_UPLOAD_MEM)) {
                handleUploadInputArgs(request);

            } else if (query != null && !query.startsWith(Constants.WS_STREAMING) && !query.startsWith(Constants.IN_UPLOAD_MEM)) {
                try {
                    int key = Integer.valueOf(query);
                    handleReturnData(response, key);
                } catch (java.lang.NumberFormatException ex) {
//                    debug("SC_BAD_REQUEST");
                    response.flushBuffer();
                    response.getOutputStream().close();
                    if (!query.startsWith(Constants.CL_STREAMING)) {
                        in = null;
                        out = null;
                    }

                }

            } else {
//                debug("SC_BAD_REQUEST");
                response.flushBuffer();
                response.getOutputStream().close();
                if (!query.startsWith(Constants.CL_STREAMING)) {
                    in = null;
                    out = null;
                }
                in = null;
                out = null;
            }

        } catch (Exception ex) {
            Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
        }

//        debug("doGet: END\n");

    }

    public int getOptimalReadBufferSize() {
        return 10;
    }

    public static InputStream getStaticInputStream() throws VlException {
        return in;
    }

    public static OutputStream getStaticOutputStream() throws VlException {
        return out;
    }

    public InputStream getInputStream() throws VlException {
        return in;
    }

    public OutputStream getOutputStream() throws VlException {
        return out;
    }

    public int getOptimalWriteBufferSize() {
        return 101;
    }

    public void start() {
//        if (out == null || in == null) {
        initPipes();
//        }
    }

    private static void debug(String msg) {
        System.err.println("HTTPTransport: " + msg);
    }

    public String getScheme() {
        return "http";
    }

    protected static void initPipes() {

//        debug("Initilizing IO Streams");
        try {

            serverIn = new PipedInputStream();
            serverOut = new PipedOutputStream(serverIn);

            out = serverOut;
            in = serverIn;

//            clientIn = new PipedInputStream(getOptimalReadBufferSize());
//            clientOut = new PipedOutputStream(clientIn);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return true;
    }

    public String getAddress() {        
        if (address == null) {
            address = "http://" + proxyWS.utils.Misc.getIPOrHostName() + ":8080/axis/" + this.getClass().getSimpleName();
        }
        return address;
    }

//    private void handleUploadInputArgs(HttpServletRequest request) {
//        try {
//            System.out.println("Getting input arguments");
//
//            ServletContext context = getServletContext();
//            Map inArgsMap = (Map) context.getAttribute(VproxyWS.utils.Constants.IN_ARGS);
//
//            ObjectInput objIn = new ObjectInputStream(request.getInputStream());
//            
//            
//            Integer numOfargs = objIn.readInt();
//            System.out.println("Num of args: " +numOfargs);
//            Object[] inputArgs = new Object[numOfargs];
//            for (int i = 0; i < numOfargs; i++) {
//                inputArgs[i] = objIn.readObject();
//                System.out.println("Got arg: " + inputArgs[i].getClass().getName());
//            }
//            inArgsMap.put(request.getQueryString(), inputArgs);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private void handleUploadInputArgs(HttpServletRequest request) {
        try {
            XStream xstream = new XStream(new DomDriver());
            ServletContext context = getServletContext();

            Map inArgsMap = (Map) context.getAttribute(proxyWS.utils.Constants.IN_ARGS);

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // maximum size that will be stored in memory
            int conf = (Integer) context.getAttribute(proxyWS.utils.Constants.CONF);
            factory.setSizeThreshold(conf);
            // the location for saving data that is larger than getSizeThreshold()
            factory.setRepository(new File(Misc.getTmpDir()));


            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum size before a FileUploadException will be thrown
            upload.setSizeMax(conf * 2);

            List fileItems = upload.parseRequest(request);

            // assume we know there are two files. The first file is a small
            // text file, the second is unknown and is written to a file on
            // the server
            Iterator i = fileItems.iterator();

            int c = 0;
            Object[] inputArgs = new Object[fileItems.size()];

            Object obj2 = null;
            while (i.hasNext()) {

                Object obj = i.next();
//                System.out.println("it[" + c + "]" + obj + " Class: " + obj.getClass().getName());
                org.apache.commons.fileupload.disk.DiskFileItem fi = (DiskFileItem) obj;

//                System.out.println("isFormField " + fi.isFormField() + "\n isInMemory " + fi.isInMemory() + "\n ");
                obj2 = xstream.fromXML(new String(fi.get()));

//                obj2 = ObjectTransformer.getObject(fi.get());
                System.out.println("Http upload Class:  " + obj2.getClass());
                inputArgs[c] = obj2;
                c++;
//                files.add(f.getAbsolutePath());
            }


            inArgsMap.put(request.getQueryString(), inputArgs);
        } catch (FileUploadException ex) {
            Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleReturnData(HttpServletResponse response, int key) {
        ServletContext context = getServletContext();
//        int key = Integer.valueOf(query);
        XStream xstream = new XStream(new DomDriver());
        HashMap returnValues = (HashMap) context.getAttribute(proxyWS.utils.Constants.RETURN_VAL);
        Object obj = returnValues.remove(key);
        if (obj == null) {
            try {
                System.err.println(this.getClass().getName() + " did not recive data from Service");
                response.sendError(response.SC_NOT_FOUND);
            } catch (IOException ex) {
                Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                xstream.toXML(obj, response.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void hgandleFileUpload(HttpServletRequest req, HttpServletResponse resp, String fileName, int key) {
        File writable;
        OutputStream outStrem;
        Map inputArgs;
        try {

            ServletContext context = getServletContext();
            debug("Get DataTransportContext");


//            String sandbox = d.getSandbox();
//            String sandbox = (String) context.getAttribute(proxyWS.utils.Constants.SANDBOX);
            DataTransportContext.init(true, getServletContext());
            String sandbox = DataTransportContext.getSandbox();
            


            String dir = null;
            if (sandbox != null && new File(sandbox).exists()) {
                dir = sandbox + File.separator;
            } else {
                dir = Misc.getTmpDir() + File.separator;
            }

            debug("Will save in: " + dir);

            VRL source = new VRL(fileName);
            String[] tmp = source.getPath().split("/");
            String name = tmp[tmp.length - 1];
            debug("File name: " + name);
            VRL newVrl = new VRL("file:///" + dir + "/" + name);
            debug("Saving File: " + newVrl);

//            writable = (VFile) d.getVnode(newVrl);
            writable = new File(newVrl.getPath());
//            outStrem = writable.getOutputStream();
            outStrem = new FileOutputStream(writable);

            ServletInputStream sin = req.getInputStream();
            org.apache.commons.io.IOUtils.copy(sin, outStrem);

            outStrem.flush();
            outStrem.close();
            sin.close();


            Map filesMap = (Map) DataTransportContext.getInputArgs(proxyWS.utils.Constants.IN_UPLOAD_FILES);
//            inputArgs = (Map) context.getAttribute(proxyWS.utils.Constants.IN_ARGS);
//            Map filesMap = (Map) inputArgs.get(proxyWS.utils.Constants.IN_UPLOAD_FILES);//(Map) d.getInputArgs(proxyWS.utils.Constants.IN_UPLOAD_FILES);
            if (filesMap == null) {
                debug("filesMap was null");
                filesMap = new HashMap();
            }
            Vector<File> files = (Vector<File>) filesMap.get(key);

            if (files == null) {
                debug("files was null");
                files = new Vector<File>();
            }
            files.add(writable);

            debug("Files for key: " + key);
            for (int i = 0; i < files.size(); i++) {
                debug("\t " + files.get(i).getPath());
            }

            filesMap.put(key, files);

//            inputArgs.put(proxyWS.utils.Constants.IN_UPLOAD_FILES, filesMap);
            DataTransportContext.addInputArg(proxyWS.utils.Constants.IN_UPLOAD_FILES, filesMap);
//            inputArgs.put(proxyWS.utils.Constants.IN_UPLOAD_DIR, dir);
            DataTransportContext.addInputArg(proxyWS.utils.Constants.IN_UPLOAD_DIR, dir);
//            context.setAttribute(proxyWS.utils.Constants.IN_ARGS, inputArgs);

            // set the response code and write the response data
            resp.setStatus(HttpServletResponse.SC_OK);


        } catch (Exception ex) {
            Logger.getLogger(HTTPTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
