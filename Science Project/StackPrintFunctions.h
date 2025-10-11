#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;

void Stack :: print(){
            for (int y=0; y<currentScale; y++){
                for (int x=0; x<currentScale; x++){

                    cout << (MapReg[(x+(y*scale))+currentRegLocation] > 0 ? "Land: " : "Water: ") << 
                        (((MapReg[(x+(y*scale))+currentRegLocation]) & FirstRingAND)>>11) 
                        << " A, " << (((MapReg[(x+(y*scale))+currentRegLocation]) & SecondRingAND)>>6) << " N, " << 
                        ((MapReg[(x+(y*scale))+currentRegLocation]) & LastRingAND) << " D | ";
                }
                cout << "\n\n";
            }
            cout << "\n\n";
        };
        void Stack :: printFacets(){
            printSimple();

            cout << "Adjacent:\n";
            for (int y=0; y<currentScale; y++){
                for (int x=0; x<currentScale; x++){
                        cout << (((MapReg[(x+(y*scale))+currentRegLocation]) & FirstRingAND)>>11) << " | ";
                }
                cout << "\n";
            }
            cout << "\n\n";

            cout << "Nearby:\n";
            for (int y=0; y<currentScale; y++){
                for (int x=0; x<currentScale; x++){
                        cout << (((MapReg[(x+(y*scale))+currentRegLocation]) & SecondRingAND)>>6) << " | ";
                }
                cout << "\n";
            }
            cout << "\n\n";
        };

        
        //
        //PRINT W&L
        void Stack :: printSimple(){
            for (int y=0; y<currentScale; y++){
                for (int x=0; x<currentScale; x++){
                        cout << (MapReg[(x+(y*scale))+currentRegLocation] >= 0 ? "L | " : "W | ");
                }
                cout << "\n";
            }
            cout << "\n\n";
        }

        //
        //PRINT X & _ NO SPACE
        void Stack :: printSimpleComp(){
            cout << "X = Land | _ = Water\n";
            for (int y=0; y<currentScale; y++){
                for (int x=0; x<currentScale; x++){
                        cout << (MapReg[(x+(y*scale))+currentRegLocation] > 0 ? "X" : "_");
                }
                cout << "\n";
            }
            cout << "\n\n";
        };