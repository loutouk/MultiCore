package fr.univnantes.multicore.examples.locks;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpinLock {

	private AtomicBoolean taken = new AtomicBoolean(false);

	public void lock() {
		while(taken.getAndSet(true))
			while(taken.get());
	}

	public void unlock() {
		taken.set(false);
	}
}
