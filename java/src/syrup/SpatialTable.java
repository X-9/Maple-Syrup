package syrup;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;


abstract public class SpatialTable<V> extends ArrayList<V>{
	private static final long serialVersionUID = 1L;
	
	private static final int INIT_TABLE_SIZE = 3000;
	private Hashtable<Integer, LinkedList<V>> table;
	
	abstract protected int generate(V value);
	
	public SpatialTable() {
		table = new Hashtable<Integer, LinkedList<V>>(INIT_TABLE_SIZE);
	}
	
	public void put(V value) {
		int key = generate(value);
		if (table.containsKey(key)) {
			table.get(key).add(value);
		} else {
			LinkedList<V> list = new LinkedList<V>();
			list.add(value);
			if (null != table.put(key, list)) {
				System.err.println("Previous value exist");
			}
		}
	}
	
	/**
	 * Returns enumeration of the keys in this spatial hash table.
	 * @return an enumeration of keys
	 */
	public Enumeration<Integer> keySet2() {
		return table.keys();
	}
	
	public ListIterator<V> get2(int key) {
		if (!table.containsKey(key)) {
			System.err.println("Failed to find a key " + key + " in the table.");
		}
		
		return table.get(key).listIterator();
	}
	
	public LinkedList<V> clear2() {
		LinkedList<V> all = new LinkedList<V>();
		for (Enumeration<Integer> keys = table.keys(); keys.hasMoreElements(); ) {
			int key = keys.nextElement();
			all.addAll(table.remove(key));
		}
		
		return all;
	}

	
	
}
