//package ppke.kripto;
//
//import java.security.InvalidAlgorithmParameterException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.security.NoSuchProviderException;
//import java.security.spec.AlgorithmParameterSpec;
//
//import javax.crypto.Cipher;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.SecretKey;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
//public class BouncyCastleProvider_AES_CBC {
// 
//    // The default block size
//    public static int blockSize = 16;
// 
//    Cipher encryptCipher = null;
//    Cipher decryptCipher = null;
// 
//    // Buffer used to transport the bytes from one stream to another
//    byte[] buf = new byte[blockSize];       //input buffer
//    byte[] obuf = new byte[512];            //output buffer
// 
//    // The key
//    byte[] key = null;
//    // The initialization vector needed by the CBC mode
//    byte[] IV = null;
// 
//    public BouncyCastleProvider_AES_CBC(){
//        //for a 192 key you must install the unrestricted policy files
//        //  from the JCE/JDK downloads page
//        key ="SECRET_1SECRET_2".getBytes();
//        //default IV value initialized with 0
//        IV = new byte[blockSize];
//    }
// 
//    public BouncyCastleProvider_AES_CBC(String pass, byte[] iv){
//        //get the key and the IV
//        key = pass.getBytes();
//        IV = new byte[blockSize];
//        System.arraycopy(iv, 0 , IV, 0, iv.length);
//    }
//    public BouncyCastleProvider_AES_CBC(byte[] pass, byte[]iv){
//        //get the key and the IV
//        key = new byte[pass.length];
//        System.arraycopy(pass, 0 , key, 0, pass.length);
//        IV = new byte[blockSize];
//        System.arraycopy(iv, 0 , IV, 0, iv.length);
//    }
// 
//    public void InitCiphers()
//            throws NoSuchAlgorithmException,
//            NoSuchProviderException,
//            NoSuchProviderException,
//            NoSuchPaddingException,
//            InvalidKeyException,
//            InvalidAlgorithmParameterException{
//       //1. create the cipher using Bouncy Castle Provider
//       encryptCipher =
//               Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
//       //2. create the key
//       SecretKey keyValue = new SecretKeySpec(key,"AES");
//       //3. create the IV
//       AlgorithmParameterSpec IVspec = new IvParameterSpec(IV);
//       //4. init the cipher
//       encryptCipher.init(Cipher.ENCRYPT_MODE, keyValue, IVspec);
// 
//       //1 create the cipher
//       decryptCipher =
//               Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
//       //2. the key is already created
//       //3. the IV is already created
//       //4. init the cipher
//       decryptCipher.init(Cipher.DECRYPT_MODE, keyValue, IVspec);
//    }
//}