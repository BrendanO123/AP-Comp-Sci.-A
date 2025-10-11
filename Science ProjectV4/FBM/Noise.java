package FBM;

import java.util.*;

public class Noise {

    // Hashing
    private static final int PrimeX = 501125321; //X++
    private static final int PrimeY = 1136930381; //Y++

    private static final float Log1_2 = (float) Math.log(1.2);

    //instance
    private Random rand= new Random();
    private int octaves = 3;
    private float lacunarity = 2.0f;
    private float gain = 0.5f;
    public float frequency = 0.01f;
    private float fractalBounding = 1 / 1.75f;

    //temp
    private int clampCount=0;

    //seed
    public int Seed = 1337;


    //constructors
    public Noise(){}
    public Noise(long seed){rand.setSeed(seed); this.Seed=rand.nextInt();}



    //setters
    public void setFractalOctaves(int octaves){this.octaves=octaves; CalculateFractalBounding();}
    public void setFractalLacunarity(float lacunarity){this.lacunarity=lacunarity;}
    public void setFractalGain(float gain){this.gain=gain; CalculateFractalBounding();}
    public void setFractalFrequency(float frequency){this.frequency=frequency;}
    public void SetFractalBounding(float bounding){this.fractalBounding=bounding;}
    public void setSeed(long seed){rand.setSeed(seed); this.Seed=rand.nextInt();}


    //getters
    public int getNumOctaves(){return octaves;}
    public float getFractalLacunarity(){return lacunarity;}
    public float getFractalGain(){return gain;}
    public float getFractalFrequency(){return frequency;}
        //temp
        public int getClampCount(){return clampCount;}

    //fast math
    public static float FastAbs(float f) { return f < 0 ? -f : f; }
    public static int FastFloor(float f) { return f >= 0 ? (int)f : (int)f - 1; }
    public static float FastMin(float a, float b) { return a < b ? a : b; }
    public static float FastMax(float a, float b) { return a > b ? a : b; }
    public static int FastRound(float f) { return f >= 0 ? (int)(f + 0.5f) : (int)(f - 0.5f); }

    public static float FastSqrt(float f) { return (float)Math.sqrt(f); }
    private static float Clamp(float a, float b, float t){return (t>=b ? b : (t<=a ? a : t));}
    public static float length(float a, float b){return FastSqrt((a * a) + (b * b));} 

    //lerp and interpolate
    public static float Lerp(float a, float b, float t) { return a + t * (b - a); }
    public static float InterpQuintic(float t) { return t * t * t * (t * (t * 6 - 15) + 10); }
    public static float SCurve(float t) { return t * t * (3 - 2 * t); }
    private float F3ToFactor(Float3 values){
        float l = length(values.y, values.z); 
        return (l >= 0.839099631177f ? ((float)Math.log(l + 0.360900368823))/Log1_2 : 1);}
    //private static float InterpSquishingFactor(float t) { return (float)Math.log(t+10); } //TODO: tweak squishing factor


    //set frac bounding
    private void CalculateFractalBounding()
    {
        float Gain = FastAbs(gain);
        float amp = Gain;
        float ampFractal = 1.0f;
        for (int i = 1; i < octaves; i++)
        {
            ampFractal += amp;
            amp *= Gain;
        }
        fractalBounding = 1 / ampFractal;
    }



    //main call, handles frequency
    public Float3 getTerrainNoise(float x, float y, int affectedLayerCount){
        x*=frequency; y*=frequency;
        return GenTerrainFBm(x, y, affectedLayerCount);
    }

    public float getFBmNoise(float x, float y){
        x*=frequency; y*=frequency;
        return GenFBm(x, y);
    }
    public float getFDSMagnitude(float x, float y){
        x*=frequency; y*=frequency;
        float h=0.0000001f;
        float xd = (GenFBm(x+h,y)-GenFBm(x-h,y))/(2*h);
        float yd= (GenFBm(x,y+h)-GenFBm(x,y-h))/(2*h);
        return length(xd, yd);
    }
    public Float3 getFBmWithSlope(float x, float y){
        x*=frequency; y*=frequency;
        return GenFBmWithSlope(x, y);
    }


