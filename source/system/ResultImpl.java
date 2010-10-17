package system;

import java.io.Serializable;
import java.util.List;

import api.Result;
import api.Task;


public class ResultImpl<T> implements Result<T>, Serializable {

	private static final long serialVersionUID = -7688137730920618986L;
	private long startTime;
	private long endTime;
	private T result;
	private List<Task<T>> subTasks;
	
	private ResultImpl(long startTime, long endTime){
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public ResultImpl(long startTime, long endTime, T result) {
		this(startTime,endTime);
		this.result = result;
	
	}

	
	public ResultImpl(long startTime, long endTime, List<Task<T>> subTasks) {
		this(startTime,endTime);
		this.subTasks = subTasks;
	}
	
	
	/**
	 * @return Returns the start time of the task
	 * 
	 * 
	 */
	@Override
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * @return Returns the end time of the task
	 * 
	 * 
	 */
	@Override
	public void setEndTime(long time) {
		this.endTime=time;
	}
	
	/**
	 * @return Returns the end time of the task
	 * 
	 * 
	 */
	@Override
	public long getEndTime() {
		return this.endTime;
	}

	/**
	 * @return Returns the value computed by the task
	 * 
	 * 
	 */
	@Override
	public T getValue() {
		return this.result;
	}

	@Override
	public List<Task<T>> getSubTasks() {
		return this.subTasks;
	}
	@Override
	public void setStartTime(long time) {
		this.startTime=time;
		
	}
	
}
