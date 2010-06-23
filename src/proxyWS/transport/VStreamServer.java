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

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.io.VStreamAccessable;

/**
 *
 * @author skoulouz
 */
public interface VStreamServer extends VStreamAccessable {

    public void start() throws VlException;

    public String getScheme();

    public boolean isRunning();
    
    public String getAddress();
}
