

import java.security.cert.X509Certificate;
import java.util.Hashtable;


public class Communicate {

	X509Certificate cert = null;
	String fserverIp = null;
	boolean isConnected = false;
	Encrypt encrypt;
	public boolean checkAuthentication()
	{
		if(cert != null)
			return true;
		else
			return false;
				
	}
	public boolean Login(String hash)
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
	public boolean addUser(String hash)
	{
		if(encrypt != null)
		   return encrypt.AddUser(hash);
		else
			return false;
	}
	public byte[] Encrypt(byte[] data)
	{
		try{
        return encrypt.EncryptData(data);
		}
		catch(Exception ex)
		{
			return null;
		}
	}

	public byte[] Decrypt(byte [] data)
	{
		try{
        return encrypt.Decrypt(data);
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	public boolean Login(String hash, String caAddress)
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
	
	
	public boolean Logout()
	{
		boolean success = encrypt.logout();
		if(success)
		{
			cert = null;
			fserverIp = null;
			isConnected = false;
			return true;
		}
		else
			return false;
	}
	public Hashtable sendMsg(Hashtable table, String ipAddress, int port)
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
			    cert = encrypt.Login(null);
			    isConnected = encrypt.initiate(ipAddress, port);
				fserverIp = ipAddress;
			}
			
		}
		return encrypt.sendMsg(table);
	}
}
