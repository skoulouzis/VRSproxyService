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
package proxyWS.examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vfs.localfs.LFile;
import nl.uva.vlet.vrl.VRL;
import proxyWS.transport.DataTransportContext;

/**
 *
 * @author alogo
 */
public class SimpleService {

    public SimpleService() {
        DataTransportContext.init(true, null);
    }

    public Integer doSomething(Integer value) {

        return new Integer(value + 1);
    }

    public String doSomethingElse(String value) {
        return value + " hello !!";
    }

    /**
     * 
     * Test proxy call, input.upload.mem, input.ref.mem, with data ref
     * @param arg
     * @return
     */
    public Object Obj2Obj(Object arg) {
        System.out.println("Obj2Obj Got " + arg.getClass().getName());
        if (arg instanceof Long) {
            try {
                System.out.println("Sleeping....");
                Long sleep = (Long) arg;
                Thread.sleep(sleep);
            } catch (InterruptedException ex) {
                Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return arg;
    }

    public Object ObjArr2Obj(Object[] arg) {
        System.out.println("ObjArr2Obj Got " + arg.getClass().getName());
        return arg;
    }

    /**
     * Test input.upload.file, nput.ref.file with data ref
     * @param path
     * @return
     */
    public Object path2Obj(String path) {

        FileInputStream fis = null;
        String data;
        System.out.println("path2Obj    Loading file(s) from " + path);
        try {
            File dir = new File(path);
            long size = 0;
            if (dir.isDirectory()) {
                for (int i = 0; i < dir.list().length; i++) {
                    if (dir.listFiles()[i].isFile()) {
                        if (dir.listFiles()[i].getName().endsWith(".pdf") || dir.listFiles()[i].getName().endsWith(".txt") || dir.listFiles()[i].getName().endsWith(".med") || dir.listFiles()[i].getName().endsWith(".doc")) {
                            size = (size + dir.listFiles()[i].length());
                        }
                    }
                }
                data = String.valueOf(size);
            } else {
                fis = new FileInputStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                data = br.readLine();
                br.close();
                System.out.println("    Read data: " + data);
            }

            return data;
        } catch (IOException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return null;
    }

    /**
     * Test input.upload.file, nput.ref.file with data ref
     * @param path
     * @return
     */
    public Object path2ObjManyArgs(String path, Object[] args) {

        FileInputStream fis = null;
        try {
            System.out.println("path2ObjManyArgs:   Loading file from " + path);
            fis = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String data = br.readLine();
            br.close();

            for (int i = 0; i < args.length; i++) {
                data = data + args[i].getClass().getName();
            }

            System.out.println("    Read data: " + data);

            return data;
        } catch (IOException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * Test input.upload.file, nput.ref.file with data ref
     * @param path
     * @return
     */
    public Object path2ObjTooManyArgs(String path, Integer num, Object[] args) {

        FileInputStream fis = null;
        try {
            System.out.println("path2ObjManyArgs:   Loading file from " + path);
            fis = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String data = br.readLine();
            br.close();

            for (int i = 0; i < args.length; i++) {
                data = data + args[i].getClass().getName();
            }
            data = data + num;
            System.out.println("    Read data: " + data);

            return data;
        } catch (IOException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     *  Test input.upload.file, nput.ref.file with direct service call. Then get the prouced data
     * @param path
     * @return
     */
    public String path2Path(String path) {

        FileInputStream fis = null;
        String fileLoc;
        try {
            System.out.println("Loading file from " + path);
            fis = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String data = br.readLine();
            br.close();
            System.out.println("Read data: " + data);

            fileLoc = "/tmp/FILE";
            FileOutputStream fos = new FileOutputStream(fileLoc);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(data);

            bw.flush();
            bw.close();

            return fileLoc;
        } catch (IOException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public String manyArgsRetunsDirLoc(String arg1, String arg2, String dirPath) {
        arg1 = arg1 + "process";
        arg2 = arg2 + "more process";
        System.out.println("arg1: " + arg1 + " arg2: " + arg2);
        return dirPath;
    }

    public String[] manyArgsRetunsDirsLoc(String arg1, String arg2, String[] dirPaths) {
        arg1 = arg1 + "process";
        arg2 = arg2 + "more process";
        System.out.println("arg1: " + arg1 + " arg2: " + arg2);

        Vector<String> dirVec = new Vector<String>();
        File f;
        for (String dir : dirPaths) {
            f = new File(dir);

            if (f.isDirectory() && !f.getName().contains("CVS")) {
                for (String name : f.list()) {
                    if (name.endsWith("txt") || name.endsWith("pdf") || name.endsWith("doc") || name.endsWith("med")) {
                        System.out.println("FileName: " + name);
                        dirVec.add(name);
                    }

                }
            } else {
                System.err.println("Was expecting dir but got: " + dir);
            }

        }

        String[] arr = new String[dirVec.size()];
        arr = dirVec.toArray(arr);
        return arr;
    }

    public String method1(int sizeKb) {
        int send = 0;
        try {
            System.out.println("will generate : " + sizeKb + " kb ");

            ByteArrayOutputStream destStream = new ByteArrayOutputStream();

            String data;

            for (int i = 0; i < sizeKb; i++) {
                try {
                    data = randomstring(1024, 1024);
                    destStream.write(data.getBytes());

                    send = send + data.getBytes().length;
//                    if ((send % (10*1024 * 1024)) == 0) {
//                        System.out.println("Data send: " + send);
//                    }

                } catch (IOException ex) {
                    Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


            System.out.println("Returning data");

            return destStream.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String method1Stream(int sizeKb) {
        try {
            System.out.println("will generate : " + sizeKb + " kb ");

//            ByteArrayOutputStream destStream = new ByteArrayOutputStream();

            OutputStream out = DataTransportContext.getVServerOutputStream(getReturnDataURI("http", "key"));

            String data;

            int send = 0;

            for (int i = 0; i < sizeKb; i++) {
                try {
                    data = randomstring(1024, 1024);
                    out.write(data.getBytes());

//                    send = send + data.getBytes().length;
//                    if ((send % 1024 * 10) == 0) {
//                        System.out.println("Data send: " + send);
//                    }


                } catch (IOException ex) {
                    Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            out.flush();
            out.close();
            System.out.println("Returning data");

            return "done";
        } catch (Exception ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String method1GFTP(int sizeKb) {
        try {
            System.out.println("will generate : " + sizeKb + " kb ");

            String fileName = proxyWS.utils.Misc.getTmpDir() + "/tmpFile";

            OutputStream out = new FileOutputStream(fileName);

            String data;

            for (int i = 0; i < sizeKb; i++) {
                try {
                    data = randomstring(1024, 1024);
                    out.write(data.getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            out.flush();
            out.close();
            System.out.println("Returning data");


            return uploadData("file://" + fileName, "gsiftp://elab.science.uva.nl/tmp/");
        } catch (Exception ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String method2(String data) {
        String encodeData = org.apache.axis.encoding.Base64.encode(data.getBytes());

        System.out.println("Returning data size: "+encodeData.getBytes().length);
        return encodeData;
    }

    public String method2Stream(String dataRef) {
        InputStream in = null;
        try {
            in = DataTransportContext.getInputStream(dataRef, true);

            OutputStream out = DataTransportContext.getVServerOutputStream(getReturnDataURI("http", "key"));
            byte[] tmp = new byte[1024];
            int len = 0;
            String data;

            System.out.println("Processing data");
            int recived = 0;
            int send = 0;
            while ((len = in.read(tmp)) != -1) {
                recived = recived + len;
                data = org.apache.axis.encoding.Base64.encode(tmp, 0, len);
                out.write(data.getBytes());

                send = send + data.getBytes().length;

//                if ((send % 1024 * 10) == 0) {
//                    System.out.println("Data recived: " + recived);
//                    System.out.println("Data send: " + send);
//                }
            }

            out.flush();
            out.close();

            System.out.println("Returning data size: "+send);

            return "done";
        } catch (IOException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VlException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public String method2GFTP(String dataRef) {
        InputStream in = null;

        String path = downloadData(dataRef);

        try {
            in = new FileInputStream(path);
            String fileName = proxyWS.utils.Misc.getTmpDir() + "/tmpFile2";
            OutputStream out = new FileOutputStream(fileName);

            byte[] tmp = new byte[1024];
            int len = 0;
            String data;
            while ((len = in.read(tmp)) != -1) {
                data = org.apache.axis.encoding.Base64.encode(tmp, 0, len);
                out.write(data.getBytes());
            }


            out.flush();
            out.close();


            return uploadData("file://" + fileName, "gsiftp://elab.science.uva.nl/tmp/");
        } catch (IOException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public static String randomstring(int lo, int hi) {
        int n = rand(lo, hi);
        byte b[] = new byte[n];
        for (int i = 0; i < n; i++) {
            b[i] = (byte) rand('a', 'z');
        }
        return new String(b, 0, b.length);
    }

    public static int rand(int lo, int hi) {
        Random rn = new Random();

        int n = hi - lo + 1;
        int i = rn.nextInt() % n;
        if (i < 0) {
            i = -i;
        }
        return lo + i;
    }

    public String getReturnDataURI(String scheme, String key) {
        return DataTransportContext.getReturnDataURI(scheme, key);
    }

    private String downloadData(String uri) {


        String path;

        VRL fileVRL = null;



        try {
            fileVRL = new VRL(uri);

            String scheme = fileVRL.getScheme();
            if (scheme == null) {
                return uri;
            }
            if (scheme.equals("file") || scheme.equals("")) {
                return uri;
            }
            VFile node = (VFile) proxyWS.transport.DataTransportContext.getVnode(fileVRL);


            VRL vOutputDir = new VRL("file://" + getOutputDir());
            VFile theFile;
            //if not local bring here
            if (!(node instanceof nl.uva.vlet.vfs.localfs.LFile)) {

                theFile = node.copyToDir(vOutputDir);


                path = theFile.getPath();
            } else {
                path = node.getPath();
            }
            return path;
        } catch (VlException ex) {
            Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return uri;
    }

    public String uploadData(String sourceUri, String destUri) {
        LFile localFile = null;
        VRL remoteVRL = null;
        VFile remoteFile = null;
        if (sourceUri != null || !sourceUri.equals("")) {
            try {
                localFile = (LFile) proxyWS.transport.DataTransportContext.getVnode(sourceUri);
                remoteVRL = new VRL(destUri);
                remoteFile = localFile.copyToDir(remoteVRL);
            } catch (VlException ex) {
                Logger.getLogger(SimpleService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return remoteFile.getVRL().toString();
    }

    private String getOutputDir() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
