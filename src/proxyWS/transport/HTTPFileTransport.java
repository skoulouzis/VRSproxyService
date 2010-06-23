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

import proxyWS.utils.Misc;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.uva.vlet.exception.VlURISyntaxException;
import nl.uva.vlet.io.VStreamReadable;
import nl.uva.vlet.vrl.VRL;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author skoulouz
 */
public class HTTPFileTransport extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        VStreamReadable readble;

//        debug("New Request: "+request.getQueryString());
        try {

            VRL fileVrl = new VRL(request.getQueryString());

            try {
//                FileInputStream fis = new FileInputStream(f);
                DataTransportContext dContext = new DataTransportContext(true, getServletContext());

                readble = (VStreamReadable) dContext.getVnode(fileVrl);
                InputStream fis = readble.getInputStream();

//                nl.uva.vlet.io.StreamUtil.copyStreams(readble.getInputStream(), response.getOutputStream());
//                nl.uva.vlet.io.StreamUtil.copyStreams(fis, response.getOutputStream());

                org.apache.commons.io.IOUtils.copy(fis, response.getOutputStream());


                response.flushBuffer();
                response.getOutputStream().flush();
                response.getOutputStream().close();

            } catch (Exception ex) {
                Logger.getLogger(HTTPFileTransport.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (VlURISyntaxException ex) {
            Logger.getLogger(HTTPFileTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        ServletContext context = getServletContext();
        String query = request.getQueryString();

        //upload
        try {

            Map inArgMap = (Map) context.getAttribute(proxyWS.utils.Constants.IN_ARGS);

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // maximum size that will be stored in memory
            factory.setSizeThreshold((int) proxyWS.utils.Constants.MAX_SIZE_FILE);
            // the location for saving data that is larger than getSizeThreshold()
            String sandbox = (String) context.getAttribute(proxyWS.utils.Constants.SANDBOX);
            String dir = null;
            if (sandbox != null && new File(sandbox).exists()) {
//                System.out.println("Will save in sandbox: " + sandbox);
                dir = sandbox + File.separator;
            } else {
//                System.out.println("Will save in tmp: " + Misc.getTmpDir() + File.separator);
                dir = Misc.getTmpDir() + File.separator;// + fi.getFieldName();
            }
            factory.setRepository(new File(dir));


            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum size before a FileUploadException will be thrown
            upload.setSizeMax(proxyWS.utils.Constants.MAX_SIZE_FILE * 90000);

            List fileItems = upload.parseRequest(request);


            Iterator i = fileItems.iterator();

            String[] filePaths = new String[fileItems.size()];

            int c = 0;

//            System.out.println("-----sandbox: " + sandbox);
            File f = null;

            if (sandbox != null && new File(sandbox).exists()) {
//                System.out.println("Will save in sandbox: " + sandbox);
                dir = sandbox + File.separator;
            } else {
//                System.out.println("Will save in tmp: " + Misc.getTmpDir() + File.separator);
                dir = Misc.getTmpDir() + File.separator;// + fi.getFieldName();
            }

            while (i.hasNext()) {

                Object obj = i.next();
//                System.out.println("it["+c+"]"+obj+" Class: "+obj.getClass().getName());
                org.apache.commons.fileupload.disk.DiskFileItem fi = (DiskFileItem) obj;

//                System.out.println("isFormField "+fi.isFormField() + "\n isInMemory "+fi.isInMemory()+ "\n ");                
                f = new File(dir + fi.getFieldName());

                fi.write(f);
                filePaths[c] = f.getAbsolutePath();
                c++;
                debug("File Saved in : " + filePaths[c]);
            }

//            String[] prevFilePaths = (String[]) inArgMap.get(query + proxyWS.utils.Constants.IN_UPLOAD_FILES);
//            Vector<String> newPaths;
//            if(prevFilePaths!=null){
//                newPaths = new String[prevFilePaths.length+filePaths.length];
//                for(int j=0;j<prevFilePaths.length;j++){
//                    newPaths[j] = prevFilePaths[j];
//                }
//            }
            debug("File(s) Saved in : " + dir);
            inArgMap.put(query + proxyWS.utils.Constants.IN_UPLOAD_FILES, filePaths);
            inArgMap.put(query + proxyWS.utils.Constants.IN_UPLOAD_DIR, dir);


        } catch (Exception ex) {
            Logger.getLogger(HTTPFileTransport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void debug(String msg) {
        System.err.println(this.getClass().getName() + ": " + msg);
    }
}


