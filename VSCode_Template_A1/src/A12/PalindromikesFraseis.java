package A12;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Scanner;

// HY252 - A1 - Exercise 2a
public class PalindromikesFraseis {

    public static void main(String[] args) {
        System.out.println("Dose leksi");
        Scanner in = new Scanner(System.in,"UTF-8");
        String nextLine = in.nextLine();
        System.out.println(Normalizer.normalize(nextLine, 
        Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+"
        , "").toLowerCase().replace('ς', 'σ'));
        
        long start = System.nanoTime();

        boolean check = isPalindromikiFrash(nextLine);

        long end = System.nanoTime();

        System.out.println("Time in seconds passed=" + (end-start)/1000000000);


        System.out.println("(MAIN) Word was a palindrome, true or false?: " + check);        
        in.close();
    }

    static boolean isPalindromikiFrash(String s) {
        int i;
        
        s = Normalizer.normalize(s, 
        Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", 
        "").toLowerCase().replace('ς', 'σ');

        int LengthOfArray = s.length();

        char[] leksi = s.toCharArray();
        char[] Anapodi = s.toCharArray();

        for (i = 0; i < LengthOfArray; i++) {
            Anapodi[i] = leksi[LengthOfArray - i - 1];
        }

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