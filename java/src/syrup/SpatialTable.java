package syrup;

import java.util.ArrayList;
import java.util.Collection;


abstract public class SpatialTable<V> extends ArrayList<V>{
	private static final long serialVersionUID = 1L;
	
	private static final int INIT_TABLE_SIZE = 5000;
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
		if (key < 0) return new ArrayList<V>();
		return nearby[key];
	}
	
	public boolean close(V v) {
		ArrayList<Integer> keys = new ArrayList<Integer>(9);
		int cellSize = 10;
		for (int i = -cellSize; i < cellSize+1; i += cellSize) {
			for (int j = -cellSize; j < cellSize+1; j += cellSize) {
				Particle p = (Particle)v;
				Particle np = new Particle();
				np.p = new Vector2D(p.p.x+i, p.p.y+j);
				int key = generate((V)np);
				if (keys.contains(key)) continue;
				if (key <0 ) continue;
				if (null == nearby[key]) {
					ArrayList<V> array = new ArrayList<V>(100);
					nearby[key] = array;
				}
				nearby[key].add(v);
			}
		}
		
		return true;
	}
}
