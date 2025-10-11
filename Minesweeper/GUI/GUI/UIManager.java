package GUI;
import java.awt.image.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import BaseGame.*;
import GUI.Handlers.*;
import AI.*;

public class UIManager {
    private JFrame frame;
    private Game game;
    private AI ai;

    private byte[] GameKeyHash;
    private byte[] AIKeyHash;
    private long key;

    private JButton[][] buttons;
    private JPanel[] panels;
    private buttonEvents bEvents;

    private boolean automatic=false;
    private int currentPanel;


    public static TilePainter painter;

    public static BufferedImage darkGrass = new BufferedImage(30, 30, 1);
    public static BufferedImage Grass = new BufferedImage(30, 30, 1);
    public static BufferedImage darkFlag = new BufferedImage(30, 30, 1);
    public static BufferedImage Flag = new BufferedImage(30, 30, 1);
    private static BufferedImage[] darkCleared = new BufferedImage[9];
    private static BufferedImage[] Cleared = new BufferedImage[9];


    public UIManager(JFrame Frame, Game game1, long key1, byte[] GameKeyHash1, byte[] AIKeyHash1, AI ai1){
        
        frame=Frame;
        game=game1;
        GameKeyHash=GameKeyHash1;
        key=key1;
        ai=ai1;
        AIKeyHash=AIKeyHash1;


        panels=new JPanel[5];
        panels[0]=ScreenMaker.createTitleScreen();
        panels[2]=ScreenMaker.createWinScreen();
        panels[3]=ScreenMaker.createLoseScreen();
        panels[4]=ScreenMaker.createPromptScreen(panels[0], this, Hasher.getHash(key));


        painter=Game.painter;
        darkGrass.setData(painter.getTile((byte)0b00000000, 0, 0));
        Grass.setData(painter.getTile((byte)0b00010000, 0, 0));
        darkFlag.setData(painter.getTile((byte)0b01100000, 0, 0));
        Flag.setData(painter.getTile((byte)0b01110000, 0, 0));

        for(byte i=0; i<9; i++){
            darkCleared[i]=new BufferedImage(30, 30, 1);
            darkCleared[i].setData(painter.getTile((byte)(0b10000000 + i), 0, 0));
        }
        for(byte i=0; i<9; i++){
            Cleared[i]= new BufferedImage(30, 30, 1);
            Cleared[i].setData(painter.getTile((byte)(0b10010000 + i), 0, 0));
        }

        automatic=false;
        currentPanel=0;
        UIManager ui = this;



        frame.setTitle("Mine Sweeper");
        frame.setMinimumSize(new Dimension(400, 420));
        frame.setSize(new Dimension(400, 420));
        frame.setLayout(null);
        frame.addWindowListener(new WindowListener() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowOpened(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });

        


        ((Box)panels[0].getComponent(3)).getComponent(0).addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                panels[0].setVisible(false);
                frame.remove(panels[0]);

                frame.add(panels[4]);
                panels[4].setVisible(true);

                panels[4].setOpaque(true);
                ui.setCurrentPanelTracker(4, Hasher.getHash(key));
            }

            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        ((Box)panels[0].getComponent(5)).getComponent(0).addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

                panels[0].setVisible(false);
                frame.remove(panels[0]);

                frame.add(panels[4]);
                panels[4].setVisible(true);
                panels[4].setOpaque(true);