    //secondary call, handles FBm logic
    private Float3 GenTerrainFBm(float x, float y, int affectedLayerCount)
    {
        if(affectedLayerCount<=0 || affectedLayerCount>=octaves){return GenFBmWithSlope(x, y);}

        int seed = Seed;
        float amp = fractalBounding;

        Float3 values = new Float3(0, 0, 0);
        for (int i = 0; i < octaves-affectedLayerCount; i++)
        {
            values.PlusEq(analyticalPerlin(seed++, x, y).MultEq(amp));

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }
        float factor = F3ToFactor(values);
        for(int i=0; i<affectedLayerCount; i++){

            values.x+=analyticalPerlin(seed++, x, y).x*amp/factor;
            //values.PlusEq(analyticalPerlin(seed++, x, y).MultEq(amp/factor));

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }
        if(values.x >1.0f || values.x<-1.0f){values.x=Clamp(-1.0f, 1.0f, values.x); values.y=values.z=0; clampCount++;}
        return values;
    }

    private float GenFBm(float x, float y)
    {
        int seed = Seed;
        float sum=0;
        float amp = fractalBounding;

        for (int i = 0; i < octaves; i++)
        {
            float noise = SinglePerlin(seed++, x, y);
            sum+= noise * amp;

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }

        return sum;
    }

    private Float3 GenFBmWithSlope(float x, float y)
    {
        int seed = Seed;
        float amp = fractalBounding;

        Float3 values = new Float3(0, 0, 0);
        for (int i = 0; i < octaves; i++)
        {
            values.PlusEq(analyticalPerlin(seed++, x, y).MultEq(amp));

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }
        return values;
    }

    //tertiary call, handles perlin logic
    public float SinglePerlin(int seed, float x, float y)
    {
        int x0 = FastFloor(x); //floor x
        int y0 = FastFloor(y); //floor y

        float xd0 = (float)(x - x0); //fract x, distance from left wall
        float yd0 = (float)(y - y0); //fract y, distance from ceiling
        float xd1 = xd0 - 1; //distance from right wall
        float yd1 = yd0 - 1; //distance from floor

        float xs = InterpQuintic(xd0); //interp
        float ys = InterpQuintic(yd0); //interp

        x0 *= PrimeX; //Hashing for left two grad vectors
        y0 *= PrimeY; //Hashing for upper two left grad vectors
        int x1 = x0 + PrimeX; //Hashing for right two grad vectors
        int y1 = y0 + PrimeY; //Hashing for bottom two grad vectors

        float xf0 = Lerp(GradCoord(seed, x0, y0, xd0, yd0), GradCoord(seed, x1, y0, xd1, yd0), xs); //lerp between values from upper vectors
        float xf1 = Lerp(GradCoord(seed, x0, y1, xd0, yd1), GradCoord(seed, x1, y1, xd1, yd1), xs); //lerp between values from lower vectors

        return Lerp(xf0, xf1, ys) * 1.4247691104677813f; //lerp between upper and lower and *=(num>=sqrt(2))
            //1/sqrt(2) is theoretical maximum for perlin noise before multiplication
            //to make maximum 1, multiply by sqrt(2). 
            //However, if the perfect conditions for 1/sqrt(2) are not possible with your set of grad vector directions
                //(pi/4 multiples are needed), 
            //your grad vectors are slightly shorter than normalized,
            //or the pseudo-rand num generator doesn't allow the grad and displacement vectors to be parallel

            //then the max will be below 1/sqrt(2) and the factor (seen above at the end of the return) should be > sqrt(2)
    }

    public Float3 F3Perlin(float x, float y){return analyticalPerlin(Seed, x*frequency, y*frequency);}

