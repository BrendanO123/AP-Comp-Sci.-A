#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;





//LOCATION
    void Stack :: removeOutliers(bool SecondRing, bool ThirdRing){
        short value;
        for (short y=0; y<currentScale; y++){
            for (short x=0; x<currentScale; x++){
                value = MapReg[(x+(y*scale))+currentRegLocation];
                if (value < 0){
                    if (((value & FirstRingAND)>>11)==8){
                        addLand(x, y, ThirdRing);
                    }
                    else if(SecondRing){
                        if (((value & SecondRingAND)>>6)==16){
                            addLand(x, y, ThirdRing);
                            for (int i=0; i<8; i++){
                                if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                                    ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){ 
                                    if (
                                            MapReg[((
                                        x+MovePaternsAdjacent[i][1])+(
                                        (y+MovePaternsAdjacent[i][0])*scale))
                                        +currentRegLocation]
                                        <0)
                                    {
                                        addLand(x+MovePaternsAdjacent[i][1], y+MovePaternsAdjacent[i][0], ThirdRing);
                                    }
                                }
                            }
                        }
                        else if(ThirdRing){
                            if ((value & LastRingAND)==56){
                                addLand(x, y, true);
                                for (int i=0; i<8; i++){
                                    if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                                        ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){ 
                                        if (
                                                MapReg[((
                                            x+MovePaternsAdjacent[i][1])+(
                                            (y+MovePaternsAdjacent[i][0])*scale))
                                            +currentRegLocation]
                                            <0)
                                        {
                                            addLand(x+MovePaternsAdjacent[i][1], y+MovePaternsAdjacent[i][0], true);
                                        }
                                    }
                                }
                                for (int i=0; i<16; i++){
                                    if(((y+MovePaternsNonAdjacent[i][0])<currentScale) && ((x+MovePaternsNonAdjacent[i][1])<currentScale) && 
                                        ((y+MovePaternsNonAdjacent[i][0])>=0) && ((x+MovePaternsNonAdjacent[i][1])>=0)){ 
                                        if (
                                                MapReg[((
                                            x+MovePaternsNonAdjacent[i][1])+(
                                            (y+MovePaternsNonAdjacent[i][0])*scale))
                                            +currentRegLocation]
                                            <0)
                                        {
                                            addLand(x+MovePaternsNonAdjacent[i][1], y+MovePaternsNonAdjacent[i][0], true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    if (((value & FirstRingAND)>>11)==0){
                        removeLand(x, y, ThirdRing);
                    }
                    else if(SecondRing){
                        if (((value & SecondRingAND)>>6)==0){
                            removeLand(x, y, ThirdRing);
                            for (int i=0; i<8; i++){
                                if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                                    ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){ 
                                    if (
                                            MapReg[((
                                        x+MovePaternsAdjacent[i][1])+(
                                        (y+MovePaternsAdjacent[i][0])*scale))
                                        +currentRegLocation]
                                        >=0)
                                    {
                                        removeLand(x+MovePaternsAdjacent[i][1], y+MovePaternsAdjacent[i][0], ThirdRing);
                                    }
                                }
                            }
                        }
                        else if(ThirdRing){
                            if ((value & LastRingAND)==0){
                                removeLand(x, y, true);
                                for (int i=0; i<8; i++){
                                    if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                                        ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){ 
                                        if (
                                                MapReg[((
                                            x+MovePaternsAdjacent[i][1])+(
                                            (y+MovePaternsAdjacent[i][0])*scale))
                                            +currentRegLocation]
                                            >=0)
                                        {
                                            removeLand(x+MovePaternsAdjacent[i][1], y+MovePaternsAdjacent[i][0], true);
                                        }
                                    }
                                }
                                for (int i=0; i<16; i++){
                                    if(((y+MovePaternsNonAdjacent[i][0])<currentScale) && ((x+MovePaternsNonAdjacent[i][1])<currentScale) && 
                                        ((y+MovePaternsNonAdjacent[i][0])>=0) && ((x+MovePaternsNonAdjacent[i][1])>=0)){ 
                                        if (
                                                MapReg[((
                                            x+MovePaternsNonAdjacent[i][1])+(
                                            (y+MovePaternsNonAdjacent[i][0])*scale))
                                            +currentRegLocation]
                                            >=0)
                                        {
                                            removeLand(x+MovePaternsNonAdjacent[i][1], y+MovePaternsNonAdjacent[i][0], true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };