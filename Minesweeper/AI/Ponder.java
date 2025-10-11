package AI;

import java.security.MessageDigest;

import GUI.UIManager;
import BaseGame.Game;

@SuppressWarnings("unused")
class Ponder {

    private byte[] AIKeyHash;
    private AI ai;
    private boolean correctInit=false;
    private Que superQue, indexQue;
    private short[] superMap, map;
    private int scale;

    public static final int ponderedBit = (1<<13);
    public static final int usedBit = (1<<12);
    public static final int stateAND = (((1<<2)-1)<<14);
        public static final int mine=(1/*01*/<<14);
    public static final int paddingClearAnd = -((((1<<2)-1)<<12)+1);
    //two bits = 1<<2 -1 = 4-1 =3
    //@ bits 13 and 14 = num<<12
    //inverted = negative-1 = -(#+1)

    public static final int complicatedBit = (1<<31);
    public static final int indexAND = -((1<<31)+1);

    public Ponder(byte[] AIKeyHash, byte[] UIKeyHash, byte[] GameKeyHash, UIManager UI, AI ai){
        if(MessageDigest.isEqual(AIKeyHash, UI.getAIKeyHash(UIKeyHash, GameKeyHash))){
            this.AIKeyHash=AIKeyHash;
            this.ai=ai;
            this.correctInit=true;
            this.scale=this.ai.getScale();
        }
    }

    public boolean runQue(byte[] Hashed){
        if(correctInit && MessageDigest.isEqual(Hashed, AIKeyHash)){
            superQue = ai.getQue(AIKeyHash);    indexQue = new Que();
            superMap=ai.getMap(AIKeyHash);  map = superMap.clone();

            int size=superQue.size();
            for(int i=0; i<size; i++){
                indexQue.Cycle(superQue.getFirst());
                superQue.Cycle(indexQue.SafeAccessLast());
            }

            int index, complicatedStaleCounter=0;
            boolean result, stale=true;
            while(indexQue.hasNext()){
                index=indexQue.getFirst();
                if((map[index] & ponderedBit)==0){
                    if((index & complicatedBit)==0){
                        complicatedStaleCounter=0;
                        try{result = runTile(index, false, ai.getFlagsLeft());}
                        catch(breakOutException e){break;}
                        if(stale){stale=!result;}
                        if(result && (map[index] & AI.clearedBit)!=0){break;}
                    }
                    else{
                        complicatedStaleCounter++;
                        if(complicatedStaleCounter==indexQue.size()){
                            index&=indexAND;
                            try{result = runTile(index, true, ai.getFlagsLeft());}
                            catch(breakOutException e){stale=false; break;}
                            if(stale){stale=!result;}
                        }
                        else{indexQue.Cycle(index);}
                    }
                }
            }
            return !stale;
        }
        return false;
    }

    private boolean runTile(int index, boolean runComplex, int flagsLeft) throws breakOutException{

        //TODO: run specific tile
        Que ponderedQue = new Que(); 
        Que runningQue = new Que();

        int x= index%scale, y=index/scale, look;
        boolean cleared = false;
        short e = map[index];
        byte expected, found;
        expected=(byte)((e & AI.expectedAND)>>8); found=(byte)((e & AI.minesAND)>>4);

        if(expected-found<=1){
            if(expected==found){superQue.add(index); throw new breakOutException();} //done bc main AI can clear

            //Place one flag
            for(byte i=0; i<8; i++){
                int x2=x+Game.movePatterns[i][1];
                if(x2>=0 && x2<scale){
                    int y2=y+Game.movePatterns[i][0];
                    if(y2>=0 && y2<scale){
                        look=x2+y2*scale;
                        if((map[look] & stateAND)==0){
                            runningQue.add(look);
                        }
                    }
                }
            }

            look=runningQue.getFirst();
            if( 
                assumptionFails(
                index, new int[]{look}, true, true, flagsLeft, ponderedQue)
            )
            {superClear(look); cleared=true;}
            while(runningQue.hasNext()){
                if(
                    assumptionFails(
                    index, new int[]{runningQue.getFirst()}, true, false, flagsLeft, ponderedQue)
                )
                {superClear(look); cleared=true;}
            }
            
            map[index]+=ponderedBit; //mark as complete
            if(cleared){throw new breakOutException();}
        }
        else if((e & AI.grassAND)-expected==1){

            //clear one tile
            //

            map[index]+=ponderedBit; //mark as complete
        }
        else if(runComplex){

            //run with multiple assumptions
            //

            map[index]+=ponderedBit; //mark as complete
        }
        else{indexQue.Cycle((index + complicatedBit));}
        return false; //return if successfully changed the map

            //all tiles that preform an action in every assumption iteration should be set as pondered as well

                //make que of every tile ever called to fill or clean in one tile's set of assumptions

                //use last padding bit to see if tile was used in the previous round
                //every round, set true if used and used previous round
                    //ignore previous round on round one

                //at the end, iterate over tile que and for every tile{
                    //clear used/unused bit
                    //if used bit is still true, set pondered bit to true
                //}
    }

    private boolean assumptionFails(int tileIndex, int[] tiles, boolean flagging, boolean ignoreUsedBit, int flagsLeft, Que ponderQue){
        short[] mapCopy = map.clone();
        Que runningQue = new Que();

        if(flagging){for(int e : tiles){flag(e, runningQue, mapCopy); flagsLeft--;}}
        else{for(int e : tiles){clear(e, runningQue, mapCopy);}}

        //TODO: continue assumption testing, eject when impossibilities arise
        return false;
    }

    private void Clean(int index, short[] Map, Que runningQue){

        int x=index%scale;
        int y=index/scale;

        int x2, x3, y2, y3, look;

        for(byte i=0; i<8; i++){
            x2=x+Game.movePatterns[i][1];
            if(x2>=0 && x2<scale){
                y2=y+Game.movePatterns[i][0];
                if(y2>=0 && y2<scale){
                    look=x2+y2*scale;
                    if((Map[look] & AI.clearedBit)==0){
                        Map[look]+=AI.clearedBit;
                        
                    }
                }
            }
        }
        //TODO: tile run clean
            //check for unfillable tiles
    }
    private void Fill(int index, short[] Map, Que runningQue){
        //TODO: tile run fill
            //check for too many flags total or per tile
    }

    private void flag(int index, Que runningQue, short[] Map){
        Map[index]+=AI.finishedBit;
        incrementAdjacent(index, (1<<4), Map, runningQue);
    }
    private void clear(int index, Que runningQue, short[] Map){
        Map[index]+=AI.clearedBit;
        incrementAdjacent(index, -1, Map, runningQue);
    }

    private void superClear(int index){ai.clear(index, AIKeyHash);}
    private void superFlag(int index){ai.flag(index, AIKeyHash);}

    private void incrementAdjacent(int index, int incrementValue, short[] Map, Que runningQue){

        int x2, y2, look;

        int x=index%scale;
        int y=index/scale;
        for(byte i=0; i<8; i++){
            x2=x+Game.movePatterns[i][1];
            if(x2>=0 && x2<scale){
                y2=y+Game.movePatterns[i][0];
                if(y2>=0 && y2<scale){
                    look=x2+y2*scale;
                    Map[look]+=incrementValue;
                    runningQue.add(look);
                }
            }
        }
    }
}
class breakOutException extends Exception{
    public breakOutException(){super();}
}
