package Classes;

import java.security.SecureRandom;
class BankAccount {

    //statics
    private static short count=0; //bank account count
    private static int base=7607, mod =211; //values Diffie-Hellman key exchange with modular exponentiation for key exchange
    private static SecureRandom rand= new SecureRandom(); //randomizer

    static{for(int i=0; i<(rand.nextInt() & 15); i++){rand.nextLong();}} //init randomizer

    //instance variables
    private final short num; //routing number but useful
    private final String routingNum, name; //why string number though
    private double value; //balance

        //access key for account
        private final byte[] key;






    //constructors
    //normal constructor
    public BankAccount(String name, double balance, int pin, int[] returnedPartial){
        num=count++;
        routingNum=String.valueOf(num);
        this.name=name;
        value=balance;

        //randomization and truncation for better speed
        int h =randomize(rand.nextInt());

        //get key
        returnedPartial[0]=keys.Encrypt(base, h, mod);
        key=keys.getKey(keys.Encrypt(pin, h, mod));
    }
    public BankAccount(String name, int pin, int[] returnedPartial){this(name, 0., pin, returnedPartial);}//no balance
    public BankAccount(String name, byte[] key, double balance, short number){ //for loading from file
        
        //allow for new number selection
        num=number;
        if(number>=count){number++; count=number;}


        //same as normal constructor
        routingNum=String.valueOf(num);
        this.name=name;
        value=balance;

        //get key
        this.key=key;
    }
    //We need a name so no more constructors

    //basics
    public String getName(){return name;}
    public String getRoutingNum(){return routingNum;}
    public short getShortNum(){return num;}
    public double getCurrentBalance(){return value;}
    public String toString(){
        //return string and ensure correct number of decimal places
        int temp=(int)(value * 100 +0.5);
        return "Account Number: " + routingNum + "\nName: " + name + "\nBalance: $" + ((temp)/100.) + (temp % 10 == 0 ? "0" : "");
    }

    //secure functions, all check input key against account key
    public boolean Deposit(double amount, byte[] hash){
        if(java.security.MessageDigest.isEqual(key, hash)){value+=amount; return true;}
        return false;
    }
    public boolean Withdraw(double amount, byte[] hash){
        if(java.security.MessageDigest.isEqual(key, hash)){value-=amount; return true;}
        return false;
    }
    public boolean setBalance(double amount, byte[] hash){
        if(java.security.MessageDigest.isEqual(key, hash)){value=amount; return true;}
        return false;
    }

    //randomizing and truncating exponent for encrypting
    private int randomize(int h){
        h^=h>>14;
        h^=3637;
        h&=254;
        return h;
    }
}
