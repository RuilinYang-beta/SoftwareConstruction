package warmup;

import java.util.HashSet;
import java.util.Set;

public class Quadratic {

    /**
     * Find the integer roots of a quadratic equation, ax^2 + bx + c = 0.
     * @param a coefficient of x^2
     * @param b coefficient of x
     * @param c constant term.  Requires that a, b, and c are not ALL zero.
     * @return all integers x such that ax^2 + bx + c = 0.
     */
    public static Set<Integer> roots(long a, long b, long c) {
//	public static Set<Integer> roots(int a, int b, int c) {
    	Set<Integer> ans = new HashSet<Integer>();
    	

    	if (a==0) {
    		if (-c/b == Math.floor(-c/b)) {
    			ans.add((int) Math.floor(-c/b));
    			return ans;
    		}
    		
    	}
    	
    	double x1 = (-b + Math.sqrt(b*b - 4*a*c)) / 2 / a;
    	double x2 = (-b - Math.sqrt(b*b - 4*a*c)) / 2 / a;
    	
    	
    	if (x1 == Math.floor(x1)) {
    		ans.add((int) Math.floor(x1));
    	}
    	
    	if (x2 == Math.floor(x2)) {
    		ans.add((int) Math.floor(x2));
    	}    	
    	
    	return ans;
    	
    }

    
    /**
     * Main function of program.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("For the equation x^2 - 4x + 3 = 0, the possible solutions are:");
        Set<Integer> result = roots(1, -4, 3);
        System.out.println(result);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
