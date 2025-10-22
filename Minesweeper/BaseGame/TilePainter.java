package BaseGame;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class TilePainter {

    public static final int numRemovalAND = (((1<<5)-1)<<4);

    private static BufferedImage tileSheet = new BufferedImage(390, 60, 1);

    private static Raster Grass;
    private static Raster GrassLight;
    private static Raster Flag;
    private static Raster FlagLight;
    private static Raster Mine;
    private static Raster MineLight;
    private static Raster FalseFlag;
    private static Raster FalseFlagLight;
    private static Raster Empty;
    private static Raster EmptyLight;
    private static Raster One;
    private static Raster OneLight;
    private static Raster Two;
    private static Raster TwoLight;
    private static Raster Three;
    private static Raster ThreeLight;
    private static Raster Four;
    private static Raster FourLight;
    private static Raster Five;
    private static Raster FiveLight;
    private static Raster Six;
    private static Raster SixLight;
    private static Raster Seven;
    private static Raster SevenLight;
    private static Raster Eight;
    private static Raster EightLight;

    public TilePainter(){
        try{

            tileSheet=ImageIO.read(new File("BaseGame/TileSheet.png"));

            Grass=tileSheet.getData(new Rectangle(0, 30, 30, 30));
            GrassLight=tileSheet.getData(new Rectangle(0, 0, 30, 30));
            Flag=tileSheet.getData(new Rectangle(30, 30, 30, 30));
            FlagLight=tileSheet.getData(new Rectangle(30, 0, 30, 30));
            FalseFlag=tileSheet.getData(new Rectangle(60, 30, 30, 30));
            FalseFlagLight=tileSheet.getData(new Rectangle(60, 0, 30, 30));
            Mine=tileSheet.getData(new Rectangle(90, 30, 30, 30));
            MineLight=tileSheet.getData(new Rectangle(90, 0, 30, 30));
            Empty=tileSheet.getData(new Rectangle(120, 30, 30, 30));
            EmptyLight=tileSheet.getData(new Rectangle(120, 0, 30, 30));
            One=tileSheet.getData(new Rectangle(150, 30, 30, 30));
            OneLight=tileSheet.getData(new Rectangle(150, 0, 30, 30));
            Two=tileSheet.getData(new Rectangle(180, 30, 30, 30));
            TwoLight=tileSheet.getData(new Rectangle(180, 0, 30, 30));
            Three=tileSheet.getData(new Rectangle(210, 30, 30, 30));
            ThreeLight=tileSheet.getData(new Rectangle(210, 0, 30, 30));
            Four=tileSheet.getData(new Rectangle(240, 30, 30, 30));
            FourLight=tileSheet.getData(new Rectangle(240, 0, 30, 30));
            Five=tileSheet.getData(new Rectangle(270, 30, 30, 30));
            FiveLight=tileSheet.getData(new Rectangle(270, 0, 30, 30));
            Six=tileSheet.getData(new Rectangle(300, 30, 30, 30));
            SixLight=tileSheet.getData(new Rectangle(300, 0, 30, 30));
            Seven=tileSheet.getData(new Rectangle(330, 30, 30, 30));
            SevenLight=tileSheet.getData(new Rectangle(330, 0, 30, 30));
            Eight=tileSheet.getData(new Rectangle(360, 30, 30, 30));
            EightLight=tileSheet.getData(new Rectangle(360, 0, 30, 30));

        }
        catch(IOException e){e.printStackTrace(); System.exit(-1);}
    }

    //bit covered(0==uncovered), bit flagged(0==no flag), bit mine(1==mine, 0==no mine), bit dark(1==light)
    //4 bits # if uncovered([0,8])

    public Raster getTile(byte state, int x, int y){ //x,y are locations where it will be written

        if(state>=0){
            //Covered

            switch (state & numRemovalAND) {
                case 0b00000000: //0
                    return GrassLight.createTranslatedChild(x, y); //Light Grass
                case 0b00010000: //16
                    return Grass.createTranslatedChild(x, y); //Dark Grass
                case 0b01000000: //64
                    return FalseFlagLight.createTranslatedChild(x, y); //Light Incorrect Flag (Unused)
                case 0b01010000: //80
                    return FalseFlag.createTranslatedChild(x, y); //Dark Incorrect Flag (Unused)
                case 0b01100000: //96
                    return FlagLight.createTranslatedChild(x, y);  //Light Flag 
                case 0b01110000: //112
                    return Flag.createTranslatedChild(x, y);  //Dark Flag
                case 0b00100000: //32
                    return MineLight.createTranslatedChild(x, y); //Light Mine (Unused except for png) 
                case 0b00110000: //48
                    return Mine.createTranslatedChild(x, y); //Dark Mine (Unused except for png) 
                default: //unknown
                    System.err.println("Switch Default in tile retrieval for uncleared tile");
                    return GrassLight.createTranslatedChild(x, y);
            }

        }
        else{
            //Revealed

            switch (state){
                case /*0b10000000*/ (byte)(-128):
                    return EmptyLight.createTranslatedChild(x, y);
                case /*0b10010000*/ (byte)(-112):
                    return Empty.createTranslatedChild(x, y);
                case /*0b10000001*/ (byte)(-127):
                    return OneLight.createTranslatedChild(x, y);
                case /*0b10010001*/ (byte)(-111):
                    return One.createTranslatedChild(x, y);
                case /*0b10000010*/ (byte)(-126):
                    return TwoLight.createTranslatedChild(x, y);
                case /*0b10010010*/ (byte)(-110):
                    return Two.createTranslatedChild(x, y);
                case /*0b10000011*/ (byte)(-125):
                    return ThreeLight.createTranslatedChild(x, y);
                case /*0b10010011*/ (byte)(-109):
                    return Three.createTranslatedChild(x, y);
                case /*0b10000100*/ (byte)(-124):
                    return FourLight.createTranslatedChild(x, y);
                case /*0b10010100*/ (byte)(-108):
                    return Four.createTranslatedChild(x, y);
                case /*0b10000101*/ (byte)(-123):
                    return FiveLight.createTranslatedChild(x, y);
                case /*0b10010101*/ (byte)(-107):
                    return Five.createTranslatedChild(x, y);
                case /*0b10000110*/ (byte)(-122):
                    return SixLight.createTranslatedChild(x, y);
                case /*0b10010110*/ (byte)(-106):
                    return Six.createTranslatedChild(x, y);
                case /*0b10000111*/ (byte)(-121):
                    return SevenLight.createTranslatedChild(x, y);
                case /*0b10010111*/ (byte)(-105):
                    return Seven.createTranslatedChild(x, y);
                case /*0b10001000*/ (byte)(-120):
                    return EightLight.createTranslatedChild(x, y);
                case /*0b10011000*/ (byte)(-104):
                    return Eight.createTranslatedChild(x, y);
                default: //unknown
                    System.err.println("Switch Default in tile retrieval for cleared tile");
                    return GrassLight.createTranslatedChild(x, y);
            }

        }
    }

    public static void main(String[] args){
        int tileSize = 30;
        BufferedImage tileSheet = new BufferedImage(13*tileSize, tileSize * 2, 1);
        genAssets(tileSheet, tileSize);
    }
    public void genAssets(){genAssets(tileSheet, 30);}
    public static void genAssets(BufferedImage spriteSheet, int tileSize){

        Graphics2D graphics = spriteSheet.createGraphics();
        int color;

        color=new Color(170,215,80).getRGB();
        for(int x=0; x<tileSize*3; x++){
            for(int y=0; y<tileSize; y++){
                spriteSheet.setRGB(x, y, color);
            }
        }

        color=new Color(162,209,72).getRGB();
        for(int x=0; x<tileSize*3; x++){
            for(int y=tileSize; y<tileSize*2; y++){
                spriteSheet.setRGB(x, y, color);
            }
        }

        color=new Color(219,50,54).getRGB();
        for(int x=tileSize*3; x<tileSize*4; x++){
            for(int y=0; y<tileSize; y++){
                spriteSheet.setRGB(x, y, color);
            }
        }

        color=new Color(185,60,132).getRGB();
        for(int x=tileSize*3; x<tileSize*4; x++){
            for(int y=tileSize; y<tileSize*2; y++){
                spriteSheet.setRGB(x, y, color);
            }
        }

        color=new Color(229,194,159).getRGB();
        for(int x=tileSize*4; x<tileSize*13; x++){
            for(int y=0; y<tileSize; y++){
                spriteSheet.setRGB(x, y, color);
            }
        }

        color=new Color(215,184,153).getRGB();
        for(int x=tileSize*4; x<tileSize*13; x++){
            for(int y=tileSize; y<tileSize*2; y++){
                spriteSheet.setRGB(x, y, color);
            }
        }


        graphics.setFont(new Font("Helvetica", Font.BOLD, 24));
        FontMetrics fm = graphics.getFontMetrics();

        Color colors[] = {
            new Color(24,118,210),
            new Color(58,143,62),
            new Color(211,48,47),
            new Color(123,32,162),
            new Color(245,130,0),
            new Color(34,138,249),
            new Color(105,105,105),
            new Color(30,20,15)
        };
        for(int i = 1; i <= 8; i++){
            String text = Integer.toString(i);
            int x = 4 * tileSize + ((tileSize - fm.stringWidth(text)) / 2) + i * tileSize;
            int y = ((tileSize + fm.getAscent()) / 2);

            graphics.setColor(colors[i-1]);
            graphics.drawString(text, x, y);
            graphics.drawString(text, x, y + tileSize);
        }

        graphics.setColor(new Color(230,51,6));

        for(int y = 0; y<=tileSize; y+= tileSize){
            graphics.drawLine(tileSize * 2, y, tileSize * 3 - 1, y + tileSize - 1);
            graphics.drawLine(tileSize * 2 + 1, y, tileSize * 3 - 1, y + tileSize - 2);
            graphics.drawLine(tileSize * 2, y + 1, tileSize * 3 - 2, y + tileSize - 1);

            graphics.drawLine(tileSize * 2, y + tileSize - 1, tileSize * 3 - 1, y);
            graphics.drawLine(tileSize * 2, y + tileSize - 2, tileSize * 3 - 2, y);
            graphics.drawLine(tileSize * 2 + 1, y + tileSize - 1, tileSize * 3 - 1, y + 1);
        }

        graphics.setColor(new Color(141,34,35));
        graphics.fillOval(3 * tileSize + (tileSize - 16) / 2 - 1, (tileSize - 16) / 2 - 1, 17, 17);


        graphics.setColor(new Color(120,42,90));
        graphics.fillOval(3 * tileSize + (tileSize - 16) / 2 - 1, tileSize + (tileSize - 16) / 2 - 1, 17, 17);

        try{
            //Load flag icon

            BufferedImage flag = ImageIO.read(new File("BaseGame/flag_icon.png"));
            for(int x=0; x<30; x++){
                for(int y=0; y<30; y++){
                    int RGB =flag.getRGB(x, y);
                    if(RGB!=0){
                        spriteSheet.setRGB(x + (3 * tileSize) / 2 - 16, y + tileSize / 2 - 16, RGB);
                        spriteSheet.setRGB(x + (3 * tileSize) / 2 - 16, y + (3 * tileSize) / 2 - 16, RGB);
                    }
                }
            }

        }
        catch(IOException e){e.printStackTrace();}

        //Write sheet
        try{ImageIO.write(spriteSheet, "PNG", new File("BaseGame/TileSheet" + tileSize + ".png"));}
        catch(IOException e){e.printStackTrace();}

    }
}
