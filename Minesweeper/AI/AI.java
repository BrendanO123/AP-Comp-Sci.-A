package AI;

import java.util.*;

import GUI.*;
import GUI.Handlers.*;
import BaseGame.*;

@SuppressWarnings("unused")
public class AI {

    private short[] map;
    private Que que= new Que();

    private Game game;
    private UIManager UI;

    private byte[] GameKeyHash, UIKeyHash;
    private long key;

    private int scale, area, staleCounter, flagLeft;
    private buttonEvents bEvents;


    public static final int clearedBit=(1<<15); //bit 16, MSB
    public static final int finishedBit=(1<<14); //bit 15


    //public static final int paddingBits = (((1<<2)-1)<<12); //bits 13 and 14
        //bit 14 is used in ponder system

    public static final int expectedAND=(((1<<4)-1)<<8); //bits [9, 12]
    public static final int minesAND=(((1<<4)-1)<<4); //bits [5, 8]
    public static final int grassAND=((1<<4)-1); //bits [1, 4] (1 (LSB), 2, 3, 4)


    public AI(long key, byte[] GameKeyHash, byte[] UIKeyHash){
        this.key=key;
        this.GameKeyHash=GameKeyHash;
        this.UIKeyHash=UIKeyHash;
        staleCounter=0;
    }


    public void addUI(UIManager UI, byte[] Hash){if(Hasher.equals(key, Hash)){this.UI=UI;}}
    public void addGame(Game game, byte[] Hash){if(Hasher.equals(key, Hash)){this.game=game;}}
    public void addButtonEvents(buttonEvents b, byte[] Hashed){if(Hasher.equals(key, Hashed)){bEvents=b;}}
    
    public Que getQue(byte[] Hashed){if(Hasher.equals(key, Hashed)){return que;}return new Que();}
    public short[] getMap(byte[] Hashed){if(Hasher.equals(key, Hashed)){return map;}return new short[1];}

    public int getFlagsLeft(){return flagLeft;}
    public int getScale(){return scale;}



    public void Start(byte[] Hashed){
        if(Hasher.equals(key, Hashed)){
            scale=game.getScale();
            area=scale*scale;
            final int upBound=scale-1;

            map = new short[area];

            map[0]=3;
            map[upBound*scale]=3;
            map[upBound]=3;
            map[upBound+(upBound*scale)]=3;


            for(int x=1; x<upBound; x++){
                map[x]=5;
            }
            for(int x=1; x<upBound; x++){
                map[x+(upBound*scale)]=5;
            }
            for(int y=1; y<upBound; y++){
                map[y*scale]=5;
            }
            for(int y=1; y<upBound; y++){
                map[upBound+(y*scale)]=5;
            }


            for(int x=1; x<upBound; x++){
                for(int y=1; y<upBound; y++){
                    map[x+y*scale]=8;
                }
            }

            flagLeft=game.getNumFlags();
            Ponder ponder = new Ponder(Hasher.getHash(key), UIKeyHash, GameKeyHash, UI, this);

            manageClear(game.Start((scale/2)+(scale*scale/2), GameKeyHash));
            UI.getFrame(UIKeyHash).repaint();
            game.revealInternal(GameKeyHash); //testing
            QueIteration();

            //TODO: ponder system
                //How do I explain prioritization / likeliness to have a meaningful ponder per tile to AI?
                //how do I make efficient enough for bad prioritization, how do I prioritize?

                // future me knows you break into sections of connected tiles and use linear equations lol
        }
    }


    private void QueIteration(){
        int index;
        short element;
        int expected, found, grass;
        try{
        while(que.size()>0){
            index=que.getFirst();
            element=map[index];
            if((element & finishedBit)==0){

                grass=(element & grassAND);
                if(grass==0){map[index]+=finishedBit; continue;}
                expected=((element & expectedAND)>>8);
                found=((element & minesAND)>>4);

                if(expected==found){
                    map[index]+=finishedBit;
                    if(grass!=found){
                        Clean(index);
                    }
                }
                else if(expected==grass){
                    map[index]+=finishedBit;
                    if(found!=grass){
                        Fill(index);
                    }
                }
                else{
                    que.Cycle(index);
                    staleCounter++;
                    if(staleCounter==que.size()){
                        System.err.println("AI failed"); 
                        game.VisualizeState(GameKeyHash);//testing
                        game.getCurrentImg();//testing
                        break;
                    }
                }
            }
        }
        }
        catch(NullPointerException e){System.out.println(que.size()); e.printStackTrace();}
    }

