//package Project;

public class Fractal_2D{
    public Fractal_2D(int starting_length, int starting_width, int ending_length, int ending_width, byte perlin_smoothing_count, 
    byte octaves, int persistence){
        System.out.println("constructor one 2D");
    }

    public Fractal_2D(int length, int width, byte perlin_smoothing_count, byte octaves, int persistence){
        System.out.println("constructor two 2D");
    }

    public Fractal_2D(int scale, byte perlin_smoothing_count, byte octaves, int persistence){
        System.out.println("constructor three 2D");
    }

    public Fractal_2D(int scale, byte octaves, int persistence){
        System.out.println("constructor four 2D");
    }

    public Fractal_2D(){
        System.out.println("constructor five 2D");
    }
}
