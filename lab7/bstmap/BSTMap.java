package bstmap;

import java.util.*;

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
        root = put(root, key, value);
        keySet.add(key);
    }

    private Node put(Node x, K key, V value) {
        if (x == null) return new Node(key, value, 1);
        int cmp = key.compareTo(x.key);
        if      (cmp < 0) x.left  = put(x.left,  key, value);
        else if (cmp > 0) x.right = put(x.right, key, value);
        else              x.value = value;
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
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        V removeValue = get(key);
        root = remove(root, key);
        keySet.remove(key);
        return removeValue;
    }

    private Node remove(Node x, K key){
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        // recursive to x.left or x.right to remove and build new x.left or x.right
        if      (cmp < 0) x.left = remove(x.left,  key);
        else if (cmp > 0) x.right = remove(x.right, key);
        else {
            if (x.left == null)   return x.right;
            if (x.right == null)  return x.left;
            // find the smallest Node in right and remove it
            // then, using the smallest Node to replace x and link x.left
            Node temp = x;
            x = minNode(temp.right);
            x.right = removeMin(temp.right);
            x.left = temp.left;
        }
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    private Node minNode(Node x) {
        if (x.left == null) return x;
        else                return minNode(x.left);
    }

    // find the smallest Node, then, using right of the smallest Node to replace itself
    private Node removeMin(Node x){
        if (x.left == null) return x.right;
        x.left = removeMin(x.left);
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value){
        if (key == null) throw new IllegalArgumentException("calls remove() with a null key");
        root = remove(root, key);
        keySet.remove(key);
        return value;
    }

    @Override
    public Iterator<K> iterator() {
        return new iteratorOfK(root);
    }

    private class iteratorOfK implements Iterator<K>{
        private Node node;

        public iteratorOfK(Node x){
            this.node = x;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public K next() {
            if(!hasNext()){
                throw new NoSuchElementException();
            } else {
                K minKey = minNode(node).key;
                // we need use remove() in BSTMap
                node = BSTMap.this.remove(node, minKey);
                return minKey;
            }
        }
    }

    public void printInOrder(){
        printInOrderByMidTravel(root);
    }

    private void printInOrderByMidTravel(Node x){
        if (x == null) return;
        printInOrderByMidTravel(x.left);
        System.out.println("key: " + x.key + " value: " + x.value);
        printInOrderByMidTravel(x.right);
    }
}
