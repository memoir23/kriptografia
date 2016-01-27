package ppke.kripto;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypting {
	
	private static final Random rand = new SecureRandom();
	
	public static byte[] writeCiphered(byte[] msg, byte[] key) throws IOException {
		assert key.length == 16;
		byte[] out;
		try {
			byte[] iv = new byte[16];
			rand.nextBytes(iv);

			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
			
			
			out = new byte[msg.length + iv.length];
				
			    System.arraycopy(iv, 0, out, 0,iv.length);
			    System.arraycopy(msg, 0, out, iv.length, msg.length);

			return out;
		} catch(GeneralSecurityException e) {
			throw new IOException(String.format("Enciphering error: '%s'", e.getMessage()), e);
		}
	}
	
	
	public static byte[] readCiphered(byte[] chiperedMsg,  byte[] iv ,byte[] key) throws IOException {

		try {
		
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
			return cipher.doFinal(chiperedMsg);
		} catch(GeneralSecurityException e) {
			throw new IOException(String.format("Deciphering error: '%s'", e.getMessage()), e);
		}
	}
	
}
