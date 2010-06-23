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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.vlet.exception.VlException;

import nl.uva.vlet.exception.VlURISyntaxException;
import nl.uva.vlet.vrl.VRL;
import org.globus.net.ServerSocketFactory;



import proxyWS.utils.Constants;

/**
 *
 * @author skoulouz
 */
public class SOAPVStreamServer implements VStreamServer, Runnable {

    private static InputStream in;
    private static OutputStream out;
    private static PipedOutputStream serverOut;
    private static PipedInputStream serverIn;
    
    private DataInputStream dis;
    private boolean stop;
    private static String address;
    private static int ports[] = {8089};
    private Thread[] threads;
    private ServerSocket _server;
    private Socket socket;
    private VRL location;

    private void authenitcate() {
    }

    private void init() throws IOException, VlURISyntaxException {
        threads = new Thread[ports.length];
        _server = ServerSocketFactory.getDefault().createServerSocket(ports[0]);
        location = new VRL(getAddress());
    }

    public static InputStream getStaticInputStream() throws VlException {
        return in;
    }

    public static OutputStream getStaticOutputStream() throws VlException {
        return out;
    }

    public InputStream getInputStream() throws VlException {
        return in;
    }

    public OutputStream getOutputStream() throws VlException {
        return out;
    }

    public int getOptimalReadBufferSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getOptimalWriteBufferSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void start() {
        if (!isRunning()) {
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(this);
                threads[i].run();
            }
        }
    }

    public String getScheme() {
        return "tcp";
    }

    public boolean isRunning() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getAddress() {
        if (address == null) {
            address = "tcp://" + proxyWS.utils.Misc.getIPOrHostName() + ":" + ports[0] + "/";
        }
        return address;
    }

    public void run() {
        while (!isStop()) {
            try {
                socket = _server.accept();
                if (this.getScheme().equals(Constants.TCPS)) {
                    authenitcate();
                }
                handleConnection();
            } catch (IOException ex) {
                Logger.getLogger(SOAPVStreamServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    protected static void initPipes() {

        debug("Initilizing IO Streams");
        try {

            serverIn = new PipedInputStream();
            serverOut = new PipedOutputStream(serverIn);

            out = serverOut;
            in = serverIn;

//            clientIn = new PipedInputStream(getOptimalReadBufferSize());
//            clientOut = new PipedOutputStream(clientIn);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnection() {
        try {
            dis = new DataInputStream(socket.getInputStream());            
            if (dis.readUTF().equals(Constants.WS_STREAMING)) {
                org.apache.commons.io.IOUtils.copy(in, socket.getOutputStream());
            }
        } catch (IOException ex) {
            Logger.getLogger(SOAPVStreamServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private static void debug(String msg) {
        System.err.println("HTTPTransport: " + msg);
    }
}