                ui.setAutomatic(true, Hasher.getHash(key));
                ui.setCurrentPanelTracker(4, Hasher.getHash(key));
            }

            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });



        StartButtonHandler starter = new StartButtonHandler(
            GameKeyHash, AIKeyHash, Hasher.getHash(key), 

            ((JTextField)(((Box)(panels[4].getComponent(3))).getComponent(1))),
            ((JTextField)(((Box)(panels[4].getComponent(6))).getComponent(1))), 

            ui, ai, game, panels);

        ((Box)(panels[4].getComponent(8))).getComponent(1).addMouseListener(starter);


        panels[0].setVisible(true);
        panels[0].setOpaque(true);
        frame.add(panels[0]);

        frame.setVisible(true);
        panels[0].requestFocus();
    }

    //TODO: make screen scrollable for higher size games
    public static BufferedImage getDarkCleared(int index){return darkCleared[index];}
    public static BufferedImage getCleared(int index){return Cleared[index];}
    public Game getGame(){return game;}
    public boolean getAutomatic(){return automatic;}
    public int getCurrentPanelTracker(){return currentPanel;}


    public JFrame getFrame(byte[] Hashed){if(Hasher.equals(key, Hashed)){return frame;}     return new JFrame();}
    public JButton[][] getButtons(byte[] Hashed){if(Hasher.equals(key, Hashed)){return buttons;}    return new JButton[1][1];}
    public JPanel[] getPanels(byte[] Hashed){if(Hasher.equals(key, Hashed)){return panels;} return new JPanel[1];}
    
    public byte[] getAIKeyHash(byte[] Hashed, byte[] GameKeyHash){
        if(Hasher.equals(key, Hashed) && java.security.MessageDigest.isEqual(this.GameKeyHash, GameKeyHash)){ return AIKeyHash;}
        return new byte[1];
    }

    public void setAutomatic(boolean value, byte[] Hashed){if(Hasher.equals(key, Hashed) && currentPanel==0){automatic=value;}}
    public void setButtonList(JButton[][] buttons, byte[] Hashed){if(Hasher.equals(key, Hashed)){this.buttons=buttons;}}
    public void setButtonEventsReference(buttonEvents BEvents, byte[] Hashed){if(Hasher.equals(key, Hashed)){bEvents=BEvents;}}


    public void setCurrentPanelTracker(int value, byte[] Hashed){
        if(Hasher.equals(key, Hashed) && value>=0 && value<5 && panels[value].isVisible()){
            currentPanel=value;
        }
    }




    public void AIGameOver(simpleGGException e, byte[] Hash){
        if(automatic && Hasher.equals(key, Hash)){
            bEvents.EndGame(e, Hasher.getHash(key));
        }
    }

    public boolean AIPlaceFlag(int index, byte[] Hash){
        if(automatic && Hasher.equals(key, Hash)){

            bEvents.decrementFlagCount(Hasher.getHash(key));

            int scale=game.getScale();
            int x = index%scale;
            int y = index/scale;

            if((((y & 1)==0)^((x & 1)==0))){
                //Odd
                buttons[x][y].setIcon(new ImageIcon(Flag));
            }
            else{
                //Even
                buttons[x][y].setIcon(new ImageIcon(darkFlag));
            }
        }
        return false;
    }

    public boolean AIRemoveFlag(int index, byte[] Hash){
        if(automatic && Hasher.equals(key, Hash)){
            bEvents.incrementFlagCount(Hasher.getHash(key));

            int scale=game.getScale();
            int x = index%scale;
            int y = index/scale;

            if((((y & 1)==0)^((x & 1)==0))){
                //Odd
                buttons[x][y].setIcon(new ImageIcon(Grass));
            }
            else{
                //Even
                buttons[x][y].setIcon(new ImageIcon(darkGrass));
            }
        }
        return false;
    }



    public boolean AISetClearedTiles(ArrayList<Integer> tiles, byte[] Hash){

        if(automatic && Hasher.equals(key, Hash)){

            int index, x, y, scale;
            scale=game.getScale();

            for (int i : tiles){

                index=((i & buttonEvents.pointerAND)>>4);
                if(index <0 || index>=scale*scale){continue;}

                x=index%scale;
                y=index/scale;

                if((((y & 1)==0)^((x & 1)==0))){

                    //Odd
                    buttons[x][y].setIcon(new ImageIcon(UIManager.getCleared(i & buttonEvents.numAnd)));
                }
                else{

                    //Even
                    buttons[x][y].setIcon(new ImageIcon(UIManager.getDarkCleared(i & buttonEvents.numAnd)));
                }
            }
        }
        return false;
    }
}