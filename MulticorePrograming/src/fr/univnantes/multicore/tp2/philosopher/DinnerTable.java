package fr.univnantes.multicore.tp2.philosopher;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DinnerTable {

	private Lock locks[];
	private int takenBy[];

	public DinnerTable(int nbPhilosophers) {
		takenBy = new int[nbPhilosophers];
		locks = new Lock[nbPhilosophers];
		for(int i = 0; i<nbPhilosophers; i++) {
			takenBy[i] = -1;
			locks[i] = new ReentrantLock(true);
		}
	}

	public int numberOfGuests() {
		return takenBy.length;
	}

	// No synchronization needed, as takenBy[philosopherId] is write-protected by locks[philosopherId]
	private void takeStick(int philosopherId, int fork) throws InterruptedException {
		locks[fork].lock();
		takenBy[fork] = philosopherId;
		// Taking a fork takes some time...
		Thread.sleep(5);
	}

	private void dropStick(int philosopherId, int fork) {
		if(takenBy[fork] == philosopherId) {
			takenBy[fork] = -1;
			locks[fork].unlock();
		}
	}

	public int nbSticks(int philosopherId) {
		int result = 0;
		if(takenBy[philosopherId] == philosopherId)
			result++;
		if(takenBy[(philosopherId+1)%takenBy.length] == philosopherId)
			result++;
		return result;
	}

	public void takeLeftStick(int philosopherId) throws InterruptedException {
		System.out.println("Philosopher " + philosopherId + " wants left fork");
		takeStick(philosopherId, philosopherId);
		System.out.println("Philosopher " + philosopherId + " has left fork");
	}

	public void takeRightStick(int philosopherId) throws InterruptedException {
		System.out.println("Philosopher " + philosopherId + " wants right fork");
		takeStick(philosopherId, (philosopherId+1)%takenBy.length);
		System.out.println("Philosopher " + philosopherId + " has right fork");
	}

	public void dropLeftStick(int philosopherId) {
		System.out.println("Philosopher " + philosopherId +  " drops left fork");
		dropStick(philosopherId, philosopherId);
	}

	public void dropRightStick(int philosopherId) {
		System.out.println("Philosopher " + philosopherId +  " drops right fork");
		dropStick(philosopherId, (philosopherId+1)%takenBy.length);
	}
}
