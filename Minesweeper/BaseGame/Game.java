package BaseGame;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

import AI.*;

public class Game{

    public static final int uncoveredBit=(1<<7);
    public static final int flaggedBit=(1<<6);
    public static final int mineBit=(1<<5);
    public static final int floodFilledBit=(1<<4);


    public static final int TileSize = 30;
    public static final double TwoPI = 2*Math.PI;


    public static final int numberAnd=((1<<4)-1);

    public static final int unflagAND=-(flaggedBit+1); 
    public static final int FFBitAND=-(floodFilledBit+1);
        //twos bit complement = bitWise NOT +1. 
        //I want only not so all other bits are true and AND logic only ignores flag bit. 
        //Therefore I make negative (NOT +1), and minus one (-1). 
        //Then this can be factored with a GCF of -1 for -(bit+1).


    public static final int[][] movePatterns = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},            {0, 1},
        {1, -1},  {1, 0},   {1, 1}
    };
    public static TilePainter painter= new TilePainter();





    protected Random rand = new Random();
    private byte[] map;

    private int scale=0;
    private int flagsLeft=0;
    private int LandLeft=Integer.MAX_VALUE;
    private int area;

    private boolean started=false;

    private long key;


    public Game(long key){
        scale=16;
        flagsLeft=40;
        this.key=key;
    }
    public Game(int Scale, double ratio, long key){
        if(Scale>=3 && Scale<(1<<14)){scale=Scale;}
        else{scale=16;}

        if(ratio>=0.125 && ratio <0.7){
            flagsLeft=(int)Math.round(scale*scale*ratio);
        }
        else{
            flagsLeft=40;
        }
        this.key=key;
    }


    public void setScale(int scale, byte[] Hashed){
        if(Hasher.equals(key, Hashed) && !started && scale>=8 && scale<(1<<14)){this.scale=scale;}
    }
    public void setRatio(double ratio, byte[] Hashed){
        if(Hasher.equals(key, Hashed) && !started && ratio>=0.125 && ratio <0.7){
            flagsLeft=(int)Math.round(scale*scale*ratio);
        }
    }


    public void ShellStart(byte[] Hashed){
        if(Hasher.equals(key, Hashed)){
            area=scale*scale;
            map = new byte[area];
            LandLeft=area-flagsLeft;
            started=true;
        }
    }

    private void ShellStart(){
        area=scale*scale;
        map = new byte[area];
        LandLeft=area-flagsLeft;
        started=true;
    }

    private boolean placeFlag(int index){
        if(index>=0 && index<area && (map[index] & uncoveredBit) == 0){
            if((map[index] & flaggedBit) == 0){
                map[index]+=flaggedBit;
                flagsLeft--;
                return true;
            }
        }
        return false;
    }
    private boolean removeFlag(int index){
        if(index>=0 && index<area && (map[index] & uncoveredBit) == 0){
            if((map[index] & flaggedBit) != 0){

                map[index]&=unflagAND;
                flagsLeft++;
                return true;
                
            }
        }
        return false;
    }




    public int getNumFlags(){return flagsLeft;}
    public int getScale(){return scale;}
    public int getLandLeft(){return LandLeft;}

    public boolean getFlag(int index){
        if(index>=0 && index<area){
            if((map[index] & flaggedBit)!=0){return true;}
        }
        return false;
    }


    public boolean placeFlag(int index, byte[] KeyHash){if(Hasher.equals(key, KeyHash)){return placeFlag(index);}   return false;}
    public boolean removeFlag(int index, byte[] KeyHash){if(Hasher.equals(key, KeyHash)){return removeFlag(index);} return false;}


    public ArrayList<Integer> clear(int index, byte[] KeyHash)throws simpleGGException{
        if(Hasher.equals(key, KeyHash)){
            if(index>=0 && index<area){
                return clear(index);
            }
        }
        return new ArrayList<Integer>();
    }

    public ArrayList<Integer> Start(int index, byte[] KeyHash){
        if(Hasher.equals(key, KeyHash)){
            if(index>=0 && index<area){
                return Start(index);
            }
        }
        return new ArrayList<Integer>();
    }
    public ArrayList<Integer> Start(int x, int y, byte[] KeyHash){
        if(Hasher.equals(key, KeyHash)){
            return Start(x+y*scale);
        }
        return new ArrayList<Integer>();
    }


    //TODO: I would like to ensure solvability by having AI play before handing to player
    private ArrayList<Integer> Start(int index){

        ShellStart();

        if(index<0 || index>=area){return new ArrayList<Integer>();}

        int x=index%scale;
        int y=index/scale;

        int xi, x2, yi, y2, id;

        for(int i=0; i<flagsLeft; i++){

            double theta = rand.nextDouble();
            double d;
            double xd;
            double yd;

            if(theta>=0.75 || theta<=0.25){
                if(theta == 0.75 || theta == 0.25){xd=Double.MAX_VALUE;}
                theta*=TwoPI;
                xd=(scale-x-1)/Math.cos(theta);
            }
            else{
                theta*=TwoPI;
                xd=-x/Math.cos(theta);
            }

            theta/=TwoPI;
            if(theta<=0.5){
                if(theta == 0.5 || theta == 0.){yd=Double.MAX_VALUE;}
                theta*=TwoPI;
                yd=y/Math.sin(theta);
            }
            else{
                theta*=TwoPI;
                yd=-((scale-y-1)/Math.sin(theta));
            }

            d=Math.min(xd, yd);
            d=ref.lerp(2.5, d, ref.AdjustDist(rand.nextDouble()));

            xi=(int)(x+Math.cos(theta)*d);
            yi=(int)(y-Math.sin(theta)*d);

            id=xi+(yi*scale);

            if((map[id] & floodFilledBit)!=0){i--; continue;}

            map[id]+=mineBit;
            map[id]+=floodFilledBit;

            for(byte j=0; j<8; j++){
                x2=xi+movePatterns[j][1];
                if(x2>=0 && x2<scale){
                    y2=yi+movePatterns[j][0];
                    if(y2>=0 && y2<scale){
                        map[x2+(y2*scale)]++;
                    }
                }
            }
        }

        try{return clear(index);}
        catch(simpleGGException e){
            System.err.println("Faulty Generation Resulting in Immediate Game Over"); 
            e.printStackTrace();
            return new ArrayList<Integer>();
        }
    }




    private ArrayList<Integer> clear(int index) throws simpleGGException{
        ArrayList<Integer> tiles = new ArrayList<Integer>();
        //returned list of indexes (first 28 bits) and values (last four bits [0, 8] or 9 possibilities)

        if(index>=0 && index<area){ 
            byte element = map[index]; 
            if ((element & uncoveredBit) !=0){ 
                tiles.add((index<<4)+(element & numberAnd)); 
                return tiles; 
            }
            if((map[index] & flaggedBit)!=0){return new ArrayList<Integer>();}

            if((element & mineBit)!=0){throw new simpleGGException(false);} //If mine, lose

            map[index]+=uncoveredBit;
            LandLeft--; //remove cleared tile from the amount left till you win
            if(LandLeft<=0){throw new simpleGGException(true);} //if last tile to be cleared, win

            if((element & numberAnd)==0){ 
                tiles=AutoClear(index); //if a 0, then auto clear
            }
            tiles.add((index<<4) + (element & numberAnd)); //add in the current tiles value (not included in autoclear) //remove cleared tile from the amount left till you win
            if(LandLeft<=0){throw new simpleGGException(true);} //if last tile to be cleared, win
        }
        return tiles;
    }


    private ArrayList<Integer> AutoClear(int index){
        ArrayList<Integer> indexes = new ArrayList<Integer>(); //que
        ArrayList<Integer> tiles = new ArrayList<Integer>(); 
        //returned list of indexes (first 28 bits) and values (last four bits [0, 8] or 9 possibilities)

        int last=1;
        int look; //variable declaration
        int x, x2, y, y2;


        x=index%scale;
        y=index/scale; //initializations
        indexes.add(y);
        indexes.add(x);
        
        while(last>=0){ //recursion until que is empty
            x=indexes.get(last);
            indexes.remove(last);
            last--;
            y=indexes.get(last);
            indexes.remove(last);
            last--;

            for (byte i=0; i<8; i++){ //move pattern loop
                x2=x+movePatterns[i][1];
                if(x2>=0 && x2<scale){
                    y2=y+movePatterns[i][0];
                    if(y2>=0 && y2<scale){ //in bounds and wrap around exclusion checking
                        look=(x2+(y2*scale)); //updating pointer
                        if (((map[look] & numberAnd) ==0) && ((map[look] & uncoveredBit) ==0) && ((map[look] & flaggedBit)==0)){ //conditions to add to que
                            indexes.add(y2);
                            indexes.add(x2); 
                            last+=2; //add to que
                        }
                        if((map[look] & flaggedBit)==0){
                            if((map[look] & uncoveredBit) ==0){map[look]+=uncoveredBit; LandLeft--;}
                            tiles.add((look<<4)+(map[look] & numberAnd)); //add all nearby to return list
                            //(all squares next to a 0 are not a mine so all nearby squares should be returned in autoclear)
                        }
                    }
                }
            }
        }
        indexes=null; 
        return tiles;
    }



    public BufferedImage getCurrentImg(){
        BufferedImage img = new BufferedImage(scale*TileSize, scale*TileSize, 1);
        byte element;
        int mineRemovalAnd = -(mineBit+1);
        for(int x=0; x<scale; x++){
            for(int y=0; y<scale; y++){
                element=map[x+(y*scale)];
                element&=FFBitAND;
                if((((y & 1)==0)^((x & 1)==0))){element+=floodFilledBit;}
                if((element & flaggedBit)!=0 && (element & mineBit)==0){element+=mineBit;}
                else if ((element & flaggedBit)==0 && (element & mineBit)!=0){element&=mineRemovalAnd;}
                img.setData(painter.getTile(element, x*TileSize, y*TileSize));
            }
        }
        try{ImageIO.write(img, "PNG", new File("BaseGame/Current.png"));}
        catch(IOException e){e.printStackTrace();}
        return img;
    }


    public void VisualizeState(byte[] Hashed){
        if(!Hasher.equals(key, Hashed)){return;}
        BufferedImage img = new BufferedImage(scale*TileSize, scale*TileSize, 1);
        byte element;
        for(int x=0; x<scale; x++){
            for(int y=0; y<scale; y++){
                element=map[x+(y*scale)];
                element&=FFBitAND;
                if((((y & 1)==0)^((x & 1)==0))){element+=floodFilledBit;}
                img.setData(painter.getTile(element, x*TileSize, y*TileSize));
            }
        }
        try{ImageIO.write(img, "PNG", new File("BaseGame/Map.png"));}
        catch(IOException e){e.printStackTrace();}
    }
    public void revealInternal(byte[] Hashed){
        if(!Hasher.equals(key, Hashed)){return;}
        BufferedImage img = new BufferedImage(scale*TileSize, scale*TileSize, 1);
        byte element;
        int uncoverAND=((1<<6)-1);
        for(int x=0; x<scale; x++){
            for(int y=0; y<scale; y++){
                element=map[x+(y*scale)];
                element&=FFBitAND;
                if((((y & 1)==0)^((x & 1)==0))){element+=floodFilledBit;}
                if((element & mineBit)==0){element&=uncoverAND; element+=uncoveredBit;}
                img.setData(painter.getTile(element, x*TileSize, y*TileSize));
            }
        }
        try{ImageIO.write(img, "PNG", new File("BaseGame/MapInternal.png"));}
        catch(IOException e){e.printStackTrace();}
    }
}