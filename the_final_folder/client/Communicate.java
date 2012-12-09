
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.Hashtable;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Communicate {

	static X509Certificate cert = null;
	static String fserverIp = null;
	static boolean isConnected = false;
	static Encrypt encrypt;
	public static boolean checkAuthentication()
	{
		if(cert != null)
			return true;
		else
			return false;
				
	}
	public static boolean Login(String hash)
	{
		if(hash != null && !hash.trim().equals(""))
		{
			encrypt = new Encrypt();
		    cert = new Encrypt().Login(hash);
		    return checkAuthentication();
		}
		else
			return false;
	}
	
	public static boolean addUser(String hash)
	{
		if(encrypt != null)
		   return encrypt.AddUser(hash);
		else
			return false;
	}
	public static byte[] Encrypt(byte[] data)
	{
		try{
        return encrypt.EncryptData(data);
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	public static byte[] Decrypt(byte [] data)
	{
		try{
        return encrypt.Decrypt(data);
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	public static boolean Login(String hash, String caAddress)
	{
		if(isConnected)
			return true;
		if(caAddress != null && !caAddress.trim().equals(""))
		{
			encrypt = new Encrypt();
		   cert = encrypt.Login(hash, caAddress);
		   
		   return checkAuthentication();
		}
		else
			return false;
	}
	
	
	public static boolean Logout()
	{
		return encrypt.logout();
	}
	public static Hashtable sendMsg(Hashtable table, String ipAddress, int port)
	{
		if(!isConnected)
		{
			isConnected = encrypt.initiate(ipAddress, port);
			fserverIp = ipAddress;
		}
		else
		if(fserverIp != null && !fserverIp.equals(ipAddress)){
			{
			    encrypt = new Encrypt();
			    cert = new Encrypt().Login(null);
			    isConnected = encrypt.initiate(ipAddress, port);
				fserverIp = ipAddress;
			}
			
		}
		return encrypt.sendMsg(table);
	}
}
