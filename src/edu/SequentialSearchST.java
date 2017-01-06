package edu;
//import java.util.*;
import edu.Queue.*;

import org.junit.*;

/******************************************************************************
 *  Compilation:  javac SequentialSearchST.java
 *  Execution:    java SequentialSearchST
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/31elementary/tinyST.txt  
 *  
 *  Symbol table implementation with sequential search in an
 *  unordered linked list of key-value pairs.
 *
 *  % more tinyST.txt
 *  S E A R C H E X A M P L E
 *
 *  % java SequentialSearchST < tiny.txt 
 *  L 11
 *  P 10
 *  M 9
 *  X 7
 *  H 5
 *  C 4
 *  R 3
 *  A 8
 *  E 12
 *  S 0
 *
 ******************************************************************************/

/**
 *  The <tt>SequentialSearchST</tt> class represents an (unordered)
 *  symbol table of generic key-value pairs.
 *  It supports the usual <em>put</em>, <em>get</em>, <em>contains</em>,
 *  <em>delete</em>, <em>size</em>, and <em>is-empty</em> methods.
 *  It also provides a <em>keys</em> method for iterating over all of the keys.
 *  A symbol table implements the <em>associative array</em> abstraction:
 *  when associating a value with a key that is already in the symbol table,
 *  the convention is to replace the old value with the new value.
 *  The class also uses the convention that values cannot be <tt>null</tt>. Setting the
 *  value associated with a key to <tt>null</tt> is equivalent to deleting the key
 *  from the symbol table.
 *  <p>
 *  This implementation uses a singly-linked list and sequential search.
 *  It relies on the <tt>equals()</tt> method to test whether two keys
 *  are equal. It does not call either the <tt>compareTo()</tt> or
 *  <tt>hashCode()</tt> method. 
 *  The <em>put</em> and <em>delete</em> operations take linear time; the
 *  <em>get</em> and <em>contains</em> operations takes linear time in the worst case.
 *  The <em>size</em>, and <em>is-empty</em> operations take constant time.
 *  Construction takes constant time.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/31elementary">Section 3.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class SequentialSearchST<Key, Value> {
    private int N;           // number of key-value pairs
    private Node first;      // the linked list of key-value pairs

    // a helper linked list data type
    private class Node {
        private Key key;
        private Value val;
        private Node next;

        public Node(Key key, Value val, Node next)  {
            this.key  = key;
            this.val  = val;
            this.next = next;
        }
    }

    /**
     * Initializes an empty symbol table.
     */
    public SequentialSearchST() {
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     *
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return N;
    }

    /**
     * Returns true if this symbol table is empty.
     *
     * @return <tt>true</tt> if this symbol table is empty;
     *         <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns true if this symbol table contains the specified key.
     *
     * @param  key the key
     * @return <tt>true</tt> if this symbol table contains <tt>key</tt>;
     *         <tt>false</tt> otherwise
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public boolean contains(Key key) {
        if (key == null) throw new NullPointerException("argument to contains() is null");
        return get(key) != null;
    }

    /**
     * Returns the value associated with the given key in this symbol table.
     *
     * @param  key the key
     * @return the value associated with the given key if the key is in the symbol table
     *     and <tt>null</tt> if the key is not in the symbol table
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public Value get(Key key) {
        if (key == null) throw new NullPointerException("argument to get() is null"); 
        for (Node x = first; x != null; x = x.next) {
        	assert(x != null);
            if (key.equals(x.key))
                return x.val;
        }
        return null;
    }

    /** 
     * Weakest pre condition is ClassInvariant.
     * Strongest post condition:
     * 	if key is null then throw exception
     *  else if value is null then delete key
     *  else if key is present in s then update value
     *  else create new key,value pair
     * */
    
    public boolean ClassInvariant(SequentialSearchST<Key, Value> s){
    	return (s != null && Integer.toString(N) != null) ? true : false;
    }
    
    public boolean CheckPreCondition(SequentialSearchST<Key, Value> s){
    	return ClassInvariant(s);
    }
    
    public boolean CheckKeyInSymbolTable(Key checkKey){
    	for (Key key : this.keys()) {
			if(key.toString().equalsIgnoreCase(checkKey.toString())) return true;
		}
    	return false;
    }
    
    public boolean CheckPostCondition(Key key, Value val, int oldSize){
    	int newSize = size();
    	if(key == null) {
    		//System.out.println("case1"); 
    		return true;
    	}
    	else if(val == null) {
    		if(!CheckKeyInSymbolTable(key) && oldSize == newSize + 1) {
    			//System.out.println("case2");
    			return true;
    		}
    	}
    	else if(oldSize == newSize && CheckKeyInSymbolTable(key) && get(key) == val) {
    		//System.out.println("case3");
    		return true;
    	}
    	else {
    		if(oldSize + 1 == newSize && CheckKeyInSymbolTable(key)  && get(key) == val) {
    			//System.out.println("case4");
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Inserts the specified key-value pair into the symbol table, overwriting the old 
     * value with the new value if the symbol table already contains the specified key.
     * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is <tt>null</tt>.
     *
     * @param  key the key
     * @param  val the value
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    
    public void put(Key key, Value val) {
    	assert CheckPreCondition(this);
    	
    	int oldSize = size();
    	if (key == null) {
    		assert CheckPostCondition(key, val, oldSize);
    		throw new NullPointerException("first argument to put() is null"); 
    	}
        if (val == null) {
            delete(key);
            assert CheckPostCondition(key, val, oldSize);
            return;
        }

        for (Node x = first; x != null; x = x.next) {
        	assert !CheckPostCondition(key, val, oldSize) && x.next != first && x != null;
            if (key.equals(x.key)) {
                x.val = val;
                assert CheckPostCondition(key, val, oldSize);
                return;
            }
        }
        first = new Node(key, val, first);
        N++;
        assert CheckPostCondition(key, val, oldSize);
    }

    /**
     * Removes the specified key and its associated value from this symbol table     
     * (if the key is in this symbol table).    
     *
     * @param  key the key
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
     */
    public void delete(Key key) {
        if (key == null) throw new NullPointerException("argument to delete() is null"); 
        first = delete(first, key);
    }

    // delete key in linked list beginning at Node x
    // warning: function call stack too large if table is large
    private Node delete(Node x, Key key) {
        if (x == null) return null;
        if (key.equals(x.key)) {
            N--;
            return x.next;
        }
        x.next = delete(x.next, key);
        return x;
    }


    /**
     * Returns all keys in the symbol table as an <tt>Iterable</tt>.
     * To iterate over all of the keys in the symbol table named <tt>st</tt>,
     * use the foreach notation: <tt>for (Key key : st.keys())</tt>.
     *
     * @return all keys in the sybol table
     */
    public Iterable<Key> keys()  {
        Queue<Key> queue = new Queue<Key>();
        for (Node x = first; x != null; x = x.next)
            queue.enqueue(x.key);
        return queue;
    }


    /**
     * Unit tests the <tt>SequentialSearchST</tt> data type.
     */
    public static void main(String[] args) {
        SequentialSearchST<String, Integer> st = new SequentialSearchST<String, Integer>();
        String[] arr = new String[] {"S", "E", "A", "R", "C", "H", "E", "X", "A", "M", "P", "L", "E"};
        for (int i = 0; i < arr.length; i++) {
        	st.put(arr[i], i);
		}
        for (String s : st.keys())
            System.out.println(s + " " + st.get(s));
    }
}