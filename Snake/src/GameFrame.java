import javax.swing.*;

public class GameFrame extends JFrame {
    public GamePanel game = new GamePanel();

    GameFrame(){
        this.setTitle("Snek");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(game);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public void reset() {
        this.remove(game);
        game = new GamePanel();
        this.add(game);
    }

}
