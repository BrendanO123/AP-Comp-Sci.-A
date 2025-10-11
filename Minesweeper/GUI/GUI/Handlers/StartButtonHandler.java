package GUI.Handlers;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;

import BaseGame.*;
import GUI.ScreenMaker;
import AI.*;


public class StartButtonHandler implements ActionListener, MouseListener{

    private JPanel main;

    private byte[] GameKeyHash, AIKeyHash, UIKeyHash;
    private JTextField scale, ratio;

    private GUI.UIManager UI;
    private AI ai;
    private Game game;

    private boolean automatic;
    private JPanel[] panels;


    public StartButtonHandler(
        byte[] gameKeyHash, byte[] AIKeyHash, byte[] UIKeyHash,     JTextField scale, JTextField ratio, 
        GUI.UIManager UI, AI ai, Game game,     JPanel[] panels){

            this.GameKeyHash=gameKeyHash;
            this.AIKeyHash=AIKeyHash;
            this.UIKeyHash=UIKeyHash;

            this.scale=scale;
            this.ratio=ratio;

            this.UI=UI;
            this.ai=ai;
            this.game=game;

            this.panels=panels;
        }

    @Override
    public void mouseClicked(MouseEvent e) {

        int ScaleInt=16;
        try{ScaleInt = Integer.parseInt(scale.getText());}
        catch(NumberFormatException e1){}
        double RatioD=256/40.;
        try{RatioD = Integer.parseInt(ratio.getText())/100.;}
        catch(NumberFormatException e1){}
        
        game.setScale(ScaleInt, GameKeyHash);
        game.setRatio(RatioD, GameKeyHash);
        game.ShellStart(GameKeyHash);


        automatic=UI.getAutomatic();
        panels[1]=ScreenMaker.createDefaultScreen(game, UI, GameKeyHash, UIKeyHash, automatic);
        main=panels[1];


        JFrame frame= UI.getFrame(UIKeyHash);
        panels[4].setVisible(false);
        frame.remove(panels[4]);

        frame.add(main);
        main.setVisible(true);

        int scale=game.getScale();
        frame.setMinimumSize(new Dimension(scale*30, scale*30+148));
        frame.setSize(new Dimension(scale*30, scale*30+148));
        frame.repaint();

        UI.setCurrentPanelTracker(1, UIKeyHash);
        panels[1].setOpaque(true);

        if(automatic){ai.Start(AIKeyHash);}
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void actionPerformed(ActionEvent e) {}
}
