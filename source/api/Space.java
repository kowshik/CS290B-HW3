package api;

/**
 * Represents a raw computing resource where tasks ({@link api.Task Task}) are
 * automatically executed by registered workers as soon as they are dropped in.
 * If a worker crashes, the computation would still continue (assuming there are
 * other workers still running), since each task is executed under a
 * transaction, which would be rolled back after the worker crashed, leaving the
 * task in the space for another worker to pick up. For more information, please
 * refer <a href="http://today.java.net/pub/a/today/2005/04/21/farm.html">How to
 * build a compute farm</a>.
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public interface Space extends java.rmi.Remote {

	/**
	 * Name of the exposed service
	 */
	String SERVICE_NAME = "Space";

	/**
	 * The client decomposes a complex computation into smaller ones, each
	 * represented by a set of {@link api.Task Task} objects, and passes them to
	 * the Space via this method. In principle, these {@link api.Task Task}
	 * objects are processed in parallel by workers.
	 * 
	 * @param task task to be added to the space
	 * @throws java.rmi.RemoteException Thrown if any read/write errors occur during the process of adding a task to the queue
	 */

	void put(Task<?> task) throws java.rmi.RemoteException;

	/**
	 * After passing all the task objects to the {@link api.Space Space}, the
	 * client retrieves the associated {@link api.Result Result} objects via the
	 * take method. This method blocks until a {@link api.Result Result} object
	 * is available to return to the client. Thus, if the client sent 10
	 * {@link api.Task Task} objects to the {@link api.Space Space}, it could
	 * execute: <br>
	 * <br>
	 * 
	 * <pre>
	 * Result[] results = new Result[10];
	 * for (int i = 0; i &lt; results.length; i++) {
	 * 	results[i] = takeResult(); // waits for a result to become available.
	 * }
	 * </pre>
	 * 
	 * @return One of the results obtained upon completion of individual tasks
	 * @throws java.rmi.RemoteException
	 */
	Result<?> takeResult() throws java.rmi.RemoteException;
}