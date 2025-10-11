package Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

class StorageManager{

    private final int num;
    private ArrayList<wrapper> accounts;

    public StorageManager(int num, ArrayList<wrapper> accounts){this.num=num; this.accounts=accounts;}
    public void updateAccounts(ArrayList<wrapper> accounts){this.accounts=accounts;}


    //save data
    public boolean save(String password){
        try{
            //byte[] that cipher needs
            byte[] initializationVector = new byte[16]; //byte[] we want
            byte[] InitTemp = keys.reHash(password.getBytes()); //larger byte[]
            for(int i=0; i<16; i++){initializationVector[i] = InitTemp[i];} //transfer some
            InitTemp=null; //through away larger byte[]
            IvParameterSpec ivParameterSpec= new IvParameterSpec(initializationVector); //store for cipher

            //create folder
            File file = new File("ref/Storage/Bank" + String.valueOf(num) + "/"); //find folder
            file.mkdir(); //ensure exists

            //get file
            file = new File("ref/Storage/Bank" + String.valueOf(num) + "/Bank" + String.valueOf(num) + ".txt"); //find file
            file.createNewFile(); //ensure exists

            //generate encryption key
            KeyGenerator gen = KeyGenerator.getInstance("AES"); gen.init(256); //initialize key generator
            SecretKey encryption = gen.generateKey(); //get key

            //initialize cipher object
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); //get instance
            cipher.init(Cipher.ENCRYPT_MODE, encryption, ivParameterSpec); //init

            //initialize key storage
            KeyStore keyStore = KeyStore.getInstance("JCEKS"); //get instance
            keyStore.load(new FileInputStream("ref/security/cacerts"), "changeit".toCharArray()); //init

            //helpers
            ProtectionParameter param1 = new KeyStore.PasswordProtection(password.toCharArray()); //create password
            SecretKeyEntry entry = new SecretKeyEntry(encryption); //create key entry

            //store entry
            keyStore.setEntry("EncryptionKey" + String.valueOf(num)+ " Alias", entry, param1); //put entry
            FileOutputStream fos = new FileOutputStream("ref/Storage/Bank" + String.valueOf(num) + "/EncryptionKey" + String.valueOf(num)); //get file output
            keyStore.store(fos, "changeit".toCharArray()); //store
            fos.close(); //replace file
            

            //store data
            int size =accounts.size(); //get size
            ArrayList<byte[]> bytes = new ArrayList<byte[]>(); //temporary byte storage
            byte[] temp=new byte[4]; //manipulatable storage

            for(int i=0; i<4; i++){
                temp[i]=(byte)(size & 15); //add one byte from LSB
                size>>=8; //remove one byte
            }
            bytes.add(temp.clone()); //add in size header
            

            //add accounts
            size =accounts.size();
            wrapper element;
            long l;
            short num;
            int byteCount=4;
            
            for(int i=0; i<size; i++){
                element=accounts.get(i); //get account

                temp=element.key; byteCount+=temp.length; bytes.add(temp.clone()); //find key, add, increase length counter
                temp=element.pin.getBytes(); byteCount+=temp.length; bytes.add(temp.clone()); //find pin, get bytes, add, increase length

                l=Double.doubleToLongBits(element.member.getCurrentBalance()); //find balance
                temp=new byte[8];
                for(int j=0; j<8; j++){
                    temp[j]=(byte)(l & 255); //put into bytes
                    l>>=8; //
                }
                bytes.add(temp.clone()); byteCount+=temp.length; //add, increase length

                num=element.member.getShortNum(); //find balance
                temp=new byte[2];
                for(int j=0; j<2; j++){
                    temp[j]=(byte)(num & 255); //put into bytes
                    num>>=8; //
                }
                bytes.add(temp.clone()); byteCount+=temp.length; //add, increase length

                temp=element.member.getName().getBytes(); //get name
                bytes.add(new byte[]{(byte)temp.length}); //add byte saying name length in bytes
                bytes.add(temp.clone()); byteCount+=temp.length+1; //add name and update counter
            }

            //repackage data for storage
            byte[] byteArr = new byte[byteCount]; //form byte[]
            byteCount=0; //repurpose as index

            for(byte[] arr : bytes){
                for(byte e : arr){
                    byteArr[byteCount]=e; //load from ArrayList
                    byteCount++; //increment index
                }
            }
            bytes=null; //clean up

            //pad data if necessary
            if(byteArr.length%16!=0){

                byte[] paddingTemp=byteArr.clone(); //create clone
                int newLength =((paddingTemp.length>>4)+1)<<4; //store necessary length
                byte filler=(byte)(newLength-paddingTemp.length); //store padding filler byte

                byteArr=new byte[newLength]; //set to new length under same name for ease of use
                for(int i=0; i<paddingTemp.length; i++){byteArr[i]=paddingTemp[i];} //copy over normal information
                for(int i=paddingTemp.length; i<newLength; i++){byteArr[i]=filler;} //pad with filler
            }

            //finish
            byte[] cipherText = cipher.doFinal(byteArr); //encrypt
            Files.write(file.toPath(), cipherText, new OpenOption[0]); //write
        }

