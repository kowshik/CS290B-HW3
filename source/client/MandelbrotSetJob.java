package client;

import java.rmi.RemoteException;

import tasks.MandelbrotSetTask;
import api.Result;
import api.Space;
import api.Task;

/**
 * A job to perform remote computation of <a
 * href="http://en.wikipedia.org/wiki/Mandelbrot_set">Mandelbrot Set</a> by
 * splitting it up into smaller tasks of type {@link api.Task task}
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public class MandelbrotSetJob extends Job {

	private double lowerX;
	private double lowerY;
	private double edgeLength;
	private int n;
	private int iterLimit;

	private int[][] allValues;

	/**
	 * 
	 * @param lowerX
	 *            X-coordinate of the lower left corner of a square in the
	 *            complex plane
	 * @param lowerY
	 *            Y-coordinate of the lower left corner of a square in the
	 *            complex plane
	 * @param edgeLength
	 *            Edge length of the square in the complex plane, whose sides
	 *            are parallel to the axes
	 * @param n
	 *            Square region of the complex plane subdivided into n X n
	 *            squares, each of which is visualized by 1 pixel
	 * @param iterLimit
	 *            Defines when the representative point of a region is
	 *            considered to be in the Mandelbrot set.
	 */
	public MandelbrotSetJob(double lowerX, double lowerY, double edgeLength,
			int n, int iterLimit) {
		super();
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.edgeLength = edgeLength;
		this.n = n;
		this.iterLimit = iterLimit;

	}

	/**
	 * Decomposes the Mandelbrot Set computation into into a list of smaller
	 * tasks of type {@link tasks.MandelbrotSetTask MandelbrotSetTask}, each of
	 * which are executed remotely in a compute space ({@link api.Space Space})
	 * 
	 * @param space
	 *            Compute space to which @{link tasks.MandelbrotSetTask
	 *            MandelbrotSetTask} objects should be sent for execution
	 * @throws RemoteException
	 * 
	 * @see client.Job Job
	 */
	public void generateTasks(Space space) throws RemoteException {

		Task<MandelbrotSetTask.MandelbrotSetChunk> aMandelbrotSetTask = new MandelbrotSetTask(
				lowerX, lowerY, edgeLength, n, iterLimit);
		space.put(aMandelbrotSetTask);

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
	public void collectResults(Space space) throws RemoteException {
		Result<MandelbrotSetTask.MandelbrotSetChunk> r = (Result<MandelbrotSetTask.MandelbrotSetChunk>) space
				.takeResult();
		this.allValues=r.getValue().getValues();

	}

	/**
	 * Returns values cached by {@link #collectResults(Space)
	 * collectResults(Space space)} method. Each value in the returned array
	 * represents the colour of a pixel to be displayed on the screen to
	 * represent the Mandelbrot Set.
	 * 
	 * @return An array that contains Mandelbrot Set integer values from all
	 *         {@link api.Result Result} objects
	 * @see client.Job Job
	 */
	public int[][] getAllResults() {
		return this.allValues;
	}

}
