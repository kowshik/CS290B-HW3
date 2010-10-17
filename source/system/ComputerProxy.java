package system;

import java.rmi.RemoteException;
import java.util.Random;

import api.Result;
import api.Task;

/**
 * For every {@link system.Computer Computer} instance that registers with the
 * compute space ({@link api.Space Space}), there is a proxy maintained by the
 * space. This allows the compute space to maintain multiple threads for each
 * instance of registered {@link system.Computer Computer} objects. This class
 * is responsible for execution of {@link api.Task Task} objects in the
 * registered remote computers.
 * 
 * @author Manasa Chandrasekhar
 * @author Kowshik Prakasam
 * 
 */
public class ComputerProxy implements Runnable {

	private Computer compObj;
	private SpaceImpl space;
	private Thread t;

	
	public ComputerProxy(Computer compObj, SpaceImpl space) {
		this.compObj = compObj;
		this.space = space;
		t = new Thread(this, "ComputerProxy " + getRandomProxyName());
		t.start();
	}

	
	
	
	public void run() {
		

	}


	public void addTask(Task<?> aTask) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private String getRandomProxyName() {
		char first = (char) ((new Random().nextInt(26)) + 65);
		char second = (char) ((new Random().nextInt(26)) + 65);
		char third = (char) ((new Random().nextInt(26)) + 65);
		return "" + first + second + third;
	}

}




