package system;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import api.Result;
import api.Task;


public interface Computer extends Remote {
	
	Result<?> execute(Task<?> t) throws RemoteException;
	Result<?> execute(Task<?> t,List<?> list) throws RemoteException;
}
