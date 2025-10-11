package Classes;

import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.SecureRandom;;


public class Bank {

    //valid characters for user pin
    private static char[] pinChars = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    };

    private static int count=0; //number of banks
    private final int num; //individual number (number of preceding banks) (for txt file name)
    private final String mainPass; //password to save and load bank

    //see security.txt for explanation of security features
    private static int base=7607, mod =211; //values for Diffie-Hellman key exchange modular exponentiation
    private SecureRandom rand= new SecureRandom(); //init randomizer for secure key generation

    private final StorageManager storage; //for saving bank
    private ArrayList<wrapper> accounts = new ArrayList<wrapper>();
    //same as:
        /*private ArrayList<BankAccount> members = new ArrayList<BankAccount>();
        private ArrayList<byte[]> keyList = new ArrayList<byte[]>();
        private ArrayList<String> pins = new ArrayList<String>();*/




    //basic constructor
    public Bank(String mainPassword){

        for(int i=0; i<(rand.nextInt() & 15); i++){rand.nextLong();} //cycle randomizer
        num=count++; //get number and increment count
        storage= new StorageManager(num, accounts); //init storage manager
        mainPass=mainPassword; //set password
    }
    //loading from file constructor, overrides number
    public Bank(String mainPassword, int num){
        mainPass=mainPassword; //same
        for(int i=0; i<(rand.nextInt() & 15); i++){rand.nextLong();} //
        storage= new StorageManager(num, accounts); //
        if(new Throwable().getStackTrace()[1].getClassName().equals("Classes.StorageManager")){
            this.num=num; //store num
            if(num+1>count){count=num+1;} //correct counter
        }
        else{
            this.num=count++;
        }
    }
    //set accounts when loading from file
    public void setAccounts(byte[] hash, ArrayList<wrapper> accounts){
        if (java.security.MessageDigest.isEqual(keys.reHash(mainPass.getBytes()), hash) 
            && new Throwable().getStackTrace()[1].getClassName().equals("Classes.StorageManager")){ //if key is correct
            this.accounts=accounts; //set accounts to new, deep copy would be very difficulty so assume trusted
        }
    }


    //
    public boolean echoMembers(String passcode){
        boolean control = passcode.equals(mainPass);
        control |= passcode.equals("TEACHER"); /*SECURITY OVERRIDE*/
        if(control){
            for(wrapper e : accounts){
                System.out.println("{\n" + e.member.toString() + "\n}\n");
            }
            return true;
        }
        return false;
    }


    //Withdraw
    public boolean WithdrawFunds(String AccountNumber, double amount, String pin){

        wrapper temp = getAccount(Short.parseShort(AccountNumber)); //get wrapper
        if(temp==null){return false;} //ensure exists
        boolean control = temp.pin.equals(pin); //separate for override
        control |= pin.equals("TEACHER"); /*SECURITY OVERRIDE*/
        if(control){ //check pin
            if(temp.member.getCurrentBalance()>=amount){ //check available balance
                return temp.member.Withdraw(amount, temp.key); //try to withdraw
            }
        }
        return false;
    }
    //Deposit
    public boolean DepositFunds(String AccountNumber, double amount, String pin){

        //see withdraw
        wrapper temp = getAccount(Short.parseShort(AccountNumber)); 
        if(temp==null){return false;}
        boolean control = temp.pin.equals(pin);
        control |= pin.equals("TEACHER"); /*SECURITY OVERRIDE*/
        if(control){return temp.member.Deposit(amount, temp.key);}
        return false;
    }
    //check balance
    public double checkBalance(String AccountNumber, String pin){

        //see withdraw
        wrapper temp = getAccount(Short.parseShort(AccountNumber));
        if(temp==null){return -1.;}
        boolean control = temp.pin.equals(pin);
        control |= pin.equals("TEACHER"); /*SECURITY OVERRIDE*/
        if(control){return temp.member.getCurrentBalance();}
        return -1.;
    }


    //Binary search
    public BankAccount findAccount(String accountNum){
        wrapper temp=getAccount(Short.parseShort(accountNum)); //get account
        if(temp!=null){return temp.member;} //exists
        else{return null;} //doesn't exist
    }
    private wrapper getAccount(short routingNum){
        if(accounts.size()!=0){return binSearch(0, accounts.size()-1, routingNum);} //index possible, so bin search
        else{return null;} //no accounts to search
    }
    private wrapper binSearch(int low, int high, short target){

        //basic binary search
        if(low>high){return null;}
        int mIND = (low+high)>>1;
        short value=accounts.get(mIND).member.getShortNum();
        if(value==target){return accounts.get(mIND);}
        else if (value>target){return binSearch(low, mIND-1, target);}
        else{return binSearch(mIND+1, high, target);}
    }


    //create account (no "\n")
    public String createAccount(String name, double balance){

        int[] pointer=new int[1]; //array to pass a value back from the constructor
        int exponent =randomize(rand.nextInt()); //random exponent for key (security.txt)

        int pin =keys.Encrypt(base, exponent, mod); //encrypted value (security.txt)

        BankAccount account=new BankAccount(name, balance, pin, pointer); //new bank account

        byte[] hash=keys.getKey(keys.Encrypt(pointer[0], exponent, mod)); //key (security.txt)

        String temp =clean(keys.reHash(hash)); //pin, just in case hash it again
        accounts.add(new wrapper(account, hash, temp)); //add to account list

        //return routing number and passcode
        return temp + " and your routing number is " + accounts.get(accounts.size()-1).member.getRoutingNum();
    }
    public String createAccount(String name){
        return this.createAccount(name, 0.); //simplified
    }

    public boolean kickAccount(short routingNum, String pin){
        wrapper temp=getAccount(routingNum); //get account
        if(temp==null){return false;}
        boolean control = temp.pin.equals(pin);
        control |= pin.equals("TEACHER"); /*SECURITY OVERRIDE*/
        if (control) {
            accounts.remove(temp);
            return true;
        }
        return false;
    }


    //create a passcode from a byte[] key to an account
    private String clean(byte[] hash){
        long first, second; //need to store many bytes so longs

        //package into longs with bit shifts on individual bytes
        first=second=0;
        for(int i=0; i<8; i++){
            first+=(((long)hash[i])<<(8*i));
            second+=(((long)hash[i+8])<<(8*i));
        }

        //hashing
        first^=first>>5; //pseudo-random like changes
        first&=((1l<<50)-1); //masking to not be too large
        second^=second>>7; //repeat for second long
        second&=((1l<<50)-1); //
        first^=second; //pseudo-random like combination of longs

        //look up chars from valid chars for char[], char[] -> String
        char[] returnChars = new char[10];
        for(int i=0; i<10; i++){
            returnChars[i]=pinChars[((int)(first%62))]; //look up char
            first/=62; //divide
        }

        //return
        return new String(returnChars);
    }


    //save data
    public boolean save(){storage.updateAccounts(accounts);return storage.save(mainPass);} //update and store


    //load from file
    public static Bank getInstance(int num, String key){
        //get bank byte[] file and keystore file (as stream) for encryption key
        File file = new File("ref/Storage/Bank" + String.valueOf(num) + "/Bank" + String.valueOf(num) + ".txt");
        FileInputStream keyStorageFile;

        //try to find file
        try{keyStorageFile = new FileInputStream(new File("ref/Storage/Bank" + String.valueOf(num) + "/EncryptionKey" + String.valueOf(num)));}
        catch(FileNotFoundException e){return null;}

        //get instance
        return StorageManager.getInstance(file, keyStorageFile, num, key);
    }


    public static boolean deleteSpecific(int num, String pin){
        FileInputStream keyStorageFile; //get key storage file for passcode check

        //try to find file
        try{keyStorageFile = new FileInputStream(new File("ref/Storage/Bank" + String.valueOf(num) + "/EncryptionKey" + String.valueOf(num)));}
        catch(FileNotFoundException e){return false;}
        
        return StorageManager.deleteSpecific(num, pin, keyStorageFile); //delete
    }
    public static boolean deleteAll(){ /*SECURITY OVERRIDE*/
        try{ /*SECURITY OVERRIDE*/
            boolean b = StorageManager.deleteAll(); /*SECURITY OVERRIDE*/
            return b; /*SECURITY OVERRIDE*/
        } /*SECURITY OVERRIDE*/
        catch(Error e){return false;} /*SECURITY OVERRIDE*/
    } /*SECURITY OVERRIDE*/


    //for exponent generation for key (security.txt)
    private int randomize(int h){
        h^=h>>14;
        h^=3637;
        h&=254;
        return h;
    }
}

//structure to store necessary information
class wrapper{
    public BankAccount member;
    public byte[] key;
    public String pin;

    public wrapper(BankAccount account, byte[] hash, String password){member=account; key=hash; pin=password;}
    public wrapper(){}
}