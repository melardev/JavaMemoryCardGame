package com.melardev.game;

import java.awt.*;

public class Card extends Rectangle {

    private int xIndex;
    private int yIndex;
    private char charHold;
    private boolean visible;

    public Card(int xOrigin, int yOrigin, int xTileIndex, int yTileIndex, char charHold) {
        super(xOrigin, yOrigin, MemoryCardGame.WIDTH_CARD, MemoryCardGame.HEIGHT_CARD);
        xIndex = xTileIndex;
        yIndex = yTileIndex;
        this.charHold = charHold;
        visible = false;
    }

    public int getXTileIndex() {
        return xIndex;
    }

    public void setXTileIndex(int xIndex) {
        this.xIndex = xIndex;
    }

    public int getYTileIndex() {
        return yIndex;
    }

    public void setYTileIndex(int yIndex) {
        this.yIndex = yIndex;
    }

    public char getChar() {
        return charHold;
    }

    public void setChar(char c) {
        charHold = c;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void draw(Graphics gImg) {
        gImg.setColor(MemoryCardGame.COLOR_CARDS);
        gImg.fillRect(x, y, width, height);
        if (isVisible()) {
            gImg.setColor(MemoryCardGame.COLOR_TEXT);
            FontMetrics metrics = gImg.getFontMetrics();
            int charWidth = metrics.charWidth(getChar());
            int charHeight = metrics.getHeight();
            int xChar = x + ((width - charWidth) / 2);
            int yChar = y + ((height + charHeight) / 2);
            gImg.drawString(Character.toString(getChar()), xChar, yChar);
        }
    }
}
