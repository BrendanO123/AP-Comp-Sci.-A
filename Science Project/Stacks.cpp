#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include <cstdlib>

using namespace std;
//New Idea is to just keep two registers, both capable of storing the entire full scale map, 
//and simply swap between regs everytime the map is zoomed. Then when finished return one register and free the other.
//For each zoom, simply read from register A the unzoomed map and write into register B the zoomed map or vice versa. 
//It won't matter that the map in reg B will be written over as it will be an old instance.

//LOCATION
class Stack{
    protected: 
        //CONSTANT(S)
        const unsigned short scale=512;

        //MAIN VARS
        unsigned int currentScale;
        short* MapReg;

        //REFERENCES
        short* ZoomReferenceTable;

    //
    //
    //LOCATION

    private:
        //KEY FUNCTIONS
        void RunStack();

        //CLEAN UP
        void CleanUp();

        //ZOOm
        void FastZoom();

        //REFRESH
        bool RefreshInternalDebug(bool checkThird);
        void RefreshThirdRing();

        //HELPERS
        void placePixel(short x, short y, bool thirdRing);
        void adjust(short x, short y, bool ThirdRing, bool P_OR_M);
            //CELL MANAGMENT
        void removeLand(short x, short y, bool ThirdRing);
        void addLand(short x, short y, bool ThirdRing);

        //NEW MAPS
        void newWhiteNoiseMap(int ChanceDenominator, bool twosPower, bool ThirdRing);
        void new_fBM_Map(float threshold, FastNoiseLite map, bool ThirdRing);

        //NORMAL FUNCTIONS
        void splitLargeOceans(int ChanceDenominator, bool twosPower, bool ThirdRing);
        void connectIslands(bool ThirdRing);

        //REMOVE OUTLIERS
        void removeOutliers(bool SecondRing, bool ThirdRing);

        //SMUDGE FUNCTIONS
        void smudgeWhite(int ChanceDenominator, bool SecondRing, bool ThirdRing);
        void smudgeNoise(FastNoiseLite map, float threshold, bool SecondRing, bool ThirdRing);



        //PRIVATE VAR
        int currentRegLocation=0;

    public:

        const short MovePaternsAdjacent[8][2] = { //ADJACENT MOVE PATTERNS
            {-1,-1}, {-1,0}, {-1,1},
            {0,-1},          {0, 1},
            {1,-1},  {1,0},  {1,1}};

       const short MovePaternsNonAdjacent[16][2] = { //MID RANGE MOVE PATTERNS
        {-2,-2}, {-2,-1}, {-2,0}, {-2,1}, {-2,2},
        {-1,-2},                          {-1,2},
        {0,-2},                           {0, 2},
        {1,-2},                           {1, 2},
        {2,-2},  {2,-1},  {2,0},  {2,1},   {2,2},
       };

       const short MovePaternsDistant[56][2] = { //DISTANT MOVE PATTERNS
        {-4,-4}, {-4,-3}, {-4,-2}, {-4,-1}, {-4,0}, {-4,1}, {-4,2}, {-4,3}, {-4,4},
        {-3,-4}, {-3,-3}, {-3,-2}, {-3,-1}, {-3,0}, {-3,1}, {-3,2}, {-3,3}, {-3,4},
        {-2,-4}, {-2,-3},                                           {2,3},   {2,4},
        {0, -4}, {0, -3},                                           {0,3},   {0,4},
        {1,-4},   {1,-3},                                           {1,3},   {1,4},
        {2,-4},   {2,-3},                                           {2,3},   {2,4},
        {3,-4},  {3,-3},  {3,-2},  {3,-1},  {3,0},  {3,1},  {3,2},  {3,3},   {3,4},
        {4,-4},  {4,-3},  {4,-2},  {4,-1},  {4,0},  {4,1},  {4,2},  {4,3},   {4,4}};






        //LOCATION
        //CONSTANTS
        const int LastBit = 1<<15;
        const int LastBitRemovalAND = LastBit-1;
        const int FirstRingAND = (((1<<4)-1)<<11); // >> 11
        const int SecondRingAND = (((1<<5)-1)<<6); // >> 6
        const int LastRingAND = ((1<<6)-1); //No shift needed



        //CONSTRUCTOR
        Stack(short* ZoomLookUp){
            ZoomReferenceTable=ZoomLookUp; //free(ZoomLookUp);

            MapReg = (short*) calloc(scale*scale*2, sizeof(short));

            RunStack();
        };



        //ACCESSORS
        short* getMap(){return MapReg;}
        short getScale(){return scale;}


        //
        //LOCATION
        //PRINT
        void print();
        void printFacets();
        void printSimpleComp();
        void printSimple();



        //CLOSE AND RESET
        void close(){
            MapReg = (short*) realloc (MapReg, scale*scale*sizeof(short));
        };
        void reset(){free(MapReg);}
};

#include "StackNewMaps.h"
#include "StackPrintFunctions.h"
#include "StackRemoveOutliers.h"
#include "StacksCleanUp.h"
#include "StacksZoom.h"
#include "StacksHelper.h"
#include "StacksRefresh.h"
#include "StacksSimpleFunc.h"
#include "StacksSmudge.h"


//LOCATION
//STACK RUNNER
void Stack :: RunStack(){
        currentScale=6;
        newWhiteNoiseMap(6, false, false);
        RefreshInternalDebug(false);
        printSimpleComp();
        FastZoom();
        RefreshInternalDebug(false);
        printSimpleComp();
    };


/*
        FastNoiseLite map = FastNoiseLite();
        map.SetNoiseType(map.NoiseType_OpenSimplex2S);
        map.SetFractalOctaves(8);
        map.SetSeed(rand());
        map.SetFractalType(map.FractalType_FBm);
        new_fBM_Map((float)(1/6.0), map, false);
*/
