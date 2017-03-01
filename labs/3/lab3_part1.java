/**
 * file: Lab3.java
 * author: Mitin Sharma
 * course: MSCS 630
 * lab: Lab 3
 * due date: 03/01/2017
 * version: 1.0
 * 
 * This is a program to find determinant
 */
import java.util.Scanner;
public class lab3 {
  public static void main(String[] args) {
    int m,n;
    Scanner s = new Scanner(System.in);
    System.out.print("Enter Modulus : ");
    m = s.nextInt();
    System.out.println("Enter n :");
    n = s.nextInt();
    int[][] a = new int[n][n];
    for(int i=0; i<n; i++) {
      for(int j=0;j<n;j++) {
        System.out.println("Array A["+i+"]["+j+"] = ");
	a[i][j]=s.nextInt();
      }
    }
    int det = deter(a,n,m);
    System.out.println("Determinant is : "+det);
  }
    
  /**
  * Mod
  * 
  * This function calculate modulus
  * 
  * @param int x, int m
  * @return int mod
  */
  public static int mod(int x, int m) {
    return x%m;
  }
  
  /**
  * Deter
  * 
  * This function calculate determinant
  * 
  * @param int a[][], int n, int m
  * @return det
  */
  public static int deter(int a[][],int n,int m) {
    int det = 0, sign = 1, p = 0, q = 0; 
    if(n==1) {
      det = a[0][0];
    }
    else if(n==2) {
      det = (mod(a[0][0],m)*mod(a[1][1],m)) - (mod(a[0][1],m)*mod(a[1][0],m));
    }
    else {
      det=0;
      for(int j1=0;j1<n;j1++) {
        int[][] mm = new int[n-1][];
	for(int k=0;k<(n-1);k++) {
	  mm[k] = new int[n-1];
        }
	for(int i=1;i<n;i++) {
          int j2=0;
	  for(int j=0;j<n;j++) {
	    if(j == j1)
	      continue;
	    mm[i-1][j2] = a[i][j];
	    j2++;
          }
        }
	det += Math.pow(-1.0,1.0+j1+1.0)* a[0][j1] * deter(mm,n-1,m);
      }
    }
    return mod(det,m);
  }
}
