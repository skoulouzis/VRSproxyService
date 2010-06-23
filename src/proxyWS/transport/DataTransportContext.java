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

import javax.servlet.ServletContext;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrs.VResourceSystem;
import org.apache.axis.MessageContext;
import proxyWS.config.Conf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import nl.uva.vlet.Global;
import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlURISyntaxException;
import nl.uva.vlet.io.VStreamAccessable;
import nl.uva.vlet.vfs.VFileSystem;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.HTTPConstants;

/**
 *
 * @author alogo
 */
public class DataTransportContext {

    private static VRSContext vrsContext;
    private static Map streamServers;
    private static Map<String, Vector<VRL>> vfsServers;
    private static Conf conf;
    private static String localHost;
    private static int time = 100;
    private static int inc = 1;
    private static int timeOut = proxyWS.utils.Constants.TIME_OUT;
    private static ServletContext servContext;
    private static Map methodCache;
    private static Map returnValues;
    private static Map inputArgsMap;

    public DataTransportContext(boolean isInService, ServletContext servletContext) {
//            final ClassLoader[] loaders = ClassScope.getCallerClassLoaderTree();
//            final Class[] classes = ClassScope.getLoadedClasses(loaders);
//
//            for (int c = 0; c < classes.length; ++c) {
//                final Class cls = classes[c];
//                if (cls.getName().equals("VproxyWS.transport.HTTPTransport")) {
//                    ser = (VStreamServer) cls.cast(new VproxyWS.transport.HTTPTransport());
//                    
//                    streamServers.put(ser, ser.getScheme());
//                    
//                    System.out.println("ID: "+ser.hashCode());
//                    
//                    System.out.println("[" + cls.getName() + "]:");
//                    System.out.println("  loaded by [" + cls.getClassLoader().getClass().getName() + "]");
//                    System.out.println("  from [" + ClassScope.getClassLocation(cls) + "]");
//                }
//
//            }
    }

    public static synchronized void init(boolean isInService, ServletContext servletContext) {

        if (vrsContext == null) {
            GlobalConfig.setIsStrictService(isInService);
            GlobalConfig.setInitURLStreamFactory(!isInService);
//            Global.setVerbose(Global.VERBOSE_DEBUG );
            Global.init();

            vrsContext = VRSContext.getDefault();
            conf = new Conf();

            streamServers = conf.getStreamServers();
            vfsServers = conf.getVfsServers();

            if (isInService) {
                if (servletContext != null) {
                    initContext(servletContext);
                } else if (servContext != null && servletContext == null) {
                    initContext(servContext);
                } else {
                    initContext(null);
                }
            }
        }

    }

