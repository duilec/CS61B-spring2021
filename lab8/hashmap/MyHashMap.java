package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Huang Jinghong
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets; // the bucket's objects
    // You should probably define some more!
    private int nodeNum = 0; // the number of nodes (the number of pair of key-values)
    // set defaults initialSize = 16 and loadFactor = 0.75
    private int tableSize = 16;; // the table size(the number of buckets)
    private double loadFactor = 0.75; // the max Load
    // build a set need using 'new'
    private Set<K> keySet = new HashSet<>();


    /** Constructors */
    public MyHashMap() {
        buckets = createTable(tableSize);
    }

    public MyHashMap(int initialSize) {
        tableSize = initialSize;
        buckets = createTable(tableSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        tableSize = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(tableSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    // For MyHashMap.java, you can choose any data structure you’d like.
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    // For our purposes,
    // we will only add elements of type Collection<Node> to our Collection[].
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        buckets = createTable(tableSize);
        nodeNum = 0;
        // tableSize: You are not required to resize down
        // loadFactor: keep loadFactor in my opinion
        keySet = new HashSet<>();
    }

    @Override
    public boolean containsKey(K key) {
        int index = getTableIndex(key.hashCode());
        if (buckets[index] != null){
            for (Node oldNode : buckets[index]) {
                if (oldNode.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = getTableIndex(key.hashCode());
        if (buckets[index] != null){
            for (Node oldNode : buckets[index]) {
                if (oldNode.key.equals(key)) {
                    return oldNode.value;
                }
            }
        }
        return null;
    }

    @Override
    public int size() {
        return nodeNum;
    }

    @Override
    public void put(K key, V value) {
        Node newNode = new Node(key, value);
        int index = getTableIndex(key.hashCode());
        // the buckets[index] NOT exist, we create bucket to insert
        if (buckets[index] == null){
            buckets[index] = createBucket();
        }
        // has same key in buckets[index], replace oldNode value
        for (Node oldNode : buckets[index]) {
            if (oldNode.key.equals(key)) {
                oldNode.value = value;
                return;
            }
        }
        // not has same key in buckets[index], add newNode to that end
        buckets[index].add(newNode);
        keySet.add(key);
        nodeNum += 1;
        if ((double)(nodeNum / tableSize) >= loadFactor ){
            changeTableSize();
        }
    }

    private int getTableIndex(int hashCode) {
        return Math.floorMod(hashCode, tableSize);
    }

    private void changeTableSize() {
        LinkedList<Node> oldNodes = new LinkedList<>();
        for (int i = 0; i < tableSize; i += 1) {
            if (buckets[i] != null){
                for (Node oldNode : buckets[i]) {
                    oldNodes.add(oldNode);
                }
            }
        }
        // double to resize table in my design
        tableSize *= 2;
        this.clear();
        for (Node oldNode : oldNodes) {
            this.put(oldNode.key, oldNode.value);
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)){
            keySet.remove(key);
            nodeNum -= 1;
            int index = getTableIndex(key.hashCode());
            for (Node node : buckets[index]){
                if (node.key.equals(key)){
                    V value = node.value;
                    buckets[index].remove(node);
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return new IteratorWithKey();
    }

    private class IteratorWithKey implements Iterator<K> {
        // you can just create a LinkedList to iterate
        LinkedList<K> keysList = createKeyList();
        int keyNum = 0;

        private LinkedList<K> createKeyList(){
            LinkedList<K> keysList = new LinkedList<>();
            for (int i = 0; i < tableSize; i += 1) {
                if (buckets[i] != null){
                    for (Node oldNode : buckets[i]) {
                        keysList.add(oldNode.key);
                    }
                }
            }
            return keysList;
        }

        @Override
        public boolean hasNext() {
            return !(keyNum == nodeNum);
        }

        @Override
        public K next() {
            if(!hasNext()){
                throw new NoSuchElementException();
            } else {
                return keysList.remove();
            }
        }
    }

}
