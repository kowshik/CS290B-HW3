package tasks;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import system.ResultImpl;
import api.Result;
import api.Task;

public class FibonacciTask<T> implements Task<Integer>, Serializable {

	
	private static final long serialVersionUID = -9046135328040176063L;
	private static final int NUMBER_OF_CHILDREN = 2;
	private int n;
	private String taskId;
	private String parentId;
	private Task.Status status;
	private long startTime;

	public FibonacciTask(int n) {
		this.n = n;
		this.setId("0-0");
		this.setParentId("0-0");
		this.setStatus(Task.Status.EXECUTE);
		this.startTime = System.currentTimeMillis();
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
			return new ResultImpl<Integer>(startTime,
					System.currentTimeMillis(), n);
		}
		List<Task<Integer>> subTasks = new Vector<Task<Integer>>();

		subTasks.add(new FibonacciTask<Integer>(n - 1, Task.Status.EXECUTE,
				this.generateLeftChildId(this.getId()), this.getId()));
		subTasks.add(new FibonacciTask<T>(n - 2, Task.Status.EXECUTE, this
				.generateRightChildId(this.getId()),this.getId()));
		this.setStatus(Task.Status.COMPOSE);
		return new ResultImpl<Integer>(startTime, System.currentTimeMillis(),
				subTasks);
	}

	@Override
	public Result<Integer> execute(List<Integer> list) {
		return this.compose(list);
	}

	public Result<Integer> compose(List<Integer> list) {
		int sum = 0;
		for (Integer value : list) {
			sum += value;
		}
		return new ResultImpl<Integer>(startTime, System.currentTimeMillis(),
				sum);
	}

	@Override
	public String getId() {
		return this.taskId;
	}

	@Override
	public void setId(String taskId) {
		this.taskId = taskId;

	}



	private String generateLeftChildId(String Id) {
		System.err.println("\n\nID : "+this.getId()+"\n\n");
		String[] parts = this.getId().split("-");
		int newLevel=Integer.parseInt(parts[0])+1;
		return newLevel+"-0";
	}

	private String generateRightChildId(String Id) {
		String[] parts = this.getId().split("-");
		int newLevel=Integer.parseInt(parts[0])+1;
		return newLevel+"-1";
	}

	@Override
	public Task.Status getStatus() {
		return status;
	}

	public void setStatus(Task.Status s) {
		this.status = s;

	}

	@Override
	public String getParentId() {
		return this.parentId;
	}

	@Override
	public void setParentId(String id) {
		this.parentId=id;
		
	}

	@Override
	public int getNumberOfChildren() {
		return this.NUMBER_OF_CHILDREN;
	}

}
