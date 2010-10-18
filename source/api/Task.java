package api;

import java.util.List;

public interface Task<T> {
	enum Status {
		DECOMPOSE, COMPOSE
	};

	Result<T> decompose();

	Result<T> compose(List<?> list);

	Task.Status getStatus();

	void setStatus(Task.Status s);

	String getParentId();

	void setParentId(String id);

	String getId();

	void setId(String taskId);

	int getDecompositionSize();
	
	List<String> getChildIds();

	void setStartTime(long startTime);
	
	
}
