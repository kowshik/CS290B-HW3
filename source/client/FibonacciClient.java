package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import api.Space;

public class FibonacciClient {

	private static final int N = 2;

	public static void main(String[] args) {

		String computeSpaceServer = args[0];

		FibonacciJob fibJob = new FibonacciJob(N);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		Space space;
		try {
			space = (Space) Naming.lookup("//" + computeSpaceServer + "/"
					+ Space.SERVICE_NAME);

			// ------Generate tasks and execute them remotely
			fibJob.generateTasks(space);
			fibJob.collectResults(space);
			int fibValue = fibJob.getAllResults();
			// -------------------------------------

			System.out.println("Fibonacci Value : " + fibValue);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}

	}

}