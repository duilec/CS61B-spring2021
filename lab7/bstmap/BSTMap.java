package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// note: compareTo()(method of class of Comparable<K>) don't need extra class to Override compareTo()
// but, compare()(method of class of Comparator<K>) need extra to Override compare()
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K,V> {
    private Node root;
    private HashSet<K> keySet = new HashSet<K>();

    private class Node {
        private K key;           // sorted by key
        private V value;           // associated data
        private Node left, right;  // left and right subtrees
        private int size;          // number of nodes in subtree

        public Node(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
        }
    }

    public BSTMap(){
    }

    /** Removes all of the mappings from this map. */
    public void clear(){
        root = null;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key){
        return containsKey(root, key) != null;
    }

    private K containsKey(Node x, K key) {
        if (x == null) return null;
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) return containsKey(x.left, key);
        else if (cmp > 0) return containsKey(x.right, key);
        else              return x.key;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return get(root, key);
    }

    private V get(Node x, K key) {
        if (x == null) return null;
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else              return x.value;
    }

    /* Returns the number of key-value mappings in this map. */
    public int size(){
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("calls put() with a null key");
//        if (value == null) {
//            delete(key);
//            return;
//        }
        root = put(root, key, value);
        keySet.add(key);
//        assert check();
    }

    private Node put(Node x, K key, V value) {
        if (x == null) return new Node(key, value, 1);
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) x.left  = put(x.left,  key, value);
        else if (cmp > 0) x.right = put(x.right, key, value);
        else              x.value   = value;
        // also use recursion to compute current size of x
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet(){
        return keySet;
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key){
        throw new UnsupportedOperationException("Not required for Lab 7");
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value){
        throw new UnsupportedOperationException("Not required for Lab 7");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Not required for Lab 7");
    }
}
