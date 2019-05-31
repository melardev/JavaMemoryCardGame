package com.melardev.game.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyBoard extends KeyAdapter {

    private boolean exit;

    public KeyBoard() {
        exit = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_END ||
                keyCode == KeyEvent.VK_C && e.isControlDown())
            exit = true;

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_Q || keyCode == KeyEvent.VK_END ||
                keyCode == KeyEvent.VK_C && e.isControlDown())
            exit = true;
    }

    public boolean hasPressedExit() {
        return exit;
    }


}
