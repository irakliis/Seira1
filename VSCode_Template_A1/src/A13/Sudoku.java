package A13;

import java.util.Random;

// HY252 - A1 - Exercise 3
public class Sudoku {

    private static final int BOARD_SIZE = 9;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;

    public static void main(String[] args) {
        int N = 10;          
        int emptyCells = 65; 
        
        generateNPairs(N, emptyCells);
    }

    static void generateNPairs(int n, int emptyCells) {
        long startTime = System.currentTimeMillis();
        
        int validBoardsCreated = 0;
        int invalidBoardsCreated = 0;
        int unsolvableBoardsCreated = 0;

        System.out.println("N=" + n);
        System.out.println("X=" + emptyCells);

        while (validBoardsCreated < n) {
            // Δημιουργία τελείως τυχαίου board με X κενά
            int[][] randomPuzzle = generateRandomBoard(emptyCells);
            
            // 1. Έλεγχος αν παραβιάζει τους κανόνες του Sudoku
            if (!isValidBoard(randomPuzzle)) {
                invalidBoardsCreated++;
            } else {
                // 2. Αν είναι έγκυρο, προσπαθούμε να το λύσουμε
                int[][] solvedPuzzle = copyBoard(randomPuzzle);
                if (solve(solvedPuzzle)) {
                    validBoardsCreated++;
                    
                    System.out.println("Board #" + validBoardsCreated);
                    printBoard(randomPuzzle);
                    System.out.println("Solution of the Board #" + validBoardsCreated);
                    printBoard(solvedPuzzle);
                } else {
                    // Ήταν έγκυρο, αλλά ο αλγόριθμος απέτυχε να βρει λύση
                    unsolvableBoardsCreated++;
                }
            }
        }

        long endTime = System.currentTimeMillis();
        double durationSec = (endTime - startTime) / 1000.0;

        // Εκτύπωση στατιστικών ακριβώς όπως στην εκφώνηση
        System.out.println("Empty cells per board    : " + emptyCells);
        System.out.println("Valid boards created     : " + validBoardsCreated);
        System.out.println("Invalid boards created   : " + invalidBoardsCreated);
        System.out.println("Unsolvable boards created: " + unsolvableBoardsCreated);
        System.out.println("Elapsed time in seconds  : " + durationSec);
    }

    static int[][] generateRandomBoard(int emptyCells) {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        int cellsToFill = 81 - emptyCells;
        Random rand = new Random();

        while (cellsToFill > 0) {
            int row = rand.nextInt(BOARD_SIZE);
            int col = rand.nextInt(BOARD_SIZE);

            if (newBoard[row][col] == 0) {
                newBoard[row][col] = rand.nextInt(9) + 1; // Τυχαίος αριθμός 1-9
                cellsToFill--;
            }
        }
        return newBoard;
    }

    static boolean isSolvableBoard(int[][] brd) {
        if (!isValidBoard(brd)) {
            return false;
        }
        int[][] tempBoard = copyBoard(brd);
        return solve(tempBoard);
    }

    static boolean isValidBoard(int[][] brd) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            boolean[] rowCheck = new boolean[BOARD_SIZE + 1];
            boolean[] colCheck = new boolean[BOARD_SIZE + 1];

            for (int j = 0; j < BOARD_SIZE; j++) {
                int rowVal = brd[i][j];
                if (rowVal < 0 || rowVal > MAX_VALUE) return false;
                if (rowVal != 0) {
                    if (rowCheck[rowVal]) return false;
                    rowCheck[rowVal] = true;
                }

                int colVal = brd[j][i];
                if (colVal < 0 || colVal > MAX_VALUE) return false;
                if (colVal != 0) {
                    if (colCheck[colVal]) return false;
                    colCheck[colVal] = true;
                }
            }
        }

        for (int rowOffset = 0; rowOffset < BOARD_SIZE; rowOffset += 3) {
            for (int colOffset = 0; colOffset < BOARD_SIZE; colOffset += 3) {
                boolean[] gridCheck = new boolean[BOARD_SIZE + 1];
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        int val = brd[rowOffset + i][colOffset + j];
                        if (val != 0) {
                            if (gridCheck[val]) return false;
                            gridCheck[val] = true;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    // Αντιγραφή ενός πίνακα 2D
    private static int[][] copyBoard(int[][] source) {
        int[][] dest = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(source[i], 0, dest[i], 0, BOARD_SIZE);
        }
        return dest;
    }

    // Εκτύπωση του board (αριθμοί χωρισμένοι με κενά, όπως στην εκφώνηση)
    private static void printBoard(int[][] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                System.out.print(board[row][column] + (column == BOARD_SIZE - 1 ? "" : " "));
            }
            System.out.println();
        }
    }

    private static boolean solve(int[][] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if (board[row][column] == 0) {
                    for (int k = MIN_VALUE; k <= MAX_VALUE; k++) {
                        board[row][column] = k;
                        if (isValidPlacement(board, row, column) && solve(board)) {
                            return true;
                        }
                        board[row][column] = 0;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isValidPlacement(int[][] board, int row, int column) {
        return (rowConstraint(board, row)
          && columnConstraint(board, column) 
          && subsectionConstraint(board, row, column));
    }

    private static boolean rowConstraint(int[][] board, int row) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        return checkConstraint(board, row, constraint);
    }

    private static boolean columnConstraint(int[][] board, int column) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        return checkConstraint(board, constraint, column);
    }

    private static boolean subsectionConstraint(int[][] board, int row, int column) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        int subsectionRowStart = (row / 3) * 3;
        int subsectionRowEnd = subsectionRowStart + 3;
        int subsectionColumnStart = (column / 3) * 3;
        int subsectionColumnEnd = subsectionColumnStart + 3;

        for (int r = subsectionRowStart; r < subsectionRowEnd; r++) {
            for (int c = subsectionColumnStart; c < subsectionColumnEnd; c++) {
                if (!checkConstraint(board, r, constraint, c)) return false;
            }
        }
        return true;
    }

    private static boolean checkConstraint(int[][] board, int row, boolean[] constraint) {
        for (int column = 0; column < BOARD_SIZE; column++) {
            if (!checkConstraint(board, row, constraint, column)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkConstraint(int[][] board, boolean[] constraint, int column) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (!checkConstraint(board, row, constraint, column)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkConstraint(int[][] board, int row, boolean[] constraint, int column) {
        if (board[row][column] != 0) {
            if (!constraint[board[row][column] - 1]) {
                constraint[board[row][column] - 1] = true;
            } else {
                return false;
            }
        }
        return true;
    }
}