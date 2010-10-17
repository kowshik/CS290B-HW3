package api;


public interface Space extends java.rmi.Remote {

	
	String SERVICE_NAME = "Space";

	void put(Task<?> task) throws java.rmi.RemoteException;

	Result<?> takeResult() throws java.rmi.RemoteException;
}