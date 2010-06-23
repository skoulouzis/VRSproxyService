/**
 * SimpleServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package proxyWS.stubs.SimpleService;

public class SimpleServiceServiceLocator extends org.apache.axis.client.Service implements proxyWS.stubs.SimpleService.SimpleServiceService {

    public SimpleServiceServiceLocator() {
    }


    public SimpleServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SimpleServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SimpleService
    private java.lang.String SimpleService_address = "http://localhost:8080/axis/services/SimpleService";

    public java.lang.String getSimpleServiceAddress() {
        return SimpleService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SimpleServiceWSDDServiceName = "SimpleService";

    public java.lang.String getSimpleServiceWSDDServiceName() {
        return SimpleServiceWSDDServiceName;
    }

    public void setSimpleServiceWSDDServiceName(java.lang.String name) {
        SimpleServiceWSDDServiceName = name;
    }

    public proxyWS.stubs.SimpleService.SimpleService getSimpleService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SimpleService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSimpleService(endpoint);
    }

    public proxyWS.stubs.SimpleService.SimpleService getSimpleService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            proxyWS.stubs.SimpleService.SimpleServiceSoapBindingStub _stub = new proxyWS.stubs.SimpleService.SimpleServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getSimpleServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSimpleServiceEndpointAddress(java.lang.String address) {
        SimpleService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (proxyWS.stubs.SimpleService.SimpleService.class.isAssignableFrom(serviceEndpointInterface)) {
                proxyWS.stubs.SimpleService.SimpleServiceSoapBindingStub _stub = new proxyWS.stubs.SimpleService.SimpleServiceSoapBindingStub(new java.net.URL(SimpleService_address), this);
                _stub.setPortName(getSimpleServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SimpleService".equals(inputPortName)) {
            return getSimpleService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://localhost:8080/axis/services/SimpleService", "SimpleServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://localhost:8080/axis/services/SimpleService", "SimpleService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SimpleService".equals(portName)) {
            setSimpleServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
