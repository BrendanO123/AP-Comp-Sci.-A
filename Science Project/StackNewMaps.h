
#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;
    
    //
    //NEW MAP (WHITE NOISE)
    void Stack :: newWhiteNoiseMap(int ChanceDenominator, bool twosPower, bool ThirdRing){
        currentRegLocation=false;
        for (short y=0; y<currentScale; y++){
            for (short x=0; x<currentScale; x++){
                if(twosPower){
                    if((rand() & ChanceDenominator)==0){placePixel(x, y, ThirdRing);}
                    else{MapReg[(x+(y*scale))]+=(short)LastBit;}
                }
                else{
                    if ((rand() % ChanceDenominator)==0){placePixel(x, y, ThirdRing);}
                    else{MapReg[(x+(y*scale))]+=(short)LastBit;}
                }
            }
        }
    };
    //
    //NEW fBM MAP
    void Stack :: new_fBM_Map(float threshold, FastNoiseLite map, bool ThirdRing){
        currentRegLocation=false;
        for (short y=0; y<currentScale; y++){
            for (short x=0; x<currentScale; x++){
                //9.8765432345676789843 Is a Meaningless key spam scaling factor
                //Should be much greater than 1, should be seemingly irrational
                if((map.GetNoise((float)(x*9.8765432345676789843), (float)(y*9.8765432345676789843))) >= threshold){
                    placePixel(x, y, ThirdRing);
                }
                else{MapReg[(x+(y*scale))]+=(short)LastBit;}
            }
        }
    };