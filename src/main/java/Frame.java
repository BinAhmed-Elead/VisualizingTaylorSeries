import javax.swing.*;

public class Frame extends JFrame {
    Panel panel;
    public Frame(){
        panel = new Panel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizeable(false);
        setVisible(true);


    }
}
