package tasks;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import system.ResultImpl;
import api.Result;
import api.Task;

public class FibonacciTask extends TaskBase<Integer> implements Task<Integer>,
		Serializable {

	private static final long serialVersionUID = -9046135328040176063L;
	private static final int NUMBER_OF_CHILDREN = 2;
	private int n;

	public FibonacciTask(int n) {
		super("0-0","0-0",Task.Status.DECOMPOSE,System.currentTimeMillis());
		this.n = n;
	}

	private FibonacciTask(int n, Task.Status s, String taskId, String parentId) {
		this(n);
		this.setStatus(s);
		this.setId(taskId);
		this.setParentId(parentId);
	}

	@Override
	public Result<Integer> execute() {
		return this.decompose();
	}

	private Result<Integer> decompose() {
		if (n < 2) {
			return new ResultImpl<Integer>(this.getStartTime(),
					System.currentTimeMillis(), n);
		}
		List<Task<Integer>> subTasks = new Vector<Task<Integer>>();

		subTasks.add(new FibonacciTask(n - 1, Task.Status.DECOMPOSE, this
				.generateLeftChildId(this.getId()), this.getId()));
		subTasks.add(new FibonacciTask(n - 2, Task.Status.DECOMPOSE, this
				.generateRightChildId(this.getId()), this.getId()));
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

	private String generateLeftChildId(String Id) {
		String[] parts = this.getId().split("-");
		int newLevel = Integer.parseInt(parts[0]) + 1;
		int childNumber = (Integer.parseInt(parts[1])) * getDecompositionSize();
		return newLevel + "-" + childNumber;
	}

	private String generateRightChildId(String Id) {
		String[] parts = this.getId().split("-");
		int newLevel = Integer.parseInt(parts[0]) + 1;
		int childNumber = (Integer.parseInt(parts[1])) * getDecompositionSize()
				+ 1;
		return newLevel + "-" + childNumber;
	}

	@Override
	public int getDecompositionSize() {
		return FibonacciTask.NUMBER_OF_CHILDREN;
	}

}
