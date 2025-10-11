package GUI.Handlers;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;

import AI.simpleGGException;
import GUI.UIManager;

class GameOverHandler{

    public GameOverHandler(simpleGGException e, int time, int scale, UIManager ui, byte[] UIKeyHash){

        JFrame append = new JFrame();

        if(e.getWon()){
            //Win

            ((JLabel)(((Box)(ui.getPanels(UIKeyHash)[2].getComponent(6))).getComponent(0))).setText("Score: " + String.valueOf(time));

            append.add(ui.getPanels(UIKeyHash)[2]);
            append.setMaximumSize(new Dimension(430, 500));
            append.setSize(430, 500);
        }

        else{
            //Lose

            append.add(ui.getPanels(UIKeyHash)[3]);
            append.setMaximumSize(new Dimension(400, 400));
            append.setSize(400, 400);
        }

        append.setLocation(60, 200);
        append.setVisible(true);
        append.addWindowListener(new WindowListener() {

            @Override
            public void windowClosing(WindowEvent e){System.exit(0);}

            public void windowOpened(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });

    }
}



class TimerIterator implements ActionListener{
    private JLabel clock;
    private int time;

    public TimerIterator(JLabel Clock, int Time){clock=Clock;time=Time;}

    public void actionPerformed(ActionEvent e){if(time<9999){time++; clock.setText(String.valueOf(time));}}
    public int getTime(){return time;}
}