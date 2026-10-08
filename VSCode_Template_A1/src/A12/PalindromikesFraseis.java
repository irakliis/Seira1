package A12;

import java.util.Scanner;

// HY252 - A1 - Exercise 2a
public class PalindromikesFraseis {

    int i;
    public static void main(String[] args) {
        System.out.println("Dose leksi");
        Scanner in = new Scanner(System.in);
        String nextLine = in.nextLine();

        int LengthOfArray = (nextLine.length());

        char[] leksi = new char[LengthOfArray];

        for (int j=0; j<LengthOfArray;j++){
            System.out.println(leksi[j]);
        }
        

        in.close();
    }

    static boolean isPalindromikiFrash(String s) {
        return false;
    }
}
