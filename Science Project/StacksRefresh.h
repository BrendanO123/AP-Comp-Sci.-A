#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;



//
    //LOCATION
    //
    //INTERNAL RINGS CHECK (DEBUG)
bool Stack :: RefreshInternalDebug(bool checkThird){
        int OriginalPos, OffsetPos, corrections=0, count;
        for (int y=0; y<currentScale; y++){
            for (int x=0; x<currentScale; x++){
                count=0;
                OriginalPos=x+(y*scale);


                for (int i=0; i<8; ++i){
                    if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                        ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){

                        if (
                            
                            MapReg[(x+MovePaternsAdjacent[i][1]/*X*/ + 
                        ((y+MovePaternsAdjacent[i][0])*scale))
                        +currentRegLocation]

                            >=0){count++;}
                    }
                }
                if(count!=((MapReg[OriginalPos+currentRegLocation] & FirstRingAND)>>11)){corrections++;}


                count=0;
                for (int i=0; i<16; ++i){
                    if(((y+MovePaternsNonAdjacent[i][0])<currentScale) && ((x+MovePaternsNonAdjacent[i][1])<currentScale) && 
                        ((y+MovePaternsNonAdjacent[i][0])>=0) && ((x+MovePaternsNonAdjacent[i][1])>=0)){
                        OffsetPos=x+MovePaternsNonAdjacent[i][1]/*X*/ + ((y+MovePaternsNonAdjacent[i][0])*scale)/*Y*SCALE*/;
                        if (
                            
                            MapReg[((x+MovePaternsNonAdjacent[i][1]) + 
                        ((y+MovePaternsNonAdjacent[i][0])*scale))
                        +currentRegLocation]
                        
                        >=0){count++;}
                    }
                }
                if(count!=((MapReg[OriginalPos+currentRegLocation] & SecondRingAND)>>6)){corrections++;}


                if (checkThird){
                    count=0;
                    for (int i=0; i<56; ++i){
                        if(((y+MovePaternsDistant[i][0])<currentScale) && ((x+MovePaternsDistant[i][1])<currentScale) && 
                            ((y+MovePaternsDistant[i][0])>=0) && ((x+MovePaternsDistant[i][1])>=0)){
                                if (
                                    MapReg[((x+MovePaternsDistant[i][1]) + 
                        ((y+MovePaternsDistant[i][0])*scale))
                        +currentRegLocation]
                        
                        >=0){count++;}
                        }
                    }
                    if(count!=(MapReg[OriginalPos+currentRegLocation] & LastRingAND)){corrections++;}
                }
            }
        }
        if (corrections!=0){
            cout << "INCORECT VALUES FOUND. Program found " << corrections << " mistakes.\n";
            return true;
        }
        else{
            cout << "MAP CORRECT.\n";
            return false;
        }
    };


void Stack :: RefreshThirdRing(){
        int count;
        for (short y=0; y<currentScale; y++){
            for (short x=0; x<currentScale; x++){
                count=0;
                for (int i=0; i<56; i++){
                    if ((x+ MovePaternsDistant[i][1])>=0 && (y+ MovePaternsDistant[i][0])>=0 && 
                    (x+ MovePaternsDistant[i][1])<currentScale && (y+ MovePaternsDistant[i][0])<currentScale){
                        if (

                            MapReg[((x+MovePaternsDistant[i][1])
                            + ((y + MovePaternsDistant[i][0])*scale))
                            + currentRegLocation]
                            
                            >=0){count++;}
                    }
                }
                MapReg[(x+(y*scale))+currentRegLocation] &= (LastBit + FirstRingAND + SecondRingAND);
                MapReg[(x+(y*scale))+currentRegLocation]+= count;
            }
        }
    };