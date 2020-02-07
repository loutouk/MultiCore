package fr.univnantes.multicore.examples.snapshot;

import java.util.List;

public interface SnapshotObject<T> {
	public void update(T value);
	public T read();
	public List<T> snapshot();
}
