package tasks;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import system.ResultImpl;
import api.Result;
import api.Task;

/**
 * Computes the Nth fibonacci number
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 */
public class FibonacciTask extends TaskBase<Integer> implements Serializable {

	private static final long serialVersionUID = -9046135328040176063L;
	private static final int NUMBER_OF_CHILDREN = 2;
	private int n;

	/**
	 * 
	 * @param n
	 *            Nth fibonacci number to be computed
	 */
	public FibonacciTask(int n) {
		super(DEFAULT_TASK_ID, DEFAULT_TASK_ID, Task.Status.DECOMPOSE, System
				.currentTimeMillis());
		this.n = n;
	}

	private FibonacciTask(int n, Task.Status s, String taskId, String parentId) {
		this(n);
		init(s, taskId, parentId);
	}

	@Override
	/**
	 * Implements the decompose phase of fibonacci generation
	 */
	public Result<Integer> decompose() {
		if (n < 2) {
			return new ResultImpl<Integer>(this.getStartTime(),
					System.currentTimeMillis(), n);
		}
		List<Task<Integer>> subTasks = new Vector<Task<Integer>>();
		int decrement = 1;
		for (String id : this.getChildIds()) {
			subTasks.add(new FibonacciTask(n - decrement,
					Task.Status.DECOMPOSE, id, this.getId()));
			decrement++;
		}
		return new ResultImpl<Integer>(this.getStartTime(),
				System.currentTimeMillis(), subTasks);
	}

	@Override
	/**
	 * Implements the conquer phase of fibonacci generation
	 */
	public Result<Integer> compose(List<?> list) {
		int sum = 0;
		for (Integer value : (List<Integer>) list) {
			sum += value;
		}
		return new ResultImpl<Integer>(this.getStartTime(),
				System.currentTimeMillis(), sum);
	}

	/**
	 * Number of subtasks created in each stage of recursion
	 */
	@Override
	public int getDecompositionSize() {
		return FibonacciTask.NUMBER_OF_CHILDREN;
	}

}
