package system;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import system.Successor.Closure;
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
	private LinkedBlockingQueue<Task<?>> tasks;

	public ComputerProxy(Computer compObj, SpaceImpl space) {
		this.compObj = compObj;
		this.space = space;
		this.tasks = new LinkedBlockingQueue<Task<?>>();
		t = new Thread(this, "ComputerProxy " + getRandomProxyName());
		t.start();

	}

	public void run() {
		while (true) {
			if (!tasks.isEmpty()) {
				try {
					Task<?> aTask = tasks.take();
					Result<?> r = null;
					switch (aTask.getStatus()) {
					case DECOMPOSE:
						r = compObj.execute(aTask);

						if (r.getSubTasks() != null) {
							Successor s = new Successor(aTask, space,
									aTask.getDecompositionSize());
							space.addSuccessor(s);
							for (Task<?> task : r.getSubTasks()) {
								space.put(task);
							}
						} else if (r.getValue() != null) {
							if (aTask.getId().equals(aTask.getParentId())) {
								space.putResult(r);
							} else {

								Closure parentClosure = space.getClosure(aTask
										.getParentId());
								parentClosure.put(r.getValue());
							}
						}
						break;
					case COMPOSE:
						Closure taskClosure = space.getClosure(aTask.getId());
						r = compObj.execute(aTask, taskClosure.getValues());

						if (r.getValue() != null) {
							if (aTask.getId().equals(aTask.getParentId())) {
								space.putResult(r);
							} else {
								Closure parentClosure = space.getClosure(aTask
										.getParentId());
								parentClosure.put(r.getValue());
							}
						}
						space.removeSuccessor(aTask.getId());
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void addTask(Task<?> aTask) {
		try {
			this.tasks.put(aTask);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private String getRandomProxyName() {
		char first = (char) ((new Random().nextInt(26)) + 65);
		char second = (char) ((new Random().nextInt(26)) + 65);
		char third = (char) ((new Random().nextInt(26)) + 65);
		return "" + first + second + third;
	}

}
