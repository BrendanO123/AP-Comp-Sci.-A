#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;





void Stack :: FastZoom(){

    int previousLocation;

    if (currentRegLocation==0){
        previousLocation=0;
        currentRegLocation=scale*scale;
    }
    else if(currentRegLocation==scale*scale){
        previousLocation=scale*scale;
        currentRegLocation=0;
    }
    else{cerr << "CURRENT REGISTER ADDRESS DISCREPANCY"; exit(-1);}



    short state;
    short additionalInformation;

    for (short y=0; y<currentScale; y++){
        for (short x=0; x<currentScale; x++){

            additionalInformation = 
                ((MapReg[(x + (y*scale))+previousLocation]>=0) ? 3*2048 : 0) + 
                (MapReg[(x + (y*scale))+previousLocation] & (LastBit + LastRingAND));

            state=0;

            for (short i=0; i<8; i++){
                if(((y+MovePaternsAdjacent[i][0])<currentScale) && ((x+MovePaternsAdjacent[i][1])<currentScale) && 
                    ((y+MovePaternsAdjacent[i][0])>=0) && ((x+MovePaternsAdjacent[i][1])>=0)){
                    if(MapReg[((x+MovePaternsAdjacent[i][1]) + ((y+MovePaternsAdjacent[i][0])*scale))+previousLocation]>=0){
                        state+=(1<<i);
                    }
                }
                //cout << ((state & (1<<i))>>i);
            }
            MapReg[((2*x) + (y*2*scale))+currentRegLocation] = ZoomReferenceTable[state*4]+additionalInformation;
            MapReg[((2*x)+1 + ((y*2)*scale))+currentRegLocation] = ZoomReferenceTable[state*4+1]+additionalInformation;
            MapReg[((2*x) + ((y*2+1)*scale))+currentRegLocation] = ZoomReferenceTable[state*4+2]+additionalInformation;
            MapReg[((2*x)+1 + ((y*2+1)*scale))+currentRegLocation] = ZoomReferenceTable[state*4+3]+additionalInformation;
            //cout << " | ";
        }
        //cout << "\n";
    }
    currentScale*=2;
    /*for (int i=0; i<1024; i++){
        //if ((ZoomReferenceTable[i] & FirstRingAND) !=0){cout << "Got Adjacency! " << (ZoomReferenceTable[i] & FirstRingAND);}
        //if ((ZoomReferenceTable[i] & LastBit) !=0){cout << "Got Ocean! " << (ZoomReferenceTable[i] & LastBit);}
    }*/


    //currentScale/=2;
    //currentRegLocation=previousLocation;
};