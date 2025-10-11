package GUI.Handlers;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;

import BaseGame.*;
import GUI.UIManager;
import AI.*;

import java.awt.*;
import java.io.*;
import javax.imageio.*;

@SuppressWarnings("unused")
public class buttonEvents implements ActionListener, MouseListener{

    private UIManager ui;
    private Game game;

    private byte[] GameKeyHash;
    private byte[] UIKeyHash;

    private JFrame frame;
    private JButton[][] buttons;
    private JPanel defaultPanel;

    private boolean automatic;
    private static boolean started=false;

    private JLabel flagCount;
    private JLabel clock;

    private int flagsLeft;
    private TimerIterator timer;
    private javax.swing.Timer superTimer;


    private final int scale;
    public static final int pointerAND=(((1<<28)-1)<<4);
    public static final int numAnd=((1<<4)-1);


    
    public buttonEvents(UIManager UI, byte[] GameKeyHash1, byte[] UIKeyHash1, JPanel main, boolean automatic){

        UIKeyHash=UIKeyHash1;   ui=UI;
        GameKeyHash=GameKeyHash1;   game=ui.getGame();

        buttons=ui.getButtons(UIKeyHash);
        frame=ui.getFrame(UIKeyHash);
        defaultPanel=main;

        this.automatic=automatic;
        scale=game.getScale();
        flagsLeft=game.getNumFlags();


        flagCount = new JLabel();
        
        try{flagCount = new JLabel(String.valueOf(flagsLeft), new ImageIcon(ImageIO.read(new File("GUI/GUI/flag_icon.png")).getScaledInstance(-1, 50, BufferedImage.SCALE_SMOOTH)), JLabel.LEFT);}
        catch(IOException e){System.err.println("Error loading asset:\n" + e.getMessage());}
        
        flagCount.setBounds(15, 50, 110, 50);
        flagCount.setFont(new Font("Font", Font.PLAIN, 28));
        flagCount.setHorizontalTextPosition(JLabel.RIGHT);
        flagCount.setVerticalTextPosition(JLabel.BOTTOM);
        flagCount.setIconTextGap(5);
        flagCount.setForeground(new Color(228, 228, 228));

        defaultPanel.add(flagCount);


        clock = new JLabel();

        try{clock = new JLabel("0", new ImageIcon(ImageIO.read(new File("GUI/GUI/clock_icon.png")).getScaledInstance(-1, 50, BufferedImage.SCALE_SMOOTH)), JLabel.LEFT);}
        catch(IOException e){System.err.println("Error loading asset:\n" + e.getMessage());}

        clock.setBounds((scale*30)-140, 50, 130, 50);
        clock.setFont(new Font("Font", Font.PLAIN, 28));
        clock.setHorizontalTextPosition(JLabel.RIGHT);
        clock.setVerticalTextPosition(JLabel.BOTTOM);
        clock.setIconTextGap(5);
        clock.setForeground(new Color(228, 228, 228));

        defaultPanel.add(clock);


        timer = new TimerIterator(clock, 0);
        superTimer = new javax.swing.Timer(1000, timer);
        superTimer.start();
    }



    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        if(ui.getCurrentPanelTracker()!=1){return;}
        if(!ui.getPanels(UIKeyHash)[1].isVisible()){return;}
        if(automatic){return;}


        int x = e.getComponent().getX()/Game.TileSize ;
        int y= (e.getComponent().getParent().getY())/Game.TileSize;
        if(y<0){return;}


