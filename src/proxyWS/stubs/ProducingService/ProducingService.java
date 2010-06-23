/**
 * ProducingService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package proxyWS.stubs.ProducingService;

public interface ProducingService extends java.rmi.Remote {
    public java.lang.String getReturnDataURI(java.lang.String scheme, java.lang.String key) throws java.rmi.RemoteException;
    public java.lang.String analyzeData(java.lang.String data) throws java.rmi.RemoteException;
    public java.lang.String writeStream(java.lang.String data) throws java.rmi.RemoteException;
    public java.lang.String processAndWriteStream(java.lang.String data) throws java.rmi.RemoteException;
    public java.lang.String processAndWriteXMLStream(java.lang.String data) throws java.rmi.RemoteException;
    public long processUploadStream(java.lang.String data) throws java.rmi.RemoteException;
    public java.lang.String processString(java.lang.String data) throws java.rmi.RemoteException;
    public java.lang.String processStreamString(java.lang.String dataRef) throws java.rmi.RemoteException;
}
