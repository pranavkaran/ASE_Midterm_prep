package edu;
/******************************************************************************
 *  Compilation:  javac BinarySearch.java
 *  Execution:    java BinarySearch whitelist.txt < input.txt
 *  Dependencies: In.java StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/11model/tinyW.txt
 *                http://algs4.cs.princeton.edu/11model/tinyT.txt
 *                http://algs4.cs.princeton.edu/11model/largeW.txt
 *                http://algs4.cs.princeton.edu/11model/largeT.txt
 *
 *  % java BinarySearch tinyW.txt < tinyT.txt
 *  50
 *  99
 *  13
 *
 *  % java BinarySearch largeW.txt < largeT.txt | more
 *  499569
 *  984875
 *  295754
 *  207807
 *  140925
 *  161828
 *  [367,966 total values]
 *  
 ******************************************************************************/

import java.util.Arrays;
import org.junit.*;

/**
 *  The <tt>BinarySearch</tt> class provides a static method for binary
 *  searching for an integer in a sorted array of integers.
 *  <p>
 *  The <em>indexOf</em> operations takes logarithmic time in the worst case.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/11model">Section 1.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class BinarySearch {

    /**
     * This class should not be instantiated.
     */
    private BinarySearch() { }

    /**
     * Returns the index of the specified key in the specified array.
     *
     * @param  a the array of integers, must be sorted in ascending order
     * @param  key the search key
     * @return index of key in array <tt>a</tt> if present; <tt>-1</tt> otherwise
     */
    public static boolean checkSorted(int[] a) {
    	boolean bReturn = false;
    	for (int i = 0; i < a.length; i++) {
			if(i < a.length -1 && !(a[i] < a[i+1])) return bReturn = false;
		}
    	return bReturn = true;
    }
    
    public static boolean checkPre(int[] a, int key) {
    	if(a != null && Integer.toString(key) != null && checkSorted(a)) return true;
    	return false;
    }
    
    public static boolean checkKeyPresent(int[] a, int key) {
    	for (int i = 0; i < a.length; i++) {
			if(a[i] == key) return true;
		}
    	return false;
    }
    
    public static boolean checkReturnValue(int[] a, int key, int returnValue) {
    	if(checkKeyPresent(a, key)) {
    		if(returnValue == a[key]) return true;
    		else return false;
    	} else  {
    		if(returnValue == -1) return true;
    		else return false;
    	}
    }
    
    public static boolean checkPost(int[] a, int key, int returnValue) {
    	if(a != null && Integer.toString(key) != null && checkReturnValue(a, key, returnValue)) return true;
    	return false;
    }
    
    public static int indexOf(int[] a, int key) {
    	assert(checkPre(a, key));
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
        	assert(checkPre(a, key) && lo <= hi);
            int mid = lo + (hi - lo) / 2;
            if      (key < a[mid]) hi = mid - 1;
            else if (key > a[mid]) lo = mid + 1;
            else {
            	assert(checkPost(a, key, mid));
            	return mid;
            }
            assert(!checkPost(a, key, mid) && lo <= hi);
        }
        assert(checkPost(a, key, -1));
    	return -1;
    }

    /**
     * Returns the index of the specified key in the specified array.
     * This function is poorly named because it does not give the <em>rank</em>
     * if the array has duplicate keys or if the key is not in the array.
     *
     * @param  key the search key
     * @param  a the array of integers, must be sorted in ascending order
     * @return index of key in array <tt>a</tt> if present; <tt>-1</tt> otherwise
     * @deprecated Replaced by {@link #indexOf(int[], int)}.
     */
    public static int rank(int key, int[] a) {
        return indexOf(a, key);
    }

    /**
     * Reads in a sequence of integers from the whitelist file, specified as
     * a command-line argument; reads in integers from standard input;
     * prints to standard output those integers that do <em>not</em> appear in the file.
     */
    public static void main(String[] args) {

        // read the integers from a file
        //In in = new In(args[0]);
        
        int[] whitelist =  new int[]{3,6,8,4,5,2,7,9,1};// in.readAllInts();
        
        int StdIn = 17;
        // sort the array
        Arrays.sort(whitelist);

        // read integer key from standard input; print if not in whitelist
        int key = StdIn;
        //System.out.println(whitelist);
        if (BinarySearch.indexOf(whitelist, key) == -1) {
        	System.out.println("im here");
            System.out.println(key);
        }
        //System.out.println(key);
    }
}