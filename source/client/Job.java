package client;

import java.rmi.RemoteException;

import api.Space;

/**
 * Interface that defines a job to be executed remotely.
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public abstract class Job {

	/**
	 * Decomposes a complex job into a list of smaller tasks of type {@link api.Task Task}, each of which are executed remotely in a compute space ({@link
	 * api.Space Space})
	 * 
	 * @param space
	 *            Compute space to which {@link api.Task Task} objects should be
	 *            sent for execution
	 * @throws RemoteException
	 *             If the compute space throws RemoteException while writing
	 *             tasks to it, then the exception is in turn thrown by this
	 *             method
	 */
	public abstract void generateTasks(Space space) throws RemoteException;

	/**
	 * Gathers {@link api.Result Result} objects from the compute space. This
	 * method should be executed only after completion of
	 * {@link #generateTasks(Space) generateTasks(Space)} method that generates
	 * the results.
	 * 
	 * @param space
	 *            Compute space encapsulating the results obtained after remote
	 *            execution of tasks
	 * @throws RemoteException
	 */
	public abstract void collectResults(Space space) throws RemoteException;

	/**
	 * Transforms values returned by {@link api.Result Result} objects into
	 * something relevant to the subclass implementing this interface.
	 * 
	 * @return A container with values from all {@link api.Result Result}
	 *         objects
	 */
	public abstract Object getAllResults();

}
