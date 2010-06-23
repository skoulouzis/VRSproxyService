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
package proxyWS.impl;

import java.util.concurrent.TimeoutException;
import proxyWS.clients.VRSProxyClient;
import proxyWS.transport.DataTransportContext;
import proxyWS.utils.Constants;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import proxyWS.utils.ContextUtils;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrs.VNode;
import org.apache.axis.MessageContext;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.IntHolder;
import org.apache.axis.providers.java.JavaProvider;
import proxyWS.utils.Misc;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VDeletable;
import org.globus.common.CoGProperties;

/**
 * This is a proxy service that enables calling WS located in the same container, 
 * and retuning an http data reference of the produced result.
 * @author S. Koulouzis
 */
public class ProxyService {

//    private MessageContext msgCtx;
//    private HttpServlet srv;
//    private ServletContext servContext;
//    private static Map methodCache;
//    private static Map returnValues;
//    private static Map inputArgsMap;
//    private static DataTransportContext dataTransCntx;
    public ProxyService() {


        // create MessageContext
//        msgCtx = MessageContext.getCurrentContext();
//        srv = (HttpServlet) msgCtx.getProperty(HTTPConstants.MC_HTTP_SERVLET);
//        servContext = srv.getServletContext();
//
//        methodCache = (Map) servContext.getAttribute(proxyWS.utils.Constants.CAHE_METHOD);
//
//        if (methodCache == null) {
//            methodCache = new HashMap();
//            servContext.setAttribute(proxyWS.utils.Constants.CAHE_METHOD, methodCache);
//        }
//
//        returnValues = (Map) servContext.getAttribute(proxyWS.utils.Constants.RETURN_VAL);
//        if (returnValues == null) {
//            returnValues = new HashMap();
//            servContext.setAttribute(proxyWS.utils.Constants.RETURN_VAL, returnValues);
//        }
//
//        inputArgsMap = (Map) servContext.getAttribute(proxyWS.utils.Constants.IN_ARGS);
//        if (inputArgsMap == null) {
//            inputArgsMap = new HashMap();
//            servContext.setAttribute(proxyWS.utils.Constants.IN_ARGS, inputArgsMap);
//        }
        DataTransportContext.init(true, null);
    }

//    public static DataTransportContext getDataTransCntx() {
//        if (dataTransCntx == null) {
//            dataTransCntx = new DataTransportContext(true, null);
//        }
//        return dataTransCntx;
//    }
    /**
     * Create dir inside tmp dir
     * @param path
     * @return the ablolut path of the crarted dir, or null if the path could not
     * be crated  
     */
    public String createSandbox(String path) {

        String sandBoxPath = Misc.getTmpDir() + File.separator + path;
//        debug(" location" + sandBoxPath);
        File sandBox = new File(sandBoxPath);
        sandBox.mkdir();
        if (sandBox.exists() && sandBox.canRead() && sandBox.canWrite()) {
//            System.out.println("Sandbox: " + sandBox.getAbsolutePath());
            return sandBox.getAbsolutePath();
        } else {
            return null;
        }
    }

    public boolean setProxyCert(String proxyString) {

        FileOutputStream out = null;
        CoGProperties cog = CoGProperties.getDefault();
        String path = cog.getProxyFile();

        return proxyWS.utils.Misc.createFile(proxyString, path);
    }

    public boolean deleteTemp() {

        String[] contents = list("file://" + proxyWS.utils.Misc.getTmpDir());
        boolean res = false;
        for (String cont : contents) {
            res = delete(cont);
        }

        return res;
    }