        if((e.isControlDown() && e.getButton()==MouseEvent.BUTTON1) || e.getButton()== MouseEvent.BUTTON2){
            //LClick

            boolean place=game.getFlag((x+y*scale));

            if(!place){

                if(game.placeFlag(x+y*scale, GameKeyHash)){
                    //Flagging
                    if((((y & 1)==0)^((x & 1)==0))){

                        //Odd
                        buttons[x][y].setIcon(new ImageIcon(UIManager.Flag));
                        //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.Flag));
                    }
                    else{

                        //Even
                        buttons[x][y].setIcon(new ImageIcon(UIManager.darkFlag));
                        //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.darkFlag));
                    }

                    flagsLeft--;
                    flagCount.setText(String.valueOf(flagsLeft));
                }
            }
            else{

                if(game.removeFlag(x+y*scale, GameKeyHash)){

                    //Unflagging
                    if((((y & 1)==0)^((x & 1)==0))){

                        //Odd
                        buttons[x][y].setIcon(new ImageIcon(UIManager.Grass));
                        //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.Grass));
                    }
                    else{

                        //Even
                        buttons[x][y].setIcon(new ImageIcon(UIManager.darkGrass));
                        //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.darkGrass));
                    }

                    flagsLeft++;
                    flagCount.setText(String.valueOf(flagsLeft));
                }
            }
        }

        else if(e.getButton()==MouseEvent.BUTTON1){
                //RClick
                
                ArrayList<Integer> list;
                if(!started){
                    //Start

                    list=game.Start(x+y*scale, GameKeyHash);

                }
                else{
                    //Clear

                    try{list=game.clear(x+y*scale, GameKeyHash);}
                    catch(simpleGGException e1){
                        list=new ArrayList<Integer>();
                        freeze();
                        GameOverHandler gameEnder = new GameOverHandler(e1, timer.getTime(), scale, ui, UIKeyHash); //end game
                    }

                }
                if(list.size()==1){
                    //Normal Clear

                    if((((y & 1)==0)^((x & 1)==0))){

                        //Odd
                        buttons[x][y].setIcon(new ImageIcon(UIManager.getCleared(list.get(0) & numAnd)));
                        //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.getCleared(list.get(0) & numAnd)));

                    }
                    else{

                        //Even
                        buttons[x][y].setIcon(new ImageIcon(UIManager.getDarkCleared(list.get(0) & numAnd)));
                        //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.getDarkCleared(list.get(0) & numAnd)));

                    }

                }
                else{
                    //Auto Clear

                    int index;
                    for (int i : list){
                        index=((i & pointerAND)>>4);

                        x=index%scale;
                        y=index/scale;
                        if((((y & 1)==0)^((x & 1)==0))){

                            //Odd
                            buttons[x][y].setIcon(new ImageIcon(UIManager.getCleared(i & numAnd)));
                            //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.getCleared(i & numAnd)));

                        }
                        else{

                            //Even
                            buttons[x][y].setIcon(new ImageIcon(UIManager.getDarkCleared(i & numAnd)));
                            //buttons[x][y].setDisabledIcon(new ImageIcon(UIManager.getDarkCleared(i & numAnd)));

                        }
                }
                if(!started){
                    //Starting click

                    started=true;
                    game.revealInternal(GameKeyHash);
                    frame.repaint();

                }

            }
        }
        e.consume();
        return;
    }

    private void freeze(){
        superTimer.stop();
        this.automatic=true;
    }
    public void EndGame(simpleGGException e, byte[] Hashed){
        if(java.security.MessageDigest.isEqual(Hashed, UIKeyHash)){
            freeze();
            GameOverHandler gameEnder = new GameOverHandler(e, timer.getTime(), scale, ui, UIKeyHash);
        }
    }
    public void decrementFlagCount(byte[] Hashed){
        if(java.security.MessageDigest.isEqual(Hashed, UIKeyHash)){
            flagsLeft--;
            flagCount.setText(String.valueOf(flagsLeft));
        }
    }
    public void incrementFlagCount(byte[] Hashed){
        if(java.security.MessageDigest.isEqual(Hashed, UIKeyHash)){
            flagsLeft++;
            flagCount.setText(String.valueOf(flagsLeft));
        }
    }


    public void actionPerformed(ActionEvent e) {}
    public void mouseClicked(java.awt.event.MouseEvent e) {}
    public void mousePressed(java.awt.event.MouseEvent e) {}
    public void mouseEntered(java.awt.event.MouseEvent e) {}
    public void mouseExited(java.awt.event.MouseEvent e) {}
}