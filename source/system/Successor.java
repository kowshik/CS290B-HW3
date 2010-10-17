package system;

public class Successor implements Runnable {

	private Thread t;
	private Status threadStatus;
	
	public static enum Status{READY,WAITING};
	
	
	public Successor(String name,Status status){
		t=new Thread(this,name);
		
	}
	public void start(){
		t.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	public Status getStatus() {
		return this.threadStatus;
		
	}

}