    public boolean delete(String pathURI) {
        try {
//            VDeletable node = (VDeletable) getDataTransCntx().getVnode(pathURI);
            VDeletable node = (VDeletable) DataTransportContext.getVnode(pathURI);
            if (node instanceof VDir) {
                if (!((VDir) node).exists()) {
                    return true;
                }

                return ((VDir) node).delete(true);
            }

            if (node instanceof VFile) {
                if (!((VFile) node).exists()) {
                    return true;
                }
            }

            return node.delete();
        } catch (VlException ex) {
            Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Returns a URL(I) where files may be uploaded so they can be used be WS located
     * in the same container.
     *  This method is used with <code>uploadFiles</code> in <code>clients.VRSProxyClient</code>
     * 
     * @param sandboxPath
     * @return the URL(I) location.
     */
    public String getFileUploadURI(String sandboxPath) {
//        getDataTransCntx().clearSandbox();
//        servContext.removeAttribute(proxyWS.utils.Constants.SANDBOX);
        DataTransportContext.clearSandbox();

        if (sandboxPath != null || !sandboxPath.equals("")) {
            String sandbox = createSandbox(sandboxPath);
//            servContext.setAttribute(proxyWS.utils.Constants.SANDBOX, sandbox);
//            getDataTransCntx().setSandbox(sandbox);
            DataTransportContext.setSandbox(sandbox);

//            debug("Seting sandbox: " + sandbox);
        }
        Random r = new Random(System.currentTimeMillis());

//        String strVRL = getDataTransCntx().getDefultFileUploadURI().toString();
//        String strVRL = getDataTransCntx().getDefultTransportURI().toString();
        String strVRL = DataTransportContext.getDefultTransportURI().toString();

//        debug("File uploading: " + strVRL);

        return strVRL + "?" + Constants.UPLOAD_FILES + "&" + r.nextInt();
    }

    /**
     * Returns the URL(I) where data may be uploaed in memory
     * This method is used with <code>uploadData</code> in <code>clients.VRSProxyClient</code>
     * @param conf the size of data to be uploaded 
     * @return the URL(I) location.
     */
    public String getUploadURI(int conf) {
        //use VRS to find appropriate client 
//        servContext.removeAttribute(proxyWS.utils.Constants.CONF);
//        getDataTransCntx().clearConf();
        DataTransportContext.clearConf();
//        servContext.setAttribute(proxyWS.utils.Constants.CONF, conf);
//        getDataTransCntx().setConf(conf);
        DataTransportContext.setConf(conf);

        Random r = new Random(System.currentTimeMillis());
        String key = Constants.IN_UPLOAD_MEM + "_" + r.nextInt();

//        String strVRL = getDataTransCntx().getDefultUploadURI().toString();
        String strVRL = DataTransportContext.getDefultTransportURI().toString();

        return strVRL + "?" + key;
    }

    /**
     * Returns the URL(I) of a file located anywhere.
     * This method is used with <code>getFileFromHttp</code> in <code>clients.VRSProxyClient</code>
     * @param path the ablolute path of the file 
     * @return the URL(I) of the file, or null if the file was not found.
     */
    public String getFileURI(String path) {
        try {

            VRL vrlPath = new VRL(path);

//            VFile file = (VFile) getDataTransCntx().getVnode(vrlPath);
            VFile file = (VFile) DataTransportContext.getVnode(vrlPath);

            if (file.exists()) {
//                String strVRL = getDataTransCntx().getDefultFileUploadURI().toString();
                String strVRL = DataTransportContext.getDefultFileUploadURI().toString();

                return strVRL + "?" + vrlPath.toString();
            }



        } catch (VlException ex) {
            Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Returns a value returned be a previous call. 
     * This is used when calling <code>asyncCallServiceReturnObject</code>
     * @param key the key of the value
     * @return the value 
     */
    public Object getReturnedValue(int key) {
//        returnValues = (Map) servContext.getAttribute(proxyWS.utils.Constants.RETURN_VAL);
//        debug("Looking for value with key: " + key);
//        Set set = DataTransportContext.getkeySet();
//
//        Integer[] keySet = new Integer[set.size()];
//        keySet = (Integer[]) set.toArray(keySet);
//
//
//        debug("kay set : ");
//        for (int i = 0; i < keySet.length; i++) {
//            debug(" " + keySet[i]);
//        }

        return DataTransportContext.getRetunValue(key);//getDataTransCntx().getRetunValue(key);//returnValues.get(key);
    }

    /**
     * Same as <code>getReturnedValue</code>, but instead an http reference is 
     * returned 
     * @param key
     * @return the http reference
     */
    public String getReturnedValueRef(int key) {
        Object returnValue = getReturnedValue(key);

        if (returnValue == null) {
            return null;
        }

//        String strVRL = getDataTransCntx().getDefultTransportURI().toString();
        String strVRL = DataTransportContext.getDefultTransportURI().toString();

        return strVRL + "?" + key;
    }

    public String[] list(String path) {
        String[] strList = null;
        try {

//        debug("Listing: "+path);
//            VNode node = getDataTransCntx().getVnode(path);
            VNode node = DataTransportContext.getVnode(path);

            VFSNode[] list;
            if (node.exists()) {

                list = ((VDir) node).list();
                strList = new String[list.length];

                for (int i = 0; i < list.length; i++) {
                    strList[i] = list[i].getVRL().toString();
//                    debug("Returning: "+strList[i]);
                }

            }

        } catch (VlException ex) {
            if (!ex.getMessage().contains("Resource not found")) {
                Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return strList;
    }

    /**
     * Asynchronous call of a target WS. args may be the actual input arguments or
     * referances. e.g. for a using an uploaded file:
     *      String targetURI = proxyService.getFileUploadURI("testUploadSandbox3");
    File[] files = new File[1];
    files[0] = new File("test/testFile1");
    clients.VRSProxyClient pClient = new clients.VRSProxyClient();
    pClient.uploadFiles(files, new URI(targetURI));
    URL url = new URL(targetURI);
    URI uri = new URI("wsdt", utils.Constants.IN_UPLOAD_FILES, "/dummypath", url.getQuery(), "");
    Object[] args = {uri.toString()};
     * 
     * 
     * @param serviceName the service name 
     * @param methodName the method name 
     * @param args the input arguments 
     * @return a key that can be used to get the result be using <code>getReturnedValue</code> 
     * or <code>getReturnedValueRef</code>
     */
    public int asyncCallServiceReturnObject(String serviceName, String methodName, Object[] args) {

        debug("Calling: " + serviceName + "." + methodName);
        java.util.Random r = new java.util.Random(System.currentTimeMillis());

        int key = r.nextInt();
        try {
//            Object serObj = getServiceObject(serviceName, msgCtx);
//            Object serObj = getServiceObject(serviceName, getDataTransCntx().getMessageContext());

            Object serObj = getServiceObject(serviceName, DataTransportContext.getMessageContext());


            Method method = getMethod(serObj, methodName);

//            Class<?>[] paramTypes = method.getParameterTypes();
            Object[] inputArgs = resolveArgs(args, method.getParameterTypes());

//            for (int i = 0; i < args.length; i++) {
//                debug("args[" + i + "]: Type" + args[i].getClass().getName() + " : " + args[i]);
//            }


//            MethodExe m = new MethodExe(serObj, method, inputArgs, key, servContext);
//            MethodExe m = new MethodExe(serObj, method, inputArgs, key, getDataTransCntx().getServContext());
            MethodExe m = new MethodExe(serObj, method, inputArgs, key, DataTransportContext.getServContext());
            Thread t = new Thread(m);
            t.start();

            //clean up the all input args
//            cleanInputArgs();
//            getDataTransCntx().cleanInputArgs();
            DataTransportContext.cleanInputArgs();

            return key;
        } catch (Exception ex) {
            Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -999;
    }

    /**
     * Calls a target service and retuns the result of that service.
     * @param serviceName the service name 
     * @param methodName the method name 
     * @param args the input arguments 
     * @return the result 
     */
    public Object callServiceReturnObject(String serviceName, String methodName, Object[] args) {
        debug("Calling: " + serviceName + "." + methodName);
        try {
//            Object serObj = getServiceObject(serviceName, msgCtx);
//            Object serObj = getServiceObject(serviceName, getDataTransCntx().getMessageContext());
            Object serObj = getServiceObject(serviceName, DataTransportContext.getMessageContext());
            Method method = getMethod(serObj, methodName);

//            Class<?>[] paramTypes = method.getParameterTypes();
            Object[] inputArgs = resolveArgs(args, method.getParameterTypes());

            Object returnValue = method.invoke(serObj, inputArgs);

            //clean up the all input args
//            cleanInputArgs();
//            getDataTransCntx().cleanInputArgs();
            DataTransportContext.cleanInputArgs();

            return returnValue;
        } catch (Exception ex) {
            Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Call a method of a service locally without doing a SOAP call
     * @param serviceName
     * @param methodName
     * @param args
     * @return a url ref to the data 
     */
    public String callService(String serviceName, String methodName, Object[] args) {
        Object returnValue = null;
        debug("Calling: " + serviceName + "." + methodName);
        try {
//            returnValue = callLocalService(serviceName, methodName, resolveArgs(args));

//            Object serObj = getServiceObject(serviceName, msgCtx);
//            Object serObj = getServiceObject(serviceName, getDataTransCntx().getMessageContext());
            Object serObj = getServiceObject(serviceName, DataTransportContext.getMessageContext());


            Method method = getMethod(serObj, methodName);

//            Class<?>[] paramTypes = method.getParameterTypes();

            Object[] inputArgs = resolveArgs(args, method.getParameterTypes());

            returnValue = method.invoke(serObj, inputArgs);

//            clean up the all input args
//            cleanInputArgs();
//            getDataTransCntx().cleanInputArgs();
            DataTransportContext.cleanInputArgs();

            //got the output. Where should it go??
//                //file://
//                //http://
//                //gsiftp:// gftp://, etc
//                //srm://
//                //srb://Service
//                //ftp://
//
//            returnValues = (Map) servContext.getAttribute(proxyWS.utils.Constants.RETURN_VAL);
//            returnValues.put(returnValue.hashCode(), returnValue);
//            servContext.setAttribute(proxyWS.utils.Constants.RETURN_VAL, returnValues);
//            getDataTransCntx().addReturnValue(returnValue.hashCode(), returnValue);
            DataTransportContext.addReturnValue(returnValue.hashCode(), returnValue);


        } catch (java.lang.IllegalArgumentException ex) {
            if (returnValue == null) {
                Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return DataTransportContext.getDefultTransportURI() + "?" + returnValue.hashCode();//getDataTransCntx().getDefultTransportURI() + "?" + returnValue.hashCode();
    }

    /**
     * Same as <cdoe>callServiceM</code>, but asynchronous 
     * @param serviceName
     * @param methodName
     * @param args
     * @return a url ref to the data 
     */
    public String asyncCallService(String serviceName, String methodName, Object[] args) {
        debug("Calling: " + serviceName + "." + methodName);
        java.util.Random r = new java.util.Random(System.currentTimeMillis());
        int key = r.nextInt();
        try {

//            Object serObj = getServiceObject(serviceName, msgCtx);
//            Object serObj = getServiceObject(serviceName, getDataTransCntx().getMessageContext());
            Object serObj = getServiceObject(serviceName, DataTransportContext.getMessageContext());

            Method method = getMethod(serObj, methodName);

            Object[] inputArgs = resolveArgs(args, method.getParameterTypes());

//            MethodExe m = new MethodExe(serObj, method, inputArgs, key, servContext);
            MethodExe m = new MethodExe(serObj, method, inputArgs, key, DataTransportContext.getServContext());
            Thread t = new Thread(m);
            t.start();

            //clean up the all input args
//            cleanInputArgs();
            DataTransportContext.cleanInputArgs();


        } catch (Exception ex) {
            Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return DataTransportContext.getDefultTransportURI() + "?" + key;
    }

    private Object callLocalService(String serviceName, String methodName, Object[] args) throws Exception {

//        Object serviceObject = getServiceObject(serviceName, msgCtx);
        Object serviceObject = getServiceObject(serviceName, DataTransportContext.getMessageContext());

        System.out.println("serviceObject :" + serviceObject.getClass().getName());

        Method method = getMethod(serviceObject, methodName);

        return method.invoke(serviceObject, args);
    }

//    private void cleanInputArgs() {
//        servContext.removeAttribute(proxyWS.utils.Constants.IN_REF_FILE);
//        servContext.removeAttribute(proxyWS.utils.Constants.IN_REF_MEM);
//        servContext.removeAttribute(proxyWS.utils.Constants.IN_UPLOAD_FILES);
//        servContext.removeAttribute(proxyWS.utils.Constants.IN_UPLOAD_DIR);
//        servContext.removeAttribute(proxyWS.utils.Constants.IN_UPLOAD_MEM);
//    }
    private void debug(String msg) {
        System.err.println(this.getClass().getName() + ": " + msg);
    }

    private synchronized Object getServiceObject(String serviceName, MessageContext msgCtx) throws Exception {

        Object serviceObject = null;

        String serviceClassName =
                (String) ContextUtils.getServiceProperty(
                msgCtx,
                serviceName,
                "className");

        String providerClassName =
                (String) ContextUtils.getServiceProperty(
                msgCtx,
                serviceName,
                "handlerClass");

//        SOAPService service = (SOAPService) msgCtx.getService();

        // create a Provider object
        providerClassName = "org.apache.axis.providers.java.RPCProvider";
        Class providerClass = Class.forName(providerClassName);

        Constructor co = providerClass.getConstructor(null);
        JavaProvider provider = (JavaProvider) co.newInstance(null);

        // get the service object
        serviceObject =
                provider.getServiceObject(
                msgCtx,
                msgCtx.getService(),
                serviceClassName,
                new IntHolder());
        return serviceObject;
    }

    private synchronized Method getMethod(Object serviceObj, String methodName) throws Exception {
        Method method = null;
        String name = serviceObj.getClass().getName() + "." + methodName;
//        if (methodCache.containsKey(name)) {
        if (DataTransportContext.isMethodCached(name)) {
//            method = (Method) methodCache.get(name);
            method = DataTransportContext.getMethod(name);
        } else {
            Class _class = serviceObj.getClass();
            Method[] _func_to_call = _class.getDeclaredMethods();

            for (int i = 0; i < _func_to_call.length; i++) {
                if (_func_to_call[i].getName().equals(methodName)) {
                    method = _func_to_call[i];
                    break;
                }
            }
            DataTransportContext.addMethodCache(name, method);
//            methodCache.put(name, method);
        }

        return method;
    }

    /**
     * Resolves the input arguments for target service invocation. Args might be:
     * 1) Uploaded in memory (context) <code>services.utils.Constants.IN_UPLOAD_MEM</code>
     * 2) Uloaded as files in tmp dir <code>services.utils.Constants.IN_UPLOAD_PATH</code>
     * 3) Downloaded in memory from 3rd location  <code>services.utils.Constants.IN_REF_MEM</code>
     * 4) Downloaded in file from 3rd location  <code>services.utils.Constants.IN_REF_FILE</code>
     * 5) The actual input args 
     * 
     * @param args
     * @return The arguments 
     * @throws javax.xml.rpc.ServiceException
     */
    private Object[] resolveArgs(Object[] args, Class<?>[] types) throws ServiceException {
        URI wsdt;
        String wsdtStr = null;
        Object[] tmpArg = null;
        VNode node;

        for (int i = 0; i < args.length; i++) {

            if (args[i] instanceof String) {
                wsdtStr = (String) args[i];
                if (wsdtStr.startsWith("wsdt")) {
                    try {
                        wsdt = new URI(wsdtStr);

                        if (wsdt.getScheme().equals("wsdt")) {

//                            inputArgsMap = (Map) servContext.getAttribute(proxyWS.utils.Constants.IN_ARGS);
//                            inputArgsMap = getDataTransCntx().getInputArgs(wsdtStr);
                            if (wsdt.getHost().equals(proxyWS.utils.Constants.IN_REF_FILE)) {

                                URI refFile = new URI(wsdt.getQuery());
                                String scheme = refFile.getScheme();

                                debug("URI: " + refFile);

                                if (scheme.equals("http")) {
                                    proxyWS.clients.VRSProxyClient client = new VRSProxyClient();

                                    File file = client.getFile(wsdt.getQuery(), null);
                                    args[i] = file.getAbsolutePath();
                                } else {
                                    VFile remoteFile = (VFile) DataTransportContext.getVnode(wsdt.getQuery());
                                    VFile localFile = remoteFile.copyToDir(new VRL("file://" + Misc.getTmpDir()));
                                    args[i] = localFile.getPath();
                                }


                            } else if (wsdt.getHost().equals(proxyWS.utils.Constants.IN_REF_DIR)) {
                                proxyWS.clients.VRSProxyClient client = new VRSProxyClient();
                                String dir = createSandbox(wsdt.getPath());
//                                System.out.println("Will save at: " + dir);

                                String[] locations = wsdt.getQuery().split("http");
                                String loc;
                                for (int j = 1; j < locations.length; j++) {
                                    loc = "http" + locations[j];
//                                    System.out.println("LOC: " + loc);
                                    URL url = new URL(loc);
                                    String path[] = url.getFile().split("/");
                                    String fileName = path[path.length - 1];
//                                    System.out.println("File name: "+fileName);
                                    File file = client.getFile(url, dir + "/" + fileName);
                                }

                                args[i] = dir;
                            } else if (wsdt.getHost().equals(proxyWS.utils.Constants.IN_REF_MEM)) {
                                try {

                                    proxyWS.clients.VRSProxyClient client = new VRSProxyClient();
                                    URL url = new URL(wsdt.getQuery());
                                    args[i] = client.getData(url.toURI(), true);

                                } catch (TimeoutException ex) {
                                    
                                    System.err.println("Timed out while tring to get data from: "+wsdt.getQuery());
                                    
                                    Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (MalformedURLException ex) {
                                    Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else if (wsdt.getHost().equals(proxyWS.utils.Constants.IN_UPLOAD_FILES)) {
//                                debug("Got argument:  "+proxyWS.utils.Constants.IN_UPLOAD_FILES);
//                                tmpArg = (Object[]) inputArgsMap.get(wsdt.getQuery() + proxyWS.utils.Constants.IN_UPLOAD_FILES);


//                                Vector<String> paths = (Vector<String>) DataTransportContext.getInputArgs(wsdt.getQuery() + proxyWS.utils.Constants.IN_UPLOAD_FILES);
                                Map filesMap = (Map) DataTransportContext.getInputArgs(proxyWS.utils.Constants.IN_UPLOAD_FILES);

//                                debug("Query: " + wsdt.getQuery());
//
                                String[] params = wsdt.getQuery().split("&");
                                int key = Integer.valueOf(params[1]);
                                String fileName = params[1];


                                int time = 100;
                                int inc = 0;
                                int timeout = proxyWS.utils.Constants.TIME_OUT;
                                while (filesMap == null) {
                                    debug("Files not ready sleeping for: " + (time * inc));
                                    inc++;
                                    Thread.sleep((time * inc));
                                    filesMap = (Map) DataTransportContext.getInputArgs(proxyWS.utils.Constants.IN_UPLOAD_FILES);
                                    if ((time * inc) > timeout) {
                                       System.err.println("Timed out while tring to get data from: "+wsdt.getQuery());
                                        break;
                                    }
                                }

                                debug("Files for key: " + key);
                                Vector<File> files = (Vector<File>) filesMap.get(key);

                                inc = 0;
                                while (files == null) {
                                    debug("Files not ready sleeping for: " + (time * inc));
                                    inc++;
                                    Thread.sleep((time * inc));
                                    filesMap = (Map) DataTransportContext.getInputArgs(proxyWS.utils.Constants.IN_UPLOAD_FILES);
                                    files = (Vector<File>) filesMap.get(key);

                                    if ((time * inc) > timeout) {
                                        debug("TIME OUT!@@!!!");
                                        System.err.println("Timed out while tring to get data from: "+wsdt.getQuery());
                                        break;
                                    }
                                }


                                debug("Files for key: " + key);
                                for (int j = 0; j < files.size(); j++) {
                                    debug("\t " + files.get(j).getPath());
                                }

                                String[] strPath = new String[files.size()];
                                for (int j = 0; j < strPath.length; j++) {
                                    strPath[j] = files.get(j).getPath();
                                }
//
//
//
//                                for (int j = 0; j < strPath.length; j++) {
//                                    debug("Files[" + i + "]: " + strPath[j]);
//                                }

                                tmpArg = (Object[]) strPath;
//                                tmpArg = (Object[]) getDataTransCntx().getInputArgs(wsdt.getQuery() + proxyWS.utils.Constants.IN_UPLOAD_FILES);
//                                debug("types[ "+i+"]: is array??"+types[i].isArray());

                                if (types[i].isArray()) {
                                    args[i] = tmpArg;
                                    for (int j = 0; j < tmpArg.length; j++) {
                                        debug("Files[" + i + "]: " + tmpArg[j]);
                                    }
                                } else {
                                    args[i] = tmpArg[0];
                                }

                            } else if (wsdt.getHost().equals(proxyWS.utils.Constants.IN_UPLOAD_DIR)) {
//                                args[i] = inputArgsMap.get(wsdt.getQuery() + proxyWS.utils.Constants.IN_UPLOAD_DIR);
//                                args[i] = getDataTransCntx().getInputArgs(wsdt.getQuery() + proxyWS.utils.Constants.IN_UPLOAD_DIR);

                                args[i] = DataTransportContext.getInputArgs(proxyWS.utils.Constants.IN_UPLOAD_DIR);

                            } else {

                                Object tmpObj;
//                                tmpObj = inputArgsMap.get(wsdt.getQuery());
                                tmpObj = DataTransportContext.getInputArgs(wsdt.getQuery());

                                if (!types[i].isArray()) {
                                    try {
                                        tmpArg = (Object[]) tmpObj;
                                        args[i] = tmpArg[0];
                                    } catch (java.lang.ClassCastException ex) {
                                        args[i] = tmpObj;
                                    }
                                } else {
                                    args[i] = tmpObj;
                                }

                            }

//                            System.out.println(" ARG class" + types[i].getName() + " types[" + i + "].isArray()" + types[i].isArray() + " Const: " + wsdt.getHost() + " input arg class:" + args[i].getClass().getName());

                        }

                    } catch (Exception ex) {
                        Logger.getLogger(ProxyService.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } else {
                //is there more???
                try {
                    tmpArg = (Object[]) args[i];
                    args[i] = resolveArgs(tmpArg, types);

                } catch (java.lang.ClassCastException ex) {
                }
            }
        }

        return args;
    }

    private String getLocalAddr() {
//        HttpServletRequest req = (HttpServletRequest) msgCtx.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
//       NetworkUtils.getLocalHostname() 

        return DataTransportContext.getHost() + ":8080";//req.getLocalAddr() + ":" + req.getLocalPort();
    }
}

class MethodExe implements Runnable {

    private Object serviceObject;
    private Method method;
    private Object[] args;
    private Object returnVal;
    private ServletContext servContext;
    private int hash;
    private Map returnValues;

    public MethodExe(Object serviceObject, Method method, Object[] args) {
        this.serviceObject = serviceObject;
        this.method = method;
        this.args = args;
    }

    public MethodExe(Object serObj, Method method, Object[] inputArgs, int nextInt, ServletContext servContext) {
        this(serObj, method, inputArgs);
        this.servContext = servContext;
        this.hash = nextInt;
    }

    public void run() {
        try {
            returnVal = method.invoke(serviceObject, args);

            returnValues = (Map) servContext.getAttribute(proxyWS.utils.Constants.RETURN_VAL);
            returnValues.put(hash, returnVal);
            servContext.setAttribute(proxyWS.utils.Constants.RETURN_VAL, returnValues);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MethodExe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MethodExe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MethodExe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object getReturnVal() {
        return returnVal;
    }

    private void debug(String msg) {
        System.err.println(this.getClass().getName() + ": " + msg);
    }
}
    
    
    
