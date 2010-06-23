package proxyWS.clients;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Stub;
import proxyWS.stubs.ProxyService.ProxyService;
import proxyWS.stubs.SimpleService.SimpleService;
import proxyWS.utils.AxisCalls;


import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.client.async.AsyncCall;
import org.apache.axis.client.async.IAsyncResult;
import org.apache.axis.client.async.Status;

/**
 *
 * @author skoulouz
 */
public class BenchMarckClient {

    private static long produceTime = -1;
    private static long consumeTime = -1;
    private static long totalTime = -1;
    private static int timeOut = 999999999;

    public static void main(String[] args) {


        long start = System.currentTimeMillis();
        if (args[0].equals("soapCall")) {
            soapCall(args[1], args[2], Integer.parseInt(args[3]));
        }

        if (args[0].equals("proxyCall_Obj")) {
            proxyCallReturnObj(args[1], args[2], Integer.parseInt(args[3]));
        }


        if (args[0].equals("proxyCall_Ref")) {
            proxyCallReturnRef(args[1], args[2], Integer.parseInt(args[3]), Boolean.valueOf(args[4]));
        }


        if (args[0].equals("stream")) {
            stream(args[1], args[2], Integer.parseInt(args[3]));
        }
        totalTime = System.currentTimeMillis() - start;

        System.out.println(produceTime + "\t" + consumeTime + "\t" + totalTime);

        //bug!!!!!!!!!!
        System.exit(0);

    }

