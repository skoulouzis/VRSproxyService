/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyWS.clients;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axis.client.async.Status;
import java.net.URL;
import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.client.async.AsyncCall;
import org.apache.axis.client.async.IAsyncResult;


import proxyWS.stubs.ProxyService.ProxyService;
import proxyWS.stubs.ProxyService.ProxyServiceServiceLocator;

/**
 *
 * @author alogo
 */
public class ProxyServiceClient {

    private static ProxyService instance;
    private static ProxyServiceServiceLocator sl;
    private static Vector<String> wsArgs;
    private static XStream xstream = new XStream(new DomDriver());
    private static int size = 0;

    public static void main(String args[]) {
        Object[] sevicesArgs;
        try {
            String endpoint = args[0];
            sl = new ProxyServiceServiceLocator();
            instance = sl.getProxyService(new URL(endpoint));

//            for (int i = 0; i < args.length; i++) {
//                System.out.println(args[i]);
//            }




            if (args[1].equals("list")) {

                String[] result = instance.list(args[2]);

                for (int i = 0; i < result.length; i++) {
                    System.out.println(result[i]);
                }

            }


            if (args[1].equals("delete")) {
                boolean result = instance.delete(args[2]);
                System.out.println(result);
            }

            if (args[1].equals("createSandbox")) {
                String result = instance.createSandbox(args[2]);

                System.out.println(result);
            }


            if (args[1].equals("createSandbox")) {

                String proxyString = proxyWS.utils.Misc.loadTextFile(new File(args[2]));
                boolean result = instance.setProxyCert(proxyString);
                System.out.println(result);
            }


            if (args[1].equals("deleteTemp")) {
                boolean result = instance.deleteTemp();
                System.out.println(result);
            }

            if (args[1].equals("getFileUploadURI")) {
                String sandboxPath = "";
                if (args.length >= 3 && args[2] != null) {
                    sandboxPath = args[2];
                }
                String result = instance.getFileUploadURI(sandboxPath);
                System.out.println(result);
            }

            if (args[1].equals("uploadFiles")) {
//                String sandboxPath = "";
//                 if (args.length >= 3 && !args[2].equals("nosb") ) {
//                      sandboxPath = args[2];
//                 }

                proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient(endpoint);

//                String targetURL = instance.getFileUploadURI(sandboxPath);


                String[] filePaths = args[2].split(",");
                File[] targetFiles = new File[filePaths.length];
                for (int i = 0; i < filePaths.length; i++) {
                    targetFiles[i] = new File(filePaths[i]);
                }
                System.out.println(pClient.uploadFiles(targetFiles, new URI(args[3])));

                System.exit(0);
            }

            if (args[1].equals("uploadFiles2")) {
                String sandboxPath = "";
                if (args.length >= 3 && !args[2].equals("nosb")) {
                    sandboxPath = args[2];
                }

                proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient(endpoint);
                String targetURL = instance.getFileUploadURI(sandboxPath);


                System.err.println("Upload URL: " + targetURL);

                String[] filePaths = args[3].split(",");
                File[] targetFiles = new File[filePaths.length];
                for (int i = 0; i < filePaths.length; i++) {
                    targetFiles[i] = new File(filePaths[i]);
//                    System.err.println("targetFiles : "+targetFiles[i].getAbsolutePath());
                }

                boolean sucess = pClient.uploadFiles(targetFiles, new URI(targetURL));

                String query = new URL(targetURL).getQuery();

                //bug xml doesnt like &
//                query = query.replaceAll("&", "AMPERSAND");

//                wsdt://input.local.files/dummypath?file.upload&234854635#


                URI data = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", query, "");
//                URI data = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_FILES, "/dummypath", "AAAAAAAAAAA", "");

                if (sucess) {
                    System.out.println(data);
                }


                System.exit(0);
            }





            if (args[1].equals("getUploadURI")) {
                String result = instance.getUploadURI(Integer.valueOf(args[2]));
                System.out.println(result);
            }

            if (args[1].equals("uploadArgs")) {

                sevicesArgs = resloveTargetWSArgs(args[2]);

                String upluadDataURI = instance.getUploadURI(size);

                proxyWS.clients.VRSProxyClient pClient = new proxyWS.clients.VRSProxyClient(endpoint);
                boolean sucess = pClient.uploadData(upluadDataURI, sevicesArgs);

                if (sucess) {
                    String query = new URI(upluadDataURI).getQuery();

                    //bug xml doesnt like &
                    query = query.replaceAll("&", "AMPERSAND");

                    URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_UPLOAD_MEM, "/dummypath", query, "");
                    System.out.println(uri);
                }
            }

            if (args[1].equals("getFileURI")) {
                String result = instance.getFileURI(args[2]);
                System.out.println(result);
            }

            if (args[1].equals("getReturnedValue")) {
                Integer key = null;
                if (args[2].startsWith("<")) {
                    key = (Integer) xstream.fromXML(args[2]);
                } else {
                    key = Integer.valueOf(args[2]);
                }
                Object result = instance.getReturnedValue(key.intValue());
                String out = null;
                if (args.length >= 6) {
                    out = args[3];
                }

                printTargetWSOutput(out, result);
            }

            if (args[1].equals("getReturnedValueRef")) {
                String result = instance.getReturnedValueRef(Integer.valueOf(args[2]));
                System.out.println(result);
            }


            if (args[1].equals("asyncCallServiceReturnObject")) {

                sevicesArgs = resloveTargetWSArgs(args[4]);


                Object result = instance.asyncCallServiceReturnObject(args[2], args[3], sevicesArgs);

                String out = null;
                if (args.length >= 6) {
                    out = args[5];
                }

                printTargetWSOutput(out, result);
            }


            if (args[1].equals("callServiceReturnObject")) {
                
                for (int i = 0; i < args.length; i++) {
                    System.err.println("ARGS: " + args[i]);
                }
                
                sevicesArgs = resloveTargetWSArgs(args[4]);

                for (int i = 0; i < sevicesArgs.length; i++) {
                    System.err.println("WS ARGS: " + sevicesArgs[i]);
                }

                Object result = instance.callServiceReturnObject(args[2], args[3], sevicesArgs);


                String out = null;
                if (args.length >= 6) {
                    out = args[5];
                }

                printTargetWSOutput(out, result);

            }


            if (args[1].equals("callService")) {


                sevicesArgs = resloveTargetWSArgs(args[4]);

                String result = instance.callService(args[2], args[3], sevicesArgs);

                System.out.println(result);
            }

            if (args[1].equals("asyncCallService")) {

                sevicesArgs = resloveTargetWSArgs(args[4]);


                String result = instance.asyncCallService(args[2], args[3], sevicesArgs);

                System.out.println(result);
            }


        } catch (Exception ex) {
            Logger.getLogger(ProxyServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void printTargetWSOutput(String arg, Object result) {

        if (arg == null || arg.equals("")) {


            if (result instanceof String) {
                System.out.println(result);
            } else {
                System.out.println(xstream.toXML(result));
            }




        } else {

            FileOutputStream fos = null;
            {
                ObjectOutputStream oos = null;
                try {
                    File out = new File(arg);
                    out.createNewFile();
                    String ext = proxyWS.utils.Misc.getFileExtention(out);
                    fos = new FileOutputStream(out);
                    if (ext == null) {
                        oos = new ObjectOutputStream(fos);
                        oos.writeObject(result);
                    } else if (ext.equalsIgnoreCase("xml")) {
                        xstream.toXML(result, fos);
                    }

                    System.out.println(out.getAbsolutePath());
                } catch (IOException ex) {
                    Logger.getLogger(ProxyServiceClient.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ProxyServiceClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (oos != null) {
                        try {
                            oos.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ProxyServiceClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

        }


    }

    private static Object[] resloveTargetWSArgs(String arg) {
        File in = new File(arg);
        Object[] sevicesArgs = null;


        if (in.exists()) {
            wsArgs = proxyWS.utils.Misc.readArgs(arg);
            sevicesArgs = new Object[wsArgs.size()];
            for (int i = 0; i < wsArgs.size(); i++) {
                FileInputStream fis = null;
                try {
                    File f = new File(wsArgs.get(i));
                    size = (int) (size + f.length());
                    fis = new FileInputStream(f);
                    String ext = proxyWS.utils.Misc.getFileExtention(f);

                    if (ext == null) {
                        sevicesArgs[i] = new ObjectInputStream(fis).readObject();
                    } else if (ext.equalsIgnoreCase("xml")) {
                        sevicesArgs[i] = xstream.fromXML(fis);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ProxyServiceClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ProxyServiceClient.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException ex) {
                            Logger.getLogger(ProxyServiceClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }
        } else {

            String[] xmlArgs = arg.split(",");
            sevicesArgs = new Object[xmlArgs.length];

            for (int i = 0; i < sevicesArgs.length; i++) {

//                System.err.println("will cast: " + xmlArgs[i]);

//                xmlArgs[i].contains("wsdt://") &&
                if ( xmlArgs[i].startsWith("<string>")) {
//                    xmlArgs[i] = xmlArgs[i].replaceFirst("<string>", "");
                    xmlArgs[i] = xmlArgs[i].replaceAll("<string>", "");

                    xmlArgs[i] = xmlArgs[i].replaceAll("</string>", "");

                    sevicesArgs[i] = xmlArgs[i];
                    
                    String str = (String) sevicesArgs[i];
                    if(str.contains("")){
                        
                    }
                } else {
                    sevicesArgs[i] = xstream.fromXML(xmlArgs[i]);
                }



//                if (sevicesArgs[i] instanceof String ) {
//                    String strArg = (String) sevicesArgs[i];
//                    if(strArg.startsWith("wsdt://")){
//                        strArg = strArg.replaceAll("AMPERSAND", "&");  
//                    }
//                    sevicesArgs[i] = strArg;
//                }
            }
        }
        size = 0;
        return sevicesArgs;
    }
    
        public static Object asncCallBack(Object[] args, String method, URL endpoint) {
        MyCallBack callBack = null;
        try {
            Service aService = new Service();
            final Call call = (Call) aService.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName(new QName(method));

            callBack = new MyCallBack(call);
            AsyncCall aCall = new AsyncCall(call, callBack);
            IAsyncResult result = aCall.invoke(args);

            synchronized (call) {
                call.wait(0);
            }

        } catch (javax.xml.rpc.ServiceException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return callBack.getResponce();
    }

}

class MyCallBack implements org.apache.axis.client.async.IAsyncCallback {

    private Call call;
    private Object responce;

    public MyCallBack(Call call) {
        this.call = call;
    }

    public void onCompletion(IAsyncResult result) {
        Status status = result.getStatus();
        if (status == Status.COMPLETED) {
            responce = result.getResponse();
            
            
            
        } else if (status == Status.EXCEPTION) {
            result.getException().printStackTrace();
        }
        synchronized (call) {
            call.notifyAll();
        }
    }

    public Object getResponce() {
        return responce;
    }
    
}