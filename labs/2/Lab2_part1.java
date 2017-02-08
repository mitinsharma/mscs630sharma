import java.util.Scanner;
/**
 * file: Lab2.java
 * author: Mitin Sharma
 * course: MSCS 630
 * lab: Lab 2 - Euclidean algorithm
 * part : 1
 * due date: 02/05/2017
 * version: 1.0
 * 
 * This file contains str2int function that
 * encrypt plain text to cipher text
 */
public class Lab2_part1 {
  private static Scanner in;
  public static void main(String[] args) {
    Lab2_part1 l = new Lab2_part1();
    System.out.printf("How many lines you want to enter: ");
    in = new Scanner(System.in);        
    String[] input = new String[in.nextInt()];
    for (int i = 0; i < input.length; i++) {
      System.out.print("\nEnter value of x: ");
      int x = in.nextInt();
      System.out.print("\nEnter value of y: ");
      int y = in.nextInt();
      if(x>y && x>0 && y>0) {
        int result = l.gcd(x, y);
        System.out.print("\n\nOutput: "+result);
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
  public int gcd(int x, int y) {
    if(y == 0) {
      return x;
    }
    return gcd(y,x%y);
  }
}

