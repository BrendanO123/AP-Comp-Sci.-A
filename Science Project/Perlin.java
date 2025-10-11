//package Project;
import java.util.*;

//class for 2D noise
class Perlin_2D{

    ArrayList<Double> value=new ArrayList<>();
    int length;
    int width;
    int[] permuteTable = this.permuteTable();

    public Perlin_2D(int starting_length, int starting_width, int totalSmoothingFactor, 
    byte smoothing_iterations){

        length=starting_length;
        width=starting_width;

        //get gradient vectors
        for (int i=0;i<(length*width);i++){
            int index =2*permuteTable[i&255];
            value.add(Project.vector_table[index]);
            value.add(Project.vector_table[index+1]);
        }
        /*
        for (int i=0;i<(length*width);i+=2){
            System.out.println(Math.sqrt(((value.get(i)*value.get(i))+(value.get(i+1)*value.get(i+1)))));
            System.out.println(Math.sqrt(((value.get(i)*value.get(i))+(value.get(i+1)*value.get(i+1))))==1.0);
        }
        */
        System.out.println(value.toString());

        for (int i=0; i<smoothing_iterations; i++){
            this.Smooth(totalSmoothingFactor/smoothing_iterations);
        }
        this.interpolate();
        System.out.println("Perlin static constructor 2D");
    }



     //permute table for indexes of gradient vectors list
     int[] permuteTable(){
        int[] value= Project.value.clone();
        //value.shuffle()
        for (int i=0; i<256; i++){
            Random x = new Random();
            int index=x.nextInt(256);
            int temp =value[index];
            value[index]=value[i];
            value[i]=temp;
        }
        return value;
        
    }



    
    //pre-generate equal spaced normalized 2D gradient vectors 
    static double[] VectorTable(){

        double[] end = new double[512];
        int[] value= Project.value.clone();

        //map value to fraction between [0,1)
        //map value to radian, [0,2Pi)
        //get x and y values for points on circle
        for (int i =0; i<512; i+=2){
            end[i]=(Math.sin((value[i/2]/(double)256)*(2*Math.PI)));
            end[i+1]=(Math.cos((value[i/2]/(double)256)*(2*Math.PI)));
        }
        return end;
    }

    void Smooth(int smoothingFactor){
        
    }

    void interpolate(){
        
    }
}


/*
class Perlin_3D{
    ArrayList<Double> value;
    int length;
    int width;
    int depth;
    public Perlin_3D(int starting_length, int starting_width, int starting_height, 
    int ending_length, int ending_width, int ending_height, byte smoothing_iterations){
        length=starting_length;
        width=starting_width;
        depth=starting_height;
        randVectorTable start = new randVectorTable(length*width*depth);
        value=new ArrayList<>(start.end.get(0));
        for (int i=0; i<(length*width*depth); i++){
            value.add(start.end.get(i));
        }
        
        System.out.println(value.toString());
        System.out.println("Perlin constructor 3D");
    }
    ArrayList<Double> randVectorTable(int length){
        final double golden = (1.0+Math.sqrt(5.0))/(2.0);
        //(0,±1,±golden)
        //(±golden,±1,0)
        //(±golden,0,±1)
        ArrayList<Double> end = new ArrayList <Double>();
        ArrayList<Integer> value= new ArrayList<Integer>();
        for (int i=0;i<length;i++){
            value.add(i);
        }
        for (int i=0; i<length; i++){
            Random x = new Random();
            int index=x.nextInt(length);
            int temp =value.get(index);
            value.set(index,value.get(i));
            value.set(i,temp);
        }
        for (int i =0; i<length; i++){
            
        }
        return end;
    }



}
*/

/*
class randVectorTable{
    final double golden = (1.0+Math.sqrt(5.0))/(2.0);
    ArrayList<Double> end = new ArrayList <Double>();;
    public randVectorTable(int length){
        ArrayList<Integer> value= new ArrayList<Integer>();
        for (int i=0;i<length;i++){
            value.add(i);
        }
        for (int i=0; i<length; i++){
            Random x = new Random();
            int index=x.nextInt(length);
            int temp =value.get(index);
            value.set(index,value.get(i));
            value.set(i,temp);
        }
        for (int i =0; i<length; i++){
            end.add(Math.sin((value.get(i)/(double)length)*(2*Math.PI)));
            end.add(Math.cos((value.get(i)/(double)length)*(2*Math.PI)));
        }
    }


}
*/