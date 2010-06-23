/**
 * SimpleService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package proxyWS.stubs.SimpleService;

public interface SimpleService extends java.rmi.Remote {
    public java.lang.String getReturnDataURI(java.lang.String scheme, java.lang.String key) throws java.rmi.RemoteException;
    public int doSomething(int value) throws java.rmi.RemoteException;
    public java.lang.String doSomethingElse(java.lang.String value) throws java.rmi.RemoteException;
    public java.lang.Object obj2Obj(java.lang.Object arg) throws java.rmi.RemoteException;
    public java.lang.Object objArr2Obj(java.lang.Object[] arg) throws java.rmi.RemoteException;
    public java.lang.Object path2Obj(java.lang.String path) throws java.rmi.RemoteException;
    public java.lang.Object path2ObjManyArgs(java.lang.String path, java.lang.Object[] args) throws java.rmi.RemoteException;
    public java.lang.Object path2ObjTooManyArgs(java.lang.String path, int num, java.lang.Object[] args) throws java.rmi.RemoteException;
    public java.lang.String path2Path(java.lang.String path) throws java.rmi.RemoteException;
    public java.lang.String manyArgsRetunsDirLoc(java.lang.String arg1, java.lang.String arg2, java.lang.String dirPath) throws java.rmi.RemoteException;
    public java.lang.String[] manyArgsRetunsDirsLoc(java.lang.String arg1, java.lang.String arg2, java.lang.String[] dirPaths) throws java.rmi.RemoteException;
    public java.lang.String method1(int sizeKb) throws java.rmi.RemoteException;
    public java.lang.String method1Stream(int sizeKb) throws java.rmi.RemoteException;
    public java.lang.String method2(java.lang.String data) throws java.rmi.RemoteException;
    public java.lang.String method2Stream(java.lang.String dataRef) throws java.rmi.RemoteException;
    public java.lang.String randomstring(int lo, int hi) throws java.rmi.RemoteException;
    public int rand(int lo, int hi) throws java.rmi.RemoteException;
}