    public static synchronized VRL getDefultTransportURI() {
        try {
            VStreamServer ser = (VStreamServer) streamServers.get("http");
            return new VRL(ser.getAddress());
        } catch (VlURISyntaxException ex) {
            Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static synchronized void initContext(ServletContext cntx) {
        HttpServlet srv;
        if (cntx == null) {
            MessageContext msgCtx = AxisServer.getCurrentMessageContext();
            srv = (HttpServlet) msgCtx.getProperty(HTTPConstants.MC_HTTP_SERVLET);
            cntx = srv.getServletContext();
        }

        setServContext(cntx);

        methodCache = (Map) getServContext().getAttribute(proxyWS.utils.Constants.CAHE_METHOD);

        if (getMethodCache() == null) {
            methodCache = new HashMap();
            getServContext().setAttribute(proxyWS.utils.Constants.CAHE_METHOD, methodCache);
        }

        returnValues = (Map) getServContext().getAttribute(proxyWS.utils.Constants.RETURN_VAL);
        if (getReturnValues() == null) {
            returnValues = new HashMap();
            getServContext().setAttribute(proxyWS.utils.Constants.RETURN_VAL, returnValues);
        }

        inputArgsMap = (Map) getServContext().getAttribute(proxyWS.utils.Constants.IN_ARGS);
        if (inputArgsMap == null) {
            inputArgsMap = new HashMap();
            getServContext().setAttribute(proxyWS.utils.Constants.IN_ARGS, inputArgsMap);
        }
    }

    public static synchronized VRL getDefultUploadURI() {
        try {
            return new VRL("http://" + getHost() + ":8080" + "/axis/" + HTTPTransport.class.getSimpleName());
        } catch (VlURISyntaxException ex) {
            Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static synchronized InputStream getInputStream(String ref, boolean block) throws VlException {
        InputStream in = null;
        nl.uva.vlet.io.VStreamAccessable resource = null;
        //small bug 
//        if (ref.startsWith("http")) {
//            try {
//                URL data = new URL(ref);
//                conn = data.openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//                in = conn.getInputStream();
//            } catch (IOException ex) {
//                Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
        resource = getVStreamAccessable(ref);

        in = resource.getInputStream();

//        }

        if (!block) {
            return in;
        } else {
            try {
                while (in == null || in.available() < 1) {


                    if ((time * inc) >= timeOut) {
                        throw new VlException("VRS Timed out: " + (time * inc));
                    }

//                    debug("Not ready yet sleeping: " + (time * inc) + " in.available():" + in.available());
                    Thread.sleep(time * inc);
                    inc++;
//                    if (ref.startsWith("http")) {
//                        URL data = new URL(ref);
//                        conn = data.openConnection();
//                        conn.setDoInput(true);
//                        conn.connect();
//                        in = conn.getInputStream();
//                    } else {
                    resource = getVStreamAccessable(ref);
                    in = resource.getInputStream();
//                    }
//                    }
                }
            } catch (InterruptedException ex) {
//                Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
                in = null;
            } catch (IOException ex) {
//                Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
                in = null;
            }
        }
        inc = 0;

        return in;
    }

    public static synchronized OutputStream getOutputStream(String ref) throws VlException {
        OutputStream out = null;

        nl.uva.vlet.io.VStreamAccessable resource = getVStreamAccessable(ref);


        out = resource.getOutputStream();

        return out;

    }

    public static synchronized InputStream getVServerInputStream(String ref) {
        try {

            VStreamServer res = getDeployedResource(ref);
            return res.getInputStream();
        } catch (VlException ex) {
            Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static synchronized OutputStream getVServerOutputStream(String returnRef) {
        try {
            //create or fetch stream resource
            VStreamServer res = getDeployedResource(returnRef);
            return res.getOutputStream();
        } catch (nl.uva.vlet.exception.VlException ex) {
            Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static synchronized nl.uva.vlet.io.VStreamAccessable getVStreamAccessable(String ref) throws VlException {
        return (VStreamAccessable) getVnode(ref);
    }

    public static synchronized String getReturnDataURI(String scheme, String key) {
        VStreamServer ser = (VStreamServer) streamServers.get(scheme);
        return ser.getAddress() + "?" + key;
    }

    private static synchronized VStreamServer getDeployedResource(String returnRef) {
        VStreamServer ser = null;
        try {

            VRL vrl = new VRL(returnRef);

            ser = (VStreamServer) streamServers.get(vrl.getScheme());
            ser.start();

        } catch (VlException ex) {
            Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ser;
    }

    private static synchronized void debug(String msg) {
        System.err.println("--------VHTTPTransport: " + msg);
    }

    public static synchronized nl.uva.vlet.vrs.VRSContext getVrsContext() {
        if (vrsContext == null) {
            vrsContext = VRSContext.getDefault();
        }
        return vrsContext;
    }

    public static synchronized VNode getVnode(VRL vrl) {
        VResourceSystem rs;
        try {
            rs = getVrsContext().openResourceSystem(vrl);
            if (rs instanceof VFileSystem) {
                VFileSystem vfs = (VFileSystem) rs;
                VFile file = vfs.newFile(vrl);
                if (!file.exists()) {
                    file.create(false);
                }
                return file;
            } else {
                return rs.getResource(vrl);
            }
        } catch (VlException ex) {
            Logger.getLogger(DataTransportContext.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static synchronized VNode getVnode(String URI) throws VlException {
        return getVrsContext().openLocation(URI);
    }

    public static synchronized VRL getDefultFileUploadURI() {
        return vfsServers.get("http").get(0);
    }

    public static synchronized String getHost() {
        if (localHost == null) {
            localHost = proxyWS.utils.Misc.getIPOrHostName();
        }
        return localHost;
    }

    public static Conf getConf() {
        return conf;
    }

    public static synchronized void addReturnValue(int key, Object value) {
        Map returnVal = getReturnValues();
        returnVal.put(key, value);
        getServContext().setAttribute(proxyWS.utils.Constants.RETURN_VAL, returnVal);
    }

    public static synchronized Object getRetunValue(int key) {
        return getReturnValues().get(key);
    }

    public static synchronized Set getkeySet() {
        return getReturnValues().keySet();
    }

    private static synchronized Map getReturnValues() {
        returnValues = (Map) getServContext().getAttribute(proxyWS.utils.Constants.RETURN_VAL);
        return returnValues;
    }

    public static synchronized void addMethodCache(String key, Method method) {
        Map cacheMap = getMethodCache();
        cacheMap.put(key, method);
        getServContext().setAttribute(proxyWS.utils.Constants.CAHE_METHOD, cacheMap);
    }

    public static synchronized Method getMethod(String key) {
        return (Method) getMethodCache().get(key);
    }

    public static synchronized boolean isMethodCached(String key) {
        return getMethodCache().containsKey(key);
    }

    private static synchronized Map getMethodCache() {
        methodCache = (Map) getServContext().getAttribute(proxyWS.utils.Constants.CAHE_METHOD);
        return methodCache;
    }

    public static synchronized void addInputArg(String key, Object arg) {
        Map argsMap = getInputArgCache();
        argsMap.put(key, arg);
        getServContext().setAttribute(proxyWS.utils.Constants.IN_ARGS, argsMap);
    }

    public static synchronized Object getInputArgs(String key) {
        return getInputArgCache().get(key);
    }

    private static synchronized Map getInputArgCache() {
        inputArgsMap = (Map) getServContext().getAttribute(proxyWS.utils.Constants.IN_ARGS);
        return inputArgsMap;
    }

    public static synchronized void clearConf() {
        getServContext().removeAttribute(proxyWS.utils.Constants.CONF);
    }

    public static synchronized void setConf(int conf) {
        getServContext().setAttribute(proxyWS.utils.Constants.CONF, conf);
    }

    public static synchronized Integer getTransportConf() {
        return (Integer) getServContext().getAttribute(proxyWS.utils.Constants.CONF);
    }

    public static synchronized void clearSandbox() {
        getServContext().removeAttribute(proxyWS.utils.Constants.SANDBOX);
    }

    public static synchronized void setSandbox(String sandBoxLocation) {
        getServContext().setAttribute(proxyWS.utils.Constants.SANDBOX, sandBoxLocation);
    }

    public static synchronized String getSandbox() {
        return (String) getServContext().getAttribute(proxyWS.utils.Constants.SANDBOX);
    }

    public static synchronized ServletContext getServContext() {
        if (servContext == null) {
            MessageContext msgCtx = AxisServer.getCurrentMessageContext();
            HttpServlet srv = (HttpServlet) msgCtx.getProperty(HTTPConstants.MC_HTTP_SERVLET);
            servContext = srv.getServletContext();
        }
        return servContext;
    }

    private static synchronized void setServContext(ServletContext aServContext) {
        servContext = aServContext;
    }

    public static synchronized MessageContext getMessageContext() {
        return AxisServer.getCurrentMessageContext();
    }

    public static synchronized void cleanInputArgs() {
        getServContext().removeAttribute(proxyWS.utils.Constants.IN_REF_FILE);
        getServContext().removeAttribute(proxyWS.utils.Constants.IN_REF_MEM);
        getServContext().removeAttribute(proxyWS.utils.Constants.IN_UPLOAD_FILES);
        getServContext().removeAttribute(proxyWS.utils.Constants.IN_UPLOAD_DIR);
        getServContext().removeAttribute(proxyWS.utils.Constants.IN_UPLOAD_MEM);
    }
}
