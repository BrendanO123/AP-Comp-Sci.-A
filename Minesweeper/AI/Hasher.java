package AI;

import java.security.*;

public class Hasher {

    private static MessageDigest md;


    public static byte[] getHash(long l){   byte[] returnList = md.digest(longToBytes(l)); md.reset();  return returnList;}
    public static boolean equals(long l, byte[] hashed){    return MessageDigest.isEqual(hashed, getHash(l));}

    public static void init(){

        try{md= MessageDigest.getInstance("MD5");}
        catch(NoSuchAlgorithmException e){e.printStackTrace();}
        
    }

    private static byte[] longToBytes(long l) {

        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
        
    }
}
