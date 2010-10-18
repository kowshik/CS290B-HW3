package tasks;

import java.util.List;

import api.Result;
import api.Task;

public abstract class TaskBase<T> implements Task<T> {

	private Status status;
	private String parentId;
	private String taskId;
	private long startTime;
	
	public TaskBase(String taskId, String parentId, Status status, long startTime){
		setId(taskId);
		setParentId(parentId);
		setStatus(status);
		setStartTime(startTime);
	}
	@Override
	public abstract Result<T> execute();

	@Override
	public abstract Result<T> compose(List<?> list);

	@Override
	public abstract int getDecompositionSize();
	
	@Override
	public Task.Status getStatus() {
		return status;
	}

	@Override
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
	public String getId() {
		return this.taskId;
	}
	
	@Override
	public void setId(String taskId) {
		this.taskId=taskId;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}
	
	
	
}
