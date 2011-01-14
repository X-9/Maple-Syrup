package syrup;

import java.util.ArrayList;
import java.util.Collection;


abstract public class SpatialTable<V> extends ArrayList<V>{
	private static final long serialVersionUID = 1L;
	
	private static final int INIT_TABLE_SIZE = 10000;
	private ArrayList<V>[] nearby;
	
	abstract protected int generate(V value);
	
	public SpatialTable() {
		nearby = new ArrayList[INIT_TABLE_SIZE];
	}
	
	public void renew() {
		nearby = new ArrayList[INIT_TABLE_SIZE];
	}
	
	public ArrayList<V> nearby(V v) {
		int key = generate(v);
		if (null == nearby[key]) {
			throw new IllegalAccessError();
		}
		return nearby[key];
	}
	
	public boolean close(V v) {
		int key = generate(v);
		if (null == nearby[key]) {
			ArrayList<V> array = new ArrayList<V>(100);
			nearby[key] = array;
		}
		nearby[key].add(v);
		
		return true;
	}
}
