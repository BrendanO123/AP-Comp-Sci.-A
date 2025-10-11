package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;

import BaseGame.*;
import GUI.Handlers.*;

public class ScreenMaker{


    public static JPanel createTitleScreen(){
        

        JPanel panel = createBlankEndScreen();

        panel.setBackground(new Color(162,199,72));
        panel.setSize(400, 400);
        panel.add(Box.createVerticalStrut(25));



        Box TitleWrapper = new Box(BoxLayout.X_AXIS);

        JLabel Title = new JLabel("MineSweeper");

        Title.setFont(new Font("Font", Font.BOLD, 26));
        Title.setForeground(new Color(25,15,10));
        Title.setSize(new Dimension(110, 30));
        Title.setHorizontalAlignment(JLabel.CENTER);

        TitleWrapper.add(Title);



        panel.add(TitleWrapper);
        panel.add(Box.createVerticalStrut(100));



        Box playWrapper = new Box(BoxLayout.X_AXIS);

        JButton play = new JButton("Single Player");

        play.setFont(new Font("Font", Font.PLAIN, 20));
        play.setForeground(new Color(25,15,10));
        play.setSize(80, 35);
        play.setMinimumSize(new Dimension(80, 35));
        play.setVisible(true);
        play.setBorderPainted(true);

        playWrapper.add(play);




        Box AiWrapper = new Box(BoxLayout.X_AXIS);

        JButton spectate = new JButton("Test AI"/*"Coming Soon"*/);

        spectate.setFont(new Font("Font", Font.PLAIN, 20));
        spectate.setForeground(new Color(25,15,10));
        spectate.setSize(80, 35);
        spectate.setMinimumSize(new Dimension(80, 35));
        spectate.setBorderPainted(true);
        spectate.setVisible(true);

        AiWrapper.add(spectate);



        panel.add(playWrapper);
        panel.add(Box.createVerticalStrut(15));



        panel.add(AiWrapper);
        panel.add(Box.createVerticalStrut(200));


        return panel;
    }





