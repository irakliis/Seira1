package A12;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Scanner;


// HY252 - A1 - Exercise 2a
public class PalindromikesFraseis {

    public static void main(String[] args) {
        System.out.println("Please type something out.\nBitch: ");
        Scanner scanner = new Scanner(System.in);
        String nextLine = scanner.nextLine();
        Normalizer.normalize(nextLine, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        System.out.println(nextLine);
        scanner.close();
        
    }

    static boolean isPalindromikiFrash(String s) {
        return false;
    }
}
