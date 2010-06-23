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

import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;
import org.apache.axis.AxisFault;

public class ContextUtils {

    public static String getTargetServicePath(MessageContext context) {
        return (String) context.getTargetService();
    }

    public static SOAPService getTargetService(MessageContext context) {
        return context.getService();
    }

    public static Object getServiceProperty(MessageContext context,
            String serviceName,
            String propName)
            throws AxisFault {
        AxisEngine engine = context.getAxisEngine();
        if (engine != null) {
            SOAPService service = (SOAPService) engine.getService(serviceName);
            return getServiceProperty(service, propName);
        }
        return null;
    }
    

    public static Object getServiceProperty(MessageContext context,
            String propName) {
        SOAPService service = (SOAPService) context.getService();
        return getServiceProperty(service, propName);
    }

    public static Object getServiceProperty(SOAPService service,
            String propName) {
        return (service != null) ? service.getOption(propName) : null;
    }

    public static void setServiceProperty(MessageContext context,
            String serviceName,
            String propName,
            Object value)
            throws AxisFault {
        AxisEngine engine = context.getAxisEngine();
        if (engine != null) {
            SOAPService service = (SOAPService) engine.getService(serviceName);
            if (service != null) {
                if (value == null) {
                    if (service.getOptions() != null) {
                        service.getOptions().remove(propName);
                    }
                } else {
                    service.setOption(propName, value);
                }
            }
        }
    }
} 
