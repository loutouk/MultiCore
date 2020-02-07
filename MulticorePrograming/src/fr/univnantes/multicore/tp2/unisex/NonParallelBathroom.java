package fr.univnantes.multicore.tp2.unisex;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NonParallelBathroom implements Bathroom {

	Lock lock = new ReentrantLock();

	public NonParallelBathroom(int nbBathroom) { }

	@Override
	public void enter(Person person) {
		lock.lock();
	}

	@Override
	public void leave(Person person) {
		lock.unlock();
	}

}
