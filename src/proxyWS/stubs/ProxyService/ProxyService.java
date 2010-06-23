/**
 * ProxyService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package proxyWS.stubs.ProxyService;

public interface ProxyService extends java.rmi.Remote {
    public java.lang.String[] list(java.lang.String path) throws java.rmi.RemoteException;
    public boolean delete(java.lang.String pathURI) throws java.rmi.RemoteException;
    public java.lang.String createSandbox(java.lang.String path) throws java.rmi.RemoteException;
    public boolean setProxyCert(java.lang.String proxyString) throws java.rmi.RemoteException;
    public boolean deleteTemp() throws java.rmi.RemoteException;
    public java.lang.String getFileUploadURI(java.lang.String sandboxPath) throws java.rmi.RemoteException;
    public java.lang.String getUploadURI(int conf) throws java.rmi.RemoteException;
    public java.lang.String getFileURI(java.lang.String path) throws java.rmi.RemoteException;
    public java.lang.Object getReturnedValue(int key) throws java.rmi.RemoteException;
    public java.lang.String getReturnedValueRef(int key) throws java.rmi.RemoteException;
    public int asyncCallServiceReturnObject(java.lang.String serviceName, java.lang.String methodName, java.lang.Object[] args) throws java.rmi.RemoteException;
    public java.lang.Object callServiceReturnObject(java.lang.String serviceName, java.lang.String methodName, java.lang.Object[] args) throws java.rmi.RemoteException;
    public java.lang.String callService(java.lang.String serviceName, java.lang.String methodName, java.lang.Object[] args) throws java.rmi.RemoteException;
    public java.lang.String asyncCallService(java.lang.String serviceName, java.lang.String methodName, java.lang.Object[] args) throws java.rmi.RemoteException;
}
