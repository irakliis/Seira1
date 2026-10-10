package A12;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// HY252 - A1 - Exercise 2b, 2c
public class PalindromikesLexikou {
    public static void main(String[] args) {
        // finding the time before the operation is executed
        long start = System.currentTimeMillis();

        int total=0,pCount=0,i;
        long letters=0;
        List<String> palindromes = new ArrayList<>();
        String temp;
        Boolean check;

        try {
            FileInputStream fis=new FileInputStream("Resources/ben.dic"); //file to be scanned
            Scanner sc=new Scanner(fis);
                while(sc.hasNextLine()) { //returns true if there is another line to read
                    total++;
                    temp = sc.nextLine();
                    letters += (temp.length());
                    check = PalindromikesFraseis.isPalindromikiFrash(temp);
                        if (check==true){
                            pCount++;
                            palindromes.add(temp);
                        }
                }
                    sc.close(); //closes the scanner
            } 
            catch(IOException e) {
                System.out.println(e);
            }

        if(total!=0){ // total words + average length + precentage
            System.out.println("Total words: "+total);
            System.out.println("Num of palindromes: "+ pCount);
            System.out.println("The average length of all of the words is = "+ ((double)letters)/total);
            System.out.println("The percentage of palindromes in the file was: " 
            + ((double)pCount /total) * 100);

        }
        int ArrSize=palindromes.size();
        for(i=0;i<ArrSize;i++){ // Printing all the palindromes that were found.
            System.out.println(palindromes.get(i));
        }
        
        Sort(palindromes, ArrSize); // Sort and print words from Z-A
        for(i=0; i<ArrSize;i++){
            System.out.println(palindromes.get(i));
        }
        //finding the time after the operation is executed
        long end = System.currentTimeMillis();
        
        //finding the time difference and converting it into seconds
        float timediff = (end - start)/1000F;

        // Printing the time it took for the program to finish
        System.out.println("It took:"+ timediff+" seconds to finish the program."); 
    }

static void Sort(List<String> list, int size) { // sort
    int i, j;
    
    for (i = 0; i < size; i++) {
        String max = list.get(i);
        int pos = i;
            for (j = i + 1; j < size; j++) {
                if (list.get(j).compareTo(max) > 0) {
                    pos = j;
                    max = list.get(j);
                }
            }
            String temp = list.get(i);
            list.set(i, list.get(pos));
            list.set(pos, temp);

            /* temp = list(i)
            list(i) = list(pos)
            list(pos) = temp */
        }
    }
}