    public static void soapCall(String producer, String consumer, int sizeKb) {

        try {
            long startProduceTime = System.currentTimeMillis();
            proxyWS.stubs.SimpleService.SimpleServiceServiceLocator ssL = new proxyWS.stubs.SimpleService.SimpleServiceServiceLocator();

            SimpleService producerWS = ssL.getSimpleService(new URL(producer));
            String randomData = producerWS.method1(sizeKb);
            produceTime = System.currentTimeMillis() - startProduceTime;


            long startConsumeTime = System.currentTimeMillis();
            SimpleService consumerWS = ssL.getSimpleService(new URL(consumer));
            consumerWS.method2(randomData);

            consumeTime = System.currentTimeMillis() - startConsumeTime;
        } catch (Exception ex) {
            Logger.getLogger(BenchMarckClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void proxyCallReturnObj(String producer, String consumer, int sizeKb) {

        try {
            long startProduceTime = System.currentTimeMillis();
            proxyWS.stubs.ProxyService.ProxyServiceServiceLocator psL = new proxyWS.stubs.ProxyService.ProxyServiceServiceLocator();


            ProxyService producerWS = psL.getProxyService(new URL(producer));

            Object[] args1 = {sizeKb};
            String randomDataReference = producerWS.asyncCallService("SimpleService", "method1", args1);
            produceTime = System.currentTimeMillis() - startProduceTime;

            long startConsumeTime = System.currentTimeMillis();
            ProxyService consumerWS = psL.getProxyService(new URL(consumer));
            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_REF_MEM, "/testSandbox4", randomDataReference, "");
            Object[] args2 = {uri.toString()};

            consumerWS.callServiceReturnObject("SimpleService", "method2", args2);


            consumeTime = System.currentTimeMillis() - startConsumeTime;
        } catch (Exception ex) {
            Logger.getLogger(BenchMarckClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void proxyCallReturnRef(String producer, String consumer, int sizeKb, boolean obj) {
       
       

        try {
            long startProduceTime = System.currentTimeMillis();
            proxyWS.stubs.ProxyService.ProxyServiceServiceLocator psL = new proxyWS.stubs.ProxyService.ProxyServiceServiceLocator();


            ProxyService producerWS = psL.getProxyService(new URL(producer));
            org.apache.axis.client.Stub s = (Stub) producerWS;
            s.setTimeout(timeOut);


            Object[] args1 = {sizeKb};
            String randomDataReference = producerWS.asyncCallService("SimpleService", "method1", args1);

            produceTime = System.currentTimeMillis() - startProduceTime;


//            System.out.println("randomDataReference: " + randomDataReference);


            long startConsumeTime = System.currentTimeMillis();

            ProxyService consumerWS = psL.getProxyService(new URL(consumer));
            URI uri = new URI("wsdt", proxyWS.utils.Constants.IN_REF_MEM, "/testSandbox4", randomDataReference, "");
            Object[] args2 = {uri.toString()};
            
            Object[] args3 = {"SimpleService", "method2", args2};
            
//            consumerWS = (ProxyService) s2;
//            String dataRef = consumerWS.asyncCallService("SimpleService", "method2", args2);
            
            
//            String dataRef = (String) proxyWS.utils.AxisCalls.call(args3, "asyncCallService", new URL(consumer), timeOut);

//            proxyWS.clients.VRSProxyClient client = new VRSProxyClient();
//
//
//            if (obj) {
//                client.getData(dataRef, true);
//            } else {
//                client.getFile(dataRef, "/tmp/DELETE_ME");
//            }

//            


            asncCallBack(args3, "asyncCallService", new URL(consumer), obj);
//            

            consumeTime = System.currentTimeMillis() - startConsumeTime;
        } catch (Exception ex) {
            Logger.getLogger(BenchMarckClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void stream(String producer, String consumer, int sizeKb) {

        try {
            long startProduceTime = System.currentTimeMillis();
            proxyWS.stubs.SimpleService.SimpleServiceServiceLocator ssL = new proxyWS.stubs.SimpleService.SimpleServiceServiceLocator();

            SimpleService producerWS = ssL.getSimpleService(new URL(producer));

            String produceDataRef = producerWS.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);

            Object[] args1 = {sizeKb};
            String result = (String) AxisCalls.asyncCall(args1, "method1Stream", new URL(producer));
//            String result = (String) AxisCalls.asncCallBack(args1, "method1Stream", new URL(producer));



            long startConsumeTime = System.currentTimeMillis();
            SimpleService consumerWS = ssL.getSimpleService(new URL(consumer));
            String resultDataRef = consumerWS.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);
            Object[] args2 = {produceDataRef};
            String result2 = (String) AxisCalls.asyncCall(args2, "method2Stream", new URL(consumer));
//                        
////            consumerWS.method2Stream(resultDataRef);
//
//
            proxyWS.clients.VRSProxyClient client = new VRSProxyClient();



//            FileOutputStream dos = new FileOutputStream("/tmp/DELETE_ME");

//            InputStream in = DataTransportContext.getInputStream(resultDataRef, true);

            client.getFile(resultDataRef, "/tmp/DELETE_ME");
//            
//            client.getData(resultDataRef, true);


            consumeTime = System.currentTimeMillis() - startConsumeTime;


        } catch (Exception ex) {
            Logger.getLogger(BenchMarckClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Object asncCallBack(Object[] args, String method, URL endpoint, boolean getObject) {
        CallBack callBack = null;
        try {
            Service aService = new Service();
            final Call call = (Call) aService.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName(new QName(method));
            call.setTimeout(9999999);

            callBack = new CallBack(call, getObject);
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

class CallBack implements org.apache.axis.client.async.IAsyncCallback {

    private Call call;
    private Object responce;
    private boolean getObject;

    public CallBack(Call call, boolean getObject) {
        this.call = call;
        this.getObject = getObject;
    }

    public void onCompletion(IAsyncResult result) {
        Status status = result.getStatus();
        if (status == Status.COMPLETED) {
            responce = result.getResponse();

            handleCallBack((String) responce);

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

    private void handleCallBack(String dataRef) {
        try {
            proxyWS.clients.VRSProxyClient client = new VRSProxyClient();

            if (getObject) {
                client.getData(dataRef, true);
            } else {
                client.getFile(dataRef, "/tmp/DELETE_ME");
            
            }
        } catch (TimeoutException ex) {
            Logger.getLogger(CallBack.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            Logger.getLogger(CallBack.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
