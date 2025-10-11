package GUI.Handlers;

import javax.swing.*;
import java.awt.event.*;

public class textManager implements ActionListener{

    private JTextField textField;
    private int min, max;
    private ButtonGroup Group;

    public textManager(JTextField textField, int min, int max, ButtonGroup group){
        this.textField=textField;
        this.min=min; this.max=max;
        Group=group;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        String text= textField.getText();
        int scale = Integer.parseInt(text);

        if(scale<min){textField.setText(String.valueOf(min));}
        else if(scale>max){textField.setText(String.valueOf(max));}
        
        Group.clearSelection();

    }


}
