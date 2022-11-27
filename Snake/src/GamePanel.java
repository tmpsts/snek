import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    static final Color backgroundColor = new Color(25, 25, 25);
    static final Color snakeHeadColor = new Color(255, 255, 255);
    static final Color appleColor = new Color(255 , 0, 0);
    static final Color gOColor = new Color(255, 255, 255);
    static final Color scoreColor = new Color(255, 255, 255);

    static Color snakeRGBColor = new Color(254, 45, 45);
    static boolean redRising = false;
    static boolean greenRising = true;
    static boolean blueRising = false;
    static boolean redFalling = false;
    static boolean greenFalling = false;
    static boolean blueFalling = false;
    static boolean redDone = true;
    static boolean greenDone = false;
    static boolean blueDone = false;

    static final Font gOFont = new Font("HelveticaNeue", Font.BOLD, 75);
    static String gOText = "Game Over";

    static final Font retryFont = new Font("HelveticaNeue", Font.BOLD, 30);
    static String retryText = "press r to restart";

    static final Font scoreFont = new Font("HelveticaNeue", Font.BOLD, 40);
    static String scoreText = "Score: ";

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(backgroundColor);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){

        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);

        if(running) {
            /*
              Grid Lines for Debugging

              for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
              g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
              g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
              }
            */
            g.setColor(appleColor);
            g.fillRoundRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE, 19, 19);


            for (int i = 0; i < bodyParts; i++) {
                snakeRGBColor = doColorTick(snakeRGBColor.getRed(), snakeRGBColor.getGreen(), snakeRGBColor.getBlue());
                if (i == 0) {
                    g.setColor(snakeHeadColor);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(snakeRGBColor);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(scoreColor);
            g.setFont(scoreFont);
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(scoreText + applesEaten, (SCREEN_WIDTH - metrics.stringWidth(scoreText + applesEaten)) / 2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }

    public Color doColorTick(int r, int ge, int b) {
        if(!blueDone && greenRising && (ge != 255)){
            ge++;
            if(ge == 255) {
                greenRising = false;
                greenDone = true;
                redFalling = true;
            }
        }
        if(greenDone && redFalling && (r != 45)){
            r--;
            if(r == 45) {
                redFalling = false;
                redDone = false;
                blueRising = true;
            }
        }
        if(!redDone && blueRising && (b != 255)){
            b++;
            if(b == 255) {
                blueRising = false;
                blueDone = true;
                greenFalling = true;
            }
        }
        if(blueDone && greenFalling && (ge != 45)){
            ge--;
            if(ge == 45) {
                greenFalling = false;
                greenDone = false;
                redRising = true;
            }
        }
        if(!greenDone && redRising && (r != 255)){
            r++;
            if(r == 255) {
                redRising = false;
                redDone = true;
                blueFalling = true;
            }
        }
        if(redDone && blueFalling && (b != 45)){
            b--;
            if(b == 45){
                blueFalling = false;
                blueDone = false;
                greenRising = true;
            }
        }

        return new Color(r, ge, b);
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
    }

    public void move() {
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for(int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        //check if head touches left border
        if(x[0] < 0) {
            running = false;
        }
        //right border
        if(x[0] > SCREEN_WIDTH){
            running = false;
        }
        //top border
        if(y[0] < 0) {
            running = false;
        }
        //bottom border
        if(y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if(!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Show score
        g.setColor(scoreColor);
        g.setFont(scoreFont);
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.drawString(scoreText + applesEaten, (SCREEN_WIDTH - scoreMetrics.stringWidth(scoreText + applesEaten)) / 2, g.getFont().getSize());
        //Game Over text
        g.setColor(gOColor);
        g.setFont(gOFont);
        FontMetrics gOMetrics = getFontMetrics(g.getFont());
        g.drawString(gOText, (SCREEN_WIDTH - gOMetrics.stringWidth(gOText)) / 2, SCREEN_HEIGHT / 2);
        //Retry Text
        g.setColor(gOColor);
        g.setFont(retryFont);
        FontMetrics retryMetrics = getFontMetrics(g.getFont());
        g.drawString(retryText, (SCREEN_WIDTH - retryMetrics.stringWidth(retryText)) / 2, (SCREEN_HEIGHT / 2) + 50);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();


    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
            if (!running && (e.getKeyCode() == KeyEvent.VK_R)) {
               new GameFrame();
            }
        }
    }
}
