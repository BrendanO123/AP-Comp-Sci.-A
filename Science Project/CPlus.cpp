#include <iostream>
#include <fstream>
#include "FastNoiseLite.h"
#include "Stacks.cpp"
#include <cstdlib>
#include <time.h>
#include "Assets.h"

using namespace std;

//BIOMES
enum Biomes : unsigned char{
        River, 

        Shore, Abyssal_Ocean, DeepOcean, ShallowOcean, LowLand, Inland, 

        Mountains, Transitions, Hills, Plains, Basins,

        Void, Hollow_Ocean, Unstable_Ocean,
        Frozen_Ocean, Cold_Ocean, Warm_Ocean,  
        Frozen_Shallow, Reef,
        Snowy_Peaks, Rocky_Peaks, Volcano,
        Snowy_Meadow, Lush_Meadow, Rocky_Meadow, Savanna, Mesa,
        Hilly_Snowed_Forest, Hilly_Cold_Forest, Hilly_Warm_Forest, Rolling_Fields, Grassland, 
        Tundra, Snowy_Forest, Cold_Forest, Redwood_Forest, Warm_Forest, Sparse_Plains, Prairie, 

        Dessert, Swamp,
        Hilly_Dry_Forest, Hilly_Moderate_Forest, Hilly_Lush_Forest, Hilly_Jungle,
        Moderate_Forest, Lush_Forest, Jungle
    };

//MAIN
int main(){

    //ASSETS
    Asset reference=Asset();
    //reference.RefreshZoomReferences();
    //SEED
    cout << "Do You Want To Enter a Seed? (1 for \"Yes\", 0 for \"No\"):";
    int control=0;
    srand(time(0));
    //srand(rand());
    try{
        cin >> control;
        if (control==1){
            cout << "Enter Seed:";
            cin >> control; 
            srand(control);
        }
    }
    catch (...){cout << "Invalid Response. Omitting Seed.";}

    //INITIALIZE STACK
    short* map;
    Stack stack = Stack(reference.getLookUpTable());
    map = stack.getMap();
    //stack.printFacets();
    cout << "\n\n";
    stack.close();
    stack.reset();

    return 0;
}