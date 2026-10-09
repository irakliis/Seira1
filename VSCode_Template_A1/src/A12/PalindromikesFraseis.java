package A12;

import java.util.Scanner;

// HY252 - A1 - Exercise 2a
public class PalindromikesFraseis {

    public static void main(String[] args) {
        long start = System.nanoTime();
        System.out.println("Dose leksi");
        Scanner in = new Scanner(System.in);
        String nextLine = in.nextLine();
        
        boolean check = isPalindromikiFrash(nextLine);

        System.out.println("(MAIN) Word was a palindrome, true or false?: " + check);
        long end = System.nanoTime();
        
        System.out.println("Time in seconds passed=" + (end-start)/1000000000);
        in.close();
    }

    static boolean isPalindromikiFrash(String s) {

        int i;
        int LengthOfArray = s.length();

        char[] leksi = s.toCharArray();
        char[] Anapodi = s.toCharArray();

        System.out.println("edoses= " + s);

        for (i = 0; i < LengthOfArray; i++) {
            Anapodi[i] = leksi[LengthOfArray - i - 1];
        }

        String inverted = new String(Anapodi);
        System.out.println("Anapodo= " + inverted);
        i = 0;
        do {
            if (!(leksi[i] == leksi[LengthOfArray - 1 - i])) {
                return false;
            }
            i++;
        } while (i < LengthOfArray / 2);
        return true;
    }
}
