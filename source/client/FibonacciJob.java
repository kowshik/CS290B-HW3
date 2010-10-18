package client;

import java.rmi.RemoteException;

import tasks.FibonacciTask;
import api.Result;
import api.Space;
import api.Task;

public class FibonacciJob extends Job{

	private int n;
	private int fibValue;
	public FibonacciJob(int n){
		this.n=n;
	}
	@Override
	public void generateTasks(Space space) throws RemoteException {
		Task<Integer> fibTask=new FibonacciTask(this.n);
		space.put(fibTask);
	}

	@Override
	public void collectResults(Space space) throws RemoteException {
		Result<Integer> r=(Result<Integer>)space.takeResult();
		this.fibValue=r.getValue();
		
	}
	

	@Override
	public Integer getAllResults() {
		return fibValue;
	}
	

}
