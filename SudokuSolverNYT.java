import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SudokuSolverNYT {

    public static void main(String[] args)   throws FileNotFoundException, UnsupportedEncodingException, IOException  {
        int[][] board = new int[9][9];
        int next = -1;
        boolean finish = false;
        int add = 0;

        URL url = new URL("https://www.nytimes.com/puzzles/sudoku/hard");
        Scanner sc = new Scanner(url.openStream());
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()) {
            sb.append(sc.next());
         }
         String result = sb.toString();
         System.out.println(result);

        int preRemove = result.indexOf("\"puzzle\":[");
        String isolated = result.substring(preRemove+10);
        preRemove = isolated.indexOf("\"puzzle\":[");
        isolated = isolated.substring(preRemove+10, preRemove+171);
        String reading[] = isolated.split(",");

        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++){
                next = Integer.parseInt(reading[add]);
                add++;
                board[i][j] = next;
            }
            printOut(board);


        //Loop to run over program
        while (!finish){
            int countZero = 0;
            int num = -10;
            int[] remaining = {1,2,3,4,5,6,7,8,9};


            for (int i = 0; i < 9; i++){
                for (int j = 0; j < 9; j++){
                    if (board[i][j] == 0){
                        remaining = checkRow(remaining, i, board);
                        remaining = checkColumn(remaining, j, board);
                        remaining = checkBox(remaining, i, j, board);
                        for (int k = 0; k < remaining.length; k++){
                            if (remaining[k] != 0)
                                num = remaining[k];
                            if (remaining[k] == 0)
                                countZero++;
                        }
                        if (countZero == 8){
                            board[i][j] = num;

                        }
                        else{
                            board = deduceMethod(i, j, board);
                        }
                        for (int x = 0; x < 9; x++)
                            remaining[x] = x + 1;
                        countZero = 0;
   
                    }
                }
            for (int l = 1; l <= 9; l++){
                board = pairBoxElimination(board, l);
                board = pairBoxEliminationWithMatchedPairs(board, l);
            }
            }



            finish = true;
            for (int r = 0; r < 9; r++)
                for (int c = 0; c < 9; c++)
                    if (board[r][c] == 0)
                        finish = false;

            System.out.println("Outputting:");            
            printOut(board);
    }
    

        //Display Final Puzzle and Save to File
        printOut(board);

}

    //Check Row
    public static int[] checkRow(int[] remaining, int r, int[][]board){
        for (int c = 0; c<9; c++)
              for (int i = 0; i < remaining.length; i++)
                if (board[r][c] == remaining[i])
                    remaining[i] = 0;
        return remaining;
    }

    //Check Column
    public static int[] checkColumn(int[] remaining, int c, int[][]board){
        for (int r = 0; r<9; r++)
              for (int i = 0; i < remaining.length; i++)
                if (board[r][c] == remaining[i])
                    remaining[i] = 0;
        return remaining;
    }

    //Check Box
    public static int[] checkBox(int[] remaining, int r, int c, int[][]board){
        //Find What Box it's in
        int box = findBox(r, c);
        int rowPos = -3, colPos = -3;
        //Find initial positions
        switch(box){
            case 1: rowPos = 0; colPos = 0; break;
            case 2: rowPos = 0; colPos = 3; break;
            case 3: rowPos = 0; colPos = 6; break;
            case 4: rowPos = 3; colPos = 0; break;
            case 5: rowPos = 3; colPos = 3; break;
            case 6: rowPos = 3; colPos = 6; break;
            case 7: rowPos = 6; colPos = 0; break;
            case 8: rowPos = 6; colPos = 3; break;
            case 9: rowPos = 6; colPos = 6; break;
        }

        //Check the Box
        for (int i = rowPos; i < rowPos+3; i++)
            for (int j = colPos; j < colPos+3; j++)
                for (int k = 0; k < remaining.length; k++)
                    if (board[i][j] == remaining[k])
                        remaining[k] = 0;
        return remaining;


    }

    //Find the 3x3
    public static int findBox(int r, int c){
        if (r < 3 && c < 3) return 1;
        if (r < 3 && c < 6) return 2;
        if (r < 3 && c < 9) return 3;
        if (r < 6 && c < 3) return 4;
        if (r < 6 && c < 6) return 5;
        if (r < 6 && c < 9) return 6;
        if (r < 9 && c < 3) return 7;
        if (r < 9 && c < 6) return 8;
        if (r < 9 && c < 9) return 9;
        return 0;
    }

    //Deduce Box Values Through Elimination
    public static int[][] deduceMethod(int r, int c, int[][] board) throws FileNotFoundException, UnsupportedEncodingException{
        int num = -10;
        int countZero = 0;
        int [] remaining;
        remaining = findRemaining(r, c, board);
        // System.out.println("\n" + toString(remaining));

        remaining = deduceRow(remaining, r, board);
        remaining = deduceCol(remaining, c, board);
        remaining = deduceBox(remaining, r, c, board);
        for (int k = 0; k < remaining.length; k++){
            if (remaining[k] != 0)
                num = remaining[k];
            if (remaining[k] == 0)
                countZero++;
         }
        if (countZero == 8){
            board[r][c] = num;
        }
        // printOut(board);
        return board;
    }

    //Method to find the direct remaining values of a given box
    public static int[] findRemaining(int r, int c, int[][] board){
            int[] remaining = {1,2,3,4,5,6,7,8,9};
            remaining = checkRow(remaining, r, board);
            remaining = checkColumn(remaining, c, board);
            remaining = checkBox(remaining, r, c, board);
            return remaining;
    }

    //Deduce through Row values
    public static int[] deduceRow (int[] remaining, int r, int[][]board){
        ArrayList<int[]> deduction = new ArrayList<int[]>();
        ArrayList<Integer> columns = new ArrayList<Integer>();
        int[]checker = remaining;
        int[] numbers = {1,2,3,4,5,6,7,8,9};
        // System.out.println("deduce row");
        for (int c = 0; c<9; c++)
            if (board[r][c] == 0){
                deduction.add(numbers);
                columns.add(c);
            }
        
        for (int i = 0; i < deduction.size(); i++){
            deduction.set(i, checkColumn(findRemaining(r,columns.get(i), board), columns.get(i), board));
            deduction.set(i, checkBox(deduction.get(i), r, columns.get(i), board));
            // System.out.println(r + " " + i + " " + deduction.get(i)[0]+ deduction.get(i)[1]+ deduction.get(i)[2]+ deduction.get(i)[3]+ deduction.get(i)[4]+ deduction.get(i)[5]+ deduction.get(i)[6]+ deduction.get(i)[7]+ deduction.get(i)[8]);
        }
        // System.out.println(toString(remaining));
        for (int i = 0; i < deduction.size(); i++)
            for (int j = i+1; j < deduction.size(); j++)
                if (Arrays.equals(deduction.get(i), deduction.get(j))){
                    
                    int[] temp = deduction.get(i);
                    int number = 0;
                    int count = 2;

                    for (int n = 0; n < 9; n++)
                        if (temp[n] != 0)
                            number++;

                    for (int n = j+1; n < deduction.size(); n++){
                        if (Arrays.equals(deduction.get(n), temp)){
                            count++;
                        }
                    }

                    if (count==number){

                    if (!(Arrays.equals(temp, remaining)))
                        for (int k = 0; k < 9; k++)
                            if (temp[k] != 0){
                                remaining[k]=0;
                                // System.out.println(toString(remaining));
                            }
                }
            }

                if (Arrays.equals(checker, remaining)){
                // System.out.println(c + " " + toString(remaining));
                remaining = valueScan(remaining, deduction);
            }
                if (Arrays.equals(checker, remaining)){
                    remaining = matchedPairs(remaining, deduction);
                }
      

        
        return remaining;

    }

    //Deduce Through Column Values
    public static int[] deduceCol (int[] remaining, int c, int[][]board){
        ArrayList<int[]> deduction = new ArrayList<int[]>();
        ArrayList<Integer> rows = new ArrayList<Integer>();
        int[] numbers = {1,2,3,4,5,6,7,8,9};
        int[] checker = remaining;
        // System.out.println("deduce col");
        for (int r = 0; r<9; r++)
            if (board[r][c] == 0){
                deduction.add(numbers);
                rows.add(r);
            }
        
        for (int i = 0; i < deduction.size(); i++){
            deduction.set(i, checkRow(findRemaining(rows.get(i), c, board), rows.get(i), board));
            deduction.set(i, checkBox(deduction.get(i), rows.get(i), c, board));
            // System.out.println(c + " " + i + " " + deduction.get(i)[0]+ deduction.get(i)[1]+ deduction.get(i)[2]+ deduction.get(i)[3]+ deduction.get(i)[4]+ deduction.get(i)[5]+ deduction.get(i)[6]+ deduction.get(i)[7]+ deduction.get(i)[8]);
        }
        // System.out.println(toString(remaining));
        for (int i = 0; i < deduction.size(); i++)
            for (int j = i+1; j < deduction.size(); j++)
                if (Arrays.equals(deduction.get(i), deduction.get(j))){
                    
                    int[] temp = deduction.get(i);
                    int number = 0;
                    int count = 2;

                    for (int n = 0; n < 9; n++)
                        if (temp[n] != 0)
                            number++;

                    for (int n = j+1; n < deduction.size(); n++){
                        if (Arrays.equals(deduction.get(n), temp)){
                            count++;
                        }
                    }

                    if (count==number){

                    if (!(Arrays.equals(temp, remaining)))
                        for (int k = 0; k < 9; k++)
                            if (temp[k] != 0){
                                remaining[k]=0;
                                // System.out.println(toString(remaining));
                            }
                }
                
            }

            if (Arrays.equals(checker,remaining)){
                // System.out.println(c + " " + toString(remaining));
                remaining = valueScan(remaining, deduction);
            }
                if (Arrays.equals(checker, remaining)){
                    remaining = matchedPairs(remaining, deduction);
                }



        
        return remaining;

    }

    //Deduce through Box Values
    public static int[] deduceBox (int[] remaining, int r, int c, int[][]board){
        ArrayList<int[]> deduction = new ArrayList<int[]>();
        ArrayList<Integer> rows = new ArrayList<Integer>();
        ArrayList<Integer> columns = new ArrayList<Integer>();
        int[]checker = remaining;
        // System.out.println("deduce Box");
        int[] numbers = {1,2,3,4,5,6,7,8,9};

        int box = findBox(r,c);
        int rowPos = -3, colPos = -3;
        switch(box){
            case 1: rowPos = 0; colPos = 0; break;
            case 2: rowPos = 0; colPos = 3; break;
            case 3: rowPos = 0; colPos = 6; break;
            case 4: rowPos = 3; colPos = 0; break;
            case 5: rowPos = 3; colPos = 3; break;
            case 6: rowPos = 3; colPos = 6; break;
            case 7: rowPos = 6; colPos = 0; break;
            case 8: rowPos = 6; colPos = 3; break;
            case 9: rowPos = 6; colPos = 6; break;
        }

        for (int i = rowPos; i < rowPos+3; i++)
            for (int j = colPos; j < colPos+3; j++)
                if (board[i][j] == 0){
                    deduction.add(numbers);
                    rows.add(i);
                    columns.add(j);
                }
        
        for (int i = 0; i < deduction.size(); i++){
            deduction.set(i, checkRow(findRemaining(rows.get(i), columns.get(i), board), rows.get(i), board));
            deduction.set(i, checkColumn(deduction.get(i), columns.get(i), board));
        }

        for (int i = 0; i < deduction.size(); i++)
            for (int j = i+1; j < deduction.size(); j++)
                if (Arrays.equals(deduction.get(i), deduction.get(j))){
                    
                    int[] temp = deduction.get(i);
                    int number = 0;
                    int count = 2;

                    for (int n = 0; n < 9; n++)
                        if (temp[n] != 0)
                            number++;

                    for (int n = j+1; n < deduction.size(); n++){
                        if (Arrays.equals(deduction.get(n), temp)){
                            count++;
                        }
                    }

                    if (count==number){

                    if (!(Arrays.equals(temp, remaining)))
                        for (int k = 0; k < 9; k++)
                            if (temp[k] != 0){
                                remaining[k]=0;
                                // System.out.println(toString(remaining));
                            }
                }
            }
            if (Arrays.equals(checker,remaining)){
                // System.out.println(c + " " + toString(remaining));
                remaining = valueScan(remaining, deduction);
            }
                if (Arrays.equals(checker, remaining)){
                    remaining = matchedPairs(remaining, deduction);
                }

        
        return remaining;

    }


    public static int[] valueScan(int[] remaining, ArrayList<int[]> deduction){
        int counter[] = {0,0,0,0,0,0,0,0,0};
        for (int i = 0; i < deduction.size(); i++){
            for (int j = 0; j < 9; j++){
                if (deduction.get(i)[j] != 0)
                    counter[j]++;
            }
            // System.out.println(i + " " + deduction.get(i)[0]+ deduction.get(i)[1]+ deduction.get(i)[2]+ deduction.get(i)[3]+ deduction.get(i)[4]+ deduction.get(i)[5]+ deduction.get(i)[6]+ deduction.get(i)[7]+ deduction.get(i)[8]);

        }
        
        for (int i = 0; i < 9; i++)
            if (counter[i] == 1 && remaining[i] == i+1){
                for (int k = 0; k < 9; k++){
                remaining[k]=0;
            }
                remaining[i] = i+1;
        }
        // toString(remaining);

        return remaining;
    }

    public static int[] matchedPairs(int[] remaining, ArrayList<int[]> deduction){
        int counter[] = {0,0,0,0,0,0,0,0,0};
        int pairCounter = 0;
        int[] pair = new int[2];
        int countingJustTwo = 0;
        int k = 0;
        
        // System.out.println("\n " + deduction.size());
        for (int i = 0; i < deduction.size(); i++){
            for (int j = 0; j < 9; j++){
                if (deduction.get(i)[j] != 0)
                    counter[j]++;
            }
            // System.out.println(i + " " + deduction.get(i)[0]+ deduction.get(i)[1]+ deduction.get(i)[2]+ deduction.get(i)[3]+ deduction.get(i)[4]+ deduction.get(i)[5]+ deduction.get(i)[6]+ deduction.get(i)[7]+ deduction.get(i)[8]);
        }

        for (int i = 0; i < counter.length; i++)
            if (counter[i] == 2)
                pairCounter++;

        while (pairCounter > 2){
            int x = (int)(Math.random()*8);
            if (counter[x] == 2){
                counter[x] = 0;
                pairCounter--;
            }
        }
        
        if (pairCounter==2){
            for (int i = 0; i < counter.length; i++)
                if (counter[i] == 2){
                    pair[k] = i+1;
                    k++;
                }
            for (int i = 0; i < remaining.length; i++){
                if (remaining[i] == pair[0] || remaining[i] == pair[1])
                    return remaining;
            }

            for (int i = 0; i < deduction.size(); i++){
                int num = 0;
                for (int j = 0; j < 9; j++){
                    if (deduction.get(i)[j] == pair[1] || deduction.get(i)[j] == pair[0])
                        num++;
                }
                if (num==2)
                    countingJustTwo++; 
            }

            if (countingJustTwo!=2)
                return remaining;


        for (int i = 0; i < deduction.size(); i++){
            for (int j = 0; j < 9; j++){
                if (deduction.get(i)[j] == pair[0] || deduction.get(i)[j] == pair[1]){
                    for (int n = 0; n < 9; n++){
                        if (deduction.get(i)[n] != pair[0] && deduction.get(i)[n] != pair[1]){
                            deduction.get(i)[n] = 0;
                        }
                    }
                    // System.out.println(toString(deduction.get(i)));
                }
            }
        }
    
        int countZero = 0;

        int[] remaining1 = valueScan(remaining, deduction);

        for (int i = 0; i < 9; i++){
            if (remaining1[i]!=0)
                countZero++;

        }
        if(countZero == 8){
            for (int i = 0; i < 9; i++)
                if (remaining1[i] == remaining[i])
                    remaining = remaining1;
        }

        // System.out.println(toString(remaining1));
        }
        return remaining;
    }

    public static int[][] pairBoxElimination (int[][] board, int box) throws FileNotFoundException, UnsupportedEncodingException{

        ArrayList<int[]> vector = new ArrayList<int[]>();
        ArrayList<Integer> rowSpace = new ArrayList<Integer>();
        ArrayList<Integer> columnSpace = new ArrayList<Integer>();
        int[] count = {0,0,0,0,0,0,0,0,0};
        int countZero = 0;
        int deductionCounter = 0;
        int num = -1;

        ArrayList<int[]> tempList = new ArrayList<int[]>();
        ArrayList<Integer> tempRows = new ArrayList<Integer>();
        ArrayList<Integer> tempCols = new ArrayList<Integer>();
        int rowPos = -1;
        int colPos = -1;
        int temp = -3;

        ArrayList<int[]> spaceList = new ArrayList<int[]>();
        ArrayList<Integer> rowOrColumnList = new ArrayList<Integer>();
        ArrayList<Integer> rowOrColumnListFor3s = new ArrayList<Integer>();
        ArrayList<int[]> deduction = new ArrayList<int[]>();

        switch(box){
            case 1: rowPos = 0; colPos = 0; break;
            case 2: rowPos = 0; colPos = 3; break;
            case 3: rowPos = 0; colPos = 6; break;
            case 4: rowPos = 3; colPos = 0; break;
            case 5: rowPos = 3; colPos = 3; break;
            case 6: rowPos = 3; colPos = 6; break;
            case 7: rowPos = 6; colPos = 0; break;
            case 8: rowPos = 6; colPos = 3; break;
            case 9: rowPos = 6; colPos = 6; break;
        }

        for (int r = rowPos; r < rowPos+3; r++){
            for (int c = colPos; c < colPos+3; c++){
                if (board[r][c] == 0){
                    vector.add(findRemaining(r, c, board));
                    columnSpace.add(c);
                    rowSpace.add(r);
                }
            }
        }

        for (int i = 0; i < vector.size(); i++){
            for (int j = 0; j < 9; j++){
                 if (vector.get(i)[j] != 0){
                    count[j]++;
                 }
            }
        }

        for (int i = 0; i < 9; i++){
            if (count[i] == 2 && Math.random() > 0.5){
                for (int j = 0; j < vector.size(); j++){
                    for (int k = 0; k < 9; k++){
                        if (vector.get(j)[k] == i+1){
                            tempList.add(vector.get(j));
                            tempRows.add(rowSpace.get(j));
                            tempCols.add(columnSpace.get(j));
                        }
                    }
                }
                temp = i+1;
                break;
            }
        }

        if (tempCols.size() ==2 && Math.random() > 0.5){
            if (tempCols.get(0).equals(tempCols.get(1))){
                // System.out.println("\nRunning Column " + box + " " + temp);
                for (int r = 0; r < 9; r++){
                    if (board[r][tempCols.get(0)] == 0){
                        // System.out.println("Row " + r);
                        if (findBox(r, tempCols.get(0)) != box){
                            // System.out.println(toString(findRemaining(r, tempCols.get(0), board)));
                            // System.out.println(toString(removeZeroes(findRemaining(r, tempCols.get(0), board))));
                            spaceList.add(removeZeroes(findRemaining(r, tempCols.get(0), board)));
                            rowOrColumnList.add(r);
                        }
                    }
                }
                
                for (int i = 0; i < spaceList.size(); i++){
                    if (spaceList.get(i).length == 2){
                        if (spaceList.get(i)[0] == temp || spaceList.get(i)[1] == temp){
                            for (int j = 0; j < 2; j++){
                                if (spaceList.get(i)[j] != temp){
                                    // printOut(board);
                                    // System.out.println();
                                    board[rowOrColumnList.get(i)][tempCols.get(0)] = spaceList.get(i)[j];
                                    // printOut(board);
                                }
                            }
                        }
                    }
                    if(spaceList.get(i).length == 3 && (spaceList.get(i)[0] == temp || spaceList.get(i)[1] == temp || spaceList.get(i)[2] == temp)){
                        for (int c = 0; c < 9; c++){
                            if (board[rowOrColumnList.get(i)][c] == 0){
                                deduction.add(findRemaining(rowOrColumnList.get(i), c, board));;
                                    if (c == tempCols.get(0)){
                                        for (int n = 0; n < 9; n++){
                                            if (deduction.get(deductionCounter)[n] == temp){
                                                deduction.get(deductionCounter)[n] = 0;
                                            }
                                        }
                                    }
                                deductionCounter++;
                                rowOrColumnListFor3s.add(c);
                            }
                        }

                        for (int j = 0; j < deduction.size(); j++){
                            for (int k = 0; k < 9; k++){
                                if (valueScan(deduction.get(j), deduction)[k] == 0){
                                    countZero++;
                                }
                            }
                            if (countZero==8){
                                for (int k = 0; k < 9; k++){
                                    if (valueScan(deduction.get(j), deduction)[k] != 0){
                                        num = valueScan(deduction.get(j), deduction)[k];
                                    }
                                }
                                board[rowOrColumnList.get(i)][rowOrColumnListFor3s.get(j)] = num;
                            }
                            countZero = 0;
                        }
                    }

                }
            }
        }

        else if (tempRows.size() == 2 ){
            if (tempRows.get(0).equals(tempRows.get(1))){
                // System.out.println("\nRunning Row " + box + " " + temp);
                for (int c = 0; c < 9; c++){
                    if (board[tempRows.get(0)][c] == 0){
                        // System.out.println("Column " + c);
                        if (findBox(tempRows.get(0), c) != box){
                            // System.out.println(toString(findRemaining(tempRows.get(0), c, board)));
                            // System.out.println(toString(removeZeroes(findRemaining(tempRows.get(0), c, board))));
                            spaceList.add(removeZeroes(findRemaining(tempRows.get(0), c, board)));
                            rowOrColumnList.add(c);
                        }
                    }
                }
                
                for (int i = 0; i < spaceList.size(); i++){
                    if (spaceList.get(i).length == 2){
                        if (spaceList.get(i)[0] == temp || spaceList.get(i)[1] == temp){
                            for (int j = 0; j < 2; j++){
                                if (spaceList.get(i)[j] != temp){
                                    // printOut(board);
                                    // System.out.println();
                                    board[tempRows.get(0)][rowOrColumnList.get(i)] = spaceList.get(i)[j];
                                    // printOut(board);
                                }
                            }
                        }
                    }
                    if(spaceList.get(i).length == 3 && (spaceList.get(i)[0] == temp || spaceList.get(i)[1] == temp || spaceList.get(i)[2] == temp)){
                        for (int r = 0; r < 9; r++){
                            if (board[r][rowOrColumnList.get(i)] == 0){
                                deduction.add(findRemaining(r, rowOrColumnList.get(i), board));;
                                    if (r == tempRows.get(0)){
                                        for (int n = 0; n < 9; n++){
                                            if (deduction.get(deductionCounter)[n] == temp){
                                                deduction.get(deductionCounter)[n] = 0;
                                            }
                                        }
                                    }
                                deductionCounter++;
                                rowOrColumnListFor3s.add(r);
                            }
                        }

                        for (int j = 0; j < deduction.size(); j++){
                            for (int k = 0; k < 9; k++){
                                if (valueScan(deduction.get(j), deduction)[k] == 0){
                                    countZero++;
                                }
                            }
                            if (countZero==8){
                                for (int k = 0; k < 9; k++){
                                    if (valueScan(deduction.get(j), deduction)[k] != 0){
                                        num = valueScan(deduction.get(j), deduction)[k];
                                    }
                                }
                                board[rowOrColumnListFor3s.get(j)][rowOrColumnList.get(i)] = num;
                            }
                            countZero = 0;
                        }
                    }
                }
            }
        }


        return board;



}


    public static int[][] pairBoxEliminationWithMatchedPairs (int[][] board, int box) throws FileNotFoundException, UnsupportedEncodingException{

        ArrayList<int[]> vector = new ArrayList<int[]>();
        ArrayList<int[]> vector1 = new ArrayList<int[]>();
        ArrayList<Integer> rowSpace = new ArrayList<Integer>();
        ArrayList<Integer> columnSpace = new ArrayList<Integer>();
        int[] count = {0,0,0,0,0,0,0,0,0};

        ArrayList<int[]> tempList = new ArrayList<int[]>();
        ArrayList<Integer> tempRows = new ArrayList<Integer>();
        ArrayList<Integer> tempCols = new ArrayList<Integer>();
        int rowPos = -1;
        int colPos = -1;
        int temp = -3;

        ArrayList<int[]> spaceList = new ArrayList<int[]>();
        ArrayList<Integer> rowOrColumnList = new ArrayList<Integer>();

        switch(box){
            case 1: rowPos = 0; colPos = 0; break;
            case 2: rowPos = 0; colPos = 3; break;
            case 3: rowPos = 0; colPos = 6; break;
            case 4: rowPos = 3; colPos = 0; break;
            case 5: rowPos = 3; colPos = 3; break;
            case 6: rowPos = 3; colPos = 6; break;
            case 7: rowPos = 6; colPos = 0; break;
            case 8: rowPos = 6; colPos = 3; break;
            case 9: rowPos = 6; colPos = 6; break;
        }

        for (int r = rowPos; r < rowPos+3; r++){
            for (int c = colPos; c < colPos+3; c++){
                if (board[r][c] == 0){
                    vector.add(findRemaining(r, c, board));
                    columnSpace.add(c);
                    rowSpace.add(r);
                }
            }
        }

        if (vector.size() > 0){
            vector1 = forcePair(vector);
            for (int i = 0; i < vector1.size(); i++)
                System.out.println(box + "box \n" + toString(vector1.get(i)));
        }

        for (int i = 0; i < vector1.size(); i++){
            for (int j = 0; j < 9; j++){
                 if (vector1.get(i)[j] != 0){
                    count[j]++;
                 }
            }
        }

        for (int i = 0; i < 9; i++){
            if (count[i] == 2 && Math.random() > 0.5){
                for (int j = 0; j < vector1.size(); j++){
                    for (int k = 0; k < 9; k++){
                        if (vector1.get(j)[k] == i+1){
                            tempList.add(vector1.get(j));
                            tempRows.add(rowSpace.get(j));
                            tempCols.add(columnSpace.get(j));
                        }
                    }
                }
                temp = i+1;
                break;
            }
        }

        if (tempCols.size() ==2 ){
            if (tempCols.get(0).equals(tempCols.get(1))){
                System.out.println("\nRunning Column " + box + " " + temp);
                for (int r = 0; r < 9; r++){
                    if (board[r][tempCols.get(0)] == 0){
                        System.out.println("Row " + r);
                        if (findBox(r, tempCols.get(0)) != box){
                            // System.out.println(toString(findRemaining(r, tempCols.get(0), board)));
                            // System.out.println(toString(removeZeroes(findRemaining(r, tempCols.get(0), board))));
                            spaceList.add(removeZeroes(findRemaining(r, tempCols.get(0), board)));
                            rowOrColumnList.add(r);
                        }
                    }
                }
                
                for (int i = 0; i < spaceList.size(); i++){
                    if (spaceList.get(i).length == 2){
                        if (spaceList.get(i)[0] == temp || spaceList.get(i)[1] == temp){
                            for (int j = 0; j < 2; j++){
                                if (spaceList.get(i)[j] != temp){
                                    // printOut(board);
                                    // System.out.println();
                                    board[rowOrColumnList.get(i)][tempCols.get(0)] = spaceList.get(i)[j];
                                    // printOut(board);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (tempRows.size() == 2 ){
            if (tempRows.get(0).equals(tempRows.get(1))){
                System.out.println("\nRunning Row " + box + " " + temp);
                for (int c = 0; c < 9; c++){
                    if (board[tempRows.get(0)][c] == 0){
                        System.out.println("Column " + c);
                        if (findBox(tempRows.get(0), c) != box){
                            // System.out.println(toString(findRemaining(tempRows.get(0), c, board)));
                            // System.out.println(toString(removeZeroes(findRemaining(tempRows.get(0), c, board))));
                            spaceList.add(removeZeroes(findRemaining(tempRows.get(0), c, board)));
                            rowOrColumnList.add(c);
                        }
                    }
                }
                
                for (int i = 0; i < spaceList.size(); i++){
                    if (spaceList.get(i).length == 2){
                        if (spaceList.get(i)[0] == temp || spaceList.get(i)[1] == temp){
                            for (int j = 0; j < 2; j++){
                                if (spaceList.get(i)[j] != temp){
                                    printOut(board);
                                    System.out.println("HERE");
                                    board[tempRows.get(0)][rowOrColumnList.get(i)] = spaceList.get(i)[j];
                                    printOut(board);
                                }
                            }
                        }
                    }
                }
            }
        }


        return board;



}

    public static ArrayList<int[]> forcePair (ArrayList<int[]> vector){
        ArrayList<int[]> toReturn = new ArrayList<int[]>();
        for (int i = 0; i < vector.size(); i++){
            toReturn.add(vector.get(i));
        }

        ArrayList<Integer> index = new ArrayList<Integer>();

        int[] count = {0,0,0,0,0,0,0,0,0};
        int pairs = 0;
        int countingJustTwo = 0;

        System.out.println("\n\n");

        for (int i = 0; i < vector.size(); i++){
            for (int j = 0; j < 9; j++){
                if (vector.get(i)[j] != 0){
                    count[j]++;
                }
            }
            System.out.println(toString(vector.get(i)));
        }

        for (int i = 0; i < 9; i++){
            if (count[i] == 2){
                pairs++;
                index.add(i+1);
            }
        }

        while (pairs > 2){
            int x = (int)(Math.random()*8);
            if (count[x] == 2){
                count[x] = 0;
                pairs--;
            }
        }

        if (pairs == 2){
            for (int i = 0; i < vector.size(); i++){
                int num = 0;
                for (int j = 0; j < 9; j++){
                    if (vector.get(i)[j] == index.get(0) || vector.get(i)[j] == index.get(1)){
                        num++;
                    }
                }
                if (num==2){
                    countingJustTwo++;
                }
            }

            if (countingJustTwo == 2){
                for (int i = 0; i < vector.size(); i++){
                    for (int j = 0; j < 9; j++){
                        if (vector.get(i)[j] == index.get(0) || vector.get(i)[j] == index.get(1)){
                            for (int k = 0; k < 9; k++){
                                if (vector.get(i)[k] != index.get(1) && vector.get(i)[k] != index.get(0)){
                                    vector.get(i)[k] = 0;
                                }
                            }
                            toReturn.set(i, vector.get(i));
                        }
                    }
                }
            }
        }


        return toReturn;
    }

    public static int[] removeZeroes (int[] arr){
        ArrayList<Integer> newArray = new ArrayList<Integer>();  

        for (int i = 0; i < 9; i++){
            if (arr[i] != 0){
                newArray.add(arr[i]);
            }
        }
        int[] tobeReturned = new int[newArray.size()];

        for (int i = 0; i < newArray.size(); i++){
            tobeReturned[i] = newArray.get(i);
        }

        return tobeReturned;

    }

    //Display and Print to File
    public static void printOut (int[][] board)  throws FileNotFoundException, UnsupportedEncodingException{
        //Print to terminal
        for (int r = 0; r < board.length; r++){
            for (int c = 0; c < board.length; c++){
                System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }

        //Print to File
        PrintWriter writer = new PrintWriter("SolvedSudoku.txt", "UTF-8");

        writer.println("    ----------------------- ");
        for (int r = 0; r < board.length; r++){
            writer.println("   | " + board[r][0] + " " + board[r][1] + " " + board[r][2] + " | " + board[r][3] 
            + " " + board[r][4] + " " + board[r][5] + " | " + board[r][6] + " " + board[r][7] + " " + board[r][8] + " | ");
            if (r==2 || r==5)
                writer.println("   |-----------------------| ");
        }
        writer.println("    ----------------------- ");
        
        writer.close(); 
    }


    //Print Out Array (Solely for testing purposes)
    public static String toString(int[] num){
        String x = "";
        for (int i = 0; i < num.length; i++)
            x+=Integer.toString(num[i]);
        return x;
    }
}