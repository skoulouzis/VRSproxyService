/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyWS.clients;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import proxyWS.stubs.SimpleService.SimpleService;
import proxyWS.stubs.SimpleService.SimpleServiceServiceLocator;
import proxyWS.utils.AxisCalls;

/**
 *
 * @author skoulouz
 */
public class SimpleServiceClient {

    public static void main(String args[]) throws FileNotFoundException, IOException, ClassNotFoundException {
        SimpleService service;

        Vector<String> wsArgs;
        

        try {
            String endpoint = args[0];
            SimpleServiceServiceLocator ssL = new SimpleServiceServiceLocator();
            service = ssL.getSimpleService(new URL(endpoint));





            if (args[1].equals("doSomething")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                int result = service.doSomething(Integer.valueOf(wsArgs.get(0)));
                System.out.println(result);
            }

            if (args[1].equals("doSomethingElse")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.doSomethingElse(wsArgs.get(0));
                System.out.println(result);
            }

            if (args[1].equals("doSomethingElse2")) {
                String result = service.doSomethingElse(args[2]);
                System.out.println(result);
            }

            if (args[1].equals("getReturnDataURI")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.getReturnDataURI(wsArgs.get(0), wsArgs.get(1));
                System.out.println(result);
            }

            if (args[1].equals("manyArgsRetunsDirLoc")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.manyArgsRetunsDirLoc(wsArgs.get(0), wsArgs.get(1), wsArgs.get(2));
//                Object[] serviceArgs = new Object[]{wsArgs.get(0), wsArgs.get(1), wsArgs.get(2)};
//                String result = (String) AxisCalls.call(serviceArgs, "manyArgsRetunsDirLoc", new URL(endpoint), 5000);
                System.out.println(result);
            }


            if (args[1].equals("manyArgsRetunsDirsLoc")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String[] paths = wsArgs.get(2).split(",");
                String[] result = service.manyArgsRetunsDirsLoc(wsArgs.get(0), wsArgs.get(1), paths);
                for (int i = 0; i < result.length; i++) {
                    System.out.println(result[i]);
                }

            }


            if (args[1].equals("method1")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.method1(Integer.valueOf(wsArgs.get(0)));
                System.out.println(result);
            }


            if (args[1].equals("method1Stream")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);
                Object[] args1 = {Integer.valueOf(wsArgs.get(0))};
                AxisCalls.asyncCall(args1, "method1Stream", new URL(endpoint));
                System.out.println(result);
            }


            if (args[1].equals("method2")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.method2(wsArgs.get(0));
                System.out.println(result);
            }

            if (args[1].equals("method2Stream")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.getReturnDataURI("http", proxyWS.utils.Constants.WS_STREAMING);
                Object[] args1 = {wsArgs.get(0)};
                AxisCalls.asyncCall(args1, "method2Stream", new URL(endpoint));
                System.out.println(result);
            }


            if (args[1].equals("obj2Obj")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                File f = new File(wsArgs.get(0));
                Object obj = null;
                XStream xstream = new XStream(new DomDriver());
                if (f.exists()) {
                    FileInputStream fis = new FileInputStream(f);
                    if (f.isFile()) {
                        String ext = proxyWS.utils.Misc.getFileExtention(f);
                        if (ext == null) {
                            obj = new ObjectInputStream(fis).readObject();
                        } else if (ext != null || ext.equalsIgnoreCase("xml")) {
                            obj = xstream.fromXML(fis);
                        }
                    }
                }

                File out = new File(wsArgs.get(1));
                FileOutputStream fos = new FileOutputStream(out);

                String ext = proxyWS.utils.Misc.getFileExtention(out);

                if (ext == null) {
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(service.obj2Obj(obj));
                } else if (ext.equalsIgnoreCase("xml")) {
                    xstream.toXML(service.obj2Obj(obj), fos);
                }

                System.out.println(out.getAbsolutePath());
            }


            if (args[1].equals("objArr2Obj")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                File f = new File(wsArgs.get(0));
                Object[] obj = null;
                XStream xstream = new XStream(new DomDriver());
                if (f.exists()) {
                    FileInputStream fis = new FileInputStream(f);
                    if (f.isFile()) {
                        String ext = proxyWS.utils.Misc.getFileExtention(f);
                        if (ext == null) {
                            obj = (Object[]) new ObjectInputStream(fis).readObject();
                        } else if (ext.equalsIgnoreCase("xml")) {
                            obj = (Object[]) xstream.fromXML(fis);
                        }
                    }
                }

                File out = new File(wsArgs.get(1));
                FileOutputStream fos = new FileOutputStream(out);

                String ext = proxyWS.utils.Misc.getFileExtention(out);

                if (ext == null) {
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(service.objArr2Obj(obj));
                } else if (ext.equalsIgnoreCase("xml")) {
                    xstream.toXML(service.objArr2Obj(obj), fos);
                }

                System.out.println(out.getAbsolutePath());
            }


            if (args[1].equals("path2Obj")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                File out = new File(wsArgs.get(1));
                XStream xstream = new XStream(new DomDriver());

                FileOutputStream fos = new FileOutputStream(out);

                String ext = proxyWS.utils.Misc.getFileExtention(out);

                if (ext == null) {
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(service.path2Obj(wsArgs.get(0)));
                } else if (ext.equalsIgnoreCase("xml")) {
                    xstream.toXML(service.path2Obj(wsArgs.get(0)), fos);
                }

                System.out.println(out.getAbsolutePath());
            }

            if (args[1].equals("path2ObjManyArgs")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                File f = new File(wsArgs.get(1));
                Object[] obj = null;
                XStream xstream = new XStream(new DomDriver());
                if (f.exists()) {
                    FileInputStream fis = new FileInputStream(f);
                    if (f.isFile()) {
                        String ext = proxyWS.utils.Misc.getFileExtention(f);
                        if (ext.equalsIgnoreCase("xml")) {
                            obj = (Object[]) xstream.fromXML(fis);
                        } else {
                            obj = (Object[]) new ObjectInputStream(fis).readObject();
                        }
                    }
                }


                File out = new File(wsArgs.get(2));

                FileOutputStream fos = new FileOutputStream(out);
                if (proxyWS.utils.Misc.getFileExtention(out).equalsIgnoreCase("xml")) {
                    xstream.toXML(service.path2ObjManyArgs(wsArgs.get(0), obj), fos);
                } else {
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(service.path2ObjManyArgs(wsArgs.get(0), obj));
                }

                System.out.println(out.getAbsolutePath());
            }


            if (args[1].equals("path2ObjTooManyArgs")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                File f = new File(wsArgs.get(2));
                Object[] obj = null;
                XStream xstream = new XStream(new DomDriver());
                if (f.exists()) {
                    FileInputStream fis = new FileInputStream(f);
                    if (f.isFile()) {
                        String ext = proxyWS.utils.Misc.getFileExtention(f);
                        if (ext.equalsIgnoreCase("xml")) {
                            obj = (Object[]) xstream.fromXML(fis);
                        } else {
                            obj = (Object[]) new ObjectInputStream(fis).readObject();
                        }
                    }
                }

                File out = new File(wsArgs.get(3));

                FileOutputStream fos = new FileOutputStream(out);
                if (proxyWS.utils.Misc.getFileExtention(out).equalsIgnoreCase("xml")) {
                    xstream.toXML(service.path2ObjTooManyArgs(wsArgs.get(0), Integer.valueOf(wsArgs.get(1)), obj), fos);
                } else {
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(service.path2ObjTooManyArgs(wsArgs.get(0), Integer.valueOf(wsArgs.get(1)), obj));
                }

                System.out.println(out.getAbsolutePath());
            }

            if (args[1].equals("path2Path")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.path2Path(wsArgs.get(0));
                System.out.println(result);
            }

            if (args[1].equals("rand")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                int result = service.rand(Integer.valueOf(wsArgs.get(0)), Integer.valueOf(wsArgs.get(1)));
                System.out.println(result);
            }

            if (args[1].equals("randomstring")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                String result = service.randomstring(Integer.valueOf(wsArgs.get(0)), Integer.valueOf(wsArgs.get(1)));
                System.out.println(result);
            }

            if (args[1].equals("createXMLObj")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);
                XStream xstream = new XStream(new DomDriver());
                //xstream.toXML(new proxyWS.config.Conf(), new FileOutputStream(args[3]));

                Long[] num = new Long[2];
                num[0] = (long) 323;
                num[1] = (long) 32332;
                xstream.toXML(num[0], new FileOutputStream(args[3]));
            }

            if (args[1].equals("createObj")) {
                wsArgs = proxyWS.utils.Misc.readArgs(args[2]);

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args[3]));


                Long[] num = new Long[2];
                num[0] = (long) 323;
                num[1] = (long) 32332;


                oos.writeObject(num[0]);

                //oos.writeObject(new Long(223));

                oos.flush();

                oos.close();
            }


        } catch (Exception ex) {
            Logger.getLogger(SimpleServiceClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
