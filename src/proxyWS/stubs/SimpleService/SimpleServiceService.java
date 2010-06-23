/**
 * SimpleServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package proxyWS.stubs.SimpleService;

public interface SimpleServiceService extends javax.xml.rpc.Service {
    public java.lang.String getSimpleServiceAddress();

    public proxyWS.stubs.SimpleService.SimpleService getSimpleService() throws javax.xml.rpc.ServiceException;

    public proxyWS.stubs.SimpleService.SimpleService getSimpleService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
