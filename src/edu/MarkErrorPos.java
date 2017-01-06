package edu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MarkErrorPos {

	public static String txt;
	public static int[] pos; 
	public static String u = ""; 
	
	public static boolean CheckPreCondition(int m, int[] pos, String s){
    	return m >= 0 && pos != null && s != "";
    }
	
	public static boolean CheckPostCondition(int[] pos, String u){
		boolean ret = false;
		char[] charArray = u.toCharArray();
		ArrayList arr = new ArrayList();
		Collections.addAll(arr, pos);
		for (int i = 0; i < charArray.length; i++) {
			if(arr.contains(i)){
				if(charArray[i] == '^') ret = true;
			} else {
				if(charArray[i] == '_') ret = true;
			}
		}
    	return ret && pos != null && u != "";
    }
	
	public static String markErrorPosMethod(int m, int[] pos, String s){
		assert CheckPreCondition(m, pos, s);
		if(m < pos.length) {
			u += GenBlank(pos[m] - u.length() - 1) + "^";
			markErrorPosMethod(m + 1, pos, s);
		} else {
			for (int i = 0; i < s.length() - pos[pos.length -1]; i++) {
				u += GenBlank(1);
			}
		}
		assert CheckPostCondition(pos, u);
		return u;
	}
	
	public static String GenBlank(int n) {
		String ret = "";
		for (int i = 0; i < n; i++) {
			ret += "_";
		}
		return ret;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		txt = "ugkdg agdagfakhlf DGafgfqfh safhjh";
		pos = new int[] {4,6,12,19,25,29};
		System.out.println(txt);
		System.out.println(markErrorPosMethod(0, pos, txt));
	}

}
