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

package proxyWS.config;

import proxyWS.transport.DataTransportContext;
import proxyWS.transport.TCPVStreamServer;
import proxyWS.transport.HTTPFileTransport;
import proxyWS.transport.HTTPTransport;
import proxyWS.transport.VStreamServer;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.vlet.exception.VlURISyntaxException;
import nl.uva.vlet.vrl.VRL;

/**
 *
 * @author skoulouz
 */
public class Conf {

    /**
     * Deployed VStream servers for streaming 
     */
    private Map<String, VStreamServer> streamServers;
    
    /**
     * Avelibale VFile servers
     */
    private Map<String, Vector<VRL>> vfsServers = new HashMap<String, Vector<VRL>>();

    public Conf() {
        //read some conf file

        //populate deployed VStreamServers
        streamServers = new HashMap();


        VStreamServer ser = new HTTPTransport();
        streamServers.put(ser.getScheme(), ser);

        ser = new TCPVStreamServer();
        streamServers.put(ser.getScheme(), ser);


        //populate avileble VStreamServers
        try {
            VRL loc = new VRL("gsiftp://pc-vlab18.science.uva.nl/");
            Vector<VRL> locations = new Vector<VRL>();
            locations.add(loc);
            vfsServers.put(loc.getScheme(), locations);

            //-----------fix this!!!! not hardcoded!!!!!!!!!!!!!
            loc = new VRL("http://" + getHost() + ":7080" + "/axis1.4/" + HTTPFileTransport.class.getSimpleName());
            locations = new Vector<VRL>();
            locations.add(loc);
            vfsServers.put(loc.getScheme(), locations);

        } catch (VlURISyntaxException ex) {
            Logger.getLogger(Conf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, VStreamServer> getStreamServers() {
        return streamServers;
    }

    public Map<String, Vector<VRL>> getVfsServers() {
        return vfsServers;
    }

    public static String getHost() {
        return DataTransportContext.getHost();
    }
    
    
    public void setVfsServers(Map servers){
        this.streamServers = servers;
    }
}
