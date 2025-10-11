#include <iostream>
#include <fstream>
#include <cstdlib>

using namespace std;
class Asset{
    private:
        short* Array;
        const short MovePatternsAdjacent[8][2] = { //ADJACENT MOVE PATTERNS
            {-1,-1}, {-1,0}, {-1,1},
            {0,-1},          {0, 1},
            {1,-1},  {1,0},  {1,1}};
        const int LastBit = 1<<15;
        const short MovePatternsNonAdjacent[16][2] = { //MID RANGE MOVE PATTERNS
        {-2,-2}, {-2,-1}, {-2,0}, {-2,1}, {-2,2},
        {-1,-2},                          {-1,2},
        {0,-2},                           {0, 2},
        {1,-2},                           {1, 2},
        {2,-2},  {2,-1},  {2,0},  {2,1},   {2,2},
       };
        //short Array[1024];
    public:
        Asset(){
            //TODO: Test outputted values for encoding and decoding errors

            ifstream Table("OutputListChar.txt");
            //ofstream echo("OutputListEcho.txt");
            Array = (short*) calloc(1024, sizeof(short));
            int a, b;
            for (int i=0; i<256; i++){
                for (int j=0; j<4; j++){
                    a =Table.get();
                    //echo.put(a);
                    b =Table.get();
                    //echo.put(b);
                    Array[j+(i*4)] = (short)(a+(b<<8));
                    //cout << (Array[j+(i*4)]);
                    //cout << "\t";
                }
                //cout << "\t";
            }
            //echo.close();
            Table.close();
        };
        short* getLookUpTable(){return Array;};

        void RefreshZoomReferences(){
            ofstream Writer("OutputListChar.txt");
            char* file = (char*) calloc(256*4*2, sizeof(char));
            for (short i=0; i<256; i++){
                short* ScaledMap = (short*) calloc(6*6, sizeof(short));
                for (short j=0; j<8; j++){
                    if(((i & (1<<j))>>j) !=1){
                        
                        ScaledMap[
                            (2*(MovePatternsAdjacent[j][0]+1))
                            +(  (MovePatternsAdjacent[j][1]+1)   *6*2)]=(short)LastBit;

                        ScaledMap[
                            (2*(MovePatternsAdjacent[j][0]+1))+1
                            +(  (MovePatternsAdjacent[j][1]+1)   *6*2)]=(short)LastBit;

                        ScaledMap[
                            (2*(MovePatternsAdjacent[j][0]+1))
                            +(  ((MovePatternsAdjacent[j][1]+1) *2+1)    *6)]=(short)LastBit;

                        ScaledMap[
                            (2*(MovePatternsAdjacent[j][0]+1))+1
                            +(  ((MovePatternsAdjacent[j][1]+1) *2+1)    *6)]=(short)LastBit;

                    }
                }
                for (short y=0; y<6; y++){
                    for (short x=0; x<6; x++){
                        if (ScaledMap[x + (y*6)]>=0){
                            for (short j=0; j<8; j++){
                                if ((x+MovePatternsAdjacent[j][1])>=2 && (x+MovePatternsAdjacent[j][1])<4 && 
                                    (y+MovePatternsAdjacent[j][0])>=2 && (y+MovePatternsAdjacent[j][0])<4){
                                    
                                    ScaledMap[x+MovePatternsAdjacent[j][1] + ((y+MovePatternsAdjacent[j][0])*6)]+=2048;
                                }
                            }
                            for (short j=0; j<16; j++){
                                if ((x+MovePatternsNonAdjacent[j][1])>=2 && (x+MovePatternsNonAdjacent[j][1])<4 && 
                                    (y+MovePatternsNonAdjacent[j][0])>=2 && (y+MovePatternsNonAdjacent[j][0])<4){
                                    
                                    ScaledMap[x+MovePatternsNonAdjacent[j][1] + ((y+MovePatternsNonAdjacent[j][0])*6)]+=64;
                                }
                            }
                        }
                    }
                }
                file[i*8] = (char)((ScaledMap[2+(2*6)]) & 255);
                file[i*8+1] = (char)(ScaledMap[2+(2*6)] & (255<<8)>>8);

                file[i*8+2] = (char)((ScaledMap[3+(2*6)]) & 255);
                file[i*8+3] = (char)(ScaledMap[3+(2*6)] & (255<<8)>>8);

                file[i*8+4] = (char)((ScaledMap[2+(3*6)]) & 255);
                file[i*8+5] = (char)(ScaledMap[2+(3*6)] & (255<<8)>>8);

                file[i*8+6] = (char)((ScaledMap[3+(3*6)]) & 255);
                file[i*8+7] = (char)(ScaledMap[3+(3*6)] & (255<<8)>>8);

                free(ScaledMap);
            }
            Writer.write(file, 256*4*2);
            free(file);
            Writer.close();
        }
};