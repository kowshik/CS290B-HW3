package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;

/**
 * Implementation of the Space Interface. Represents a raw computing resource
 * where tasks (({@link api.Task Task}) are automatically executed by registered
 * workers as soon as they are dropped in. If a worker crashes, the computation
 * would still continue (assuming there are other workers still running), since
 * each task is executed under a transaction, which would be rolled back after
 * the worker crashed, leaving the task in the space for another worker to pick
 * up. For more information, please refer <a
 * href="http://today.java.net/pub/a/today/2005/04/21/farm.html">How to build a
 * compute farm</a>.
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public class SpaceImpl extends UnicastRemoteObject implements Space,
		Computer2Space, Runnable {

	private static final long serialVersionUID = 3093568798450948074L;
	private Map<String, Successor> waitingTasks;
	private LinkedBlockingQueue<Result<?>> results;
	private List<ComputerProxy> proxies;
	private static final int PORT_NUMBER = 2672;

	public SpaceImpl() throws RemoteException {
		
		this.waitingTasks = Collections
		.synchronizedMap(new HashMap<String, Successor>());
		this.results = new LinkedBlockingQueue<Result<?>>();
		this.proxies = Collections
				.synchronizedList(new Vector<ComputerProxy>());

	}

	public void put(Task<?> aTask) throws RemoteException {

		if (proxies.size() > 0) {
			int random = new Random().nextInt(this.proxies.size());
			proxies.get(random).addTask(aTask);

		}
		System.err
				.println("Unable to register tasks due to absence of computer proxies");

	}

	public void putResult(Result<?> result) throws RemoteException {
		results.add(result);
	}

	

	public Result<?> takeResult() throws RemoteException {
		try {
			return results.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;

	}

	public void register(Computer computer) throws RemoteException {
		ComputerProxy aProxy = new ComputerProxy(computer, this);
		this.proxies.add(aProxy);
	}

	public static void main(String[] args) {

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {

			Space space = new SpaceImpl();
			Registry registry = LocateRegistry.createRegistry(PORT_NUMBER);
			registry.rebind(Space.SERVICE_NAME, space);
			System.out.println("Space instance bound");
		} catch (Exception e) {
			System.err.println("SpaceImpl exception:");
			e.printStackTrace();

		}
	}

	@Override
	public void run() {
		while(true){
		  synchronized(waitingTasks) { 
			  Set<Entry<String, Successor>> s = waitingTasks.entrySet();
		     for(Entry<String, Successor> e : s){
		    	if( e.getValue().getStatus() == Successor.Status.READY){
		    		
		    	}
		     }
		        
		  }

		}

	}

}
