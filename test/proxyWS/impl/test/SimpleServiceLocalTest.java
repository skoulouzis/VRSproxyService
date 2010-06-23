/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proxyWS.impl.test;

import junit.framework.TestCase;
import proxyWS.examples.SimpleService;

/**
 *
 * @author skoulouz
 */
public class SimpleServiceLocalTest extends TestCase {

    public SimpleServiceLocalTest(String testName) {
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

    public void testMethod2() {

        proxyWS.examples.SimpleService inst = new proxyWS.examples.SimpleService();

        for (int i = 1024; i < 19456; i = i + 1024) {
            System.out.println("produced size: " + inst.method2(inst.method1(i)));

        }

        for (int i = 512000; i < 1024000; i = i + 10240) {
            System.out.println("produced size: " + inst.method2(inst.method1(i)));

        }




    }

    private void debug(String msg) {
        System.err.println(this.getClass().getName() + ": " + msg);
    }
}
