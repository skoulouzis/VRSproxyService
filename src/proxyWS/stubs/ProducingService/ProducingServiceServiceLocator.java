/**
 * ProducingServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package proxyWS.stubs.ProducingService;

public class ProducingServiceServiceLocator extends org.apache.axis.client.Service implements proxyWS.stubs.ProducingService.ProducingServiceService {

    public ProducingServiceServiceLocator() {
    }


    public ProducingServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProducingServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ProducingService
    private java.lang.String ProducingService_address = "http://localhost:8080/axis/services/ProducingService";

    public java.lang.String getProducingServiceAddress() {
        return ProducingService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ProducingServiceWSDDServiceName = "ProducingService";

    public java.lang.String getProducingServiceWSDDServiceName() {
        return ProducingServiceWSDDServiceName;
    }

    public void setProducingServiceWSDDServiceName(java.lang.String name) {
        ProducingServiceWSDDServiceName = name;
    }

    public proxyWS.stubs.ProducingService.ProducingService getProducingService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ProducingService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getProducingService(endpoint);
    }

    public proxyWS.stubs.ProducingService.ProducingService getProducingService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            proxyWS.stubs.ProducingService.ProducingServiceSoapBindingStub _stub = new proxyWS.stubs.ProducingService.ProducingServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getProducingServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setProducingServiceEndpointAddress(java.lang.String address) {
        ProducingService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (proxyWS.stubs.ProducingService.ProducingService.class.isAssignableFrom(serviceEndpointInterface)) {
                proxyWS.stubs.ProducingService.ProducingServiceSoapBindingStub _stub = new proxyWS.stubs.ProducingService.ProducingServiceSoapBindingStub(new java.net.URL(ProducingService_address), this);
                _stub.setPortName(getProducingServiceWSDDServiceName());
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
        if ("ProducingService".equals(inputPortName)) {
            return getProducingService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://localhost:8080/axis/services/ProducingService", "ProducingServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://localhost:8080/axis/services/ProducingService", "ProducingService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ProducingService".equals(portName)) {
            setProducingServiceEndpointAddress(address);
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
