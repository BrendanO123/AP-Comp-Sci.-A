#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;







void Stack :: splitLargeOceans(int ChanceDenominator, bool twosPower, bool ThirdRing){
        for (short y=0; y<currentScale; y++){
            for (short x=0; x<currentScale; x++){
                short value = MapReg[(x+(y*scale))+currentRegLocation];
                if (value<0){
                    if (((value & FirstRingAND)>>11)==0 && ((value & SecondRingAND)>>6)==0){
                        if (ThirdRing){
                            if ((value & LastRingAND)==0){
                                if (twosPower){
                                    if((rand() & ChanceDenominator) ==0){addLand(x, y, true);}
                                }
                                else{
                                    if((rand() % ChanceDenominator) ==0){addLand(x, y, true);}
                                }
                            }
                        }
                        else{
                            if (twosPower){
                                if((rand() & ChanceDenominator) ==0){addLand(x, y, true);}
                            }
                            else{
                                if((rand() % ChanceDenominator) ==0){addLand(x, y, true);}
                            }
                        }
                    }
                }
            }
        }
    };



    //
    //
    //LOCATION
    //UNFINISHED FUNCTIONS
    void Stack :: connectIslands(bool ThirdRing){};