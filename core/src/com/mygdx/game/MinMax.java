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
            return Heuristic.combineHeuristic(colorBoard, color);
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
