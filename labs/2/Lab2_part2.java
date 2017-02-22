import java.util.Scanner;

/**
 * file: Lab2.java
 * author: Mitin Sharma
 * course: MSCS 630
 * lab: Lab 2 - Euclidean algorithm
 * part : 2
 * due date: 02/08/2017
 * version: 1.0
 * 
 * This file calculates euclidean algorithm calculations
 */
public class Lab2_part2 {
  private static Scanner in;
  public static void main(String[] args) {
    Lab2_part2 l = new Lab2_part2();
    System.out.printf("How many lines you want to enter: ");
    in = new Scanner(System.in);        
    String[] input = new String[in.nextInt()];
    for (int i = 0; i < input.length; i++) {
      System.out.print("\nEnter value of x: ");
      long x = in.nextLong();
      System.out.print("\nEnter value of y: ");
      long y = in.nextLong();
      if(x>y && x>0 && y>0) {
        long result[] = l.euclidAlgExt(x,y);
    	System.out.print("\n\nOutput: "+result[0]+" "+result[1]+" "+result[2]);
      }
      else {
    	System.out.print("Error: x should be greater than y, and both should be nonzero.1");
      }
    }
  }
  /**
   * gcd
   * 
   * This is recursion function
   * returns greatest common factor of two 
   * passed parameters
   * @param x
   * @param y
   * @return
   */
  public long gcd(long x, long y) {
    if(y == 0) {
      return x;
    }
    return gcd(y,x%y);
  }
  /**
  * euclidAlgExt
  * returns array of long type
  * @param a
  * @param b
  * @return array of long
  */
  public long[] euclidAlgExt(long a, long b) {
    long result[]= {0,0,0};
    Lab2_part2 l = new Lab2_part2();
	result[0] = l.gcd(a, b); 
    long x = 0, y = 1, lx = 1, ly = 0;
    long tmp;
    while (b != 0) {
      long q = a / b;
      long r = a % b;
      a = b;
      b = r;
      tmp = x;
      x = lx - q * x;
      lx = tmp;
      tmp = y;
      y = ly - q * y;
      ly = tmp;
    }
    result[1]=lx;
    result[2]=ly;
    return result;
  }
}

