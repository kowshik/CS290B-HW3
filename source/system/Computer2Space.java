package system;


public interface Computer2Space extends java.rmi.Remote {
	
	String SERVICE_NAME="Space";
	
	void register(Computer computer) throws java.rmi.RemoteException;
}