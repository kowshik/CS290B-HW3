package client;

import java.awt.geom.Point2D;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import tasks.TspTask;
import api.Result;
import api.Space;
import api.Task;

/**
 * Defines a Travelling Salesman Problem through the generic @{link client.Job
 * Job} interface
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public class TspJob extends Job {

	private double[][] cities;
	private int numOfChildren;
	private int[] minRoute;
	private String taskId;
	private int parentId;

	// Used to time the execution of methods for profiling
	private Map<String, Long> timeMap;

	/**
	 * @param cities
	 *            Represents the x and y coordinates of cities. cities[i][0] is
	 *            the x-coordinate of city[i] and cities[i][1] is the
	 *            y-coordinate of city[i].
	 */
	public TspJob(final double[][] cities) {
		this.cities = cities.clone();
		this.timeMap = new HashMap<String, Long>();
		//this.numOfChildren = cities.length-1;;
	}

	/**
	 * Decomposes the Travelling Salesman Problem computation into into a list
	 * of smaller tasks of type @{link tasks.TspTask TspTask}, each of which are
	 * executed remotely in a compute space ({@link api.Space Space})
	 * 
	 * @param space
	 *            Compute space to which @{link tasks.TspTask TspTask} objects
	 *            should be sent for execution
	 * @throws RemoteException
	 * 
	 * @see client.Job Job
	 */
	public void generateTasks(Space space) throws RemoteException {
		int endCity = 0;

		int startCity=endCity;
			Task<int[]> aTspTask = new TspTask(startCity, endCity, cities);
			timeMap.put(taskId, System.currentTimeMillis());
			space.put(aTspTask);
			
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
		 
			Result<int[]> r = (Result<int[]>) space.takeResult();
			
			
			int[] route = r.getValue();
			this.minRoute = route;
	}

	/**
	 * Returns values cached by {@link #collectResults(Space)
	 * collectResults(Space space)} method. Each value in the returned array
	 * represents the a city index in the optimal solution to the Travelling
	 * Salesman Problem.
	 * 
	 * @return An array that contains cities from all {@link api.Result Result}
	 *         objects that form an optimal solution to TSP
	 * @see client.Job Job
	 */
	@Override
	public int[] getAllResults() {
		return this.minRoute;
	}

	

}
