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
package proxyWS.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author S. koulouzis
 */
public class Misc {

    private static boolean debug;

    public static void main(String args[]) {

//        for (int i = 0; i < args.length; i++) {
//            System.out.println("args[" + i + "] " + args[i]);
//        }
        if (args[0].equals("obj2XML")) {
            String fileArg = "";
            if (args.length >= 4) {
                fileArg = args[3];
            }
            String res = obj2XML(args[1], args[2], fileArg);
            System.out.println(res);
        }
        if (args[0].equals("ddc")) {
            String[] filePaths = args[1].split("#");
            String[] nodes = args[2].split("#");
            domainDecomposition(filePaths, nodes);
        }
        
    }

    public static File bin2File(byte[] data, String name) {
        File f = null;
        try {
//            System.out.println("Saving at: " + name);
            f = new File(name);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.flush();
            fos.close();

        } catch (IOException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return f;
    }

    public static byte[] File2Bin(String filepath) {
        return File2Bin(new File(filepath));
    }

    public static byte[] File2Bin(File file) {
        FileInputStream fins = null;
        ByteArrayOutputStream baos = null;
        try {
            fins = new FileInputStream(file);
            long lSize = file.length();
            int size = (int) (lSize / 1);

            byte[] tmp = new byte[size];
            baos = new ByteArrayOutputStream(size);
            int len = 0;
            while ((len = fins.read(tmp)) != -1) {
                baos.write(tmp, 0, len);
            }
        } catch (IOException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fins.close();
            } catch (IOException ex) {
                Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return baos.toByteArray();
    }

    /** Read a InputStreamReader, UTF-8 safe
     *
     * @param isr InputStreamReader to read
     * @return contents
     */
    public static String loadTextFile(File file) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        InputStreamReader is = null;
        is = new InputStreamReader(new FileInputStream(file), "UTF-8");
        return loadTextStream(is);
    }

    /** Read a InputStreamReader, UTF-8 safe
     *
     * @param isr InputStreamReader to read
     * @return contents
     */
    public static String loadTextStream(InputStreamReader isr) throws IOException {

        BufferedReader bs = null;
        StringBuffer sb = new StringBuffer(1024);

        bs = new BufferedReader(isr);
        String s = null;

        while ((s = bs.readLine()) != null) {
            sb.append(new String(s.getBytes(), "UTF-8"));
        }
        bs.close();

        return sb.toString();
    }

    public static String mkdir(String path) {
        File f = new File(getTmpDir() + path);
        f.mkdirs();

        return f.getAbsolutePath();
    }

    public static String getTmpDir() {
        String tmp = System.getenv("java.io.tmpdir");

        if (tmp == null || tmp.equalsIgnoreCase("")) {
            tmp = System.getProperty("java.io.tmpdir");
            if (tmp == null || tmp.equalsIgnoreCase("")) {
                tmp = ".";
            }
        }
//        System.out.println("TMP dir is"+tmp);
        return tmp + File.separator;
    }

    public static boolean createFile(String text, String filePath) {

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(filePath, true));
            bw.write(text);
            bw.newLine();
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe2) {
                    ioe2.printStackTrace();
                }
            }
        }

        return new File(filePath).exists();

    }

    /**
     * Costly 
     * @return
     */
    public static String getIPOrHostName() {
        InetAddress addr;
        byte[] ipAddr;
        String ipAddrStr = "";
        String addrStr = null;
        try {
            addr = InetAddress.getLocalHost();

            // Get IP Address
            ipAddr = addr.getAddress();


            for (int i = 0; i < ipAddr.length; i++) {
                if (i > 0) {
                    ipAddrStr += ".";
                }
                ipAddrStr += ipAddr[i] & 0xFF;
            }

            addr = InetAddress.getByName(ipAddrStr);
            addrStr = addr.getHostName();


        } catch (UnknownHostException e) {
            e.printStackTrace();

        }
        return ipAddrStr;
    }

    public static String translateProtocol(int val) {
        String str = null;
        switch (val) {
            case Constants.TCP:
                str = "tcp";
                break;
            case Constants.TCPS:
                str = "tcps";
                break;
            case Constants.UDP:
                str = "udp";
                break;
            case Constants.RTSP:
                str = "rtsp";
                break;
            case Constants.STYX:
                str = "styx";
                break;
            case Constants.GridFTP:
                str = "gftp";
                break;
            case Constants.HTTP:
                str = "http";
                break;
            case Constants.HTTPS:
                str = "https";
                break;
            default:
                str = "http";
                break;
        }
        return str;
    }

    /** Get the extension of a file.
     *
     * @param f   File to get the extension from
     */
    public static String getFileExtention(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static Vector<String> readArgs(File file) {
        return readArgs(file.getAbsolutePath());
    }

    public static Vector<String> readArgs(String path) {

        FileInputStream fstream = null;
        Vector<String> args = new Vector<String>();
        try {
            fstream = new FileInputStream(path);
            DataInputStream in = new DataInputStream(fstream);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;

            while ((strLine = br.readLine()) != null) {
                args.add(strLine);
            }
        } catch (IOException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fstream.close();
            } catch (IOException ex) {
                Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return args;
    }

    public static String obj2XML(String primitiveType, String value, String outPutPath) {
        FileOutputStream fos = null;
        File out = null;
        boolean noFileOut = true;

        try {
            Object obj = null;

            if (outPutPath == null || outPutPath.equals("")) {
                noFileOut = true;
            } else {
                noFileOut = false;
                out = new File(outPutPath);
                fos = new FileOutputStream(out);
            }

            XStream xstream = new XStream(new DomDriver());

            if (primitiveType.equals("string")) {
                obj = value;
            }

            if (primitiveType.equals("stringArr")) {
                String[] strArr = value.split(",");
                obj = strArr;
            }
            if (primitiveType.equals("int")) {
                obj = Integer.valueOf(value);
            }
            if (primitiveType.equals("intArr")) {
                String[] strArr = value.split(",");
                obj = new Integer[strArr.length];
                Integer[] tmp = null;
                for (int i = 0; i < strArr.length; i++) {
                    tmp[i] = Integer.valueOf(strArr[i]);
                }
                obj = tmp;
            }
            if (primitiveType.equals("long")) {
                obj = Long.valueOf(value);
            }

            if (primitiveType.equals("longArr")) {
                String[] strArr = value.split(",");
                obj = new Long[strArr.length];
                Long[] tmp = null;
                for (int i = 0; i < strArr.length; i++) {
                    tmp[i] = Long.valueOf(strArr[i]);
                }
                obj = tmp;
            }

            if (fos == null) {
                return xstream.toXML(obj);
            } else {
                xstream.toXML(obj, fos);
                return out.getAbsolutePath();
            }


        } catch (FileNotFoundException ex) {
            Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Misc.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        return null;
    }

    public static Map domainDecomposition(String[] fileLocations, String[] nodes) {
        HashMap<String, Vector<String>> asign = new HashMap<String, Vector<String>>();
        Vector<String> files;

        int as = (fileLocations.length / nodes.length);
        int left = fileLocations.length - (as * nodes.length);

        Debug("     LEN: " + fileLocations.length + " avilNodes: " + nodes.length + " perNode: " + as + " left: " + left);


        int c = 0;
        for (int i = 0; i < (nodes.length); i++) {
            files = new Vector<String>();
            for (int j = 0; j < as; j++) {
                files.add(fileLocations[c]);
                c++;
            }
            asign.put(nodes[i], files);
        }

        int k = c;
        for (int i = 0; i < (left); i++) {
            files = asign.get(nodes[i]);
            files.add(fileLocations[k]);
            asign.put(nodes[i], files);
            k++;
        }

        if (debug) {
            for (int i = 0; i < (nodes.length); i++) {
                Debug("     ------------");
                files = asign.get(nodes[i]);
                for (int j = 0; j < files.size(); j++) {
                    Debug("     Node: " + nodes[i] + "File[" + j + "]: " + files.get(j));
                }
            }
        }

        return asign;
    }
    
    
    private static void Debug(String string) {
        System.out.println("Utils: " + string);
    }


}
