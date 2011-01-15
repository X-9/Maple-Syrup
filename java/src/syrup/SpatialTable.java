package syrup;

import java.util.ArrayList;

/**
 * Based on:
 * 
 * [1] Matthias Teschner Bruno Heidelberger Matthias M¨uller Danat Pomeranets Markus Gross.
 * 	   'Optimized Spatial Hashing for Collision Detection of Deformable Objects.', 2003.
 * 
 * [2] Hubert Nguyen. 'Point-Based Visualization of Metaballs on a GPU / GPU Gems 3.', 2008.
 * 
 * @param <V>
 */
abstract public class SpatialTable<V> extends ArrayList<V> {
	private static final long serialVersionUID = 1L;
	
	private static final int INIT_NEARBY_SIZE = 50;
	private ArrayList<V>[][] nearby;
	private int row;
	private int column;
	
	abstract protected int posX(V value);
	abstract protected int posY(V value);
	
	public SpatialTable(int row, int column) {
		this.row = row; this.column = column;
		nearby = new ArrayList[row][column];
	}
	
	public ArrayList<V> nearby(V v) {
		int x = posX(v);
		int y = posY(v);
		if (x < 0 || y < 0) return new ArrayList<V>();
		return nearby[x][y];
	}
	
	public void rehash() {
		nearby = new ArrayList[row][column];
		for (V v : this) {
			addInterRadius(v);
		}
	}
	
	private void addInterRadius(V v) {
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				int x = posX(v)+i;
				int y = posY(v)+j;
				if (null == nearby[x][y]) {
					nearby[x][y] = new ArrayList<V>(INIT_NEARBY_SIZE);
				}
				nearby[x][y].add(v);
			}
		} // for
	}
}
