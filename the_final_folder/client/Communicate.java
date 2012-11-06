
import java.security.cert.X509Certificate;
import java.util.Hashtable;

import javax.crypto.SecretKey;

public class Communicate {

	static X509Certificate cert = null;
	static boolean isConnected = false;
	public static boolean checkAuthentication()
	{
		if(cert != null)
			return true;
		else
			return false;
				
	}
	public static boolean Login(String hash)
	{
		cert = Encrypt.Login(hash);
		return checkAuthentication();
	}
	public static boolean Logout()
	{
		return Encrypt.logout();
	}
	public static Hashtable sendMsg(Hashtable table, String ipAddress, int port)
	{
		if(!isConnected)
			isConnected = Encrypt.initiate(ipAddress, port);
		return Encrypt.sendMsg(table);
	}
}
