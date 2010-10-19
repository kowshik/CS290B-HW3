package tasks;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import system.ResultImpl;
import api.Result;
import api.Task;

public class FibonacciTask extends TaskBase<Integer> implements
		Serializable {

	private static final long serialVersionUID = -9046135328040176063L;
	private static final int NUMBER_OF_CHILDREN = 2;
	private int n;

	
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
		this.setStatus(Task.Status.COMPOSE);
		return new ResultImpl<Integer>(this.getStartTime(),
				System.currentTimeMillis(), subTasks);
	}

	@Override
	public Result<Integer> compose(List<?> list) {
		int sum = 0;
		for (Integer value : (List<Integer>) list) {
			sum += value;
		}
		return new ResultImpl<Integer>(this.getStartTime(),
				System.currentTimeMillis(), sum);
	}

	@Override
	public int getDecompositionSize() {
		return FibonacciTask.NUMBER_OF_CHILDREN;
	}

	

}
