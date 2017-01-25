/**
 * file: Lab1.java
 * author: Mitin Sharma
 * course: MSCS 630
 * lab: Lab 1
 * due date: 01/25/2017
 * version: 1.0
 * 
 * This file contains str2int function that
 * encrypt plain text to cipher text
 */
import java.util.Scanner;

public class Lab1 {
  public static void main(String[] args) {
	Lab1 l = new Lab1();
    Scanner in = new Scanner(System.in);
    System.out.printf("How many lines you want to enter: ");        
	String[] input = new String[in.nextInt()];
	in.nextLine(); 
	if (input.length>=1 && input.length<=50) {
      for (int i = 0; i < input.length; i++) {
	    input[i] = in.nextLine();
	    String[] splitArray = input[i].split("\\s+"); 
	    if (input[i].length()==0 || (l.arrayEleCount(splitArray)<1 && l.arrayEleCount(splitArray)>500)) {
	      System.out.print("Each line must have 1 word and up to 500 words. Please enter line again.");
	      input[i] = in.nextLine();	
	    }
	  }
      System.out.printf("\nOutput:\n");
	  for (String s : input) {
	    int[] result = l.str2int((s.toLowerCase()).replaceAll("\\s",""));
	    System.out.print("\n");
	    for (int r : result) {
	      System.out.print(""+r+" ");
	    }
	  }
	  in.close();
	}
	else {
	  System.out.print("Please enter number of lines between 1 to 50");
	}
  }
  /**
   * arrayEleCount
   * 
   * This function count array elements
   * to check words per line condition
   * 
   * @param arr
   * @return
   */
  public int arrayEleCount(String[] arr) {
    int i=0;
	for (String s: arr) {
    	i++;
    }
	return i;
  }
  /**
   * str2int
   * 
   * This function encrypt plain text
   * to cipher text and returns array of int
   * 
   * @param plainText
   * @return int array
   */
  public int[] str2int(String plainText){
    int[] num = new int[plainText.length()];
	char[] logic = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	for (int i = 0; i < plainText.length(); i++){
	  num[i] = new String(logic).indexOf(plainText.charAt(i));
	}
  return num;
  }
}
