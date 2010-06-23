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
package proxyWS.impl.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;
import junit.framework.TestCase;
import nl.uva.vlet.exception.VlURISyntaxException;
import nl.uva.vlet.vrl.VRL;
import proxyWS.config.Conf;

/**
 *
 * @author skoulouz
 */
public class ProxyWSTest extends TestCase {

    private proxyWS.stubs.ProxyService.ProxyService proxyService;
    private proxyWS.stubs.ProxyService.ProxyServiceServiceLocator proxySl;
    private static final String testDataLocation = "testData";

    public ProxyWSTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            proxySl = new proxyWS.stubs.ProxyService.ProxyServiceServiceLocator();
            proxyService = proxySl.getProxyService(new URL("http://localhost:8080/axis/services/ProxyService"));
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /**
     * Test of createSandbox method, of class VProxyService.
     */
    public void testCreateSandbox() {
        try {
            System.out.println("testCreateSandbox");
            String path = "testSandBox";
            String result = proxyService.createSandbox(path);
            System.out.println("    Result: " + result);
            assertNotNull(result);

        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getFileUploadURI method, of class VProxyService.
     */
    public void testGetFileUploadURI() {
        try {
            System.out.println("testGetFileUploadURI");

            //test uploading in tmp
            String sandboxPath = "";//"testSandBoxUploadFiles";

            String targetURI = proxyService.getFileUploadURI(sandboxPath);
            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();

            assertNotNull(targetURI);

            System.out.println("    File upload URI: " + targetURI);

            File[] files = new File[2];
            files[0] = new File(testDataLocation + "/testFile1.txt");
            files[1] = new File(testDataLocation + "/testFile2.txt");

            boolean result = pClient.uploadFiles(files, new URI(targetURI));
            assertTrue(result);

            //test uploading/downloading in sandbox
            sandboxPath = "testSandBoxUploadFiles";
            targetURI = proxyService.getFileUploadURI(sandboxPath);
            result = pClient.uploadFiles(files, new URI(targetURI));
            assertTrue(result);

        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testlist() {
        System.out.print("testlist");
        try {
            String path = "file:///tmp/";
            String[] result = proxyService.list(path);

            if (result != null) {
                for (String res : result) {
                    System.out.println("  list: " + res);
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of getUploadURI method, of class VProxyService.
     */
    public void testGetUploadURI() {
        try {
            System.out.println("testGetUploadURI");

            Object[] data = new Object[2];

            data[0] = new String("String Data");
            data[1] = new Integer(88539237);

            //will be replaced with a feneric conf class. For now its the size of data to upload
            int conf = 1024;

            String tagetURI = proxyService.getUploadURI(conf);
            System.out.println("    Data upload URI: " + tagetURI);

            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();
            boolean result = pClient.uploadData(tagetURI, data);

            assertTrue(result);

        } catch (IOException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Test of callService method, of class VProxyService.
     */
    public void testSimpleCallService() {
        try {
            System.out.println("testSimpleCallService");
            String serviceName = "SimpleService";
            String methodName = "Obj2Obj";

            //test simple proxy call. Data are provided in the call
            Object inData = new String("Input data");
            Object[] args = {inData};
            String dataRef = proxyService.callService(serviceName, methodName, args);
            System.out.println("    Data ref: " + dataRef);

            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();
            Object o = pClient.getData(dataRef, true);

            proxyWS.examples.SimpleService ss = new proxyWS.examples.SimpleService();


            assertEquals(ss.Obj2Obj(inData), o);


        } catch (TimeoutException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testUploadDataInMemCallService() throws VlURISyntaxException {
        System.out.println("testUploadDataInMemCallService");
        String dataRef;
        Object o;
        Vector<Byte[]> parts = null;
        try {
            //test upload data in mem before proxy call
            String serviceName = "SimpleService";
            String methodName = "Obj2Obj";

//            String inData = new String("Input data");
            Conf inData = new Conf();
            Map map = inData.getVfsServers();
            VRL loc = new VRL("gsiftp://pc-vlab18.science.uva.nl/");
            Vector<VRL> locations = new Vector<VRL>();
            for (int i = 0; i < 10; i++) {
                locations.add(loc);
            }
            map.put(loc.getScheme(), locations);
            loc = new VRL("ssh://pc-vlab18.science.uva.nl/");
            for (int i = 0; i < 10; i++) {
                locations.add(loc);
            }
            map.put(loc.getScheme(), locations);

            String upluadDataURI = proxyService.getUploadURI(1024 * 1024);
            URL url = new URL(upluadDataURI);
            System.out.println("    Data upload URI: " + upluadDataURI);

            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();
            pClient.uploadData(upluadDataURI, inData);


            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_MEM, "/dummypath", url.getQuery(), "");
            Object[] args3 = {uri.toString()};

            dataRef = proxyService.callService(serviceName, methodName, args3);
            System.out.println("    Data ref: " + dataRef);
            o = pClient.getData(dataRef, true);
            assertEquals(inData.getClass().getName(), o.getClass().getName());


            methodName = "ObjArr2Obj";
            System.out.println("    " + methodName);

            //too much mamory data 
            int size = 5;
            Object[] args = new Object[size];
            Random r = new Random();
            byte[] bytes = new byte[1 * 1024 * 1024];
            for (int i = 0; i < size; i++) {
                r.nextBytes(bytes);
                args[i] = bytes;
            }

            upluadDataURI = proxyService.getUploadURI(bytes.length * size);
            url = new URL(upluadDataURI);
            System.out.println("    Data upload URI" + upluadDataURI);
            pClient.uploadData(upluadDataURI, args);


            uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_MEM, "/dummypath", url.getQuery(), "");
            Object[] args4 = {uri.toString()};

            dataRef = proxyService.callService(serviceName, methodName, args4);
            System.out.println("    Data ref: " + dataRef);
            o = pClient.getData(dataRef, true);
            Object dc = (Object[]) o;

            assertNotNull(o);

        } catch (TimeoutException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        }
    }
    
    public void testUploadFilesCallService() {
        System.out.println("testUploadFilesCallService");
        boolean result;
        String[] dataPaths;
        String targetURI;
        URL url;
        URI uri;
        proxyWS.clients.VRSProxyClient pClient;
        String dataRef;
        String data;
        String serviceName;
        String methodName;
        long size = 0;
        File[] files;
        proxyWS.examples.SimpleService ss = new proxyWS.examples.SimpleService();
        {
            try {
                serviceName = "SimpleService";
                methodName = "path2Obj";
                pClient = new proxyWS.clients.VRSProxyClient();

                targetURI = proxyService.getFileUploadURI("testUploadSandbox");
                System.out.println("Will upload in: " + targetURI);
                url = new URL(targetURI);
                files = new File[1];
                files[0] = new File(testDataLocation + "/testFile1.txt");
                
                result = pClient.uploadFiles(files, new URI(targetURI));
                assertTrue(result);
                
                 uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");
                Object[] args4 = {uri.toString()};
                 dataRef = proxyService.callService(serviceName, methodName, args4);
                data = (String) pClient.getData(dataRef, true);
                assertEquals(ss.path2Obj(files[0].getAbsolutePath()), data);


                targetURI = proxyService.getFileUploadURI("testUploadSandbox");
                System.out.println("Will upload in: " + targetURI);
                url = new URL(targetURI);
                files = new File[1];
                files[0] = new File(testDataLocation + "/testFile2.txt");
                
                result = pClient.uploadFiles(files, new URI(targetURI));
                assertTrue(result);
                 uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");
                Object[] args5 = {uri.toString()};
                 dataRef = proxyService.callService(serviceName, methodName, args5);
                data = (String) pClient.getData(dataRef, true);
                assertEquals(ss.path2Obj(files[0].getAbsolutePath()), data);


//                test upload files in sandbox and working on sandbox
                targetURI = proxyService.getFileUploadURI("testUploadSandbox2");
                url = new URL(targetURI);
                files = new File[2];
                files[0] = new File(testDataLocation + "/testFile1.txt");
                files[1] = new File(testDataLocation + "/testFile2.txt");
                result = pClient.uploadFiles(files, new URI(targetURI));
                assertTrue(result);

                uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_DIR, "/dummypath", url.getQuery(), "");

                Object[] args6 = {uri.toString()};
                dataRef = proxyService.callService(serviceName, methodName, args6);
                data = (String) pClient.getData(dataRef, true);
                size = (files[0].length() + files[1].length());
                assertEquals(String.valueOf(size), data);


                //test many large files
                File dataDir = new File(testDataLocation + "/datadir");
                targetURI = proxyService.getFileUploadURI("datadir");

                url = new URL(targetURI);
                result = pClient.uploadFiles(dataDir.listFiles(), new URI(targetURI));
                assertTrue(result);

                uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_DIR, "/dummypath", url.getQuery(), "");

                System.out.println("Data are in: " + url.getQuery());

                Object[] args7 = {uri.toString()};

                dataRef = proxyService.callService(serviceName, methodName, args7);
                data = (String) pClient.getData(dataRef, true);
                assertEquals(ss.path2Obj(testDataLocation + "/datadir"), data);


           
                
                methodName = "manyArgsRetunsDirsLoc";
                File dataDir1 = new File(testDataLocation + "/datadir");
                targetURI = proxyService.getFileUploadURI("dir1");
                url = new URL(targetURI);
                result = pClient.uploadFiles(dataDir1.listFiles(), new URI(targetURI));
                URI uri1 = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_DIR, "/dummypath", url.getQuery(), "");
                assertTrue(result);

                File dataDir2 = new File(testDataLocation + "/dir2");
                targetURI = proxyService.getFileUploadURI("dir2");
                url = new URL(targetURI);
                result = pClient.uploadFiles(dataDir2.listFiles(), new URI(targetURI));
                URI uri2 = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_DIR, "/dummypath", url.getQuery(), "");
                assertTrue(result);

                String[] dirs = {uri1.toString(), uri2.toString()};
                Object[] args8 = {"data1", "data2", dirs};

                dataRef = proxyService.callService(serviceName, methodName, args8);
                dataPaths = (String[]) pClient.getData(dataRef, true);

                String[] paths = {dataDir1.getAbsolutePath(), dataDir2.getAbsolutePath()};

                assertEquals(ss.manyArgsRetunsDirsLoc("data1", "data2", paths).length, dataPaths.length);

            } catch (Exception ex) {
                Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    public void testMixedCallService() {
        System.out.println("testMixedCallService");
        try {
            //test mixed input args;
            String serviceName = "SimpleService";
            String methodName = "path2ObjManyArgs";

            String targetURI = proxyService.getFileUploadURI("testUploadSandbox3");
            URL url = new URL(targetURI);
            File[] files = new File[1];
            files[0] = new File(testDataLocation + "/testFile1.txt");
            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();

            boolean result = pClient.uploadFiles(files, new URI(targetURI));
            assertTrue(result);

            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");

            Object[] manyArgs = {new Long(8866999), new Boolean(false), new Integer(44)};

            Object[] args = {uri.toString(), manyArgs};


            String dataRef = proxyService.callService(serviceName, methodName, args);

            String data = (String) pClient.getData(dataRef, true);

            proxyWS.examples.SimpleService ss = new proxyWS.examples.SimpleService();

            assertEquals(ss.path2ObjManyArgs(testDataLocation + "/testFile1.txt", manyArgs), data);

        } catch (TimeoutException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
            fail(ex.toString());
        }
    }

    public void testTooMixedCallService() {
        System.out.println("testTooMixedCallService");
        try {
            //test mixed input args;
            String serviceName = "SimpleService";
            String methodName = "path2ObjTooManyArgs";


            String targetURI = proxyService.getFileUploadURI("testUploadSandbox4");
            URL url = new URL(targetURI);
            File[] files = new File[1];
            files[0] = new File(testDataLocation + "/testFile1.txt");
            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();
            boolean result = pClient.uploadFiles(files, new URI(targetURI));
            assertTrue(result);


            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");
            Integer num = new Integer(99);
            Object[] manyArgs = {new Long(8866999), new Boolean(false), new Integer(44)};
            Object[] args = {uri.toString(), num, manyArgs};

            String dataRef = proxyService.callService(serviceName, methodName, args);

            String data = (String) pClient.getData(dataRef, true);

            proxyWS.examples.SimpleService ss = new proxyWS.examples.SimpleService();

            assertEquals(ss.path2ObjTooManyArgs(testDataLocation + "/testFile1.txt", num, manyArgs), data);


        } catch (TimeoutException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testDownlodFileCallService() {
        System.out.println("testDownlodFileCallService");
        try {
            //test mixed input args;
            String serviceName = "SimpleService";
            String methodName = "path2Path";

            String targetURI = proxyService.getFileUploadURI("testUploadSandbox3");
            File[] files = new File[1];
            files[0] = new File(testDataLocation + "/testFile1.txt");
            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();
            boolean result = pClient.uploadFiles(files, new URI(targetURI));
            assertTrue(result);
            URL url = new URL(targetURI);


            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");
            Object[] args = {uri.toString()};

            String dataRef = proxyService.callService(serviceName, methodName, args);
            String data = (String) pClient.getData(dataRef, true);

            data = "file://" + data;

            VRL dataVRL = new VRL(data);

            String fileLoc = proxyService.getFileURI(dataVRL.toString());
            System.out.println("File is at: " + fileLoc);
            File f = pClient.getFile(fileLoc, "/tmp/OUT_FILE");

            assertTrue(f.exists());
            if(f.length()<=0){
                fail();
            }


        } catch (TimeoutException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VlURISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testRefFileCallService() {
        System.out.println("testRefFileCallService");
        Object data;
        try {
            //test mixed input args;
            String serviceName = "SimpleService";
            String methodName = "path2Obj";
            String fileLocation = "http://localhost:8080/docs/introduction.html";

            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_REF_FILE, "/dummypath", fileLocation, "");
            Object[] args = {uri.toString()};

            data = proxyService.callServiceReturnObject(serviceName, methodName, args);

            System.out.println("    Data: " + data);


            fileLocation = "http://localhost:8080/docs/introduction.htmlhttp://localhost:8080/docs/setup.htmlhttp://localhost:8080/docs/realm-howto.html";

            uri = new URI("wsdt", proxyWS.utils.Constants.IN_REF_DIR, "/testSandbox4", fileLocation, "");
            Object[] args2 = {uri.toString()};

            data = proxyService.callServiceReturnObject(serviceName, methodName, args2);

            System.out.println("    Data: " + data);


        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testSimpleAsyncCallService() {

        try {
            System.out.println("testSimpleAsyncCallService");
            String serviceName = "SimpleService";
            String methodName = "Obj2Obj";

            //test simple proxy call. Data are provided in the call
            Long inData = new Long(15000);
            Object[] args = {inData};
            String dataRef = proxyService.asyncCallService(serviceName, methodName, args);

            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();

            Object o = pClient.getData(dataRef, true);

            System.out.println("    Data: " + o);
            assertEquals(inData, o);

        } catch (TimeoutException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testCallServiceReturnObject() {
        System.out.println("testCallServiceReturnObject");
        try {
            String serviceName = "SimpleService";
            String methodName = "path2Path";

            String targetURI = proxyService.getFileUploadURI("testUploadSandbox3");
            File[] files = new File[1];
            files[0] = new File(testDataLocation + "/testFile1.txt");
            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();
            boolean result = pClient.uploadFiles(files, new URI(targetURI));
            assertTrue(result);
            URL url = new URL(targetURI);


            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");
            Object[] args = {uri.toString()};

            Object data = proxyService.callServiceReturnObject(serviceName, methodName, args);

            System.out.println("    Returns : " + data);

            assertNotNull(data);


        } catch (MalformedURLException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testCallServiceAsyncReturnObject() {
        System.out.println("testCallServiceAsyncReturnObject");
        try {
            String serviceName = "SimpleService";
            String methodName = "path2Path";

            String targetURI = proxyService.getFileUploadURI("testUploadSandbox3");
            File[] files = new File[1];
            files[0] = new File(testDataLocation + "/testFile1.txt");
            proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient();
            boolean result = pClient.uploadFiles(files, new URI(targetURI));
            assertTrue(result);
            URL url = new URL(targetURI);


            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");
            Object[] args = {uri.toString()};

            int key = proxyService.asyncCallServiceReturnObject(serviceName, methodName, args);

            Object data = proxyService.getReturnedValue(key);

            int MAX = 5000;
            int time = 0;
            int inc = 1;
            while (data == null) {
                time = 100 * inc;
                if (time >= MAX) {
                    break;
                }
                Thread.sleep(time);
                System.out.println("    Sleeping for : " + time);
                inc++;
                data = proxyService.getReturnedValue(key);
            }


            System.out.println("    Returns : " + data);

            assertNotNull(data);


        } catch (InterruptedException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testDelete() {
        boolean res;
        try {
            String[] contents = proxyService.list("file:///opt/tomcat/temp/");

            for (String cont : contents) {
                res = proxyService.delete(cont);

                System.out.println("delete: " + cont + " " + res);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ProxyWSTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
