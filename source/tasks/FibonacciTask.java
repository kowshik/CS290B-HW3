package tasks;

import java.util.List;
import java.util.Vector;

import system.ResultImpl;
import api.Result;
import api.Task;

public class FibonacciTask<T> implements Task<Integer> {

	private int n;
	private String taskIdentifier;
	public FibonacciTask(int n) {
		this.n = n;
	}

	@Override
	public Result<Integer> execute() {
		long startTime = System.currentTimeMillis();
		if (n < 2) {
			return new ResultImpl<Integer>(startTime,
					System.currentTimeMillis(), n);
		}
		List<Task<Integer>> subTasks = new Vector<Task<Integer>>();
		subTasks.add(new FibonacciTask<T>(n - 1));
		subTasks.add(new FibonacciTask<T>(n - 2));
		return new ResultImpl<Integer>(startTime, System.currentTimeMillis(),
				subTasks);
	}

	@Override
	public Integer compose(List<Integer> list) {
		int sum = 0;
		for (Integer value : list) {
			sum += value;
		}
		return sum;
	}

	@Override
	public String getTaskIdentifier() {
		return this.taskIdentifier;
	}

	@Override
	public void setTaskIdentifier(String taskIdentifier) {
		this.taskIdentifier=taskIdentifier;
		
	}

}
