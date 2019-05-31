package com.melardev.game;


import com.melardev.game.input.KeyBoard;
import com.melardev.game.input.MouseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.melardev.game.MemoryCardGame.*;

public class MemoryPanel extends JComponent implements Runnable {

    private int numOfReports;
    private volatile boolean running;

    private BufferedImage gameImage;
    private int numberOfTimesGameUpdated;
    private KeyBoard keyBoard;
    private MemoryCardGame game;
    private Thread threadGame;
    private long startTime;
    private int sleepsSkipped;

    private int framesSkipped;
    private MouseHandler mouseHandler;
    private ArrayList<Card> cards;
    private ArrayList<Card> flipped;
    private ScheduledThreadPoolExecutor executor;
    private boolean isHiding;

    public MemoryPanel(MemoryCardGame game) {
        this.game = game;
        setSize(new Dimension(MemoryCardGame.GAME_WIDTH, MemoryCardGame.GAME_HEIGHT));
        executor = new ScheduledThreadPoolExecutor(1);
        sleepsSkipped = 0;
        setFocusable(true);
        running = false;
        numberOfTimesGameUpdated = 0;
        numOfReports = 0;
        framesSkipped = 0;
        gameImage = new BufferedImage(MemoryCardGame.GAME_WIDTH, MemoryCardGame.GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
        cards = new ArrayList<>();
        ArrayList<Character> charsUsed = new ArrayList<>();

        flipped = new ArrayList<>(2);

        // Generate the chars to be used
        for (int i = 0; i < (CARDS_NUMBER / 2); i++) {
            char c = (char) (65 + i);
            charsUsed.add(c);
            charsUsed.add(c);
        }

        // Shuffle the order of the chars
        Collections.shuffle(charsUsed, new Random(System.nanoTime()));

        // Create the Card objects that are going to be used in the game
        boolean exit = false;
        for (int row = 0; row < MemoryCardGame.ROWS_NUMBER; row++) {
            if (exit)
                break;
            for (int col = 0; col < MemoryCardGame.COLS_NUMBER; col++) {
                if (((col) + row * MemoryCardGame.COLS_NUMBER) >= 52) {
                    exit = true;
                    break;
                }

                int xTopLeft = MemoryCardGame.MARGIN + (MemoryCardGame.WIDTH_CARD + MemoryCardGame.GAP) * col;
                int yTopLeft = MemoryCardGame.MARGIN + (MemoryCardGame.HEIGHT_CARD + MemoryCardGame.GAP) * row;
                // char c = (char) (65 + (col + row * MemoryCardGame.COLS_NUMBER));
                // char tempo = charsUsed.remove(0);
                cards.add(new Card(xTopLeft, yTopLeft, col, row, charsUsed.remove(0)));
            }
        }

        mouseHandler = new MouseHandler();
        keyBoard = new KeyBoard();
        addKeyListener(keyBoard);
        addMouseListener(mouseHandler);
        requestFocus();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                running = false;
                StringBuilder stats = new StringBuilder();
                long secondsPassed = ((System.nanoTime() - startTime) / 1_000_000_000L);
                stats.append("updates per second : " + numberOfTimesGameUpdated / secondsPassed);
                stats.append("\nnumber of updates : " + numberOfTimesGameUpdated);
                stats.append("\ntime in seconds passed " + secondsPassed);
                stats.append("frames skipped" + framesSkipped);
                System.out.println(stats.toString());
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        System.out.println("Inside addNotify");
        if (!running || threadGame == null) {
            threadGame = new Thread(this, "Game Thread");
            threadGame.start();
            System.out.println("threadGame thread started");
        }
    }

    @Override
    public void run() {
        long now, lastTime, delta;
        long timeToSleepInMsec, temp;
        lastTime = System.nanoTime();
        startTime = lastTime;

        running = true;

        while (running) {
            now = System.nanoTime();
            delta = now - lastTime;
            lastTime = now;
            temp = delta / NANO_SECS_TIME_PER_FRAME;

            int skips = 0;
            while (temp > 1 && skips < MAX_SKIPS) {
                gameUpdate();
                System.out.println("Inside slow");
                skips++;
                temp -= 1;
            }

            framesSkipped += skips;
            gameUpdate();
            gameRender();
            timeToSleepInMsec = (NANO_SECS_TIME_PER_FRAME - ((System.nanoTime() - lastTime))) / 1_000_000L;
            try {
                if (timeToSleepInMsec > 0) {
                    // We are in the good road, let's sleep for a while
                    // System.out.println("Go to sleep " + timeToSleep);
                    Thread.sleep(timeToSleepInMsec);
                } else {
                    // We are running out of time, no sleeping, unless we did not sleep for MAX_SLEEP_SKIPPED times
                    sleepsSkipped++;
                    if (sleepsSkipped >= MAX_SLEEP_SKIPPED) {
                        sleepsSkipped = 0;
                        Thread.yield();
                        System.out.println("[MAX_SLEEP_SKIPPED] Sleeping because we had no chance to sleep before.");
                        // Should I add 50ms to LastTime ? Because now i have 50
                        // mils of delay ...
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long diffTimeSinceStartGame = (System.nanoTime() - startTime) / 1_000_000L;
            // Each ~2 sec set title
            if (diffTimeSinceStartGame > (2000L * (numOfReports + 1))) {
                numOfReports++;
                // Number Of Times updated / seconds passed since the start of the game
                this.game.setTitle("Updates per second : " + numberOfTimesGameUpdated / (diffTimeSinceStartGame / 1000L));
            }
        }
        System.out.println("exiting...");
        System.exit(0);
    }

    private void gameUpdate() {
        numberOfTimesGameUpdated++;
        if (isHiding)
            return;

        if (keyBoard.hasPressedExit())
            running = false;

        if (mouseHandler.getXPos() != -1 && mouseHandler.getYPos() != -1) {
            // We have 0 or 1 card flipped, so let's flip the one the user clicked
            if (flipped.size() < 2) {
                int xPos = mouseHandler.getXPos();
                int yPos = mouseHandler.getYPos();
                for (Card card : cards) {
                    if (card.contains(xPos, yPos)) {
                        System.out.printf("Inside [%d,%d] holds %s \n", card.getXTileIndex(), card.getYTileIndex(), card.getChar());
                        flipped.add(card);
                        card.setVisible(true);
                        mouseHandler.reset();
                        break;
                    }
                }
            }

            if (flipped.size() >= 2) {
                // If now we flipped 2, then if they don't match, wait a little bit before hiding them
                if (flipped.get(0).getChar() == flipped.get(1).getChar()) {
                    flipped.clear();
                } else {
                    isHiding = true;
                    // Let the card visible of half second
                    executor.schedule(() -> {
                        flipped.get(0).setVisible(false);
                        flipped.get(1).setVisible(false);
                        flipped.clear();
                        isHiding = false;
                    }, 500, TimeUnit.MILLISECONDS);
                }
            }
        } else {
            // flipped.clear();
        }
    }

    private void gameRender() {
        Graphics g = getGraphics();
        // Get a canvas to draw on
        Graphics gImg = gameImage.createGraphics(); // We must get graphics and use it
        // , We can not get graphics
        // inline each time because it
        // returns
        // one different graphics instance each time

        // Paint with base collor
        gImg.setColor(MemoryCardGame.COLOR_TABLE);
        gImg.fillRect(0, 0, gameImage.getWidth(), gameImage.getHeight());

        // Drawing the Memory Cards
        gImg.setFont(new Font("Verdan", 0, 30));
        for (Card c : cards) {
            c.draw(gImg);
        }

        gImg.setColor(MemoryCardGame.COLOR_TEXT);

        // Display the image
        g.drawImage(gameImage, 0, 0, null);
    }


    @Override
    public Dimension getPreferredSize() {
        super.getPreferredSize();
        return new Dimension(MemoryCardGame.GAME_WIDTH, MemoryCardGame.GAME_HEIGHT);
    }

    @Override
    public Dimension getSize(Dimension dimension) {
        super.getSize(dimension);
        return new Dimension(MemoryCardGame.GAME_WIDTH, MemoryCardGame.GAME_HEIGHT);
    }
}
