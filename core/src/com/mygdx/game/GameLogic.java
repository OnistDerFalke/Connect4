package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Field.FieldColor;

public class GameLogic extends ApplicationAdapter {

    public enum GameEnding {
        INGAME, DRAW, PLAYERWIN, COMPUTERWIN;
    }

    private Game game;
    ShapeRenderer shapeRenderer;
    private final byte rows = 6;
    private final byte columns = 7;
    private final int size = 60;
    private final float offset = 0.1f*size;
    private Field[][] gameBoard;
    private Boolean playerMove;
    private GameEnding gameEnding;

    @Override
    public void create () {
        shapeRenderer = new ShapeRenderer();
        gameEnding = GameEnding.INGAME;
        playerMove = true;
        gameBoard = new Field[rows][columns];
        for(byte i = 0; i < rows; i++)
            for(byte j = 0; j < columns; j++)
                gameBoard[i][j] = new Field(shapeRenderer);
        game = new Game(rows, columns, size, offset, gameBoard, gameEnding);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(.8f, .8f, .8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        drawBackground();

        gameEnding = game.getGameEnding();
        if(gameEnding == GameEnding.INGAME) {
            if (playerMove) {
                playerMove = !game.playerMove();
            }
            for(byte i = 0; i < rows; i++)
                for(byte j = 0; j < columns; j++)
                    gameBoard[i][j].draw(i, j, size, rows, columns, offset);

            if (!playerMove) {
                    playerMove = game.computerMove();
            }
            for(byte i = 0; i < rows; i++)
                for(byte j = 0; j < columns; j++)
                    gameBoard[i][j].draw(i, j, size, rows, columns, offset);
        } else if (gameEnding == GameEnding.PLAYERWIN) {
           changeAllColor(FieldColor.PLAYER);
        } else if (gameEnding == GameEnding.COMPUTERWIN) {
            changeAllColor(FieldColor.COMPUTER);
        } else {
            changeAllColor(FieldColor.EMPTY);
        }
    }

    public void changeAllColor(FieldColor color) {
        for(byte i = 0; i < rows; i++) {
            for(byte j = 0; j < columns; j++) {
                gameBoard[i][j].setColor(color);
                gameBoard[i][j].draw(i, j, size, rows, columns, offset);
            }
        }
    }

    public void drawBackground() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(.06f, 0.22f, 0.79f,1);
        shapeRenderer.rect((Gdx.graphics.getWidth() - (size + offset)*columns)/2 - offset,
                (Gdx.graphics.getHeight() - (size + offset)*rows)/2 - offset,
                (size + offset)*columns + offset,
                (size + offset)*rows + offset);
        shapeRenderer.end();
    }
}