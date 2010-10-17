package api;

import java.util.List;



public interface Task<T> {
	
	Result<T> execute();
	
	T compose(List<T> list);
	
	String getTaskIdentifier();
	void setTaskIdentifier(String id);
	
}
