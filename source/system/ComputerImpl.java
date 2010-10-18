package system;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import api.Result;
import api.Task;

public class ComputerImpl extends UnicastRemoteObject implements Computer {

	private static final long serialVersionUID = -4634299253959618077L;

	public ComputerImpl() throws RemoteException {
		super();
	}

	@Override
	public Result<?> decompose(Task<?> t) {
		return t.decompose();
	}

	public static void main(String[] args) {
		String computeSpaceServer = args[0];
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {

			ComputerImpl comp = new ComputerImpl();
			Computer2Space space = (Computer2Space) Naming.lookup("//"
					+ computeSpaceServer + "/" + Computer2Space.SERVICE_NAME);
			space.register(comp);
			System.out.println("Computer ready");
		} catch (RemoteException e) {
			System.err.println("ComputerImpl exception : ");
			e.printStackTrace();

		} catch (MalformedURLException e) {
			System.err.println("ComputerImpl exception : ");
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("ComputerImpl exception : ");
			e.printStackTrace();
		}
	}

	@Override
	public Result<?> compose(Task<?> t, List<?> list) throws RemoteException {
		return t.compose(list);
	}

}
