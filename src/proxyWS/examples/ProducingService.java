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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import nl.uva.vlet.exception.VlException;
import proxyWS.config.Conf;
import proxyWS.transport.DataTransportContext;
import proxyWS.transport.TCPVStreamServer;

/**
 *
 * @author skoulouz
 */
public class ProducingService {

//    private static proxyWS.transport.DataTransportContext d;
    private Conf conf;

    public ProducingService() {
//        d = new proxyWS.transport.DataTransportContext(true, null);
        DataTransportContext.init(true, null);
    }

    public String analyzeData(String dataRef) {
        try {

            InputStream in = DataTransportContext.getInputStream(dataRef, true);

            InputStreamReader input = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(input);


            String incoming = "";
            String fullData = "";


            while (incoming != null) {
                incoming = reader.readLine();
                fullData = fullData + incoming;
            }


            fullData = org.apache.axis.encoding.Base64.encode(fullData.getBytes());

            Random r = new Random();
            int num = r.nextInt(5000);
            debug("Sleeping: " + num);
            Thread.sleep(num);

            String returnRef = DataTransportContext.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);
            debug("Data will be availele in: " + returnRef);

            OutputStream out = DataTransportContext.getVServerOutputStream(returnRef);
            OutputStreamWriter output = new OutputStreamWriter(out);
            BufferedWriter writer = new BufferedWriter(output);

            writer.write(fullData, 0, fullData.length());
            String end = "--------------THE END------------";
            writer.write(dataRef, 0, end.length());
            writer.flush();
            writer.close();

            return returnRef;
        } catch (Exception ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getReturnDataURI(String scheme, String key) {
        return DataTransportContext.getReturnDataURI(scheme, key);
    }

    public String writeStream(String data) {
        int size = Integer.valueOf(data);
        try {


            OutputStream out = DataTransportContext.getVServerOutputStream(getReturnDataURI("http", "key"));

            Random r = new Random();
            int sizeSent = 0;
            int byteSize = 1 * 1024 * 1024;
            byte[] bytes = new byte[byteSize];
            for (int i = 0; i < size; i++) {
                r.nextBytes(bytes);
                out.write(bytes);
                sizeSent = sizeSent + bytes.length;
                debug("Writing : " + sizeSent);
            }

            out.flush();
            out.close();

            debug("Streamed size: " + sizeSent);

            return "done";
        } catch (Exception ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String processAndWriteStream(String data) {
        int size = Integer.valueOf(data);
        try {

            Random r = new Random();
            int byteSize = 1 * 1024 * 1024;
            byte[] bytes = new byte[byteSize];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();



            for (int i = 0; i < size; i++) {
                debug("appending step  : " + (i + 1) + "/" + (size));
                r.nextBytes(bytes);
                baos.write(bytes);
            }

            int sleep = r.nextInt(10000);
            debug("Sleeping for: " + sleep);
            Thread.sleep(sleep);

            OutputStream out = DataTransportContext.getVServerOutputStream(getReturnDataURI("http", "key"));
            int sizeSent = 0;

            bytes = baos.toByteArray();
            out.write(bytes);
            sizeSent = bytes.length;

            out.flush();
            out.close();

            debug("Streamed size: " + sizeSent);

            return "done";
        } catch (Exception ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String processAndWriteXMLStream(String data) {
        try {
            int size = Integer.valueOf(data);

            conf = DataTransportContext.getConf();

            Map serv = conf.getStreamServers();

            serv.put("tcp", new TCPVStreamServer());
            serv.put("tcps", new TCPVStreamServer());
            serv.put("someSchme", new TCPVStreamServer());
            conf.setVfsServers(serv);



            XStream x = new XStream(new DomDriver());
            String xmlConf = x.toXML(conf);
            debug("Conf is :");
            debug(xmlConf);

            Random r = new Random();
            int sleep = r.nextInt(10000);
            debug("Sleeping for: " + sleep);
            Thread.sleep(sleep);


            OutputStream out = DataTransportContext.getVServerOutputStream(getReturnDataURI("http", "key"));

            out.write(xmlConf.getBytes());
            out.flush();
            out.close();


        } catch (IOException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public long processUploadStream(String data) {
        long start;
        try {

            debug("Recived: " + data);

            InputStream in = DataTransportContext.getVServerInputStream(getReturnDataURI("http", "key"));

            int len = 0;
            long sent = 0;
            byte[] tmp = new byte[1024];

            start = System.currentTimeMillis();

            while ((len = in.read(tmp)) != -1) {
//                debug("-----------Reading: " + len);
                sent = sent + len;
                if (sent % (1024 * 1024) == 0) {
                    debug(" Speed: " + (sent / 1024.0 * 1024.0) / ((System.currentTimeMillis() - start) / 1000.0) + " m/sec ");
                }

            }

            debug("recived: " + sent);

            return sent;

        } catch (IOException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private void debug(String msg) {
        System.err.println(this.getClass().getName() + ": " + msg);
    }

    public String processString(String data) throws UnsupportedEncodingException {
//        debug("Starting processString:");
        long start = System.currentTimeMillis();
        ByteArrayInputStream destStream = new ByteArrayInputStream(data.getBytes());
        InputStreamReader input = new InputStreamReader(destStream, "UTF-8");
        String base64 = xmlStream(input);
//        debug("processString \t time: " + (System.currentTimeMillis() - start)+" size: "+base64.getBytes().length);

        System.out.println((System.currentTimeMillis() - start) + "\t" + base64.getBytes().length);
        return base64;
    }

    public String processStreamString(String dataRef) throws UnsupportedEncodingException {
//        debug("Starting processStreamString:");
        long start = System.currentTimeMillis();
        String base64 = null;
        InputStream in = null;
        try {
//            debug("geting inputstream for: " + dataRef);
            in = DataTransportContext.getInputStream(dataRef, true);

//            debug("creating  InputStreamReader for: " + dataRef);
            InputStreamReader input = new InputStreamReader(in, "UTF-8");

//            debug("Start encodeing: ");
            base64 = xmlStream(input);

        } catch (VlException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        debug("processStreamString \t time: " + (System.currentTimeMillis() - start) + " size: " + base64.getBytes().length);
        return base64;
    }

    private String xmlStream(InputStream input) {
        int prev = 0;
        String base64Encode = null;
        try {
            XMLStreamReader xmlReader;


            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
//            inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
            inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
            xmlReader = inputFactory.createXMLStreamReader(input);

            while (xmlReader.hasNext()) {
                Integer eventType = xmlReader.next();

                if (eventType.equals(XMLEvent.START_ELEMENT)) {
//                    debug("<" + xmlReader.getLocalName() + ">");

//                    if (xmlReader.getLocalName().equals("doc")) {
//                        debug("     getAttributeCount: " + xmlReader.getAttributeCount());
//                        debug("     getAttributeName: " + xmlReader.getAttributeName(0));
//                        debug("     getAttributeValue: " + xmlReader.getAttributeValue(0));
//                    }
//                    
                    if (xmlReader.getLocalName().equals("field")) {
//                        debug("     getAttributeCount: " + xmlReader.getAttributeCount());
//                        debug("     getAttributeName: " + xmlReader.getAttributeName(0));
//                        debug("     getAttributeType: " + xmlReader.getAttributeType(0));
//                        debug("     getAttributeValue: " + xmlReader.getAttributeValue(0));
                        if (xmlReader.getAttributeValue(0).equals("content")) {
                            prev = 1;
                        }
                    }
                    if (xmlReader.getLocalName().equals("value") && prev == 1) {
//                        debug("     Contenet: "+xmlReader.getElementText());
                        String cont = xmlReader.getElementText();
                        base64Encode = org.apache.axis.encoding.Base64.encode(cont.getBytes());
                        prev = -1;
                    }
                }

                if (eventType.equals(XMLEvent.END_ELEMENT)) {
//                    debug("</" + xmlReader.getLocalName() + ">");
                }

                if (eventType.equals(XMLEvent.END_DOCUMENT)) {
//                    xmlReader.close();
//                    break;
                }
            }
            input.close();
        } catch (IOException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return base64Encode;
    }

    private String xmlStream(InputStreamReader input) {
        int prev = 0;
        String base64Encode = null;
        try {
            XMLStreamReader xmlReader;

//            debug("Getting XMLInputFactory");

            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
//            inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
//            inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
            xmlReader = inputFactory.createXMLStreamReader(input);


//            debug("xmlReader created");

            while (xmlReader.hasNext()) {
                Integer eventType = xmlReader.next();

//                debug("got next event");

                if (eventType.equals(XMLEvent.START_ELEMENT)) {
//                    debug("<" + xmlReader.getLocalName() + ">");

//                    if (xmlReader.getLocalName().equals("doc")) {
//                        debug("     getAttributeCount: " + xmlReader.getAttributeCount());
//                        debug("     getAttributeName: " + xmlReader.getAttributeName(0));
//                        debug("     getAttributeValue: " + xmlReader.getAttributeValue(0));
//                    }
//                    
                    if (xmlReader.getLocalName().equals("field")) {
//                        debug("     getAttributeCount: " + xmlReader.getAttributeCount());
//                        debug("     getAttributeName: " + xmlReader.getAttributeName(0));
//                        debug("     getAttributeType: " + xmlReader.getAttributeType(0));
//                        debug("     getAttributeValue: " + xmlReader.getAttributeValue(0));
                        if (xmlReader.getAttributeValue(0).equals("content")) {
                            prev = 1;
                        }
                    }
                    if (xmlReader.getLocalName().equals("value") && prev == 1) {
//                        debug("     Contenet: "+xmlReader.getElementText());
                        String cont = xmlReader.getElementText();
                        base64Encode = org.apache.axis.encoding.Base64.encode(cont.getBytes());
                        prev = -1;
                    }
                }

                if (eventType.equals(XMLEvent.END_ELEMENT)) {
//                    debug("</" + xmlReader.getLocalName() + ">");
                }

                if (eventType.equals(XMLEvent.END_DOCUMENT)) {
//                    xmlReader.close();
//                    break;
                }
            }
            input.close();
        } catch (IOException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(ProducingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return base64Encode;
    }
}
