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

import org.apache.axis.client.Call;
import org.apache.axis.client.async.IAsyncResult;
import org.apache.axis.client.async.Status;

/**
 *
 * @author skoulouz
 */
public class MyCallBack implements org.apache.axis.client.async.IAsyncCallback {

    private Call call;
    private Object responce;

    public MyCallBack(Call call) {
        this.call = call;
    }

    public void onCompletion(IAsyncResult result) {
        Status status = result.getStatus();
        if (status == Status.COMPLETED) {
            responce = result.getResponse();
            
            
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAA");
            
            System.out.println(responce);
            
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
}