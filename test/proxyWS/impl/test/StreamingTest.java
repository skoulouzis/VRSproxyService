/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyWS.impl.test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.rpc.ServiceException;
import nl.uva.vlet.exception.VlException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import proxyWS.config.Conf;
import proxyWS.transport.DataTransportContext;
import proxyWS.utils.AxisCalls;

/**
 *
 * @author skoulouz
 */
public class StreamingTest extends TestCase {

    public StreamingTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAnalyzeData() {
        String ref;
        String result;
        try {
            proxyWS.stubs.ProducingService.ProducingServiceServiceLocator sl = new proxyWS.stubs.ProducingService.ProducingServiceServiceLocator();
//            VproxyWS.stubs.ProducingService.ProducingService service = sl.getProducingService(new URL("http://elab.science.uva.nl:8080/axis/services/ProducingService"));
            proxyWS.stubs.ProducingService.ProducingService service = sl.getProducingService();
//

            String dataRef = service.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);

            Object[] args = {"http://localhost:8080/RELEASE-NOTES.txt"};
            result = (String) AxisCalls.asyncCall(args, "analyzeData", new URL("http://localhost:8080/axis/services/ProducingService"));
//            service.analyzeData("http://staff.science.uva.nl/~skoulouz/pmwiki/index.php/ProxyWS/Description");

            debug("Result??: " + result);
            DataTransportContext.init(false, null);

//            proxyWS.transport.DataTransportContext d = new proxyWS.transport.DataTransportContext(false, null);


            debug("Service is produsing data at: " + dataRef);
            InputStream in = DataTransportContext.getInputStream(dataRef, true);



            InputStreamReader input = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(input);


            String incoming = "";
            String fullData = "";


            while (incoming != null) {
                incoming = reader.readLine();
//                fullData = fullData + incoming;
                System.out.println("Data: " + incoming);
            }

            assertNotNull(fullData);
        } catch (Exception ex) {
            Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testReadBigStream() throws TimeoutException {
        String result;
        {
            InputStream in = null;
            try {
                proxyWS.stubs.ProducingService.ProducingServiceServiceLocator sl = new proxyWS.stubs.ProducingService.ProducingServiceServiceLocator();
                proxyWS.stubs.ProducingService.ProducingService service = sl.getProducingService();
                String dataRef = service.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);
                Random r = new Random();
                int produceSize = r.nextInt(150) + 1;
                int expected = produceSize * 1024 * 1024;
                Object[] args = {String.valueOf(produceSize)};
                result = (String) AxisCalls.asyncCall(args, "writeStream", new URL("http://localhost:8080/axis/services/ProducingService"));
                debug("Result??: " + result);
//                proxyWS.transport.DataTransportContext d = new proxyWS.transport.DataTransportContext(false, null);
                DataTransportContext.init(false, null);
                debug("Service is produsing data at: " + dataRef);
                in = DataTransportContext.getInputStream(dataRef, true);

                int len = 0;
                int size = 0;
                byte[] bytes = new byte[1024];
                while ((len = in.read(bytes)) != -1) {
                    size = size + len;
                }

                debug("Size: " + size);

                assertEquals(expected, size);
            } catch (IOException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (VlException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServiceException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void testProcessAndWriteStream() throws TimeoutException {
        String result;
        {
            InputStream in = null;
            try {
                proxyWS.stubs.ProducingService.ProducingServiceServiceLocator sl = new proxyWS.stubs.ProducingService.ProducingServiceServiceLocator();
                proxyWS.stubs.ProducingService.ProducingService service = sl.getProducingService();
                String dataRef = service.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);
                Random r = new Random();
                int produceSize = r.nextInt(21) + 1;
                int expected = produceSize * 1024 * 1024;
                Object[] args = {String.valueOf(produceSize)};
                result = (String) AxisCalls.asyncCall(args, "processAndWriteStream", new URL("http://localhost:8080/axis/services/ProducingService"));
                debug("Result??: " + result);
//                proxyWS.transport.DataTransportContext d = new proxyWS.transport.DataTransportContext(false, null);
                DataTransportContext.init(false, null);
                debug("Service is produsing data at: " + dataRef);
                in = DataTransportContext.getInputStream(dataRef, true);


                int len = 0;
                int size = 0;
                byte[] bytes = new byte[1024];

                debug("Start Reading : ");
                while ((len = in.read(bytes)) != -1) {
                    if (len % (bytes.length * 2) == 0) {
                        debug("Read:" + size);
                    }
                    size = size + len;
                }

                debug("Size: " + size);

                assertEquals(expected, size);
            } catch (IOException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (VlException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServiceException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void testProcessAndWriteXMLStream() throws TimeoutException {
        String result;
        Conf conf;
        {
            InputStream in = null;
            try {
                proxyWS.stubs.ProducingService.ProducingServiceServiceLocator sl = new proxyWS.stubs.ProducingService.ProducingServiceServiceLocator();
                proxyWS.stubs.ProducingService.ProducingService service = sl.getProducingService();
                String dataRef = service.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);
                Random r = new Random();
                int produceSize = r.nextInt(21);
                int expected = produceSize * 1024 * 1024;
                Object[] args = {String.valueOf(produceSize)};
                result = (String) AxisCalls.asyncCall(args, "processAndWriteXMLStream", new URL("http://localhost:8080/axis/services/ProducingService"));
                debug("Result??: " + result);
//                proxyWS.transport.DataTransportContext d = new proxyWS.transport.DataTransportContext(false, null);
                DataTransportContext.init(false, null);
                debug("Service is produsing data at: " + dataRef);
                in = DataTransportContext.getInputStream(dataRef, true);

                int len = 0;
                int size = 0;
                byte[] bytes = new byte[1024];

                debug("Start Reading : ");

                XStream x = new XStream(new DomDriver());
                conf = (Conf) x.fromXML(in);



//            assertEquals(expected, size);
            } catch (IOException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (VlException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ServiceException ex) {
                Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void testBigStreamClient() {
        Long result;
        OutputStream out;
        InputStream in;
        Map<String, List<String>> prop;
        Set<String> keys;
        URL url;
        try {
            proxyWS.stubs.ProducingService.ProducingServiceServiceLocator sl = new proxyWS.stubs.ProducingService.ProducingServiceServiceLocator();
            proxyWS.stubs.ProducingService.ProducingService service = sl.getProducingService();
            String dataRef = service.getReturnDataURI("http", proxyWS.utils.Constants.CL_STREAMING);
            Random r = new Random();

            Object[] obj = {"args"};
            result = (Long) AxisCalls.asyncCall(obj, "processUploadStream", new URL("http://localhost:8080/axis/services/ProducingService"));

            debug("Result??: " + result);
//            proxyWS.transport.DataTransportContext d = new proxyWS.transport.DataTransportContext(false, null);
            DataTransportContext.init(false, null);
            debug("Service is reading data at: " + dataRef);

            Thread.sleep(4000);

            debug("Getting output stream");
            out = DataTransportContext.getOutputStream(dataRef);


            byte[] tmp = new byte[1024];
            long size = 0;
            debug("Writing");
            for (int i = 0; i < (10 * 1024); i++) {
                r.nextBytes(tmp);
                out.write(tmp);
                size = size + tmp.length;
            }

            out.flush();
            out.close();

            debug("Sent: " + size);
        } catch (Exception ex) {
            Logger.getLogger(StreamingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void debug(String msg) {
        System.err.println(this.getClass().getName() + ": " + msg);
    }
}
