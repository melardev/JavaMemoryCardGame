package com.melardev.game.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseHandler extends MouseAdapter {

    private int xPos;
    private int yPos;

    public MouseHandler() {
        xPos = -1;
        yPos = -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        xPos = e.getX();
        yPos = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        xPos = -1;
        yPos = -1;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public void reset() {
        xPos = -1;
        yPos = -1;
    }

}
