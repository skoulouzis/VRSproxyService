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
package proxyWS.clients;

import java.io.FileNotFoundException;
import proxyWS.transport.DataTransportContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


import java.net.URI;
import java.net.URL;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import nl.uva.vlet.exception.VlException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;

import proxyWS.utils.Misc;

import com.thoughtworks.xstream.XStream;

import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;
import nl.uva.vlet.io.VStreamReadable;
import nl.uva.vlet.vfs.VFile;
import proxyWS.utils.AxisCalls;

/**
 *
 * @author S. Koulouzis 
 */
public class VRSProxyClient {

    private proxyWS.stubs.ProxyService.ProxyService service;
    private String portAddress;
    private static DataTransportContext dataTransCntx;
    private static XStream xstream;
    private int inc = 0;
    private int timeout = proxyWS.utils.Constants.TIME_OUT;
    private int time = 0;

    /** Creates a new instance of Client */
    public VRSProxyClient() throws ServiceException {
        xstream = new XStream(new DomDriver());
    }

    public VRSProxyClient(String portAddress) throws ServiceException {
        try {
            this.portAddress = portAddress;
            proxyWS.stubs.ProxyService.ProxyServiceServiceLocator sl = new proxyWS.stubs.ProxyService.ProxyServiceServiceLocator();
            service = sl.getProxyService(new URL(portAddress));
            xstream = new XStream(new DomDriver());
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the data produced by a proxy call 
     * @param url
     * @return the data 
     */
    public Object getData(URI url, boolean block) throws TimeoutException {
        Object res = null;

        boolean timedout = false;

        InputStream in = null;
        try {
            VStreamReadable streamble = (VStreamReadable) getDataTransCntx().getVnode(url.toString());
            try {
                in = streamble.getInputStream();
            } catch (VlException ex) {

                if (block) {
                    while (in == null || in.available() < 1 || res == null) {
                        try {
                            time = 300 * inc;
                            if (time >= timeout) {
                                timedout = true;

                                System.err.println("Timed out while tring to get data from: " + url);

                                throw new TimeoutException("Timed out: " + time);
                            }
                            Thread.sleep(time);
                            inc++;
                            streamble = (VStreamReadable) getDataTransCntx().getVnode(url.toString());
                            in = streamble.getInputStream();
                            res = xstream.fromXML(in);

                            if (res != null) {
                                break;
                            }
                        } catch (Exception ex1) {
                            res = null;
                        }
                    }
                    return res;
                }
            }

            res = xstream.fromXML(in);
        } catch (Exception ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public ObjectInputStream getObjectInpuStream(URI url, boolean block) throws TimeoutException {
        ObjectInputStream res = null;

        boolean timedout = false;

        InputStream in = null;
        try {
            VStreamReadable streamble = (VStreamReadable) getDataTransCntx().getVnode(url.toString());
            try {
                in = streamble.getInputStream();
            } catch (VlException ex) {

                if (block) {
                    while (in == null || in.available() < 1 || res == null) {
                        try {
                            time = 300 * inc;
                            if (time >= timeout) {
                                timedout = true;

                                System.err.println("Timed out while tring to get data from: " + url);

                                throw new TimeoutException("Timed out: " + time);
                            }
                            Thread.sleep(time);
                            inc++;
                            streamble = (VStreamReadable) getDataTransCntx().getVnode(url.toString());
                            in = streamble.getInputStream();
                            res = xstream.createObjectInputStream(in);

                            if (res != null) {
                                break;
                            }
                        } catch (Exception ex1) {
                            res = null;
                        }
                    }
                    return res;
                }
            }

            res = xstream.createObjectInputStream(in);
        } catch (Exception ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    /**
     * Returns the data produced by a proxy call 
     * @param url
     * @return the data 
     */
    public Object getData(String servlet, boolean block) throws TimeoutException {
        try {
            return getData(new URI(servlet), block);
        } catch (URISyntaxException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Downloads a file given an http location. 
     * @param servlet 
     * @param saveLocation, if null the file will be saved in the tmp dir with the 
     * name inputFile + <hashCode>
     * @return the saved file 
     */
    public File getFile(String location, String saveLocation) {
        try {
            return getFile(new URI(location), saveLocation);
        } catch (URISyntaxException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

    public File getFile(URL url, String saveLocation) {
        try {
            return getFile(url.toURI(), saveLocation);
        } catch (URISyntaxException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public File getFile(URI url, String saveLocation) {
        File f = null;
        try {

            FileOutputStream fos = null;

            int totalSize = 0;
            String infile;
            if (saveLocation == null || saveLocation.equals("")) {
                infile = Misc.getTmpDir() + "/inputFile" + this.hashCode();
            } else {
                infile = saveLocation;
            }

//        VStreamReadable readble = (VStreamReadable) getDataTransCntx().getVnode(url.toString());
            f = new File(infile);
//        URLConnection connection;
            proxyWS.transport.DataTransportContext.init(false, null);


            InputStream in = null;

//        try {
            while (in == null) {
                try {
                    try {

                        in = proxyWS.transport.DataTransportContext.getInputStream(url.toString(), true);
                    } catch (VlException ex) {

                        in = null;
//                        System.err.println("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");

//                        Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if ((time * inc) >= proxyWS.utils.Constants.TIME_OUT) {
                        try {
                            throw new VlException("VRS Timed out: " + (time * inc));
                        } catch (VlException ex) {
                            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

//                    debug("Not ready yet sleeping: " + (time * inc) + " in.available():" + in.available());
                    Thread.sleep(time * inc);
                    inc++;
                } catch (InterruptedException ex) {
                    Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


//            connection = url.toURL().openConnection();
//            connection.setDoOutput(true);
//            connection.connect();
            fos = new FileOutputStream(f);
//            nl.uva.vlet.io.StreamUtil.copyStreams(readble.getInputStream(), fos);
//            org.apache.commons.io.IOUtils.copy(connection.getInputStream(), fos);
            org.apache.commons.io.IOUtils.copy(in, fos);
            fos.flush();
            fos.close();

//            if (f.length() <= 1) {
//                System.err.println(f.getAbsoluteFile() + " WARNING FILE IS ------00000000000000000");
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
//        }

        } catch (IOException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return f;
    }

//    public void asyncUploadFiles(File[] targetFiles, URI targetURL) {
//        Part[] parts;
//        long size = 0;
//        Thread thred;
//        UploadFiles upload;
//
//        upload = new UploadFiles(targetFiles, targetURL);
//        thred = new Thread(upload);
//        thred.start();
//    }
    /** 
     * Uploads files in the location provided by the proxy service 
     * @param parts the file parts 
     * @param targetURL
     * @return true if success 
     */
//    private boolean uploadFiles(Part[] parts, URI targetURL) {
//        org.apache.commons.httpclient.methods.PostMethod filePost = null;
//        try {
//            filePost = new org.apache.commons.httpclient.methods.PostMethod(targetURL.toString());
//
//            filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
//
//            filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
//
//            HttpClient client = new HttpClient();
//            client.getHttpConnectionManager().
//                    getParams().setConnectionTimeout(5000);
//
//            int status = client.executeMethod(filePost);
//
//            if (status == HttpStatus.SC_OK) {
////                System.out.println("Upload complete, response=" + filePost.getResponseBodyAsString());
//                return true;
//            } else {
//                System.err.println(
//                        "Upload failed, response=" +
//                        HttpStatus.getStatusText(status) + ". check if " + targetURL + " exists");
//
//                return false;
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            filePost.releaseConnection();
//        }
//        return false;
//    }
    public boolean uploadFiles(File dir, URI targetURL) {
        File[] f = {dir};
        return uploadFiles(f, targetURL);
    }

    /** 
     * Uploads files in the location provided by the proxy service 
     * @param targetFiles the files 
     * @param targetURL
     * @return true if success 
     */
    public boolean uploadFiles(File[] targetFiles, URI targetURL) {
        String target = targetURL.toString();
        DataTransportContext d = getDataTransCntx();
        URI localFileURI;
        VFile localFile;
        OutputStream out;
        InputStream in;

        try {
            for (int i = 0; i < targetFiles.length; i++) {
                if (targetFiles[i].isFile() && targetFiles[i].exists() && !targetFiles[i].getName().startsWith(".")) {
                    localFileURI = new URI("file://" + targetFiles[i].getAbsolutePath());
                    localFile = (VFile) d.getVnode(localFileURI.toString());
                    in = localFile.getInputStream();

                    target = target + "&" + localFileURI;

                    out = d.getOutputStream(target);

                    org.apache.commons.io.IOUtils.copyLarge(in, out);

                    out.flush();
                    out.close();

                    in.close();

                    target = targetURL.toString();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
//    public boolean uploadFiles(File[] targetFiles, URI targetURL) {
//        Part[] parts;
//        Vector<Part> vParts = new Vector<Part>();
//        long size = 0;
//        try {
//            for (int i = 0; i < targetFiles.length; i++) {
//                if (targetFiles[i].isFile() && targetFiles[i].exists() && !targetFiles[i].getName().startsWith(".")) {
////                    System.out.println("Uploading file["+i+"] " + targetFiles[i].getAbsolutePath());
//                    vParts.add(new FilePart(targetFiles[i].getName(), targetFiles[i]));
//                    size = size + targetFiles[i].length();
//                    if (size >= proxyWS.utils.Constants.MAX_SIZE_FILE) {
//                        parts = (Part[]) vParts.toArray(new Part[vParts.size()]);
//                        if (!uploadFiles(parts, targetURL)) {
//                            return false;
//                        }
//                        vParts.clear();
//                    }
//                }
//
//                if (targetFiles[i].isDirectory()) {
////                    System.out.println("    DIR: " + targetFiles[i].getAbsolutePath());
//                    uploadFiles(targetFiles[i].listFiles(), targetURL);
//                }
//            }
//
//            parts = (Part[]) vParts.toArray(new Part[vParts.size()]);
//            vParts.clear();
//            return uploadFiles(parts, targetURL);
//        } catch (Exception ex) {
//            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return false;
//    }
    /**
     * Uploads data in the location provided by the ProxyService 
     * @param servlet
     * @param data 
     * @return true if success 
     * @throws java.io.IOException
     */
    public boolean uploadData(String servlet, Object data) throws IOException {
        Object[] dataArr = {data};
        return uploadData(servlet, dataArr);
    }

    /**
     * Uploads data in the location provided by the ProxyService 
     * @param servlet
     * @param parts the data parts 
     * @return true if success 
     * @throws java.io.IOException
     */
    public boolean uploadData(String servlet, Part[] parts) throws IOException {
        org.apache.commons.httpclient.methods.PostMethod postMethod = null;

        postMethod = new org.apache.commons.httpclient.methods.PostMethod(servlet);

        postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

        postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().
                getParams().setConnectionTimeout(5000);

        int status = client.executeMethod(postMethod);

        if (status == HttpStatus.SC_OK) {
//            System.out.println("Upload complete, response=" + postMethod.getResponseBodyAsString());
            postMethod.releaseConnection();
            return true;
        } else {
            System.err.println(
                    "Upload failed, response=" +
                    HttpStatus.getStatusText(status) + ". Check if " + servlet + " exists");
            postMethod.releaseConnection();
            return false;
        }
    }

//    public boolean uploadData(String servlet, Object[] data) throws IOException {
//
//
//        ObjectOutputStream objOut = null;
////        URLConnection writebale;
//        try {
//            VStreamWritable writebale = getDataTransCntx().getVStreamAccessable(servlet);
//
//
////            writebale = new URL(servlet).openConnection();
////            writebale.setDoOutput(true);
////            
////            writebale.connect();
//            
//            
//            
//
//            objOut = new ObjectOutputStream(writebale.getOutputStream());
//
//
//            debug("Will write " + data.length + " args");
//
//            objOut.writeInt(new Integer(data.length));
//
//            for (int i = 0; i < data.length; i++) {
//                objOut.writeObject(data[i]);
//            }
//
////            objOut.flush();
////            objOut.close();
//            
////            writebale.getInputStream().read();
//            
//
//        } catch (Exception ex) {
//            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
//        } 
//        return true;
//    }
    /**
     * Uploads data in the location provided by the ProxyService 
     * @param servlet
     * @param data the data  
     * @return true if success 
     * @throws java.io.IOException
     */
    public boolean uploadData(String servlet, Object[] data) throws IOException {

        org.apache.commons.httpclient.methods.PostMethod postMethod = null;

        postMethod = new org.apache.commons.httpclient.methods.PostMethod(servlet);

        postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);


        Vector<Part> vParts = new Vector<Part>();
        Part[] arr;
        ByteArrayPartSource bytePart;
        long size = 0;
        String xmlData;
        for (int i = 0; i < data.length; i++) {

            xmlData = xstream.toXML(data[i]);
            byte[] byteData = xmlData.getBytes();

//            byte[] byteData =  ObjectTransformer.getBytes(data[i]);             
            bytePart = new ByteArrayPartSource(data[i].getClass().getCanonicalName(), byteData);

            size = size + bytePart.getLength();
            vParts.add(new FilePart(data[i].getClass().getCanonicalName(), bytePart));

            if (size >= proxyWS.utils.Constants.MAX_SIZE_DATA) {
                arr = (Part[]) vParts.toArray(new Part[vParts.size()]);
                if (!uploadData(servlet, arr)) {
                    return false;
                }
                vParts.clear();
            }

//            System.out.println("Uploading data: " + bytePart.getFileName() + " size: " + (size / (1024 * 1024)));

        }
        arr = (Part[]) vParts.toArray(new Part[vParts.size()]);
        return uploadData(servlet, arr);

    }

    public proxyWS.stubs.ProxyService.ProxyService getService() {
        return service;
    }

    public proxyWS.stubs.ProxyService.ProxyService getService(String portAddress) {
        try {
            return getService(new URL(portAddress));
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public proxyWS.stubs.ProxyService.ProxyService getService(URL portAddress) {
        try {
            proxyWS.stubs.ProxyService.ProxyServiceServiceLocator sl = new proxyWS.stubs.ProxyService.ProxyServiceServiceLocator();
            return sl.getProxyService(portAddress);
        } catch (ServiceException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public Object asncCallBack(String method, Object[] args) {
        try {
            return AxisCalls.asncCallBack(args, method, new URL(portAddress));
        } catch (MalformedURLException ex) {
            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static DataTransportContext getDataTransCntx() {
        if (dataTransCntx == null) {
            dataTransCntx = new DataTransportContext(false, null);
        }
        return dataTransCntx;
    }

    private void debug(String msg) {
        System.err.println(this.getClass().getName() + ": " + msg);
    }
}//class UploadFiles implements Runnable {
//
//    private Part[] parts;
//    private URI targetURL;
//    private File[] files;
//
////    public UploadFiles(Part[] parts, URI targetURL) {
////        this.parts = parts;
////        this.targetURL = targetURL;
////    }
//    public UploadFiles(File[] files, URI targetURL) {
//        this.files = files;
//        this.targetURL = targetURL;
//    }
//
//    public void run() {
//        uploadFiles(files, targetURL);
//    }
//
//    /** 
//     * Uploads files in the location provided by the proxy service 
//     * @param parts the file parts 
//     * @param targetURL
//     * @return true if success 
//     */
//    public boolean uploadFiles(Part[] parts, URI targetURL) {
//        org.apache.commons.httpclient.methods.PostMethod filePost = null;
//        try {
//            filePost = new org.apache.commons.httpclient.methods.PostMethod(targetURL.toString());
//
//            filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
//
//            filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
//
//            HttpClient client = new HttpClient();
//            client.getHttpConnectionManager().
//                    getParams().setConnectionTimeout(5000);
//
//            int status = client.executeMethod(filePost);
//
//            if (status == HttpStatus.SC_OK) {
////                System.out.println("Upload complete, response=" + filePost.getResponseBodyAsString());
//                return true;
//            } else {
//                System.err.println(
//                        "Upload failed, response=" +
//                        HttpStatus.getStatusText(status) + ". check if " + targetURL + " exists");
//
//                return false;
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            filePost.releaseConnection();
//        }
//        return false;
//    }
//
//    /** 
//     * Uploads files in the location provided by the proxy service 
//     * @param targetFiles the files 
//     * @param targetURL
//     * @return true if success 
//     */
//    private boolean uploadFiles(File[] targetFiles, URI targetURL) {
//        Part[] parts;
//        Vector<Part> vParts = new Vector<Part>();
//        long size = 0;
//        try {
//            for (int i = 0; i < targetFiles.length; i++) {
//                if (targetFiles[i].isFile() && targetFiles[i].exists() && !targetFiles[i].getName().startsWith(".")) {
////                    System.out.println("Uploading file["+i+"] " + targetFiles[i].getAbsolutePath());
//                    vParts.add(new FilePart(targetFiles[i].getName(), targetFiles[i]));
//                    size = size + targetFiles[i].length();
//                    if (size >= proxyWS.utils.Constants.MAX_SIZE_FILE) {
//                        parts = (Part[]) vParts.toArray(new Part[vParts.size()]);
//                        if (!uploadFiles(parts, targetURL)) {
//                            return false;
//                        }
//                        vParts.clear();
//
//
//
//                    }
//                }
//
//                if (targetFiles[i].isDirectory()) {
////                    System.out.println("    DIR: " + targetFiles[i].getAbsolutePath());
//                    uploadFiles(targetFiles[i].listFiles(), targetURL);
//                }
//            }
//
//            parts = (Part[]) vParts.toArray(new Part[vParts.size()]);
//            vParts.clear();
//            return uploadFiles(parts, targetURL);
//        } catch (Exception ex) {
//            Logger.getLogger(VRSProxyClient.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return false;
//    }
//}
