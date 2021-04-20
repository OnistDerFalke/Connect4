package com.mygdx.game;

public class Heuristic {

    public static int combineHeuristic(Field.FieldColor[][] colorBoard, Field.FieldColor color) {
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
                    result += leftMiter(colorBoard, i, j, color, wasAllyChecked);
                    result += rightMiter(colorBoard, i, j, color, wasAllyChecked);

                    wasAllyChecked[i][j] = true;
                } else if (colorBoard[i][j] != Field.FieldColor.EMPTY) {
                    result -= vertical(colorBoard, i, j, colorBoard[i][j], wasEnemyChecked);
                    result -= horizontal(colorBoard, i, j, colorBoard[i][j], wasEnemyChecked);
                    result -= leftMiter(colorBoard, i, j, colorBoard[i][j], wasAllyChecked);
                    result -= rightMiter(colorBoard, i, j, colorBoard[i][j], wasAllyChecked);

                    wasEnemyChecked[i][j] = true;
                }
            }

        return result;
    }


    private static int vertical(Field.FieldColor[][] colorBoard, byte row, byte column, Field.FieldColor color, boolean[][] wasChecked) {
        final byte strikeToWin = CONSTANTS.WIN_VALUE - 1;

        byte tab[] = new byte[2 * strikeToWin + 1];
        for (byte i = -strikeToWin; i <= strikeToWin; i++) {
            if (row + i < 0 || row + i >= CONSTANTS.ROWS)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row + i][column] != Field.FieldColor.EMPTY && colorBoard[row + i][column] != color)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row + i][column] == Field.FieldColor.EMPTY)
                tab[i + strikeToWin] = 0;
            else if (wasChecked[row + i][column])
                tab[i + strikeToWin] = -1;
            else
                tab[i + strikeToWin] = 1;
        }

        return combineBonus(tab);
    }

    private static int horizontal(Field.FieldColor[][] colorBoard, byte row, byte column, Field.FieldColor color, boolean[][] wasChecked) {
        final byte strikeToWin = CONSTANTS.WIN_VALUE - 1;

        byte tab[] = new byte[2 * strikeToWin + 1];
        for (byte i = -strikeToWin; i <= strikeToWin; i++) {
            if (column + i < 0 || column + i >= CONSTANTS.COLUMNS)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row][column + i] != Field.FieldColor.EMPTY && colorBoard[row][column + i] != color)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row][column + i] == Field.FieldColor.EMPTY)
                tab[i + strikeToWin] = 0;
            else if (wasChecked[row][column + i])
                tab[i + strikeToWin] = -1;
            else
                tab[i + strikeToWin] = 1;
        }

        return combineBonus(tab);
    }

    //lewy gorny -> prawy dolny
    private static int leftMiter(Field.FieldColor[][] colorBoard, byte row, byte column, Field.FieldColor color, boolean[][] wasChecked) {
        final byte strikeToWin = CONSTANTS.WIN_VALUE - 1;

        byte tab[] = new byte[2 * strikeToWin + 1];
        for (byte i = -strikeToWin; i <= strikeToWin; i++) {
            if (row + i < 0 || row + i >= CONSTANTS.ROWS || column + i < 0 || column + i >= CONSTANTS.COLUMNS)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row + i][column + i] != Field.FieldColor.EMPTY && colorBoard[row + i][column + i] != color)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row + i][column + i] == Field.FieldColor.EMPTY)
                tab[i + strikeToWin] = 0;
            else if (wasChecked[row + i][column + i])
                tab[i + strikeToWin] = -1;
            else
                tab[i + strikeToWin] = 1;
        }

        return combineBonus(tab);
    }

    //prawy gorny -> lewy dolny
    private static int rightMiter(Field.FieldColor[][] colorBoard, byte row, byte column, Field.FieldColor color, boolean[][] wasChecked) {
        final byte strikeToWin = CONSTANTS.WIN_VALUE - 1;

        byte tab[] = new byte[2 * strikeToWin + 1];
        for (byte i = -strikeToWin; i <= strikeToWin; i++) {
            if (row - i < 0 || row - i >= CONSTANTS.ROWS || column + i < 0 || column + i >= CONSTANTS.COLUMNS)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row - i][column + i] != Field.FieldColor.EMPTY && colorBoard[row - i][column + i] != color)
                tab[i + strikeToWin] = -1;
            else if (colorBoard[row - i][column + i] == Field.FieldColor.EMPTY)
                tab[i + strikeToWin] = 0;
            else if (wasChecked[row - i][column + i])
                tab[i + strikeToWin] = -1;
            else
                tab[i + strikeToWin] = 1;
        }

        return combineBonus(tab);
    }

    private static int combineBonus(byte[] tab) {
        int result = 0;

        for (byte i = 0; i < CONSTANTS.WIN_VALUE; i++) {
            boolean isFour = true;
            byte sum = 0;
            for (byte j = i; j < i + CONSTANTS.WIN_VALUE; j++) {
                if (tab[j] == -1)
                    isFour = false;
                else
                    sum += tab[j];
            }
            if(isFour) {
                result += CONSTANTS.BONUS[sum - 1];
            }
        }

        return result;
    }
}
