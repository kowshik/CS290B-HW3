package api;

import java.util.List;



public interface Task<T> {
	
	enum Status{EXECUTE, COMPOSE};
	
	Result<T> execute();
	
	Result<T> execute(List<T> list);
	
	String getId();
	void setId(String id);
	
	String getParentId();
	void setParentId(String id);
	
	int getNumberOfChildren();
	
	
	
	Status getStatus();
	void setStatus(Task.Status s);
}
