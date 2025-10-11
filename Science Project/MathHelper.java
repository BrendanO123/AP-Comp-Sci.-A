import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class MathHelper {
    private static final short[] green = {0, (short)255, 0};
    private static final short[] blue = {0, 0, (short)255};
    private static final short[] black = {0, 0, 0};

    private static final double gold = 0.5+Math.sqrt(1.25);
    private static final double psi = 1-gold;
    private static final double rootFive = Math.sqrt(5);

    public static void main(String[] args){
        MathHelper operator = new MathHelper();
        operator.ExplicitFib(20, 10., -6., 0);
    }

    public double[] ExplicitFib(int length, int index){
        if(index>-1){return ExplicitFib(length, 0.,1., index);}
        else{return ExplicitFib(length, 0.,1.,0);}
    }
    public double[] ExplicitFib(int length){
        return ExplicitFib(length, 0.,1.,0);
    }

    public double[] ExplicitFib(int length, double UZero, double UOne, int index){
        double[] calculated = new double[length];
        double a=(UOne-(UZero*psi))/rootFive;
        double b=((UZero*gold)-UOne)/rootFive;
        for(int i =0; i< length; i++){
            calculated[i]=Math.round((a*Math.pow(gold, i+index) + b*Math.pow(psi, i+index))*10000)/10000.;
        }
        System.out.print("\nCalculated: ");
            for(int i =0; i< length; i++){
                System.out.print(calculated[i] + ", ");
            }
        System.out.print("\n\n");
        return calculated;
    }



    public int[] ExplicitFibInt(int length, int UZero, int UOne, int index){
        int[] calcuated = new int[length];
        double a=(UOne-(UZero*psi))/rootFive;
        double b=((UZero*gold)-UOne)/rootFive;
        for(int i =0; i< length; i++){
            calcuated[i]=(int)Math.round(a*Math.pow(gold, i+index) + b*Math.pow(psi, i+index));
        }
        System.out.print("\nCalculated: ");
            for(int i =0; i< length; i++){
                System.out.print(calcuated[i] + ", ");
            }
        System.out.print("\n");
        return calcuated;
    }

    public int[] ExplicitFibInt(int length, int index){
        if(index>-1){return ExplicitFibInt(length, 0,1, index);}
        else{return ExplicitFibInt(length, 0,1,0);}
    }
    public int[] ExplicitFibInt(int length){
        return ExplicitFibInt(length, 0,1,0);
    }

    public void Run(){
        //-24 25, min – max when short
        //-18 19, min – max when 4096
        //-12 15, min – max when 256
        //catchMinMax(256);
        BufferedImage img = new BufferedImage(4096, 4096, 1);
        //BufferedImage graph = new BufferedImage(256*256, 15+12, 1);
        float value;
        int color;
        //float[][] values = new float[256*256][2];
        for (int i=0; i<4096; i++){
            for (int j=0; j<4096; j++){
                value=analysis(i, -j);
                if(value>=0){
                    value/=19;
                    color=colorLerp(green, black, value);
                    img.setRGB(i, j, color);
                }
                else{
                    value/=-18;
                    color=colorLerp(blue, black, value);
                    img.setRGB(i, j, color);
                }
                /*if(j!=0){values[j+(i*256)][0]=(i/(float)j);}
                else{values[j+(i*256)][0]=Integer.MAX_VALUE;}
                values[j+(i*256)][1]=value;*/
            }
        }
        try{ImageIO.write(img, "png", new File("Set.png"));}
        catch(IOException e){e.printStackTrace();}
        img=null;
    }
    public void catchMinMax(int range){
        int min=0, max=0, value;
        for (int i=0; i<range; i++){
            for (int j=0; j<range; j++){
                value=analysis(i, -j);
                if(value>max){max=value;}
                if(value<min){min=value;}
            }
        }
        System.out.println(min + " " + max);
    }
    public int Analysis(int a, int b){
        if(a>0 && b<=0){return analysis(a,b);}
        return 0;
    }
    private int analysis(int a, int b){
        int count=1;
        int temp;
        while (true){
            temp=b+a;
            a=b;
            b=temp;
            count++;
            if(!((a>0) ^ (b>0))){break;}
        }
        if(temp<=0){count*=-1;}
        return count;
    }
    public int Analysis(double a, double b){
        if(a>0 && b<=0){return analysis(a,b);}
        return 0;
    }
    private int analysis(double a, double b){
        int count=1;
        double temp;
        while (true){
            temp=b+a;
            a=b;
            b=temp;
            count++;
            if(!((a>0) ^ (b>0))){break;}
        }
        if(temp<=0){count*=-1;}
        return count;
    }
    public static float Lerp(float a, float b, float t){return a + t * (b - a);}
    public static float clamp(float a, float b, float t){return (t>=a ? a : (t<=b ? b : t));}
    private int colorLerp(short[] a, short[] b, float t){
        int[] rgb = new int[3];
        for (byte i =0; i<3; i++){
            rgb[i]=(int)clamp(255, 0, (Lerp(a[i], b[i], t)));
        }
        //System.out.println((int)Lerp(a[1], b[1], t) + " " + (int)Lerp(a[2], b[2], t) + " " + a[1] + ", " + b[1] + " | " + a[2] + ", " + b[2]);
        return new Color(rgb[0], rgb[1], rgb[2]).getRGB();
    } 
}