    private Float3 analyticalPerlin(int seed, float x, float y){
        int x0 = FastFloor(x); //floor x
        int y0 = FastFloor(y); //floor y

        float xfl0 = (float)(x - x0); //fract x, distance from left wall
        float yfl0 = (float)(y - y0); //fract y, distance from ceiling
        float xfl1 = xfl0 - 1; //distance from right wall
        float yfl1 = yfl0 - 1; //distance from floor

        float xs = InterpQuintic(xfl0); //interp
        float ys = InterpQuintic(yfl0); //interp

        x0 *= PrimeX; //Hashing for left two grad vectors
        y0 *= PrimeY; //Hashing for upper two left grad vectors
        int x1 = x0 + PrimeX; //Hashing for right two grad vectors
        int y1 = y0 + PrimeY; //Hashing for bottom two grad vectors

        int hash = Grad2DHash(seed, x0, y0); Float2 g00 = new Float2(Gradients2D[hash], Gradients2D[hash | 1]); //grad vec a
        hash = Grad2DHash(seed, x1, y0); Float2 g10 =new Float2(Gradients2D[hash], Gradients2D[hash | 1]); //grad vec b 
        hash = Grad2DHash(seed, x0, y1); Float2 g01 =new Float2(Gradients2D[hash], Gradients2D[hash | 1]); //grad vec c
        hash = Grad2DHash(seed, x1, y1); Float2 g11 =new Float2(Gradients2D[hash], Gradients2D[hash | 1]); //grad vec d

        float x2 = xfl0 * xfl0;
        float x3 = x2 * xfl0;
        float xd1 = ( 36 * x3  -  105 * x2  +  100 * xfl0  -  30 ) * x2;
        float xd0 = ( 36 * x2  -  75 * xfl0  +  40 ) * x3;

        float y2 = yfl0 * yfl0;
        float y3 = y2 * yfl0;
        float yd0 = (6 * y2  -  15 * yfl0 + 10) * y3;

        float dx = 
            g00.x 

            + ( 30 * yfl0 * ( g10.y - g00.y ) * ( x2 - 2 * xfl0 + 1 ) * x2 )  
            + ( g10.x * xd1 - g00.x * xd0 )

            + (( g01.x - g00.x ) * yd0 )

            + ( yd0 * (( g00.x - g01.x ) * xd0  +  ( g11.x - g10.x ) * xd1 ))
            + (( 30 * ( x2 -2 * xfl0 + 1 ) * x2 ) * ((( g00.y - g10.y ) * yfl0 * yd0 )  
            + (( g11.y - g01.y ) * (( 6 * y3  -  21 * y2  +  25 * yfl0  -  10) * y3 ))));

        

        float yd2 = ( 36 * y3  -  105 * y2  +  100 * yfl0  -  30 ) * y2;
        float yd1 = ( 36 * y2  -  75 * yfl0  +  40 ) * y3;
        
        float xd2 = (6 * x2  -  15 * xfl0 + 10) * x3;
        
        float dy = 
            g00.y 
        
            + ( g10.y - g00.y ) * xd2
            + ( 30 * ( g01.x - g00.x ) * xfl0 * y2 * ( y2 - 2 * yfl0 + 1 ))  
            + ( g01.y * yd2 - g00.y * yd1 )
        
            + 30 * y2 * ( y2 - 2 * yfl0 + 1 ) * 
                (( g00.x - g01.x ) * xd2 * xfl0 
                + ( g11.x - g10.x) * (( 6 * x3  -  21 * x2  +  25 * xfl0  -  10) * x3 ))
            + xd2 * (( g00.y - g10.y ) * yd1 + ( g11.y - g01.y ) * yd2 );


        
        
        
        float xf0 = Lerp(g00.x * xfl0 + g00.y * yfl0, g10.x * xfl1 + g10.y * yfl0, xs); //lerp between values from upper vectors
        float xf1 = Lerp(g01.x * xfl0 + g01.y * yfl1, g11.x * xfl1 + g11.y * yfl1, xs); //lerp between values from lower vectors

        return new Float3(Lerp(xf0, xf1, ys) * 1.4247691104677813f, dx, dy); //lerp between upper and lower and *=(num>=sqrt(2))
            //1/sqrt(2) is theoretical maximum for perlin noise before multiplication
            //to make maximum 1, multiply by sqrt(2). 
            //However, if the perfect conditions for 1/sqrt(2) are not possible with your set of grad vector directions
                //(pi/4 multiples are needed), 
            //your grad vectors are slightly shorter than normalized,
            //or the pseudo-rand num generator doesn't allow the grad and displacement vectors to be parallel

            //then the max will be below 1/sqrt(2) and the factor (seen above at the end of the return) should be > sqrt(2)
    }

