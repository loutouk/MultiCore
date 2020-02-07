package fr.univnantes.multicore.examples;

import java.util.ArrayList;
import java.util.List;

//Fair Lock implementation with nested monitor lockout problem
// Credit: http://tutorials.jenkov.com/java-concurrency/nested-monitor-lockout.html

public class NestedLockout implements Runnable {

	private String name;

	public NestedLockout(String name) {
		this.name = name;
	}

	public void run() {
		try {
			lock.lock();
			System.out.println(name + " starts SC");
			Thread.sleep(100);
			System.out.println(name + " ends SC");
			lock.unlock();
		} catch (InterruptedException e) {e.printStackTrace();}
	}

	public static FairLock lock = new FairLock();

	public static void main( String[] args ) {
		new Thread(new NestedLockout("A")).start();
		new Thread(new NestedLockout("B")).start();
	}
}

class FairLock2 {
	  List<Thread> waitingThreads = new ArrayList<Thread>();
	  
	  public synchronized void lock() throws InterruptedException {
	    synchronized(waitingThreads){
	      waitingThreads.add(Thread.currentThread());
	      while(waitingThreads.get(0) != Thread.currentThread())
	        waitingThreads.wait();
	    }
	  }

	  public synchronized void unlock(){
	    synchronized(waitingThreads){
	      waitingThreads.notify();
	    }
	  }
	}



class FairLock {
	private boolean           isLocked       = false;
	private Thread            lockingThread  = null;
	private List<Object> waitingThreads = new ArrayList<Object>();

	public void lock() throws InterruptedException{
		Object queueObject = new Object();
		synchronized(this){
			waitingThreads.add(queueObject);
			while(isLocked || waitingThreads.get(0) != queueObject){
				synchronized(queueObject){
					try{
						queueObject.wait();
					}catch(InterruptedException e){
						waitingThreads.remove(queueObject);
						throw e;
					}
				}
			}
			waitingThreads.remove(queueObject);
			isLocked = true;
			lockingThread = Thread.currentThread();
		}
	}

	public synchronized void unlock(){
		if(this.lockingThread != Thread.currentThread()){
			throw new IllegalMonitorStateException("Calling thread has not locked this lock");
		}
		isLocked      = false;
		lockingThread = null;
		if(waitingThreads.size() > 0){
			Object queueObject = waitingThreads.get(0);
			synchronized(queueObject){
				queueObject.notify();
			}
		}
	}
}
