package system;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import api.Space;
import api.Task;

public class Successor implements Runnable {

	private Thread t;
	private Status threadStatus;
	private String id;
	private SpaceImpl space;
	private Task<?> task;
	private Closure aClosure;
	
	
	public static enum Status{READY,WAITING};
	
	private Successor(int joinCounter){
		this.threadStatus=Status.WAITING;
		this.aClosure=new Closure(joinCounter);
	}
	
	public Successor(Task aTask, SpaceImpl spaceImpl, int joinCounter){
		this(joinCounter);
		this.space=spaceImpl;
		this.task=aTask;
		this.id=task.getId();
		t=new Thread(this,aTask.getId());
		
	}
	public void start(){
		t.start();
	}
	
	@Override
	public void run() {
		try {
			
			space.put(task);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	public synchronized Status getStatus() {
		return this.threadStatus;
		
	}
	
	public synchronized void setStatus(Status s) {
		this.threadStatus=s;
		
	}
	
	public String getId() {
		return this.id;
	}

	
	public class Closure {
		private List<Object> values;
		private int joinCounter;
		public Closure(int joinCounter){
			this.joinCounter=joinCounter;
			this.values=new Vector<Object>();
		}
		
		public void put(Object value){
			
			values.add(value);
			joinCounter--;
			if(this.joinCounter==0){
				setStatus(Status.READY);
			}
		}
	}


	public Closure getClosure() {
		return this.aClosure;
		
	}
}
