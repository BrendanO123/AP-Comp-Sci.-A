import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.util.*;

import FBM.Float3;
import FBM.Noise;
import FBM.*;

@SuppressWarnings("unused")
public class Main{
    
    public static void main(String[] args){

        /*final Color gray = new Color(128, 128, 128);
        final Color green = new Color(34, 139, 34);

        int scale = 1024;

        Noise noiseGen = new Noise();
        noiseGen.setFractalOctaves(8);
        noiseGen.setSeed(System.currentTimeMillis());
        File png = new File("Fractal_Types.png");
        //File png = new File("Preview.png");

        BufferedImage img = new BufferedImage(scale * 2, scale * 2, BufferedImage.TYPE_INT_RGB);
        
        for(int x=0; x<scale; x++){
            for(int y=0; y<scale; y++){
                Float3 value=noiseGen.getTerrainNoise((float) x, (float) y, 4);
                img.setRGB(x, y, new Color(value.x/2 + 0.5f, value.x/2 + 0.5f, value.x/2 + 0.5f).getRGB());
                img.setRGB(x, y+scale, new Color(value.y/4.14f + 0.5f, 0.f, value.x/2 + 0.5f).getRGB());//tested max was ±2.067122
                img.setRGB(x+scale, y, new Color(value.z/4.14f + 0.5f, 0.f, value.x/2 + 0.5f).getRGB());
                img.setRGB(x+scale, y+scale, new Color(value.y/4.14f + 0.5f, value.z/4.14f + 0.5f, value.x/2 + 0.5f).getRGB());
            }
        }*/

        int scale = 256;

        Random rand = new Random(System.currentTimeMillis());
        Noise fractal = new Noise();
        Noise perlin = new Noise();
        fractal.setFractalOctaves(8);
        fractal.setSeed(System.currentTimeMillis());
        perlin.setSeed(System.currentTimeMillis()+1);
        File png = new File("Fractal_Types.png");

        BufferedImage img = new BufferedImage(scale * 3, scale, BufferedImage.TYPE_INT_RGB);
        
        for(int x=0; x<scale; x++){
            for(int y=0; y<scale; y++){
                float valueF = fractal.getFBmNoise((float) x * 4.f, (float) y * 4.f);
                float valueP = fractal.SinglePerlin(perlin.Seed, (float) x * 4.f * perlin.frequency, (float) y * 4.f * perlin.frequency);
                float valueW = rand.nextFloat();
                img.setRGB(x + scale + scale, y, new Color(valueF/2 + 0.5f, valueF/2 + 0.5f, valueF/2 + 0.5f).getRGB());
                img.setRGB(x + scale, y, new Color(valueP/2 + 0.5f, valueP/2 + 0.5f, valueP/2 + 0.5f).getRGB());
                img.setRGB(x, y, new Color(valueW, valueW, valueW).getRGB());
            }
        }

        try{ImageIO.write(img, "png", png);}
        catch(IOException e){e.printStackTrace();}


        /*int scale = 48;
        System.out.print("x_{1}=\\left[");
        BufferedImage img = new BufferedImage(scale*2, scale*2, BufferedImage.TYPE_INT_RGB);
        for(int x=0; x<scale; x++){
            for(int y=0; y<scale; y++){
                Float3 value=noiseGen.getTerrainNoise((float) 3*x, (float) 3*y, 0);
                img.setRGB(x, y, new Color(value.x/2 + 0.5f, value.x/2 + 0.5f, value.x/2 + 0.5f).getRGB());
                img.setRGB(x, y+scale, new Color(value.y/4.14f + 0.5f, 0.f, value.x/2 + 0.5f).getRGB());//tested max was ±2.067122
                img.setRGB(x+scale, y, new Color(value.z/4.14f + 0.5f, 0.f, value.x/2 + 0.5f).getRGB());
                img.setRGB(x+scale, y+scale, new Color(value.y/4.14f + 0.5f, value.z/4.14f + 0.5f, value.x/2 + 0.5f).getRGB());
                System.out.print(value.x);
                if(x!=scale-1 || y!=scale-1){System.out.print(",\\ ");}
            }
        }
        System.out.println("\\right]");
        try{ImageIO.write(img, "png", png);}
        catch(IOException e){e.printStackTrace();}*/
    }
}