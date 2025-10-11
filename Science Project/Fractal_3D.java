//package Project;
public class Fractal_3D{
    public Fractal_3D(int starting_length, int starting_width, int starting_height, 
    int ending_length, int ending_width, int ending_height, byte perlin_smoothing_count, byte octaves, int persistence){
        System.out.println("constructor one 3D");
    }

    public Fractal_3D(int length, int width, int height, byte perlin_smoothing_count, byte octaves, int persistence){
        System.out.println("constructor two 3D");
    }

    public Fractal_3D(int scale, byte perlin_smoothing_count, byte octaves, int persistence){
        System.out.println("constructor three 3D");
    }

    public Fractal_3D(int scale, byte octaves, int persistence){
        System.out.println("constructor four 3D");
    }

    public Fractal_3D(){
        System.out.println("constructor five 3D");
    }
}