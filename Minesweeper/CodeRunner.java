import java.util.*;
import javax.swing.*;
import BaseGame.Game;
import GUI.UIManager;
import AI.*;

//TODO: Java Docs?

public class CodeRunner{
    
    public static void main(String[] args){

        Hasher.init();


        Random rand = new Random();
        rand.setSeed(rand.nextLong()+System.currentTimeMillis());

        long[] keys = new long[3];
        for(byte i=0; i<3; i++){
            keys[i]=rand.nextLong()+System.currentTimeMillis();
        }


        AI aiShell = new AI(keys[0], Hasher.getHash(keys[1]), Hasher.getHash(keys[2]));
        Game game = new Game(keys[1]);
        aiShell.addGame(game, Hasher.getHash(keys[0]));
        

        JFrame frame=new JFrame();
        frame.setSize(game.getScale()*Game.TileSize,game.getScale()*Game.TileSize+120);
        UIManager ui = new UIManager(frame, game, keys[2], Hasher.getHash(keys[1]), Hasher.getHash(keys[0]), aiShell);
        aiShell.addUI(ui, Hasher.getHash(keys[0]));

        keys=null;
    }
}