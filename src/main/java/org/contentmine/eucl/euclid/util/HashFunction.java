package org.contentmine.eucl.euclid.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HashFunction {

	/** from https://stackoverflow.com/questions/33085493/how-to-hash-a-password-with-sha-512-in-java
	 * 
	 * @param string to hash
	 * @return
	 */
	public static String getSHA512Hash(String string){
        return getHash(string, "SHA-512");
	}

	public static String getMD5Hash(String string){
        return getHash(string, "MD5");
	}

	private static String getHash(String string, String algorithm) {
		String generatedHash = null;
	    try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
//		        md.update(salt.getBytes(StandardCharsets.UTF_8));
	        byte[] bytes = md.digest(string.getBytes(StandardCharsets.UTF_8));
	        StringBuilder sb = new StringBuilder();
	        for(int i=0; i< bytes.length ;i++){
	            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        generatedHash = sb.toString();
	    } catch (NoSuchAlgorithmException e){
	        e.printStackTrace();
	    }
		return generatedHash;
	}
}