    public static JPanel createPromptScreen(JPanel titleScreen, UIManager UI, byte[] Hashed){

        JPanel panel = createBlankEndScreen();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(Box.createVerticalStrut(15));
        panel.setBackground(new Color(198, 210, 235));



        Box settingWrapper = new Box(BoxLayout.X_AXIS);

        JLabel settings = new JLabel("Settings");

        settings.setFont(new Font("Font", Font.BOLD, 26));
        settings.setForeground(new Color(25,15,10));
        settings.setSize(new Dimension(110, 30));
        settings.setMaximumSize(new Dimension(110, 30));
        settings.setMinimumSize(new Dimension(110, 30));
        settings.setHorizontalAlignment(JLabel.CENTER);

        settingWrapper.add(settings);




        panel.add(settingWrapper);
        panel.add(Box.createVerticalStrut(45));




        Box scale = createInputBox(
            "Scale", 
        new String[]{"Small", "Medium", "Large"}, 
        new int[]{10, 16, 24}, 
        false, 0,
        8, 25);




        panel.add(scale);
        panel.add(Box.createVerticalStrut(12));
        panel.add(Box.createHorizontalGlue());





        Box mines = createInputBox(
            "Mine Ratio (%)", 
        new String[]{"Few", "Normal", "Hard", "Godly"}, 
        new int[]{13, 16, 18, 21}, 
        false, 0,
        13, 21);

        mines.setMaximumSize(new Dimension(230, 20));




        panel.add(mines);
        panel.add(Box.createVerticalStrut(200));




        Box buttons = new Box(BoxLayout.X_AXIS);

        JButton back = new JButton("Back");
        JButton continueButton = new JButton("Continue");

        back.setFont(new Font("Font", Font.PLAIN, 20));
        back.setForeground(new Color(25,15,10));
        back.setSize(80, 30);
        back.setMinimumSize(new Dimension(60, 20));
        back.setVisible(true);

        back.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                panel.setVisible(false);
                UI.getFrame(Hashed).remove(panel);

                UI.getFrame(Hashed).add(titleScreen);
                titleScreen.setVisible(true);

                UI.setAutomatic(false, Hashed);
                UI.setCurrentPanelTracker(0, Hashed);
            }

            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            });



        continueButton.setFont(new Font("Font", Font.PLAIN, 20));
        continueButton.setForeground(new Color(25,15,10));
        continueButton.setSize(80, 30);
        continueButton.setMinimumSize(new Dimension(60, 20));
        continueButton.setVisible(true);



        buttons.add(back);
        buttons.add(continueButton);






        panel.add(buttons);


        panel.setSize(400, 400);
        panel.setMinimumSize(new Dimension(400, 400));

        return panel;
    }


    public static JPanel createDefaultScreen(
        Game game, UIManager UI, byte[] GameKeyHash, byte[] UIKeyHash, boolean automatic){



        JPanel defaultPanel= new JPanel();
        JFrame frame=UI.getFrame(UIKeyHash);


        int scale=game.getScale();
        JButton[][] buttons = new JButton[scale][scale];
        UI.setButtonList(buttons, UIKeyHash);


        BufferedImage img = game.getCurrentImg();
        ButtonGroup group = new ButtonGroup();


        frame.setMinimumSize(new Dimension(scale*30, scale*30+149));
        defaultPanel.setMinimumSize(new Dimension(scale*30, scale*30+149));


        buttonEvents buttonEvents_Manager = new buttonEvents(UI, GameKeyHash, UIKeyHash, defaultPanel, automatic);
        UI.setButtonEventsReference(buttonEvents_Manager, UIKeyHash);

        
        Box buttonPanel = new Box(BoxLayout.Y_AXIS);
        Box[] rows = new Box[scale];
        for(int y=0; y<scale; y++){
            rows[y]=new Box(BoxLayout.X_AXIS);
            rows[y].setLocation(0, y*30);
            rows[y].setMaximumSize(new Dimension(30*scale, 30));
            rows[y].setMinimumSize(new Dimension(30*scale, 30));
            for (int x=0; x<scale; x++){
                buttons[x][y]=new JButton(new ImageIcon(img.getSubimage(x*30, y*30, 30, 30)));
                buttons[x][y].setBounds(x*30, y*30, 30, 30);
                buttons[x][y].setMaximumSize(new Dimension(30, 30));
                buttons[x][y].setMinimumSize(new Dimension(30, 30));
                buttons[x][y].addMouseListener(buttonEvents_Manager);
                rows[y].add(buttons[x][y]);
                group.add(buttons[x][y]);
            }
            buttonPanel.add(rows[y]);
        }
        buttonPanel.setBounds(0, 120, scale*30, scale*30);

        JLabel title = new JLabel("Mine Sweeper", JLabel.CENTER);

        title.setBounds(0, 10, scale*30, 120);
        title.setFont(new Font("Font", Font.BOLD, 30));
        title.setVerticalAlignment(JLabel.TOP);
        title.setBackground(new Color(74,117,44));
        title.setOpaque(true);
        title.setForeground(new Color(30,20,15));


        JLabel fill = new JLabel();

        fill.setBounds(0, 0, scale*30, 10);
        fill.setBackground(new Color(74,117,44));
        fill.setOpaque(true);
        

        JScrollPane buttonPane = new JScrollPane(buttonPanel);
        int xWidthBar = /*buttonPane.getVerticalScrollBar().getWidth()*/ 20;
        int yWidthBar = /*buttonPane.getHorizontalScrollBar().getWidth()*/ 20;
        int extent = scale*15;
        int buttonPanelSize = scale*30;

        defaultPanel.setMinimumSize(new Dimension(xWidthBar+extent, yWidthBar+120+extent));
        //defaultPanel.setMaximumSize(new Dimension(buttonPanelSize+xWidthBar, buttonPanelSize+120+yWidthBar));
        defaultPanel.setBounds(0, 0, buttonPanelSize+xWidthBar, buttonPanelSize+120+yWidthBar);

        buttonPane.setMinimumSize(new Dimension(xWidthBar+buttonPanelSize, yWidthBar+buttonPanelSize));
        //buttonPane.setMaximumSize(new Dimension(buttonPanelSize+xWidthBar, buttonPanelSize+xWidthBar));
        buttonPane.setBounds(0, 120, /*(int)defaultPanel.getBounds().getX()*/buttonPanelSize+xWidthBar, /*(int)defaultPanel.getBounds().getY()-120*/buttonPanelSize+yWidthBar);

        buttonPanel.setMinimumSize(new Dimension(buttonPanelSize, buttonPanelSize));
        buttonPanel.setMaximumSize(new Dimension(buttonPanelSize, buttonPanelSize));
        buttonPanel.setBounds(0, 0, buttonPanelSize, buttonPanelSize);

        buttonPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        buttonPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        int xVariance = (int)(buttonPanelSize-(buttonPane.getBounds().getX()-xWidthBar));
        int yVariance = (int)(buttonPanelSize-(buttonPane.getBounds().getY()-yWidthBar));

        if(xVariance!=0){buttonPane.getHorizontalScrollBar().setValues(0, xVariance, 0, xVariance);}
        if(yVariance!=0){buttonPane.getVerticalScrollBar().setValues(0, yVariance, 0, yVariance);}
        buttonPane.getViewport().setExtentSize(new Dimension(/*(int)(buttonPane.getBounds().getX()-xWidthBar)*/buttonPanelSize, /*(int)(buttonPane.getBounds().getY()-yWidthBar)*/buttonPanelSize));

        defaultPanel.setBackground(new Color(74,117,44));
        defaultPanel.setOpaque(true);

        defaultPanel.add(fill);
        defaultPanel.add(title);
        buttonPanel.setVisible(true);
        buttonPane.setVisible(true);
        defaultPanel.add(buttonPane);
        defaultPanel.setLayout(null);

        return defaultPanel;
    }





    public static JPanel createWinScreen(){
        return createWinScreen(0);
    }

    public static JPanel createWinScreen(int Score){

        JPanel panel = createBlankEndScreen();
        panel.add(Box.createRigidArea(new Dimension(25, 25)));


        Box mainWrapper = new Box(BoxLayout.X_AXIS);
        JLabel main = createDefaultLabel();
        main.setText("Game Over!");
        main.setFont(new Font("Font", Font.BOLD, 30));
        mainWrapper.add(main);

        panel.add(mainWrapper);
        panel.add(Box.createVerticalStrut(80));


        Box FirstLineWrapper = new Box(BoxLayout.X_AXIS);
        JLabel FirstLine = createDefaultLabel();
        FirstLine.setText("Congratulations!");
        FirstLine.setMinimumSize(new Dimension(400, 90));
        FirstLineWrapper.add(FirstLine);


        panel.add(FirstLineWrapper);


        Box SecondLineWrapper = new Box(BoxLayout.X_AXIS);
        JLabel SecondLine = createDefaultLabel();
        SecondLine.setText("You Win!");
        SecondLine.setMinimumSize(new Dimension(400, 60));
        SecondLineWrapper.add(SecondLine);


        panel.add(SecondLineWrapper);
        panel.add(Box.createVerticalStrut(30));

        Box scoreWrapper = new Box(BoxLayout.X_AXIS);
        JLabel score =createDefaultLabel();
        score.setText(("Score: " + String.valueOf(0)));
        scoreWrapper.add(score);



        panel.add(scoreWrapper);
        panel.add(Box.createVerticalStrut(120));



        Box closeWrapper = new Box(BoxLayout.X_AXIS);
        JButton close = createCloseButton();
        close.setSize(80, 30);
        close.setMinimumSize(new Dimension(60, 20));
        close.setMaximumSize(new Dimension(100, 30));
        close.setFont(new Font("Font", Font.PLAIN, 20));
        closeWrapper.add(close);

        panel.add(closeWrapper);
        panel.add(Box.createRigidArea(new Dimension(50, 10)));

        panel.setMinimumSize(new Dimension(430, 460));
        panel.setSize(430, 460);
        panel.setAlignmentX(JLabel.CENTER);

        panel.setVisible(true);
        panel.setOpaque(true);
        
        return panel;
    }



    public static JPanel createLoseScreen(){


        JPanel panel = createBlankEndScreen();
        panel.add(Box.createRigidArea(new Dimension(25, 25)));


        Box mainWrapper = new Box(BoxLayout.X_AXIS);
        JLabel main = createDefaultLabel();
        main.setText("Game Over!");
        main.setFont(new Font("Font", Font.BOLD, 30));
        mainWrapper.add(main);

        panel.add(mainWrapper);
        panel.add(Box.createVerticalStrut(80));


        Box FirstLineWrapper = new Box(BoxLayout.X_AXIS);
        JLabel FirstLine = createDefaultLabel();
        FirstLine.setText("You Lose!");
        FirstLine.setMinimumSize(new Dimension(400, 90));
        FirstLineWrapper.add(FirstLine);


        panel.add(FirstLineWrapper);


        Box SecondLineWrapper = new Box(BoxLayout.X_AXIS);
        JLabel SecondLine = createDefaultLabel();
        SecondLine.setText("Better Luck Next Time!");
        SecondLine.setMinimumSize(new Dimension(400, 60));
        SecondLineWrapper.add(Box.createHorizontalStrut(25));
        SecondLineWrapper.add(SecondLine);
        SecondLineWrapper.add(Box.createHorizontalStrut(25));


        panel.add(SecondLineWrapper);
        panel.add(Box.createVerticalStrut(120));


        Box closeWrapper = new Box(BoxLayout.X_AXIS);
        JButton close = createCloseButton();
        close.setSize(80, 30);
        close.setMinimumSize(new Dimension(60, 20));
        close.setMaximumSize(new Dimension(100, 30));
        close.setFont(new Font("Font", Font.PLAIN, 20));
        closeWrapper.add(close);

        panel.add(closeWrapper);
        panel.add(Box.createRigidArea(new Dimension(50, 10)));

        panel.setMinimumSize(new Dimension(460, 460));
        panel.setSize(400, 400);
        panel.setAlignmentX(JLabel.CENTER);

        panel.setVisible(true);
        panel.setOpaque(true);

        return panel;
    }




    public static JPanel createBlankEndScreen(){

        JPanel panel = new JPanel();

        panel.setOpaque(true);
        panel.setBounds(0, 0, 400, 270);
        //panel.setBackground(new Color(162,199,72));
        panel.setBackground(new Color(198, 210, 235));
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        return panel;
    }

    public static JLabel createDefaultLabel(){

        JLabel main = new JLabel();

        main.setFont(new Font("Font", Font.PLAIN, 28));
        main.setForeground(new Color(25,15,10));
        main.setSize(400, 60);
        main.setMinimumSize(new Dimension(400, 60));
        main.setHorizontalTextPosition(JLabel.CENTER);

        main.setVisible(true);
        return main;
    }
    
    public static JButton createCloseButton(){

        JButton close = new JButton("Exit");
        close.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getComponent().equals(close)){System.exit(0);}
            }
            
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });


        close.setSize(60, 45);
        close.setAlignmentX(JButton.CENTER);
        close.setVisible(true);

        return close;
    }

    public static Box createInputBox(

        String inputName, String[] DefaultValueNames, int[] defaultValues, 
        boolean separator, int separatorIndex, int fieldMin, int fieldMax){



        JTextField textField = new JTextField(3);
        textField.setFont(new Font("Font", Font.PLAIN, 16));
        textField.setForeground(new Color(25,15,10));

        textField.setSize(60, 20);
        textField.setMaximumSize(new Dimension(60, 20));
        textField.setMaximumSize(new Dimension(60, 20));
        textField.setText("16");
        textField.setHighlighter(null);

        

        JMenu defaults = new JMenu();
        defaultMenuManager man = new defaultMenuManager(textField);
        ButtonGroup defaultButtons = new ButtonGroup();


        int length = (DefaultValueNames.length <= defaultValues.length ? DefaultValueNames.length : defaultValues.length);
        JRadioButtonMenuItem[] buttons = new JRadioButtonMenuItem[length];

        for(int i=0; i<length; i++){
            if(separator && separatorIndex==i){defaults.addSeparator();}
            buttons[i] = new JRadioButtonMenuItem((DefaultValueNames[i]) + ": " + String.valueOf(defaultValues[i]));
            buttons[i].addItemListener(man);
            defaultButtons.add(buttons[i]);
            defaults.add(buttons[i]);
        }




        
        textField.addActionListener(new textManager(textField, fieldMin, fieldMax, defaultButtons));


        defaults.setVisible(true);
        defaults.setText("Defaults");

        JMenuBar bar = new JMenuBar();
        bar.add(defaults);
        bar.setSize(70, 20);
        bar.setMaximumSize(new Dimension(70, 20));
        bar.setMinimumSize(new Dimension(70, 20));
        

        JLabel ScaleLabel = new JLabel(inputName + ": ");
        ScaleLabel.setFont(new Font("Font", Font.PLAIN, 16));
        ScaleLabel.setForeground(new Color(25,15,10));
        ScaleLabel.setSize(40, 20);
        ScaleLabel.setMaximumSize(new Dimension(40, 20));
        ScaleLabel.setMaximumSize(new Dimension(40, 20));



        Box box = new Box(BoxLayout.X_AXIS);

        box.add(ScaleLabel);
        box.add(textField);
        box.add(bar);

        box.setVisible(true);
        box.setSize(170, 20);
        box.setMaximumSize(new Dimension(170, 20));

        return box;
    }
}