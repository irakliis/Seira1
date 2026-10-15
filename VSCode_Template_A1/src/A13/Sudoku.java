package A13;

// HY252 - A1 - Exercise 3
public class Sudoku {

    public static void main(String[] args) {
        
    }

    static boolean isValidBoard(int[][] brd) {
        int i,j,n;

        int pos[] = new int[9];
        for(i=0; i<9;i++){
            int k=0;            
            for (j=0; j<9; j++){
                int zeros = 9;
                if(brd[i][j]!=0){
                   pos[k]=j; 
                   zeros--;
                }
                if (zeros!=0){
                    
                }
            }
        }

        return false;
    }

    static boolean isSolvableBoard(int[][] brd) {
        return false;
    }
}