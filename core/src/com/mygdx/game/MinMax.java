package com.mygdx.game;

import com.mygdx.game.Field.FieldColor;

public class MinMax {
    public byte findMove(Field[][] gameBoard, FieldColor color) {
        byte move = -1;
        FieldColor[][] colorBoard = new FieldColor[CONSTANTS.ROWS][CONSTANTS.COLUMNS];
        for(byte i = 0; i < CONSTANTS.ROWS; i++)
            for(byte j = 0; j < CONSTANTS.COLUMNS; j++)
                colorBoard[i][j] = gameBoard[i][j].getColor();

        int heuristics = 0;
        int temp = 0;
        for (byte i = 0; i < CONSTANTS.COLUMNS; i++) {
            FieldColor[][] tempBoard = simulateMove(i, colorBoard, color);
            if (tempBoard != null){
                temp = recursiveHeuristic(tempBoard, color, 1, false);
                System.out.println(i + ": " + temp);
                if (move < 0 || temp > heuristics)
                {
                    heuristics = temp;
                    move = i;
                }
            }
        }

        return move;
    }

    private int recursiveHeuristic(FieldColor[][] colorBoard, FieldColor color, int depth, boolean isMin) {
        if (depth == CONSTANTS.MAX_DEPTH) {
            return combineHeuristic(colorBoard, color);
        } else {
            int heuristic = 0;
            boolean changed = false;
            int temp = 0;
            for (byte i = 0; i < CONSTANTS.COLUMNS; i++) {
                FieldColor[][] tempBoard = simulateMove(i, colorBoard, color);
                if (tempBoard != null) {
                    temp = recursiveHeuristic(tempBoard, color, 1, !isMin);
                    if (!isMin && (!changed || temp > heuristic)) {
                        heuristic = temp;
                        changed = true;
                    } else if (isMin && (!changed || temp < heuristic)) {
                        heuristic = temp;
                        changed = true;
                    }
                }
            }
            return heuristic;
        }
    }

    private int combineHeuristic(FieldColor[][] colorBoard, FieldColor color) {
        int result = 0;

        boolean[][] wasAllyChecked = new boolean[CONSTANTS.ROWS][CONSTANTS.COLUMNS];
        boolean[][] wasEnemyChecked = new boolean[CONSTANTS.ROWS][CONSTANTS.COLUMNS];
        for(byte i = 0; i < CONSTANTS.ROWS; i++)
            for(byte j = 0; j < CONSTANTS.COLUMNS; j++) {
                wasAllyChecked[i][j] = false;
                wasEnemyChecked[i][j] = false;
            }

        for(byte i = 0; i < CONSTANTS.ROWS; i++)
            for(byte j = 0; j < CONSTANTS.COLUMNS; j++) {
                if(colorBoard[i][j] == color) {
                    result += vertical(colorBoard, i, j, color, wasAllyChecked);
                    result += horizontal(colorBoard, i, j, color, wasAllyChecked);

                    wasAllyChecked[i][j] = true;
                } else if (colorBoard[i][j] != FieldColor.EMPTY) {
                    result -= vertical(colorBoard, i, j, colorBoard[i][j], wasEnemyChecked);
                    result -= horizontal(colorBoard, i, j, colorBoard[i][j], wasEnemyChecked);

                    wasEnemyChecked[i][j] = true;
                }
            }

        return result;
    }

    private int vertical(FieldColor[][] colorBoard, byte row, byte column, FieldColor color, boolean[][] wasChecked){
        int result = 0;
        byte tempup = 0, tempdown = 0;
        byte emptyAbove = 0;
        byte i = 0;
        while(i < CONSTANTS.WIN_VALUE && row + i < CONSTANTS.ROWS) { //down
            if(colorBoard[row + i][column] == color) {
                i++;
                tempdown++;
            } else if(colorBoard[row + i][column] != FieldColor.EMPTY) {
                break;
            }
        }

        i = 0;
        while(i < CONSTANTS.WIN_VALUE && row - i >= 0) { //up
            if(colorBoard[row - i][column] == color) {
                if(!wasChecked[row - i][column]) {
                    tempup++;
                } else {
                    break;
                }
            } else if(colorBoard[row - i][column] != FieldColor.EMPTY) {
                break;
            } else {
                emptyAbove++;
            }
            i++;
        }

        if(tempup + tempdown - CONSTANTS.WIN_VALUE > 0) {
            result += CONSTANTS.BONUS[3];
        }
        for (byte j = 1; j <= emptyAbove; j++) {
            if (tempup + tempdown - 1 >= CONSTANTS.WIN_VALUE - j) {
                result += CONSTANTS.BONUS[CONSTANTS.WIN_VALUE - j - 1];
            }
        }

        return result;
    }

    private int horizontal(FieldColor[][] colorBoard, byte row, byte column, FieldColor color, boolean[][] wasChecked){
        int result = 0;
        byte tempright = 0, templeft = 0;
        byte emptyright = 0, emptyleft = 0;
        byte i = 0;
        while(i < CONSTANTS.WIN_VALUE && column + i < CONSTANTS.COLUMNS) { //right
            if(colorBoard[row][column + i] == color) {
                tempright++;
            } else if(colorBoard[row][column + i] != FieldColor.EMPTY) {
                break;
            } else {
                emptyright++;
            }
            i++;
        }

        i = 0;
        while(i < CONSTANTS.WIN_VALUE && column - i >= 0) { //left
            if(colorBoard[row][column - i] == color) {
                if(!wasChecked[row][column - i]) {
                    templeft++;
                } else {
                    break;
                }
            } else if(colorBoard[row][column - i] != FieldColor.EMPTY) {
                break;
            } else {
                emptyleft++;
            }
            i++;
        }

        byte together = (byte)(tempright + templeft - 1);
        if(tempright + templeft - CONSTANTS.WIN_VALUE > 0) {
            result += CONSTANTS.BONUS[3];
        }

        for (byte j = 1; j <= emptyleft; j++) {
            if (together >= CONSTANTS.WIN_VALUE - j) {
                result += CONSTANTS.BONUS[CONSTANTS.WIN_VALUE - j - 1];
            }
        }
        for (byte j = 1; j <= emptyright; j++) {
            if (together >= CONSTANTS.WIN_VALUE - j) {
                result += CONSTANTS.BONUS[CONSTANTS.WIN_VALUE - j - 1];
            }
        }
        for (byte j = 1; j < CONSTANTS.WIN_VALUE - together; j++) {
            if((emptyright >= j) && (emptyleft >= CONSTANTS.WIN_VALUE - together - j)) {
                result += CONSTANTS.BONUS[together - 1];
            }
        }

        return result;
    }


    private FieldColor[][] simulateMove(byte column, FieldColor[][] colorBoard, FieldColor color) {
        FieldColor[][] result = new FieldColor[CONSTANTS.ROWS][CONSTANTS.COLUMNS];
        for(byte i = 0; i < CONSTANTS.ROWS; i++)
            for(byte j = 0; j < CONSTANTS.COLUMNS; j++)
                result[i][j] = colorBoard[i][j];

        for(byte r = (byte) (CONSTANTS.ROWS - 1); r >= 0; r--) {
            if(result[r][column] == FieldColor.EMPTY) {
                result[r][column] = color;
                return result;
            }
        }

        return null;
    }
}
