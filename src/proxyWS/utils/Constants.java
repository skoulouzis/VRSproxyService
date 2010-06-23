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

/**
 *
 * @author skoulouz
 */
public class Constants {

    public static final String IN_UPLOAD_MEM = "input.local.mem";
    public static final String IN_UPLOAD_FILES = "input.local.files";
    public static final String IN_UPLOAD_DIR = "input.local.dir";
    public static final String IN_REF_MEM = "input.ref.mem";
    public static final String IN_REF_FILE = "input.ref.file";
    public static final String IN_REF_DIR = "input.ref.dir";
    public static final String IN_MIXED = "input.mixed";
    public static final String OUT_FILE = "out.local.file";
    public static final String SANDBOX = "sandbox";
    public static final String CONF = "conf";
    public static final String WS_STREAMING = "ws.streaming";
    public static final String CL_STREAMING = "client.streaming";
    public static final String UPLOAD_FILES = "file.upload";
    public static final String NO_DATA = "out.no.data.recived";
    public static final String IN_ARGS = "input.args.map";
    public static final String RETURN_VAL = "return.values.map";
    public static final String CAHE_METHOD = "cache.method";
    public static final long MAX_SIZE_DATA = 3 * 1024 * 1024;
    public static final long MAX_SIZE_FILE = 10 * 1024 * 1024;    //schemes
    public static final int TCP = 100;
    public static final int TCPS = 101;
    public static final int UDP = 200;
    public static final int UDPS = 201;
    public static final int STYX = 300;
    public static final int RTSP = 400;
    public static final int GridFTP = 500;
    public static final int HTTP = 600;
    public static final int HTTPS = 601;
    public static final int TIME_OUT = 600000;
}
