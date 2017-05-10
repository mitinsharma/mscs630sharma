/**
* file: DriverAES.java
* author: Mitin Sharma
* course: MSCS 630
* lab: Lab 5
* due date: 05/01/2017
* version: 1.0
* 
* This program returns ciphertext using AES,
* in this we are passing plaintext and hex key in
* parameter.
*/
import java.util.Scanner;

public class DriverAES {
  static Scanner scan = new Scanner(System.in);
	 public static void main(String[] args){
	   long start, elapsed;
		 String key = scan.nextLine();
		 String ptext = scan.nextLine();
		 start = System.nanoTime();
		 AESCipher aes = new AESCipher();
		 String cipher = aes.AES(ptext, key);
		 System.out.println(cipher);
		 elapsed = (System.nanoTime() - start)/ 1000000;
		 System.out.print("\nTotal Time : "+elapsed+" ms");
	}  
}
