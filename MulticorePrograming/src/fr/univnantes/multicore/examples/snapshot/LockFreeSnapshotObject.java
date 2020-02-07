package fr.univnantes.multicore.examples.snapshot;

import java.util.List;

public class LockFreeSnapshotObject<T> implements SnapshotObject<T> {
		
	private final SnapshotObject<T> array = new CollectObject<T>();
	
	public void update(T value) {
		array.update(value);
	}

	public T read() {
		return array.read();
	}

	public List<T> snapshot() {
		List<T> collect1 = array.snapshot();
		List<T> collect2 = array.snapshot();
	    while (!collect1.equals(collect2)) {
	      collect1 = collect2;
	      collect2 = array.snapshot();
	    } 
	    return collect1;
	}

}

