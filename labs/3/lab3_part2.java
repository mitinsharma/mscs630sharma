/**
 * file: Lab3.java
 * author: Mitin Sharma
 * course: MSCS 630
 * lab: Lab 3 - part 2
 * due date: 03/01/2017
 * version: 1.0
 * 
 * This is a program to convert plain text into ciphertext
 */
import java.util.Scanner;

public class lab3_part2 {
  public static void main(String[] args) {
    String c, ps;
    Scanner s = new Scanner(System.in);
    System.out.print("Enter Special Character : ");
    c = s.nextLine();
    System.out.println("Enter n :");
    ps = s.nextLine();
    char[] cha = ps.toCharArray();
    char[] cc = c.toCharArray();
    int t = cha.length;
    int d = t/16;
    for(int i = 1; i<=d+1; i++) {
      int k=(i-1)*16,j=16*i;
      if(j>t) { 
        j = k + (t%16);
      }
      String newchar = ps.substring(k,j);
      char[] charr = newchar.toCharArray();
      int[][] result = getHexMatP(charr,cc);
      display(result);
      System.out.print("\n");			
    }
  }

  /**
  * getHexMatP
  * 
  * This function make 2-d array of ascii values
  * 
  * @param char[] cha, char[] cc
  * @return int[][] a
  */
  public static int[][] getHexMatP(char[] cha,char[] cc) {
    int csize = cha.length;
    int[][] a = new int[4][4];
    int count=0;
    for(int i=0; i<4; i++) {
      for(int j=0; j<4; j++){
        if(csize>count) {
	  a[i][j] = (int)cha[count];
	  count++;
	}
	else {
	  a[i][j] = (int)cc[0];
	}
      }
    }
    return a;
  }
  
  /**
  * Display
  * 
  * This function display hex values in array
  * 
  * @param int[][] a
  * @return 
  */
  public static void display(int a[][]){
    for(int i=0; i<4; i++) {
      for(int j=0; j<4; j++) {
        System.out.print(String.format("%02X", a[j][i])+"\t");
      }
      System.out.print("\n");
    }
  }
}
