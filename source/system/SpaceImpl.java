package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
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
 * where tasks ({@link api.Task Task}) are automatically executed by registered
 * workers as soon as they are dropped in. If a worker crashes, the computation
 * would still continue (assuming there are other workers still running), since
 * each task is executed under a transaction, which would be rolled back after
 * the worker crashed, leaving the task in the space for another worker to pick
 * up. For more information, please refer <a
 * href="http://today.java.net/pub/a/today/2005/04/21/farm.html">How to build a
 * compute farm</a>.
 * 
 * The multithreading design implemented by this class is that of the <a
 * href="http://en.wikipedia.org/wiki/Cilk">Cilk</a> runtime. Please read the
 * architecture of Cilk to understand the class better.
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public class SpaceImpl extends UnicastRemoteObject implements Space,
		Computer2Space, Runnable {

	private Thread t;
	private static final long serialVersionUID = 3093568798450948074L;
	private Map<String, Successor> waitingTasks;
	private LinkedBlockingQueue<Result<?>> results;
	private List<ComputerProxy> proxies;
	private static final int PORT_NUMBER = 2672;

	/**
	 * Default constructor
	 * 
	 * @throws RemoteException
	 */
	public SpaceImpl() throws RemoteException {

		this.waitingTasks = Collections
				.synchronizedMap(new HashMap<String, Successor>());
		this.results = new LinkedBlockingQueue<Result<?>>();
		this.proxies = Collections
				.synchronizedList(new Vector<ComputerProxy>());
		t = new Thread(this, "Space");
		t.start();
	}

	@Override
	/**
	 *  Remote method used by the clients to add tasks to the queue of tasks in this compute space.
	 *  This method is thread-safe and blocks during concurrent calls to this method by clients running simultaneously (or) {@link system.ComputerProxy ComputerProxy} objects trying to return {@link api.Result Result} objects to this compute space.
	 *  @throws RemoteException
	 */
	public void put(Task<?> aTask) throws RemoteException {
		if (proxies.size() > 0) {
			int random = new Random().nextInt(this.proxies.size());
			proxies.get(random).addTask(aTask);
			return;

		}

		System.err
				.println("Unable to register tasks due to absence of computer proxies");

	}

	/**
	 * Used to add to the queue of {@link api.Result Result} objects in this
	 * compute space
	 * 
	 * @throws RemoteException
	 */
	public void putResult(Result<?> result) throws RemoteException {
		results.add(result);
	}

	@Override
	/** 
	 * Remote method for the clients to fetch results from the compute space. This method is thread-safe and blocks until a {@link api.Result Result} is added to the queue by Computer Proxies.
	 * 
	 * @return A generic result from the beginning of the queue
	 * @throws RemoteException
	 */
	public Result<?> takeResult() throws RemoteException {
		try {
			return results.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Remote method for the computers to register to the compute space
	 * 
	 * @throws RemoteException
	 */
	@Override
	public void register(Computer computer) throws RemoteException {
		ComputerProxy aProxy = new ComputerProxy(computer, this);
		this.proxies.add(aProxy);
	}

	/**
	 * Starts the compute space and binds remote objects into the RMI registry
	 * 
	 * @param args
	 *            Command-line arguments can be passed (if any)
	 */
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

	/**
	 * Polls for {@link system.Successor Successor} threads to be execute once
	 * they move into READY status
	 */
	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				Set<Entry<String, Successor>> successorSet = waitingTasks
						.entrySet();
				for (Entry<String, Successor> e : successorSet) {
					Successor s = e.getValue();
					if (s.getStatus() == Successor.Status.READY) {
						s.start();
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param s
	 *            Successor thread to be added to the queue
	 */
	public void addSuccessor(Successor s) {
		synchronized (this) {
			waitingTasks.put(s.getId(), s);
		}

	}

	/**
	 * 
	 * @param successorId Successor thread to be removed from the queue
	 */
	public void removeSuccessor(String successorId) {
		synchronized (this) {
			waitingTasks.remove(successorId);
		}

	}

	/**
	 * 
	 * @param id ID of the successor thread whose Closure object is required
	 * @return Gets the closure object corresponding to the Successor thread.
	 */
	public Successor.Closure getClosure(String id) {
		synchronized (this) {
			return waitingTasks.get(id).getClosure();
		}

	}
}
