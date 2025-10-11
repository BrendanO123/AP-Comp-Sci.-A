package GUI.Handlers;

import javax.swing.*;
import java.awt.event.*;

public class defaultMenuManager implements ActionListener, ItemListener{

    private JTextField textField;

    
    public defaultMenuManager(JTextField textField){this.textField=textField;}


    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange()==ItemEvent.SELECTED){
            String text =((JMenuItem)e.getItemSelectable()).getText();
            text=text.substring(text.indexOf(' ')+1);
            textField.setText(text);
            textField.repaint();
        }
    }

    public void actionPerformed(ActionEvent e) {}
}