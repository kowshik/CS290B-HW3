package api;

import java.util.List;


public interface Result<T> {
	
	void setStartTime(long time);
	long getStartTime();
	
	void setEndTime(long time);
	long getEndTime();
	
	T getValue(); 	
	
	List<Task<T>> getSubTasks();


	
}