    private static float GradCoord(int seed, int xPrimed, int yPrimed, float xd, float yd)
    {
        int hash = Hash(seed, xPrimed, yPrimed);
        hash ^= hash >> 15; //bit shift XOR is pseudorandom
        hash &= 127 << 1; //set bit considerations
            //list is 256 long (8 bits) and last bit (even or odd) shows if it is an x or y value

        float xg = Gradients2D[hash]; //retrieve grad vectors
        float yg = Gradients2D[hash | 1]; //retrieve grad vectors

        return xd * xg + yd * yg; //dot product
    }

    private static int Hash(int seed, int xPrimed, int yPrimed) //I am not messing with their hashing. Dear god
    {
        //I want to say this is just a fast and medium predictability pseudorandom number generator
        int hash = seed ^ xPrimed ^ yPrimed;

        hash *= 0x27d4eb2d;
        return hash;
    }

    private static int Grad2DHash(int seed, int xPrimed, int yPrimed){
        int hash = seed ^ xPrimed ^ yPrimed;
        hash *= 0x27d4eb2d;

        hash ^= hash >> 15;
        hash &= 127 << 1;

        return hash;
    }

    private static final float[] Gradients2D = {
        0.130526192220052f,  0.99144486137381f,   0.38268343236509f,   0.923879532511287f,  0.608761429008721f,  0.793353340291235f,  0.793353340291235f,  0.608761429008721f,
        0.923879532511287f,  0.38268343236509f,   0.99144486137381f,   0.130526192220051f,  0.99144486137381f,  -0.130526192220051f,  0.923879532511287f, -0.38268343236509f,
        0.793353340291235f, -0.60876142900872f,   0.608761429008721f, -0.793353340291235f,  0.38268343236509f,  -0.923879532511287f,  0.130526192220052f, -0.99144486137381f,
       -0.130526192220052f, -0.99144486137381f,  -0.38268343236509f,  -0.923879532511287f, -0.608761429008721f, -0.793353340291235f, -0.793353340291235f, -0.608761429008721f,
       -0.923879532511287f, -0.38268343236509f,  -0.99144486137381f,  -0.130526192220052f, -0.99144486137381f,   0.130526192220051f, -0.923879532511287f,  0.38268343236509f,
       -0.793353340291235f,  0.608761429008721f, -0.608761429008721f,  0.793353340291235f, -0.38268343236509f,   0.923879532511287f, -0.130526192220052f,  0.99144486137381f,
        0.130526192220052f,  0.99144486137381f,   0.38268343236509f,   0.923879532511287f,  0.608761429008721f,  0.793353340291235f,  0.793353340291235f,  0.608761429008721f,
        0.923879532511287f,  0.38268343236509f,   0.99144486137381f,   0.130526192220051f,  0.99144486137381f,  -0.130526192220051f,  0.923879532511287f, -0.38268343236509f,
        0.793353340291235f, -0.60876142900872f,   0.608761429008721f, -0.793353340291235f,  0.38268343236509f,  -0.923879532511287f,  0.130526192220052f, -0.99144486137381f,
       -0.130526192220052f, -0.99144486137381f,  -0.38268343236509f,  -0.923879532511287f, -0.608761429008721f, -0.793353340291235f, -0.793353340291235f, -0.608761429008721f,
       -0.923879532511287f, -0.38268343236509f,  -0.99144486137381f,  -0.130526192220052f, -0.99144486137381f,   0.130526192220051f, -0.923879532511287f,  0.38268343236509f,
       -0.793353340291235f,  0.608761429008721f, -0.608761429008721f,  0.793353340291235f, -0.38268343236509f,   0.923879532511287f, -0.130526192220052f,  0.99144486137381f,
        0.130526192220052f,  0.99144486137381f,   0.38268343236509f,   0.923879532511287f,  0.608761429008721f,  0.793353340291235f,  0.793353340291235f,  0.608761429008721f,
        0.923879532511287f,  0.38268343236509f,   0.99144486137381f,   0.130526192220051f,  0.99144486137381f,  -0.130526192220051f,  0.923879532511287f, -0.38268343236509f,
        0.793353340291235f, -0.60876142900872f,   0.608761429008721f, -0.793353340291235f,  0.38268343236509f,  -0.923879532511287f,  0.130526192220052f, -0.99144486137381f,
       -0.130526192220052f, -0.99144486137381f,  -0.38268343236509f,  -0.923879532511287f, -0.608761429008721f, -0.793353340291235f, -0.793353340291235f, -0.608761429008721f,
       -0.923879532511287f, -0.38268343236509f,  -0.99144486137381f,  -0.130526192220052f, -0.99144486137381f,   0.130526192220051f, -0.923879532511287f,  0.38268343236509f,
       -0.793353340291235f,  0.608761429008721f, -0.608761429008721f,  0.793353340291235f, -0.38268343236509f,   0.923879532511287f, -0.130526192220052f,  0.99144486137381f,
        0.130526192220052f,  0.99144486137381f,   0.38268343236509f,   0.923879532511287f,  0.608761429008721f,  0.793353340291235f,  0.793353340291235f,  0.608761429008721f,
        0.923879532511287f,  0.38268343236509f,   0.99144486137381f,   0.130526192220051f,  0.99144486137381f,  -0.130526192220051f,  0.923879532511287f, -0.38268343236509f,
        0.793353340291235f, -0.60876142900872f,   0.608761429008721f, -0.793353340291235f,  0.38268343236509f,  -0.923879532511287f,  0.130526192220052f, -0.99144486137381f,
       -0.130526192220052f, -0.99144486137381f,  -0.38268343236509f,  -0.923879532511287f, -0.608761429008721f, -0.793353340291235f, -0.793353340291235f, -0.608761429008721f,
       -0.923879532511287f, -0.38268343236509f,  -0.99144486137381f,  -0.130526192220052f, -0.99144486137381f,   0.130526192220051f, -0.923879532511287f,  0.38268343236509f,
       -0.793353340291235f,  0.608761429008721f, -0.608761429008721f,  0.793353340291235f, -0.38268343236509f,   0.923879532511287f, -0.130526192220052f,  0.99144486137381f,
        0.130526192220052f,  0.99144486137381f,   0.38268343236509f,   0.923879532511287f,  0.608761429008721f,  0.793353340291235f,  0.793353340291235f,  0.608761429008721f,
        0.923879532511287f,  0.38268343236509f,   0.99144486137381f,   0.130526192220051f,  0.99144486137381f,  -0.130526192220051f,  0.923879532511287f, -0.38268343236509f,
        0.793353340291235f, -0.60876142900872f,   0.608761429008721f, -0.793353340291235f,  0.38268343236509f,  -0.923879532511287f,  0.130526192220052f, -0.99144486137381f,
       -0.130526192220052f, -0.99144486137381f,  -0.38268343236509f,  -0.923879532511287f, -0.608761429008721f, -0.793353340291235f, -0.793353340291235f, -0.608761429008721f,
       -0.923879532511287f, -0.38268343236509f,  -0.99144486137381f,  -0.130526192220052f, -0.99144486137381f,   0.130526192220051f, -0.923879532511287f,  0.38268343236509f,
       -0.793353340291235f,  0.608761429008721f, -0.608761429008721f,  0.793353340291235f, -0.38268343236509f,   0.923879532511287f, -0.130526192220052f,  0.99144486137381f,
        0.38268343236509f,   0.923879532511287f,  0.923879532511287f,  0.38268343236509f,   0.923879532511287f, -0.38268343236509f,   0.38268343236509f,  -0.923879532511287f,
       -0.38268343236509f,  -0.923879532511287f, -0.923879532511287f, -0.38268343236509f,  -0.923879532511287f,  0.38268343236509f,  -0.38268343236509f,   0.923879532511287f,
   };
}