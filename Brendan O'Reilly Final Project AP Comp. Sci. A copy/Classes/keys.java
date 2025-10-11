package Classes;

//imports
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class keys {
    //creates hashes
    private static MessageDigest hasher;

    //initializes and catches exception 
    static{
        try{hasher=MessageDigest.getInstance("MD5");}
        catch(NoSuchAlgorithmException e){e.printStackTrace(); System.exit(-1);}
    }

    //statically creates a key from an int
    public static byte[] getKey(int pin){
        return hasher.digest(String.valueOf(pin).getBytes());
    }

    //creates a partially modular exponentiated value to share back to maker
    public static int Encrypt(int base, int exponent, int mod){
        int product=1;
        for(int i=0; i<exponent; i++){product*=base; product%=mod;}
        return product;
    }
    //uses message digest to get a hash
    public static byte[] reHash(byte[] hash){
        return hasher.digest(hash);
    }
}
