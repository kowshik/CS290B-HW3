package system;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import system.Successor.Closure;
import api.Result;
import api.Task;

/**
 * For every {@link system.Computer Computer} instance that registers with the
 * compute space ({@link api.Space Space}), there is a proxy maintained by the
 * space. This allows the compute space to maintain multiple threads for each
 * instance of registered {@link system.Computer Computer} objects. This class
 * is responsible for execution of {@link api.Task Task} objects in the
 * registered remote computers.
 * 
 * Each proxy maintains a queue of tasks that need to be executed one after the
 * other on a remote machine. These tasks can either represent the Divide phase
 * or the Conquer phase in the <a
 * href="http://en.wikipedia.org/wiki/Divide_and_conquer_algorithm">Divide and
 * conquer algorithm</a>.
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public class ComputerProxy implements Runnable {
	private static final String LOG_FILE_PREFIX = "/cs/student/kowshik/computerproxy_";
	private Computer compObj;
	private SpaceImpl space;
	private Thread t;
	private LinkedBlockingQueue<Task<?>> tasks;
	private String id;
	private Logger logger;
	private Handler handler;

	/**
	 * 
	 * @param compObj
	 *            Computer registed with the compute space ({@link api.Space
	 *            Space})
	 * @param space
	 *            Implementation of ({@link api.Space Space}) which is
	 *            responsible for maintaining each instance of this class
	 */
	public ComputerProxy(Computer compObj, SpaceImpl space) {
		this.compObj = compObj;
		this.space = space;
		this.tasks = new LinkedBlockingQueue<Task<?>>();
		this.id = new Random().nextInt() + "";
		this.logger = Logger.getLogger("ComputerProxy" + id);
		this.logger.setUseParentHandlers(false);
		this.handler = null;
		try {
			this.handler = new FileHandler(LOG_FILE_PREFIX + id + ".log");

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
		t = new Thread(this, "ComputerProxy " + getRandomProxyName());

		t.start();

	}

	/**
	 * Loops infinitely and attempts to fetch a {@link api.Task Task} object
	 * from the proxy's queue and executes it. If the thread is interrupted, the
	 * task is returned to the compute space's queue. If the task execution is
	 * successful, then the {@link api.Result Result} produced is also added to
	 * compute space's queue of {@link api.Result Result} objects.
	 * 
	 * These tasks can either represent the Divide phase or the Conquer phase in
	 * the <a
	 * href="http://en.wikipedia.org/wiki/Divide_and_conquer_algorithm">Divide
	 * and conquer algorithm</a>. Divide tasks are represented by the DECOMPOSE
	 * status and Conquer tasks are represented by the CONQUER status. The proxy
	 * switches the status of the task to COMPOSE, immediately after the Divide
	 * phase is over.
	 */
	public void run() {
		while (true) {
			if (!tasks.isEmpty()) {
				Task<?> aTask = null;
				try {

					aTask = tasks.take();
					Result<?> r = null;
					switch (aTask.getStatus()) {
					case DECOMPOSE:
						r = compObj.decompose(aTask);
						if (r.getSubTasks() != null) {
							Successor s = new Successor(aTask, space,
									aTask.getDecompositionSize());
							space.addSuccessor(s);

							for (Task<?> task : r.getSubTasks()) {
								space.put(task);
							}
						} else if (r.getValue() != null) {
							if (aTask.getId().equals(aTask.getParentId())) {
								space.putResult(r);
								logger.info("Elapsed Time="
										+ (r.getEndTime() - r.getStartTime()));

							} else {

								Closure parentClosure = space.getClosure(aTask
										.getParentId());
								parentClosure.put(r.getValue());
								logger.info("Elapsed Time="
										+ (r.getEndTime() - r.getStartTime()));

							}
						}
						aTask.setStatus(Task.Status.COMPOSE);
						break;
					case COMPOSE:
						Closure taskClosure = space.getClosure(aTask.getId());
						r = compObj.compose(aTask, taskClosure.getValues());

						if (r.getValue() != null) {
							if (aTask.getId().equals(aTask.getParentId())) {
								space.putResult(r);
							} else {
								Closure parentClosure = space.getClosure(aTask
										.getParentId());
								parentClosure.put(r.getValue());
							}
						}
						space.removeSuccessor(aTask.getId());
						logger.info("Elapsed Time="
								+ (r.getEndTime() - r.getStartTime()));
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					System.err
							.println("ComputerProxy : RemoteException occured in thread : "
									+ this.t.getName());
					System.err.println("Reassigning task to task queue");
					try {
						space.put(aTask);
					} catch (RemoteException ex) {
						ex.printStackTrace();
					}
				}

			}
		}
	}

	/**
	 * 
	 * @param aTask
	 *            A task to be added to this proxy's queue
	 */
	public void addTask(Task<?> aTask) {
		try {
			this.tasks.put(aTask);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return A random thread name made up of exactly three alphabets
	 */
	private String getRandomProxyName() {
		char first = (char) ((new Random().nextInt(26)) + 65);
		char second = (char) ((new Random().nextInt(26)) + 65);
		char third = (char) ((new Random().nextInt(26)) + 65);
		return "" + first + second + third;
	}

}
