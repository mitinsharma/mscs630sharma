/**
* file: DriverAES.java
* author: Mitin Sharma
* course: MSCS 630
* lab: Lab 4
* due date: 04/05/2017
* version: 1.0
* 
* This is a program take input of 16 hex string
* from user and passes as a parameter to aesRoundKeys
* to achieve roundkeys of given input
*/
import java.util.Scanner;

public class DriverAES {
  /**
  * Main Function
  * 
  * This is a main function, has reference to AESCipher class.
  * 
  * @param String[] args
  * @return void
  */
  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);
    System.out.print("Input : ");
    while (scan.hasNext()) {
      String KeyHex = scan.nextLine();
      String[] res = AESCipher.aesRoundKeys(KeyHex);
      for (String a : res) {
        System.out.println(a);
      }
      System.out.println();
    }
    scan.close();
  }
}
