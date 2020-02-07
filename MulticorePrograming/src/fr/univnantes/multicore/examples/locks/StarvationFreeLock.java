package fr.univnantes.multicore.examples.locks;

import java.util.concurrent.atomic.AtomicInteger;

public class StarvationFreeLock {

	private AtomicInteger rank = new AtomicInteger(0);
	private volatile int next = 0;

	public void lock() {
		int mine = rank.getAndIncrement();
		while(mine != next);
	}

	public void unlock() {
		next++;
	}
}
