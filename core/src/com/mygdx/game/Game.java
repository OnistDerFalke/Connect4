package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.Field.FieldColor;
import com.mygdx.game.GameLogic.GameEnding;

import java.awt.*;
import java.util.Random;

public class Game {
    private float top;
    private float bottom;
    private float[] leftSides;
    private float[] rightSides;
    private Field[][] gameBoard;
    private GameEnding gameEnding;

    public Game(Field[][] gameBoard, GameEnding gameEnding) {
        this.gameBoard = gameBoard;
        this.gameEnding = gameEnding;

        top = (Gdx.graphics.getHeight() - (CONSTANTS.SIZE + CONSTANTS.OFFSET)*CONSTANTS.ROWS)/2;
        bottom = (Gdx.graphics.getHeight() + (CONSTANTS.SIZE + CONSTANTS.OFFSET)*CONSTANTS.ROWS)/2;

        leftSides = new float[CONSTANTS.COLUMNS];
        for (byte i = 0; i < CONSTANTS.COLUMNS; i++)
            leftSides[i] = (Gdx.graphics.getWidth() - (CONSTANTS.SIZE + CONSTANTS.OFFSET)*CONSTANTS.COLUMNS)/2 + i*(CONSTANTS.SIZE + CONSTANTS.OFFSET);

        rightSides = new float[CONSTANTS.COLUMNS];
        for (byte i = 0; i < CONSTANTS.COLUMNS; i++)
            rightSides[i] = (Gdx.graphics.getWidth() - (CONSTANTS.SIZE + CONSTANTS.OFFSET)*CONSTANTS.COLUMNS)/2 + CONSTANTS.SIZE + i*(CONSTANTS.SIZE + CONSTANTS.OFFSET);
    }

    public GameEnding getGameEnding() {
        return gameEnding;
    }

    public Boolean playerMove() {
        if (Gdx.input.isTouched()) {
            Point cursorPosition = new Point(Gdx.input.getX(), Gdx.input.getY());
            if(cursorPosition.y >= top && cursorPosition.y <= bottom) {
                for(byte i = 0; i < CONSTANTS.COLUMNS; i++) {
                    if(cursorPosition.x >= leftSides[i] && cursorPosition.x <= rightSides[i]) {
                        for(byte k = (byte) (CONSTANTS.ROWS - 1); k >= 0; k--) {
                            if(gameBoard[k][i].getColor() == FieldColor.EMPTY) {
                                gameBoard[k][i].setColor(FieldColor.PLAYER);
                                checkIfEnd(k, i, FieldColor.PLAYER);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public Boolean computerMove() {
        Random random = new Random();
        MinMax minmax = new MinMax();
        //int rand = 2;
        int rand = minmax.findMove(gameBoard, FieldColor.COMPUTER);

        // symulowanie ruchu przeciwnika
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(byte k = (byte) (CONSTANTS.ROWS - 1); k >= 0; k--) {
            if(gameBoard[k][rand].getColor() == FieldColor.EMPTY) {
                gameBoard[k][rand].setColor(FieldColor.COMPUTER);
                if(gameEnding == GameEnding.INGAME)
                    checkIfEnd(k, (byte)rand, FieldColor.COMPUTER);
                return true;
            }
        }
        return false;
    }

    public void checkIfEnd(byte row, byte column, FieldColor color) {
        // pion
        byte counter = 0;
        for(byte i = 0; i < CONSTANTS.ROWS; i++) {
            if(gameBoard[i][column].getColor() == color) {
                counter++;
            } else {
                counter = 0;
            }

            if (counter >= 4) {
                setGameEnding(color);
                return;
            }
        }

        // poziom
        counter = 0;
        for(byte i = 0; i < CONSTANTS.COLUMNS; i++) {
            if(gameBoard[row][i].getColor() == color) {
                counter++;
            } else {
                counter = 0;
            }

            if (counter >= 4) {
                setGameEnding(color);
                return;
            }
        }

        // lewy-gorny -> prawy-dolny ukos
        byte tmpRow = row, tmpCol = column;
        while(tmpRow > 0 && tmpCol > 0) {
            tmpRow--;
            tmpCol--;
        }
        counter = 0;
        int min = Math.min(CONSTANTS.ROWS - tmpRow, CONSTANTS.COLUMNS - tmpCol); //tutaj bylo jeszcze -1
        for(byte i = 0; i < min; i++) {
            if(gameBoard[tmpRow][tmpCol].getColor() == color) {
                counter++;
            } else {
                counter = 0;
            }
            tmpRow++;
            tmpCol++;
            if (counter >= 4) {
                setGameEnding(color);
                return;
            }
        }

        // prawy-gorny -> lewy-dolny ukos
        tmpRow = row;
        tmpCol = column;
        while(tmpRow > 0 && tmpCol < CONSTANTS.COLUMNS - 1) {
            tmpRow--;
            tmpCol++;
        }
        counter = 0;
        min = Math.min(CONSTANTS.ROWS - tmpRow, tmpCol + 1);
        for(byte i = 0; i < min; i++) {
            if(gameBoard[tmpRow][tmpCol].getColor() == color) {
                counter++;
            } else {
                counter = 0;
            }
            tmpRow++;
            tmpCol--;
            if (counter >= 4) {
                setGameEnding(color);
                return;
            }
        }

        //zapelniona plansza
        boolean overflow = true;
        for(byte i = 0; i < CONSTANTS.COLUMNS; i++) {
            if(gameBoard[0][i].getColor() == FieldColor.EMPTY) {
                overflow = false;
            }
        }
        if (overflow) {
            setGameEnding(FieldColor.EMPTY);
        }
    }

    public void setGameEnding(FieldColor color) {
        if (color == FieldColor.PLAYER) {
            gameEnding = GameEnding.PLAYERWIN;
            System.out.println("player");
        } else if (color == FieldColor.COMPUTER) {
            gameEnding = GameEnding.COMPUTERWIN;
            System.out.println("computer");
        } else {
            gameEnding = GameEnding.DRAW;
        }
    }
}


