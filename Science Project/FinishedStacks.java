import java.text.ParseException;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class FinishedStacks{

    public static int max, min;

    public static short[][] newLandMap(Random randNum, FastNoiseLite LandNoise) throws IOException{
        
        //randNum.setSeed(1234567890987654322l);

        LandNoise.SetSeed(randNum.nextInt());
        LandNoise.SetFractalType(FastNoiseLite.FractalType.FBm);

        StackFunctions LandMap = new StackFunctions(randNum, LandNoise);

        //TODO: LandMap stack

        LandMap.newWhiteNoiseMap(10, (short)16,(short)16);

        StackFunctions.Scale_Type Local = StackFunctions.Scale_Type.Local;
        StackFunctions.Scale_Type Adjacent = StackFunctions.Scale_Type.Adjacent;
        StackFunctions.Scale_Type Distant = StackFunctions.Scale_Type.Distant;

        StackFunctions.Noise_Type Perlin = StackFunctions.Noise_Type.Perlin;

        //System.out.println(System.currentTimeMillis() + " Start");

        long previousTime =System.currentTimeMillis();
        final long initialTime=System.currentTimeMillis();

        ArrayList<Integer> Problems = new ArrayList<Integer>();

        short[][] DistMap = new short[2048][2048];

        try {
            LandMap.DivideFalseDeserts(Local, 2);
            LandMap.DivideFalseDeserts(Local, 2);

            LandMap.ConnectIslands(Adjacent, Integer.MAX_VALUE,Integer.MAX_VALUE,10,10,10);
            LandMap.ConnectIslands(Adjacent, Integer.MAX_VALUE,Integer.MAX_VALUE,10,15,15);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.5);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,10,15,15);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.3);

            LandMap.DivideFalseDeserts(Local, 20);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,15,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,25,30);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.3);
            LandMap.Smudge(Local, Perlin, 0.4);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,5,15,15);
            
            LandMap.ZoomBlurred(Local, Perlin, 0.3);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);

            LandMap.ZoomBlurred(Local, Perlin, 0.4);

            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            LandMap.ConnectIslands(Local, Integer.MAX_VALUE,Integer.MAX_VALUE,8,10,10);
            

            LandMap.ZoomBlurred(Local, Perlin, 0.3);
            LandMap.ConnectIslands(Local, 2,2,5,Integer.MAX_VALUE,Integer.MAX_VALUE);
            LandMap.ConnectIslands(Local, 2,3,5,Integer.MAX_VALUE,Integer.MAX_VALUE);
            LandMap.ConnectIslands(Local, 2,3,5,Integer.MAX_VALUE,Integer.MAX_VALUE);
            LandMap.RefreshThirdRing();
            LandMap.ZoomBlurred(Distant, Perlin, 0.4);
            
            LandMap.RemoveOutliers(Local);
            LandMap.RefreshThirdRing();
            LandMap.RemoveOutliers(Distant);

            System.out.println("\n" + (System.currentTimeMillis()-previousTime) + " Generation Time (Milliseconds)\n");
            previousTime=System.currentTimeMillis();
            DistMap=LandMap.CleanUpUpdated((short)150);
            System.out.println((System.currentTimeMillis()-previousTime) + " Clean Up Time (Milliseconds)");
        }
        catch (ParseException e){
            e.printStackTrace();
            System.exit(-1);
        }

        //BufferedImage DistMapImage = new BufferedImage(LandMap.width, LandMap.length, BufferedImage.TYPE_INT_RGB);

        max =1;
        min=-1;
        for (short x=0; x<DistMap.length; x++){
            for (short y=0; y<DistMap[0].length; y++){
                if (DistMap[x][y]>max){max=(int)DistMap[x][y];}
                if (DistMap[x][y]<min){min=(int)DistMap[x][y];}
            }
        }

        //System.out.println(max); //

        /*for (short x=0; x<DistMap.length; x++){
            for (short y=0; y<DistMap[0].length; y++){
                DistMapImage.setRGB(x, y, (DistMap[x][y]>0 ? new Color(0, (int)((DistMap[x][y]/(double)max)*255), 0).getRGB() : Math.abs(DistMap[x][y])<=max ? new Color(0, 0, (int)(255+((DistMap[x][y]/(double)max)*255))).getRGB() : new Color(0, 0, 0).getRGB()));
            }
        }

        ImageIO.write(DistMapImage, "PNG", new File("DistMapImage.png"));

        DistMapImage=null;*/

        Problems.addAll(LandMap.refreshInternalDebug());
        System.out.println("\n" + (Problems.size()/2) + " Internal Refresh Errors\n");
        LandMap.RefreshThirdRing();

        /*ArrayList<ArrayList<Short>> IslandMap = LandMap.getMap();

        int width = IslandMap.size();
        int length = IslandMap.get(0).size();

        BufferedImage LandImageDebug = new BufferedImage(width*2, length*2, BufferedImage.TYPE_INT_RGB);

        for (int x=0; x<width; x++){
            for (int y=0; y<length; y++){
                short shortValue= (IslandMap.get(x).get(y));
                LandImageDebug.setRGB(x,y,(shortValue>0 ? Color.GREEN.getRGB() : Color.BLUE.getRGB()));
                LandImageDebug.setRGB(width+x,y, new Color(((int)(((shortValue&30720)>>11)/8.0*255)),0,0).getRGB());
                LandImageDebug.setRGB(x,length+y,new Color(0,((int)(((shortValue&1984)>>6)/16.0*255)),0).getRGB());
                LandImageDebug.setRGB(width+x,length+y,new Color(0,0,((int)((shortValue&63)/56.0*255))).getRGB());
            }
        }

        ImageIO.write(LandImageDebug, "PNG", new File("LandImageDebug.png"));

        IslandMap=null;*/

        //Problems.addAll(LandMap.refreshInternalDebug());

        System.out.println((System.currentTimeMillis()-initialTime) + " Final (Milliseconds)\n");

        return DistMap;
    }
}