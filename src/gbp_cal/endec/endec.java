package gbp_cal.endec;

import gbp_cal.Sets;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;

public class endec {
    /**
     * @param args
     * Enhanced AES Encryption by NICK
     */
    //private static final byte[] keyValue = 
       // new byte[] {'T','h','i','s','I','s','A','S','e','c','r','e','t','K','e','y'};
    private final char[] keyValue = 
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()+-/,.".toCharArray();
    private String key = ""; // 128 bit key
    private String initVector = ""; // 16 bytes IV
    public endec() {
        key = InvertStr(generateKey());
        initVector = generateBytes();
    }
    private String InvertStr(String in) { // By Nick
        String out = "";
        for(int i = in.length()-1; i >= 0; i--)
                out += in.charAt(i);
        return out;
    }
    public String getKey() {
        return this.key;
    }
    private String generateKey() {
        for(int i = 0; i < 16; i++)
                key += keyValue[new Random().nextInt(keyValue.length)];
        return key;
    }
    public String getBytes() {
            return this.initVector;
    }
    private String generateBytes() {
            for(int i = 0; i < 16; i++)
                    initVector += keyValue[new Random().nextInt(keyValue.length)];
                    //initVector = "RandomInitVector";
        return initVector;
    }
    public String encrypt(String value) {
            return encrypt(key, initVector, value);
    }
    private String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return InvertStr( Base64.encodeBase64String(encrypted) );
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    //public String decrypt(String value) {
            //return decrypt(key, initVector, value);
    //}
    public String decrypt(String key, String initVector, String encrypted) {
        try {
        	String temp = InvertStr( encrypted );
        	encrypted = temp;
        	
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(DatatypeConverter.parseBase64Binary(encrypted));
            return new String(original);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
