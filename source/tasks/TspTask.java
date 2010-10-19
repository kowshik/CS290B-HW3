package tasks;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import system.ResultImpl;

import api.Task;
import api.Result;

/**
 * Computes an optimal solution for the <a
 * href="http://en.wikipedia.org/wiki/Travelling_salesman_problem">Travelling
 * Salesman Problem</a>
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 */
public final class TspTask extends TaskBase<int[]> implements Task<int[]>,
		Serializable {

	private static final long serialVersionUID = 3276207466199157936L;
	private double[][] cities;
	private int startCity;
	private int endCity;
	private int NUMBER_OF_LEVELS = 2;
	private long startTime;
	private int numberOfChildren;
	private int[] cityList;

	/**
	 * @param startCity
	 *            Starting city to be assumed in computation of the solution
	 * @param endCity
	 *            Ending city to be assumed in computation of the solution
	 * @param cities
	 *            Represents the x and y coordinates of cities. cities[i][0] is
	 *            the x-coordinate of city[i] and cities[i][1] is the
	 *            y-coordinate of city[i].
	 * @param taskIdentifier
	 *            A unique task identifier for this task
	 */

	
	public TspTask(int startCity, int endCity, double[][] cities) {
		super(DEFAULT_TASK_ID, DEFAULT_TASK_ID, Task.Status.DECOMPOSE, System
				.currentTimeMillis());
		this.startCity = startCity;
		this.endCity = endCity;
		this.cities = cities.clone();
		this.numberOfChildren=cities.length-1;
		this.cityList = new int[cities.length];
		for (int i=0;i<cities.length;i++)
			this.cityList[i]=i;

	}
	 

	private TspTask(int startCity, int endCity, double[][] cities,
			String taskId, String parentId, Task.Status s, Long startTime, int noOfChildren) {
		super(taskId, parentId, s, startTime);
		this.startCity = startCity;
		this.endCity = endCity;
		this.cities = cities.clone();
		this.startTime = System.currentTimeMillis();
		this.numberOfChildren = noOfChildren;
		this.cityList = new int[cities.length];
		for (int i=0;i<cities.length;i++){
			this.cityList[i]=i;
			
		}
	}



	/**
	 * 
	 * Generates the solution to the Travelling Salesman Problem.
	 * 
	 * @return An array containing cities in order that constitute an optimal
	 *         solution to TSP
	 * 
	 * @see api.Task Task
	 */
	public Result<int[]> decompose() {

		System.out.println("In tsp decompose");
		List<Task<int[]>> subTasks = new Vector<Task<int[]>>();
	//	int[] cityList = new int[cities.length - 1];

		int[] permutation = getFirstPermutation(this.startCity, this.endCity,
				this.cityList);

		// int numberOfChildren = permutation.length;
		System.out.println("got first permutation");
		// System.out.println("Permutation-length:"+permutation.length +
		// "Last element:"+ permutation[permutation.length]);
		if (this.getTaskLevel() < NUMBER_OF_LEVELS) {
			System.out.println("Task level:" + this.getTaskLevel());
			List<String> childids = this.getChildIds();

			for (int i = 0; i < childids.size(); i++) {

				String child = childids.remove(0);
				System.out.println("Childids: " + child);
				subTasks.add(new TspTask(cityList[i], this.startCity,
						cities, child, this.getId(), Task.Status.DECOMPOSE,
						this.startTime, this.numberOfChildren--));
				System.out.println("Added a new subtask :" + child);
			}
			
			this.setStatus(Task.Status.COMPOSE);
			return new ResultImpl<int[]>(startTime, System.currentTimeMillis(),
					subTasks);
		} else {
			
			int[] minRoute = null;
			double endToStartLength = findLength(cities[this.endCity][0],
					cities[this.endCity][1], cities[this.startCity][0],
					cities[this.startCity][1]);
			double minLength = Double.MAX_VALUE;
			do {
				double thisLength = 0;
				int i;
				for (i = 0; i < permutation.length - 1; i++) {
					thisLength += findLength(cities[permutation[i]][0],
							cities[permutation[i]][1],
							cities[permutation[i + 1]][0],
							cities[permutation[i + 1]][1]);
				}
				thisLength += findLength(cities[permutation[i]][0],
						cities[permutation[i]][1], cities[this.endCity][0],
						cities[this.endCity][1]);
				double startToNextLength = findLength(
						cities[this.startCity][0], cities[this.startCity][1],
						cities[permutation[0]][0], cities[permutation[0]][1]);

				thisLength += endToStartLength + startToNextLength;

				if (thisLength < minLength) {
					minLength = thisLength;
					minRoute = permutation.clone();
				}

			} while (nextPermutation(permutation));

			int[] fullMinRoute = Arrays.copyOf(minRoute, minRoute.length + 2);
			fullMinRoute[fullMinRoute.length - 2] = this.endCity;
			fullMinRoute[fullMinRoute.length - 1] = this.startCity;

			return new ResultImpl<int[]>(startTime, System.currentTimeMillis(),
					fullMinRoute);
		}
	}

	public Result<int[]> compose(List<?> list){
		int thisLength =0;
		double minLength = Double.MAX_VALUE;
		int[] minRoute = null;
		double endToStartLength = findLength(cities[this.endCity][0],
				cities[this.endCity][1], cities[this.startCity][0],
				cities[this.startCity][1]);
		
		for (int i = 0; i < list.size() - 1; i++) {
			int[] curList = (int[])list.remove(i);
			for(int j=0; j< curList.length-1; j++) {
			thisLength += findLength(cities[curList[j]][0],
					cities[curList[j]][1],
					cities[curList[j+1]][0],cities[curList[j+1]][1]);
			} 
		thisLength += findLength(cities[curList[i]][0],
				cities[curList[i]][1], cities[this.endCity][0],
				cities[this.endCity][1]);
		/*double startToNextLength = findLength(
				cities[this.startCity][0], cities[this.startCity][1],
				cities[curList[0]][0], cities[curList[0]][1]);*/

		thisLength += endToStartLength;

		if (thisLength < minLength) {
			minLength = thisLength;
			minRoute = curList.clone();
		}
		
		}
		return new ResultImpl<int[]>(startTime, System.currentTimeMillis(),
				minRoute);
		
	}

	private int[] getFirstPermutation(int startCity, int endCity,
			int[] cityList) {
		System.out.println("In first permutation");
		System.out.println("Start City"+startCity);
		System.out.println("Endcity"+endCity);
		//System.out.println("Cities length :"+cities.length);
		int[] firstPermutation = new int[cityList.length -1];
		int index = 0;
		for (int i = 0; i < cityList.length; i++) {
			System.out.println("In first permutation: cityList Length"+cityList.length);
			if (i != endCity && i != startCity) {
				firstPermutation[index] = cityList[i];
				//System.out.print("First Permutation:"+firstPermutation[index]+",");
				index++;
				//System.out.print("cityList:"+cityList[i]+",");

			}
		}
		Arrays.sort(firstPermutation);
		cityList = firstPermutation.clone();
		
		return firstPermutation;
	}

	/**
	 * Works with <a
	 * href='http://en.wikipedia.org/wiki/Permutation'>permutations</a> Accepts
	 * an array of <b>ints</b> and reorders it's elements to recieve
	 * lexicographically next permutation
	 * 
	 * @param p
	 *            permutation
	 * @return false, if given array is lexicographically last permutation, true
	 *         otherwise
	 */

	private boolean nextPermutation(int[] p) {
		int a = p.length - 2;
		while (a >= 0 && p[a] >= p[a + 1]) {
			a--;
		}
		if (a == -1) {
			return false;
		}
		int b = p.length - 1;
		while (p[b] <= p[a]) {
			b--;
		}
		int t = p[a];
		p[a] = p[b];
		p[b] = t;
		for (int i = a + 1, j = p.length - 1; i < j; i++, j--) {
			t = p[i];
			p[i] = p[j];
			p[j] = t;
		}
		return true;
	}

	/**
	 * Computes the distance between two points
	 */
	private double findLength(double x1, double y1, double x2, double y2) {
		return Point2D.distance(x1, y1, x2, y2);

	}

	/**
	 * Returns the unique identifier of this task
	 * 
	 * @see api.Task Task
	 */

	private int getTaskLevel() {
		String[] parts = this.getId().split(ID_DELIM);
		int level = Integer.parseInt(parts[0]);
		return level;
	}



	@Override
	public int getDecompositionSize() {
		
		return this.numberOfChildren;
	}

}
