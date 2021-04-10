package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;

public class Field {
    ShapeRenderer renderer;
    public enum FieldColor {
        COMPUTER, PLAYER, EMPTY;
    }

    private FieldColor value;

    public Field(ShapeRenderer renderer) {
        this.value = FieldColor.EMPTY;
        this.renderer = renderer;
    }

    public void draw(byte x, byte y, int size, byte rows, byte columns, float offset) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        if(value == FieldColor.EMPTY) {
            renderer.setColor(.8f, .8f, .8f, 1);
        } else if(value == FieldColor.COMPUTER) {
            renderer.setColor(Color.YELLOW);
        } else if(value == FieldColor.PLAYER) {
            renderer.setColor(Color.RED);
        }
        renderer.rect((Gdx.graphics.getWidth() - (offset + size)*columns)/2 + (offset + size)*y,
                (Gdx.graphics.getHeight() + (offset + size)*rows)/2 - (offset + size)*(x + 1), size, size);
        renderer.end();
    }

    public FieldColor getColor() {
        return this.value;
    }

    public void setColor(FieldColor value) {
        this.value = value;
    }
}
