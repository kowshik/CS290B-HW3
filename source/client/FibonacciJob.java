package client;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import tasks.FibonacciTask;
import api.Result;
import api.Space;
import api.Task;

/**
 * A job to perform remote computation of Nth Fibonacci number
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */

public class FibonacciJob extends Job {

	private static final String LOG_FILE = "/cs/student/kowshik/fibonacci_job.log";

	private Logger logger;
	private Handler handler;

	private int n;
	private int fibValue;
	private long startTime;

	/**
	 * 
	 * @param n
	 *            The Nth fibonacci number to be generated
	 */
	public FibonacciJob(int n) {

		this.n = n;

		this.logger = Logger.getLogger("FibonacciJob");
		this.logger.setUseParentHandlers(false);
		this.handler = null;
		try {
			this.handler = new FileHandler(LOG_FILE);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.handler.setFormatter(new SimpleFormatter());
		logger.addHandler(handler);
	}
	/**
	 * Executed fibonacci computation in a compute space ({@link api.Space Space})
	 * 
	 * @param space
	 *            Compute space to which @{link tasks.FibonacciTask
	 *            FibonacciTask} objects should be sent for execution
	 * @throws RemoteException
	 * 
	 * @see client.Job Job
	 */
	@Override
	public void generateTasks(Space space) throws RemoteException {
		Task<Integer> fibTask = new FibonacciTask(this.n);
		this.startTime = System.currentTimeMillis();
		space.put(fibTask);
	}
	/**
	 * Gathers {@link api.Result Result} objects from the compute space and
	 * caches them in a simple data structure that can be quickly retrieved by
	 * the client through the {@link #getAllResults getAllResults()} method
	 * 
	 * @param space
	 *            Compute space containing the results obtained after remote
	 *            execution of tasks
	 * @throws RemoteException
	 * @see client.Job Job
	 */
	@Override
	public void collectResults(Space space) throws RemoteException {
		Result<Integer> r = (Result<Integer>) space.takeResult();
		this.fibValue = r.getValue();
		logger.info("Elapsed Time=" + (System.currentTimeMillis() - startTime));
		this.handler.close();

	}

	/**
	 * Returns values representing the Nth fibonacci number that was cached by {@link #collectResults(Space)
	 * collectResults(Space space)} method. 
	 * 
	 * @return Nth fibonacci number
	 * 
	 * @see client.Job Job
	 */
	@Override
	public Integer getAllResults() {
		return fibValue;
	}

}