        //catch so many exceptions that it is generalized
        catch(IOException IO){IO.printStackTrace(); return false;} //fail
        catch(Exception e){e.printStackTrace(); System.exit(-1);} //other failures

        return true;
    }



    //load from file
    public static Bank getInstance(File file, FileInputStream keyStoreFile, int num, String password){

        Bank bank=null;//create bank object, null in case it fails
        
        try{
            //exact same byte[] set up as the start of store 
            byte[] initializationVector = new byte[16];
            byte[] InitTemp = keys.reHash(password.getBytes());
            for(int i=0; i<16; i++){initializationVector[i] = InitTemp[i];}
            InitTemp=null;
            IvParameterSpec ivParameterSpec= new IvParameterSpec(initializationVector);


            //get keystore from file
            KeyStore keyStore = KeyStore.getInstance("JCEKS"); //get object
            keyStore.load(keyStoreFile, "changeit".toCharArray()); //load

            //get key retriever
            SecretKeyEntry getter;
            try{getter = //get retriever
            (SecretKeyEntry)keyStore.getEntry( 
                "EncryptionKey" + String.valueOf(num)+ " Alias", //name of file
                 new KeyStore.PasswordProtection(password.toCharArray()));} //password to file
            catch(UnrecoverableKeyException e){return null;}

            if(getter==null){return null;} //failed
            
            //get key
            SecretKey retrieved = getter.getSecretKey();

            //initialize decoder
            Cipher decoder = Cipher.getInstance("AES/CBC/PKCS5Padding"); //get object
            decoder.init(Cipher.DECRYPT_MODE, retrieved, ivParameterSpec); //init

            //read and decode
            byte[] fileByteArr = Files.readAllBytes(file.toPath()); //read bytes
            byte[] decoded;
            try{decoded = decoder.doFinal(fileByteArr);} //decode
            catch(BadPaddingException e){return null;}


            //vars
            byte[] temp = new byte[4];
            int pointer=0;

            //get size
            for(byte i=0; i<4; i++){temp[i]=decoded[pointer]; pointer++;} //get size bytes
            int size = toInt(temp); //interpret as int
            temp=null; //get ride of temp

            //vars for creating bank
            bank = new Bank(password, num); //set bank to new object
            ArrayList<wrapper> accounts = new ArrayList<wrapper>(); //make an account list

            //load account
            for(int i=0; i<size; i++){
                accounts.add(new wrapper()); //add to arr list

                //get key
                byte[] key = new byte[16]; //length
                for(byte j=0; j<16; j++){key[j]=decoded[pointer]; pointer++;} //transfer
                accounts.get(i).key=key; //put into account

                //get user inputted passcode
                byte[] pin = new byte[10]; //length
                for(byte j=0; j<10; j++){pin[j] = decoded[pointer]; pointer++;} //transfer
                accounts.get(i).pin=new String(pin); //put into account

                //get balance
                byte[] balanceBytes = new byte[8]; //length
                for(byte j=0; j<8; j++){balanceBytes[j] = decoded[pointer]; pointer++;} //transfer

                //get routing num
                byte[] numBytes = new byte[2]; //length
                for(byte j=0; j<2; j++){numBytes[j]=decoded[pointer]; pointer++;} //transfer

                //get name
                byte NameByteCount = decoded[pointer]; pointer++; //get name length
                byte[] nameBytes = new byte[NameByteCount]; //length
                for(byte j=0; j<NameByteCount; j++){nameBytes[j] = decoded[pointer]; pointer++;} //transfer

                //make member and add to account
                accounts.get(i).member=new BankAccount(new String(nameBytes), key, toDouble(balanceBytes), toShort(numBytes));
            }

            //store accounts
            bank.setAccounts(keys.reHash(password.getBytes()), accounts);
        }
        catch(Exception e){e.printStackTrace(); System.exit(-1);} //catches
        try{keyStoreFile.close();}
        catch(IOException e){}
        return bank; //return new bank object or null if failed
    }

    public static boolean deleteSpecific(int num, String pin, FileInputStream keyStoreFile) throws Error{
        boolean control=false;
        if(pin.equals("TEACHER")){control=true;}/*SECURITY OVERRIDE*/
        if(!control){

            try{
                KeyStore keyStore = KeyStore.getInstance("JCEKS"); //get object
                keyStore.load(keyStoreFile, "changeit".toCharArray()); //load

                if(keyStore.getEntry( //get keystore and check passcode
                    "EncryptionKey" + String.valueOf(num)+ " Alias", 
                    new KeyStore.PasswordProtection(pin.toCharArray())) !=null){control=true;}
                }
                catch(Exception e){return false;}
        }
        if(control){
            try{
                return delete(num); //delete folder
            }
            catch(Exception e){throw new Error();} //throw error if partial fail
        }
        try{keyStoreFile.close();} //close
        catch(IOException e){}
        return false;
    }
    private static boolean delete(int num) throws Error{
        try{
            String name = "Bank" + String.valueOf(num);

            //delete bank byte[] file
            File file = new File("ref/Storage/" + name + "/" + name + ".txt");
            file.createNewFile();
            file.delete();

            //delete key storage file
            file = new File("ref/Storage/" + name + "/EncryptionKey" + String.valueOf(num));
            file.createNewFile();
            file.delete();

            //delete folder
            file = new File("ref/Storage/" + name);
            file.createNewFile();
            file.delete();
            return true;
        }
        catch(Exception e){return false;}
    }
    public static boolean deleteAll() throws Error{/*SECURITY OVERRIDE*/
        try{ /*SECURITY OVERRIDE*/
            //vars /*SECURITY OVERRIDE*/
            File file = new File("ref/Storage/"); //get main folder file /*SECURITY OVERRIDE*/
            File movingFile; /*SECURITY OVERRIDE*/
            int count=0; //bank number iterator /*SECURITY OVERRIDE*/
            while(file.list().length>1){ //while their are files left /*SECURITY OVERRIDE*/
                movingFile=new File("ref/Storage/Bank" + String.valueOf(count)); //get next folder/*SECURITY OVERRIDE*/
                if(movingFile.exists()){if(!delete(count)){throw new Error();}} //if exists, delete /*SECURITY OVERRIDE*/
                count++; //iterate /*SECURITY OVERRIDE*/
            } /*SECURITY OVERRIDE*/
            return true; /*SECURITY OVERRIDE*/
        } /*SECURITY OVERRIDE*/
        catch(Exception e){throw new Error();} /*SECURITY OVERRIDE*/
    }/*SECURITY OVERRIDE*/



    //convert byte[]s to primitives
    private static short toShort(byte[] input){ //short

        //vars
        int size=(input.length <2 ? input.length : 2);//size we care about is at most two bytes (even if list is longer)
        short s=0; //returned value (+= is used so start at 0)
        byte b; //byte primitive

        //loop
        for(byte i=0; i<size; i++){
            b=input[i]; //store primitive

            //add unsigned byte value, bit shifted into place
            s+=((short)(b&127))<<(i<<3); //get unsigned portion of byte into place
            if((b&128) != 0){
                s+=128<<(i<<3); //if sign bit is filled, treat it as a normal (non MSB) bit
            }
        }

        //return
        return s;
    }
    private static int toInt(byte[] input){ //int 

        //almost exactly the same as short but longer
        //vars
        int size=(input.length <4 ? input.length : 4);
        int Int=0;
        byte b;

        //loop
        for(byte i=0; i<size; i++){
            b=input[i]; //primitive
            Int+=((int)(b&127))<<(i<<3); //unsigned and slotted into place
            if((b&128) != 0){
                Int+=128<<(i<<3); //sign bit
            }
        }
        return Int;
    }
    private static double toDouble(byte[] input){ //double

        //very similar but longer still, we first make a long then turn to a double with built in method
        //vars
        int size=(input.length <8 ? input.length : 8);
        long l=0;
        byte b;

        for(byte i=0; i<size; i++){
            b=input[i];//loop
            l+=((long)(b&127))<<(i<<3); //unsigned in place
            if((b&128) != 0){
                l+=128l<<(i<<3); //sign bit
            }
        }
        return Double.longBitsToDouble(l); //to double and return
    }
}
