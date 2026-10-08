package A12;

import java.util.Arrays;
import java.util.Scanner;

// HY252 - A1 - Exercise 2a
public class PalindromikesFraseis {
    public static void main(String[] args) {
        long time = System.nanoTime();
        System.out.println("Dose leksi");
        Scanner in = new Scanner(System.in);
        String nextLine = in.nextLine();

        isPalindromikiFrash(nextLine);

        in.close();
        System.out.println("Time in seconds passed=" + time/(10^9));
    }

    static boolean isPalindromikiFrash(String s) {

        int i;
        int LengthOfArray = (s.length());
        char[] leksi = s.toCharArray();
        char[] Anapodi = s.toCharArray();

        System.out.println("edoses= "+ s);
        for(i=0;i<LengthOfArray;i++){
            Anapodi[i]=leksi[LengthOfArray-i-1];
        }
        String inverted = new String(Anapodi);
        System.out.println("Anapodo= "+inverted);
        System.out.println(inverted.equals(s));
        
        return false;
    }
}
