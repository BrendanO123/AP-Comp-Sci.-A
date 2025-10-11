public class Extras {
    public static void main(String[] args){
        Parent obj1 = new Parent(); //obj is parent
        Parent obj2 = new Child(); //obj is child
            //child constructor calls parent constructor first no matter what
                //parent constructor calls to an overdidden method will call method from child
                //parent constructor which prints type will print child

        Parent[] arr1 = new Parent[1]; //element of arr of Parent is the same as above

        obj1.echo(); //parent echo
            //printing this.getClass() is still child as this is an object of Child
        obj2.echo(); //child echo
            //is always child.echo() even when called from parent constructor

        obj1.ParentMethod(); //parent method
        obj2.ParentMethod(); //parent method

        /*obj2.ChildMethod();*/             //doesn't compile bc method call fails bc expects parent object
        ((Child)obj2).ChildMethod();  //method call works bc now expects child object, casting works bc obj2.getClass() is Child
        /*((Child)obj1).ChildMethod();*/    //doesn't run bc casting fails
    }
}
class Parent{
    
    public void echo(){
        System.out.println("Parent Class Echo");
        System.out.println(this.getClass());
    }

    public Parent(){
        echo();
    }

    public void ParentMethod(){
        System.out.println("Parent Method");
    }
}

class Child extends Parent{
    public Child(){

    }
    public Child(int x){
        super();
    }
    public void echo(){
        System.out.println("Child Class Echo");
        System.out.println(this.getClass());
    }
    public void ChildMethod(){
        System.out.println("Child Method");
    }
}