    private void Fill(int index){

        int x2, y2, x, y, look;
        x=index%scale;
        y=index/scale;

        for(byte i =0; i<8; i++){
            x2=x+Game.movePatterns[i][1];
            if(x2>=0 && x2<scale){
                y2=y+Game.movePatterns[i][0];
                if(y2>=0 && y2<scale){
                    look=x2+y2*scale;
                    if(map[look]>=0 && (map[look] & finishedBit)==0){flag(x2+y2*scale);}
                }
            }
        }
    }
    private void Clean(int index){

        int x2, y2, x, y, look;
        x=index%scale;
        y=index/scale;

        for(byte i =0; i<8; i++){
            x2=x+Game.movePatterns[i][1];
            if(x2>=0 && x2<scale){
                y2=y+Game.movePatterns[i][0];
                if(y2>=0 && y2<scale){
                    look=x2+y2*scale;
                    if((map[look]>=0) && (map[look] & finishedBit)==0){
                        clear(x2+y2*scale);
                    }
                }
            }
        }
    }

    private void flag(int index){
        flagLeft--;
        incrementMines(index%scale, index/scale);
        map[index]+=finishedBit;
        UI.AIPlaceFlag(index, UIKeyHash);
    }
    public void flag(int index, byte[] Hashed){if(Hasher.equals(key, Hashed)){flag(index);}}
    
    private void unflag(int index){
        flagLeft++;
        decrementMines(index%scale, index/scale);
        map[index]-=finishedBit;
        UI.AIRemoveFlag(index, UIKeyHash);
    }


    private void clear(int index){
        try{manageClear(game.clear(index, GameKeyHash));}
        catch(simpleGGException e){UI.AIGameOver(e, UIKeyHash);}
    }
    public void clear(int index, byte[] Hashed){if(Hasher.equals(key, Hashed)){clear(index);}}

    private void manageClear(ArrayList<Integer> list){

        UI.AISetClearedTiles(list, UIKeyHash);
        int index;
        staleCounter=0;

        for(int e : list){

            index=((e & buttonEvents.pointerAND)>>4);
            if((map[index] & clearedBit) == 0){
                decrementGrass(index%scale, index/scale);
                map[index]+=clearedBit;
                map[index]+=((e & buttonEvents.numAnd)<<8);
                if((map[index] & expectedAND)==0){map[index]+=finishedBit;}
                else{que.add(index);}
            }
        }
    }





    private void decrementGrass(int x, int y){
        int x2, y2;
        for(byte i =0; i<8; i++){
            x2=x+Game.movePatterns[i][1];
            if(x2>=0 && x2<scale){
                y2=y+Game.movePatterns[i][0];
                if(y2>=0 && y2<scale){
                    map[x2+y2*scale]--;
                }
            }
        }
    }
    private void incrementMines(int x, int y){
        int x2, y2;
        for(byte i =0; i<8; i++){
            x2=x+Game.movePatterns[i][1];
            if(x2>=0 && x2<scale){
                y2=y+Game.movePatterns[i][0];
                if(y2>=0 && y2<scale){
                    map[x2+y2*scale]+=(1<<4);
                }
            }
        }
    }
    private void decrementMines(int x, int y){
        int x2, y2;
        for(byte i =0; i<8; i++){
            x2=x+Game.movePatterns[i][1];
            if(x2>=0 && x2<scale){
                y2=y+Game.movePatterns[i][0];
                if(y2>=0 && y2<scale){
                    map[x2+y2*scale]-=(1<<4);
                }
            }
        }
    }
}

