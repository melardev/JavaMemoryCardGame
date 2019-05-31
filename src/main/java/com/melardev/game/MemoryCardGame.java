package com.melardev.game;

import javax.swing.*;
import java.awt.*;

public class MemoryCardGame extends JFrame {

    public static final int MAX_SLEEP_SKIPPED = 50;
    public static final int MAX_SKIPS = 5;
    public static final int FPS = 20;
    public static final long NANO_SECS_TIME_PER_FRAME = 1_000_000_000L / FPS; // 1 sec / FPS

    public static final Color COLOR_TABLE = new Color(0x334C4C);
    public static final Color COLOR_TEXT = new Color(0x005C00);
    public static final Color COLOR_CARDS = new Color(0x99CCFF);

    public static final int CARDS_NUMBER = 26 * 2;
    public static final int ROWS_NUMBER = 7;
    public static final int COLS_NUMBER = 8;
    public static final int MARGIN = 20;
    public static final int GAP = 10;
    public static final int WIDTH_CARD = 70;
    public static final int HEIGHT_CARD = 70;

    public final static int GAME_WIDTH = (MARGIN + (COLS_NUMBER * WIDTH_CARD) + (GAP * (COLS_NUMBER - 1)) + MARGIN);
    public final static int GAME_HEIGHT = (MARGIN + (ROWS_NUMBER * HEIGHT_CARD) + (GAP * (ROWS_NUMBER - 1)) + MARGIN);

    public MemoryCardGame() {
        Container mainContentPane = getContentPane();
        mainContentPane.setLayout(new BorderLayout());
        MemoryPanel memoryPanel = new MemoryPanel(this);
        mainContentPane.add(memoryPanel, BorderLayout.CENTER);
        setUndecorated(false); //no effect when false

        // Ignore automatic paints, we are gonna paint what we need manually ourselves
        setIgnoreRepaint(true);

        pack(); // Pack so we get the insets populated
        // now we have the insets, let's adjust the size
        setSize(GAME_WIDTH + getInsets().left + getInsets().right,
                GAME_HEIGHT + getInsets().top + getInsets().bottom);


        setLocation(200, 200);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String args[]) {
        MemoryCardGame window = new MemoryCardGame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //there is already addShutdownCallback
    }

}
