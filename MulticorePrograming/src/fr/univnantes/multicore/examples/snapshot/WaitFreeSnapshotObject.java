package fr.univnantes.multicore.examples.snapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitFreeSnapshotObject<T> implements SnapshotObject<T> {

	private class Cell {
		final T data;
		final List<T> helping;
		Cell(T data, List<T> helping) {
			this.data = data;
			this.helping = helping;
		}
	}
	
	final protected SnapshotObject<Cell> array = new CollectObject<Cell>();

	public void update(T value) {
		array.update(new Cell(value,snapshot()));
	}

	public List<T> snapshot() {
		List<Cell> collect2 = array.snapshot();
		boolean changed [] = {};
		boolean found = false;
		while(!found) {
			found = true;
			List<Cell> collect1 = collect2;
			collect2 = array.snapshot();
			if(collect2.size() > changed.length) {
				found = false;
				changed = Arrays.copyOf(changed, collect2.size());
			}
			for(int i = 0; i < collect1.size(); ++i) {
				if(collect1.get(i) != collect2.get(i)) {
					found = false;
					if(changed[i]) 
						return collect2.get(i).helping;
					changed[i] = true;
				}
			}
		}
		return getData(collect2);
	}

	public T read() {
		Cell c = array.read();
		if(c == null) {
			array.update(new Cell(null, null));
			return null;
		}
		return c.data;
	}
	
	private List<T> getData(List<Cell> collect) {
		List<T> data = new ArrayList<T>();
		for(Cell c : collect) {
			if(c!=null)	data.add(c.data);
		}
		return data;
	}
}
