package syrup;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Based on:
 * 
 * [1] Matthias Teschner, Bruno Heidelberger, Matthias Müller, Danat Pomeranets, Markus Gross.
 * 	   'Optimized Spatial Hashing for Collision Detection of Deformable Objects.', 2003.
 * 
 * [2] Hubert Nguyen. 'Point-Based Visualization of Metaballs on a GPU / GPU Gems 3.', 2008.
 * 
 * @param <V> - whatever type you want to put there.
 * 
 * Spatial hash table contains objects as an array, but also keep track on
 * spatial positions of elements. In constant time it may give a number of
 * surrounding items. However, element position has changed, spatial hash
 * table doesn't contains valid information about item positions and rehash
 * is needed.
 */
abstract public class SpatialTable<V> implements Iterable<V> {
	
	private static final int INIT_NEARBY_SIZE = 50;		// how many we expect nearby
	private final ArrayList<V> table;					// table contains available values
	
	/**
	 * Think about matrix position as hash value. Each position contains
	 * array of objects that are close enough to interact.
	 */
	private ArrayList<V>[][] nearby;
	private int row;
	private int column;
	
	// instead hash function, override object position in the table
	abstract protected int posX(V value);
	abstract protected int posY(V value);
	
	public SpatialTable(int row, int column) {
		this.row = row; this.column = column;
		table = new ArrayList<V>((row*column)/3); // assumes object takes only 1/3 of space
		nearby = new ArrayList[row][column];	  // ftw? java doesn't allow to create generic arrays or does it?
	}
	
	@Override
	public Iterator<V> iterator() {
		return table.iterator();
	}
	
	/**
	 * Append value to the table and identify its position in the space.
	 * Don't need to rehash table after append operation.
	 * 
	 * @return true (as per the general contract of Collection.add).
	 */
	public boolean add(V value) {
		addInterRadius(value);
		table.listIterator(table.size()).add(value);
		return true;
	}
	
	/**
	 * Returns array of near by objects in space.
	 * 
	 * @param value - central object
	 * @return array containing close elements
	 */
	public ArrayList<V> nearby(V value) {
		int x = posX(value);
		int y = posY(value);
		return nearby[x][y];
	}

	/**
	 * Updates the spatial relationships of objects. Rehash function
	 * needed if elements change their position in the space.
	 */
	public void rehash() {
		nearby = new ArrayList[row][column];	// ftw again?
		for (V v : this) {
			addInterRadius(v);
		}
	}
	
	/**
	 * According to [2] add object to the spatial hash multiple times,
	 * for every grid cell intersecting its influence area.
	 */
	private void addInterRadius(V value) {
		// add element to position and also to neighbour cells
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				int x = posX(value)+i;
				int y = posY(value)+j;
				if (x < 0 || y < 0) continue;
				if (null == nearby[x][y]) {
					nearby[x][y] = new ArrayList<V>(INIT_NEARBY_SIZE);
				}
				nearby[x][y].add(value);
			}
		} // for
	}

}
