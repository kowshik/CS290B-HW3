package api;

import java.util.List;

public interface Task<T> {
	enum Status {
		DECOMPOSE, COMPOSE
	};

	Result<T> execute();

	Result<T> compose(List<?> list);

	Task.Status getStatus();

	void setStatus(Task.Status s);

	String getParentId();

	void setParentId(String id);

	String getId();

	void setId(String taskId);

	int getDecompositionSize();

	void setStartTime(long startTime);
	
	
}
