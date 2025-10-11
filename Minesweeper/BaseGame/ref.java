package BaseGame;

import java.util.*;

public class ref{
    public static final double Log2 = Math.log(2.);

    @Deprecated
    public static int binarySearch(ArrayList<Integer> list, int value){

        int LBound=0, UBound=list.size(), element, index;
        while(true){
            index=ref.mean(LBound, UBound);
            element=list.get(index);
            if(element>value){UBound=index;}
            else if (element<value){LBound=index;}
            else{return index;}
            if(LBound+1==UBound){return -1;}
        }

    }

    public static int round(double a){return (int)(a+0.5);}
    public static int abs(int a){return (a>0 ? a : -a);}
    public static double abs(double a){return (a>0 ? a : -a);}
    public static int mean(int a, int b){return round((a+b)/2.);}
    public static float lerp(float a, float b, float t){return a + t * (b - a);}
    public static double lerp(double a, double b, double t){return a + t * (b - a);}
    public static double min(double a, double b){return (b>a ? b : a);}
    public static double SCurve(double a){return a*a*(3-2*a);}
    public static double AdjustDist(double t){return (Math.log(Math.sqrt(t)+1)/Log2);}
}