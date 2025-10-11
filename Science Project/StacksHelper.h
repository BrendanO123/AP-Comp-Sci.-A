#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;



    //
    //
    //
    void Stack :: placePixel(short x, short y, bool thirdRing){
        for (int i=0; i<8; ++i){
            if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){

                MapReg[/*OffsetPos*/
                ((x+MovePaternsAdjacent[i][1])/*X*/ + 
                ((y+MovePaternsAdjacent[i][0])*scale))/*Y * Scale*/]
                +=2048;
            }
        }
        for (int i=0; i<16; ++i){
            if(((y+MovePaternsNonAdjacent[i][0])<currentScale) && ((x+MovePaternsNonAdjacent[i][1])<currentScale) && 
                ((y+MovePaternsNonAdjacent[i][0])>=0) && ((x+MovePaternsNonAdjacent[i][1])>=0)){

                MapReg[/*OffsetPos*/
                ((x+MovePaternsNonAdjacent[i][1])/*X*/ + 
                ((y+MovePaternsNonAdjacent[i][0])*scale))/*Y * Scale*/]
                +=64;
            }
        }
        if(thirdRing){
            for (int i=0; i<56; ++i){
                if(((y+MovePaternsDistant[i][0])<currentScale) && ((x+MovePaternsDistant[i][1])<currentScale) && 
                    ((y+MovePaternsDistant[i][0])>=0) && ((x+MovePaternsDistant[i][1])>=0)){

                    MapReg[/*OffsetPos*/
                    ((x+MovePaternsDistant[i][1])/*X*/ + 
                    ((y+MovePaternsDistant[i][0])*scale))/*Y * Scale*/]
                    ++;
                }
            }
        }
    };


    //
    //
    //
    void Stack :: adjust(short x, short y, bool thirdRing, bool P_OR_M){
        for (int i=0; i<8; ++i){
            if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){

                if(P_OR_M){
                    MapReg[/*OffsetPos*/
                    ((x+MovePaternsAdjacent[i][1])/*X*/ + 
                    ((y+MovePaternsAdjacent[i][0])*scale))/*Y * Scale*/
                    + currentRegLocation]
                    +=2048;
                }
                else{
                    MapReg[/*OffsetPos*/
                    ((x+MovePaternsAdjacent[i][1])/*X*/ + 
                    ((y+MovePaternsAdjacent[i][0])*scale))/*Y * Scale*/
                    + currentRegLocation]
                    -=2048;
                }
            }
        }
        for (int i=0; i<16; ++i){
            if(((y+MovePaternsNonAdjacent[i][0])<currentScale) && ((x+MovePaternsNonAdjacent[i][1])<currentScale) && 
                ((y+MovePaternsNonAdjacent[i][0])>=0) && ((x+MovePaternsNonAdjacent[i][1])>=0)){

                if(P_OR_M){
                    MapReg[/*OffsetPos*/
                    ((x+MovePaternsNonAdjacent[i][1])/*X*/ + 
                    ((y+MovePaternsNonAdjacent[i][0])*scale))/*Y * Scale*/
                    + currentRegLocation]
                    +=64;
                }
                else{
                    MapReg[/*OffsetPos*/
                    ((x+MovePaternsNonAdjacent[i][1])/*X*/ + 
                    ((y+MovePaternsNonAdjacent[i][0])*scale))/*Y * Scale*/
                    + currentRegLocation]
                    -=64;
                }
            }
        }
        if(thirdRing){
            for (int i=0; i<56; ++i){
                if(((y+MovePaternsDistant[i][0])<currentScale) && ((x+MovePaternsDistant[i][1])<currentScale) && 
                    ((y+MovePaternsDistant[i][0])>=0) && ((x+MovePaternsDistant[i][1])>=0)){

                    if(P_OR_M){
                        MapReg[/*OffsetPos*/
                        ((x+MovePaternsDistant[i][1])/*X*/ + 
                        ((y+MovePaternsDistant[i][0])*scale))/*Y * Scale*/
                        + currentRegLocation]
                        ++;
                    }
                    else{
                        MapReg[/*OffsetPos*/
                        ((x+MovePaternsDistant[i][1])/*X*/ + 
                        ((y+MovePaternsDistant[i][0])*scale))/*Y * Scale*/
                        + currentRegLocation]
                        --;
                    }
                }
            }
        }
    };
    //
    //
    //LOCATION
    void Stack :: removeLand(short x, short y, bool ThirdRing) {
        MapReg[(x+(y*scale))+currentRegLocation]+=(short)LastBit;
        adjust(x, y, ThirdRing, false);
    };
    void Stack :: addLand(short x, short y, bool ThirdRing) {
        MapReg[(x+(y*scale))+currentRegLocation]&=LastBitRemovalAND;
        adjust(x, y, ThirdRing, true);
    };