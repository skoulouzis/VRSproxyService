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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import proxyWS.utils.Misc;
import proxyWS.utils.ObjectTransformer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 * @author skoulouz
 */
public class HTTPDataUpload extends HttpServlet {
    private XStream xstream;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            xstream = new XStream(new DomDriver());
            ServletContext context = getServletContext();

            Map inArgsMap = (Map) context.getAttribute(proxyWS.utils.Constants.IN_ARGS);

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // maximum size that will be stored in memory
            int conf = (Integer) context.getAttribute(proxyWS.utils.Constants.CONF);
            factory.setSizeThreshold(conf);
            // the location for saving data that is larger than getSizeThreshold()
            factory.setRepository(new File(Misc.getTmpDir()));


            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum size before a FileUploadException will be thrown
            upload.setSizeMax(conf * 2);

            List fileItems = upload.parseRequest(request);

            // assume we know there are two files. The first file is a small
            // text file, the second is unknown and is written to a file on
            // the server
            Iterator i = fileItems.iterator();

            int c = 0;
            Object[] inputArgs = new Object[fileItems.size()];

            Object obj2 = null;
            while (i.hasNext()) {

                Object obj = i.next();
//                System.out.println("it[" + c + "]" + obj + " Class: " + obj.getClass().getName());
                org.apache.commons.fileupload.disk.DiskFileItem fi = (DiskFileItem) obj;

//                System.out.println("isFormField " + fi.isFormField() + "\n isInMemory " + fi.isInMemory() + "\n ");

                obj2 = xstream.fromXML(new String(fi.get()));
                                
//                obj2 = ObjectTransformer.getObject(fi.get());

                System.out.println("Http upload Class:  " + obj2.getClass());
                inputArgs[c] = obj2;
                c++;
//                files.add(f.getAbsolutePath());
            }


            inArgsMap.put(request.getQueryString(), inputArgs);

        } catch (Exception ex) {
            Logger.getLogger(HTTPDataUpload.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
