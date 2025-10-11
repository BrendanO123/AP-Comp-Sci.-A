package FBM;

public class Float3 {
    public float x, y, z;

    public Float3(){this.x=this.y=this.z=1.0f;}
    public Float3(float x, float y, float z){this.x=x; this.y=y; this.z=z;}

    public Float3 PlusEq(Float3 other){this.x+=other.x; this.y+=other.y; this.z+=other.z; return this;}
    public Float3 SubEq(Float3 other){this.x-=other.x; this.y-=other.y; this.z-=other.z; return this;}
    public Float3 MultEq(float k){this.x*=k; this.y*=k; this.z*=k; return this;}
    public Float3 DivEq(float k){this.x/=k; this.y/=k; this.z/=k; return this;}
    public Float3 Normalize(){this.DivEq(this.length()); return this;}

    public static Float3 Plus(Float3 main, Float3 other){return new Float3(main.x+other.x, main.y+other.y, main.z+other.z);}
    public static Float3 Sub(Float3 main, Float3 other){return new Float3(main.x-other.x, main.y-other.y, main.z-other.z);}
    public static Float3 Mult(Float3 main, float k){return new Float3(main.x*k, main.y*k, main.z*k);}
    public static Float3 Div(Float3 main, float k){return new Float3(main.x/k, main.y/k, main.z/k);}

    public float DotProduct(Float3 other){return ((this.x * other.x) + (this.y*other.y) + (this.z*other.z));}
    public static float DotProduct(Float3 main, Float3 other){return ((main.x * other.x) + (main.y*other.y) + (main.z*other.z));}

    public float length(){return Noise.FastSqrt(this.x * this.x + this.y * this.y + this.z * this.z);}
    public static float length(Float3 main){return Noise.FastSqrt(main.x * main.x + main.y * main.y + main.z * main.z);}
}
class Float2 {
    public float x, y;

    public Float2(){this.x=this.y=1.0f;}
    public Float2(float x, float y){this.x=x; this.y=y;}

    public Float2 PlusEq(Float2 other){this.x+=other.x; this.y+=other.y; return this;}
    public Float2 SubEq(Float2 other){this.x-=other.x; this.y-=other.y; return this;}
    public Float2 MultEq(float k){this.x*=k; this.y*=k; return this;}
    public Float2 DivEq(float k){this.x/=k; this.y/=k; return this;}
    public Float2 Normalize(){this.DivEq(this.length()); return this;}

    public Float2 Plus(Float2 other){return new Float2(this.x+other.x, this.y+other.y);}
    public Float2 Sub(Float2 other){return new Float2(this.x-other.x, this.y-other.y);}
    public Float2 Mult(float k){return new Float2(this.x*k, this.y*k);}
    public Float2 Div(float k){return new Float2(this.x/k, this.y/k);}

    public static Float2 Plus(Float2 main, Float2 other){return new Float2(main.x+other.x, main.y+other.y);}
    public static Float2 Sub(Float2 main, Float2 other){return new Float2(main.x-other.x, main.y-other.y);}
    public static Float2 Mult(Float2 main, float k){return new Float2(main.x*k, main.y*k);}
    public static Float2 Div(Float2 main, float k){return new Float2(main.x/k, main.y/k);}

    public float DotProduct(Float2 other){return ((this.x * other.x) + (this.y*other.y));}
    public static float DotProduct(Float2 main, Float2 other){return ((main.x * other.x) + (main.y*other.y));}

    public float length(){return Noise.FastSqrt(this.x * this.x + this.y * this.y);}
    public static float length(Float2 main){return Noise.FastSqrt(main.x * main.x + main.y * main.y);}
}
