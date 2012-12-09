import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.Certificate;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import sun.misc.IOUtils;


public class Encrypt {
	static private  String keystoreFile = "groupA.jks";
	//private static String clientAlias = "groupA";
	static private  String caClientAlias = "groupA";
	static private  String clientPassword = "ece6102";
	static private  X509Certificate clientCert;
	private  Socket socket;
	private  Socket serverSocket;
	private  ObjectOutputStream out;
	private  ObjectInputStream in;
	private  ObjectOutputStream sout = null;
	private  ObjectInputStream sin = null;
	private  Hashtable<String, byte[]> response;
	private  SecretKey caSecret, serverSecret;
	static private  X509Certificate cert;
	private  SealedObject so;
	static private Cipher ecipher = null;
	static private  Cipher dcipher = null;
	static private  KeyStore keyStore;
	public  String username;
	public  String pwd;
	public  String serverIp = "127.0.0.1";
	static public  String fserverIp = null;
	static public  PublicKey pubKey;
	static public RSAPublicKey pub_Key;
	static public RSAPrivateKey priv_Key;
	static public  PrivateKey privKey;
	static public  int sport;
	static public  BouncyCastleProvider bcp;
	
	public static boolean AddUser(String hash)
	{
		try
		{
			if(cert != null){
		File keyFile = new File(keystoreFile);
	    // Load the keystore contents
	    FileInputStream in = new FileInputStream(keyFile);
	    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	    keystore.load(in, clientPassword.toCharArray());
	    
	    //keystore.load(in, password);
	    in.close();

	    // Add the certificate
	    keystore.setCertificateEntry(hash, cert);

	    // Save the new keystore contents
	    FileOutputStream out = new FileOutputStream(keystoreFile);
	    keystore.store(out, clientPassword.toCharArray());
	    out.close();
	    
	    return true;
			}
			else
				return false;
	    }
		catch(Exception ex)
		{
			System.out.println(ex.toString());
			return false;
		}
	}
	public static void GetKeys(String hash) throws Exception
	{
		FileInputStream input = null;
		//try{
		{
			InputStream inStream = new FileInputStream("groupA.crt"); 
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certi =(X509Certificate)cf.generateCertificate(inStream);
			inStream.close();
			pub_Key = (RSAPublicKey)certi.getPublicKey();
			
			
			//Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
			//applyCipher("groupA.p8.encrypt", "groupA.p8.decrypt", cipher);
			
			File keyFile = new File("groupA.p8");
			DataInputStream in = new DataInputStream(new FileInputStream(keyFile));
			byte [] fileBytes = new byte[(int) keyFile.length()];
			in.readFully(fileBytes);
			in.close();
			//keyFile.delete();
			
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(fileBytes);
			priv_Key = (RSAPrivateKey)kf.generatePrivate(ks);
			
	       input = new FileInputStream(keystoreFile);
	       keyStore = KeyStore.getInstance("JKS");
	       keyStore.load(input, clientPassword.toCharArray());
	       input.close();
	       
	       privKey = (PrivateKey) keyStore.getKey(caClientAlias, clientPassword.toCharArray());
	       
	       clientCert = (X509Certificate) keyStore.getCertificate(caClientAlias);
		   pubKey = clientCert.getPublicKey();
		   //keyStore.setCertificateEntry("teamA", clientCert);
           cert = (X509Certificate) keyStore.getCertificate(hash);
           //cert = clientCert;
           
	       dcipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	       bcp = new BouncyCastleProvider();
	       Security.addProvider(bcp);
	       System.out.println(cert);
		}
		
		
	}
	public  X509Certificate Login(String hash, String caAddress)
	{
		serverIp = caAddress;
		return Login(hash);
	}
	public  X509Certificate Login(String hash)
	{
		//System.out.println(hash + ": lgAttempting to encrypt channel");
		try{
			if(cert == null)
			    GetKeys(hash);
		Hashtable table = new Hashtable();
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
	    keyGen.initialize(Skip.sDHParameterSpec);
	    KeyPair   keypair = null;
	    PrivateKey diffiePriv = null;
	    PublicKey  diffiePub = null;
	    keypair = keyGen.genKeyPair();
	    // Get the generated public and private keys
	    boolean status = false;
		diffiePriv = keypair.getPrivate();
		diffiePub  = keypair.getPublic();
		if(cert == null)
		  { /*
			socket = new Socket(serverIp,1001);
	        out = new ObjectOutputStream(socket.getOutputStream());
	        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
	        table.put("authenticate", diffiePub.getEncoded());
	        //if(clientCert != null)
			    //table.put("cert", clientCert);
			table.put("key", hash);
			
			
			out.writeObject(table);
			out.flush();
			response = (Hashtable)in.readObject();
			byte []key = response.get("authenticateResponse");
            caSecret = InitiateProcess(key, diffiePriv, diffiePub);
			Hashtable hsend = new Hashtable();
			hsend.put("queryId", 1);
			ecipher = Cipher.getInstance("DES");
			ecipher.init(Cipher.ENCRYPT_MODE, caSecret);
			so = new SealedObject(hsend, ecipher);
			Hashtable request = new Hashtable();
	        request.put("message", so);
	        request.put("cert", clientCert);
	        out.flush();
	        out.writeObject(request);
	        hsend = (Hashtable)in.readObject();
	        dcipher.init(Cipher.DECRYPT_MODE, caSecret);
	        so = (SealedObject)hsend.get("response");
	        cert = (X509Certificate)so.getObject(dcipher);
	        keyStore.setCertificateEntry(caClientAlias, cert);
	        out.flush();
	        return cert; */
		  }
		//else
		{
			return cert;
		}
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	public boolean initiate(String ipAddress, int port)
	{
		boolean isConnected = false;
		try{   
			InetAddress inteAddress = InetAddress.getByName(ipAddress);
			fserverIp = ipAddress;
		      SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);
		  
		      // create a socket
		      serverSocket = new Socket();
		  
		      // this method will block no more than timeout ms.
		      int timeoutInMs = 10*1000;   // 10 seconds
		      serverSocket.connect(socketAddress, timeoutInMs);
		      
				//serverSocket = new Socket(ipAddress,port); //TODO: Remove +10
				sout = new ObjectOutputStream(serverSocket.getOutputStream());
		        sin = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()));
		        sport = port;
		if(true)
		{
			Hashtable table = new Hashtable();
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		    keyGen.initialize(Skip.sDHParameterSpec);
		    KeyPair   keypair = null;
		    PrivateKey diffiePriv = null;
		    PublicKey  diffiePub = null;
		    keypair = keyGen.genKeyPair();
		    // Get the generated public and private keys
			diffiePriv = keypair.getPrivate();
			diffiePub  = keypair.getPublic();
			
			if(serverSocket != null && !serverSocket.isClosed())
			{   
				   {
						if(cert != null)
						{
						    table.put("authenticate", diffiePub.getEncoded());
						    table.put("cert", clientCert);
						    sout.writeObject(table);
						    sout.flush();
						    table = (Hashtable)sin.readObject();
			 				   if(table.containsKey("authenticateResponse"))
			 				   {
			 					  byte[] sKey = (byte[])table.get("authenticateResponse");
				    			  cert = (X509Certificate)table.get("cert");
				    			  if(caSecret == null || socket == null || !socket.isConnected())
				    			  {
				    				  InetAddress inetAddressCA = InetAddress.getByName(serverIp);
				    			      SocketAddress socketAddressCA = new InetSocketAddress(inetAddressCA, 2358);
				    			  
				    			      // create a socket
				    			      socket = new Socket();
				    			  
				    			      // this method will block no more than timeout ms.
				    			      //timeoutInMs = 10*1000;   // 10 seconds
				    			      socket.connect(socketAddressCA, timeoutInMs);
				    			      
									//socket = new Socket(serverIp,2358);
							        out = new ObjectOutputStream(socket.getOutputStream());
							        in = new ObjectInputStream(socket.getInputStream());
							        
							        table = new Hashtable();
							        table.put("authenticate", diffiePub.getEncoded());
									table.put("cert", clientCert);
									out.writeObject(table);
									out.flush();
									Hashtable resp = (Hashtable)in.readObject();
									byte []key = (byte[]) resp.get("authenticateResponse");
									caSecret = InitiateProcess(key, diffiePriv, diffiePub);
				    			  }
							        
							        Hashtable hsend = new Hashtable();
									hsend.put("queryId", 2);
									hsend.put("cert", cert);
									ecipher = Cipher.getInstance("DES");
									ecipher.init(Cipher.ENCRYPT_MODE, caSecret);
									
									so = new SealedObject(hsend, ecipher);
									table = new Hashtable();
									table.put("message", so);
									table.put("cert", clientCert);
							        out.flush();
							        out.writeObject(table);
							        
							        hsend = (Hashtable)in.readObject();
							        dcipher.init(Cipher.DECRYPT_MODE, caSecret);
							        so = (SealedObject)hsend.get("response");
							        X509Certificate scert = (X509Certificate)so.getObject(dcipher);
							        if(scert != null)
							        {
							        	serverSecret = InitiateProcess(sKey, diffiePriv, diffiePub);
							        	isConnected = true;
							        }
							        out.close();
							        in.close();
			 				   }
					        
						}
						sout.close();
						sin.close();
						sout = null;
						sin = null;
						serverSocket.close();
						socket.close();
					
				   }
				
			}
			
		}
		return isConnected;
		}
		catch(Exception ex)
		{
			return isConnected;
		}
	}
	public boolean logout()
	{
		try{
		if(keyStore.isCertificateEntry(caClientAlias))
			keyStore.deleteEntry(caClientAlias);
		Hashtable table = sendMsg(null);
		if(table != null && Integer.parseInt(new String((String)table.get("responseCode"))) == 1)
		{
		   out.close();
		   in.close();
		   sout.close();
		   sin.close();
		   caSecret = null;
		   serverSecret = null;
		   username = null;
		   pwd = null;
		   ecipher = null;
		   dcipher = null;
		   clientCert = null;
		   if(socket != null)
		     socket.close();
		   if(serverSocket != null)
		     serverSocket.close();
		   keyStore = null;
		   return true;
		}
		return false;
		}
		catch(Exception ex)
		{
			return false;
		}
	}
	public  Hashtable sendMsg(Hashtable table)
	{
		try{
			InetAddress inteAddress = InetAddress.getByName(fserverIp);
		      SocketAddress socketAddress = new InetSocketAddress(inteAddress, sport);
		  
		      // create a socket
		      serverSocket = new Socket();
		  
		      // this method will block no more than timeout ms.
		      int timeoutInMs = 10*1000;   // 10 seconds
		      serverSocket.connect(socketAddress, timeoutInMs);
		      
			//serverSocket = new Socket(serverIp,sport);
			sout = new ObjectOutputStream(serverSocket.getOutputStream());
			sin = new ObjectInputStream(serverSocket.getInputStream());
		//if(serverSocket != null && !serverSocket.isClosed())
		{   
			if(cert != null && serverSecret != null)
			{
				
				Hashtable hsend = new Hashtable();
				//hsend.put("queryId", 3);
				if(table != null)
				   hsend.put("data",table);
				else
				   hsend.put("terminate",1);
				ecipher = Cipher.getInstance("DES");
				ecipher.init(Cipher.ENCRYPT_MODE, serverSecret);
				
				so = new SealedObject(hsend, ecipher);
				Hashtable request = new Hashtable();
				request.put("message", so);
				request.put("cert", clientCert);
		        
		        sout.writeObject(request);
		        sout.flush();
		        
		        //sin = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()));
		        
		        hsend = (Hashtable)sin.readObject();
		        int responseCode = (Integer)hsend.get("responseCode");
		        sout.close();
				sin.close();
				sout = null;
				sin = null;
				serverSocket.close();
				
		        if( responseCode == 99)
		        	return null;
		        dcipher.init(Cipher.DECRYPT_MODE, serverSecret);
		        so = (SealedObject)hsend.get("message");	
		        request = (Hashtable)so.getObject(dcipher);
		        if(request.containsKey("data"))
		        {
		        	return (Hashtable)request.get("data");
		        }
			}
		}
		    return null;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	public static String GenerateParameterSet() {
	    try {
	        // Create the parameter generator for a 1024-bit DH key pair
	        AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
	        //paramGen.init(1024);
            paramGen.init(1024);
	        // Generate the parameters
	        AlgorithmParameters params = paramGen.generateParameters();
	        DHParameterSpec dhSpec
	            = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);

	        // Return the three values in a string
	        return ""+dhSpec.getP()+","+dhSpec.getG()+","+dhSpec.getL();
	    } catch (NoSuchAlgorithmException e) {
	    } catch (InvalidParameterSpecException e) {
	    }
	    return null;
	}
	
    public SecretKey InitiateProcess(byte[] key, PrivateKey diffiePriv, PublicKey diffiePub)
    {
    	
	try {
		
       
	    // Get the generated public and private keys
		FileInputStream input = new FileInputStream(keystoreFile);
	    KeyStore keyStore = KeyStore.getInstance("JKS");
	    keyStore.load(input, clientPassword.toCharArray());

	    //PrivateKey privateKey = (PrivateKey) keyStore.getKey(caAlias, caPassword);
	    //diffiePub = (PublicKey)keyStore.getKey(clientAlias, clientPassword.toCharArray());

	    
		 
        // Get public key
        //publicKey = caCert.getPublicKey();
        Hashtable<String,byte[]> response = new Hashtable<String,byte[]>();
        
	    // Retrieve the public key bytes of the other party
        byte[] publicKeyBytes = key;

	    // Convert the public key bytes into a PublicKey object
	    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
	    //KeyFactory keyFact = KeyFactory.getInstance("DH");
	    KeyFactory keyFact = KeyFactory.getInstance("DH");
	    diffiePub = keyFact.generatePublic(x509KeySpec);

	    // Prepare to generate the secret key with the private key and public key of the other party
	    KeyAgreement ka = KeyAgreement.getInstance("DH");
	    ka.init(diffiePriv);
	    ka.doPhase(diffiePub, true);

	    // Specify the type of key to generate;
	    // see Listing All Available Symmetric Key Generators
	    String algorithm = "DES";

	    // Generate the secret key
	    SecretKey secretKey = ka.generateSecret(algorithm);
        return secretKey;
	    
	} 
	catch (java.security.InvalidKeyException e) {
		return null;
	} 
	catch (java.security.spec.InvalidKeySpecException e) {
		System.out.println(e.getMessage());
		return null;
	} 

	catch (java.security.NoSuchAlgorithmException e) {
		return null;	
	}
	catch(Exception evt)
	{
		return null;
	}
	
    }

    public  byte[] EncryptData(byte[] data) throws Exception
	{
    	RSAPublicKey rsaPublicKey = (RSAPublicKey)pub_Key;
    	
    	Cipher encryptCipher = Cipher.getInstance("RSA", bcp);
    	encryptCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
    	//byte[] messageCrypte = encryptCipher.doFinal(data);
    	byte[] messageCrypte = blockCipher(encryptCipher,data,Cipher.ENCRYPT_MODE);
    	//char[] encryptedTranspherable = Hex.encode(messageCrypte);
    	return messageCrypte;
	}

	public byte[] Decrypt(byte [] data) throws Exception
	{
		RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)priv_Key;

		Cipher decryptCipher = Cipher.getInstance("RSA", bcp);
		decryptCipher.init(Cipher.DECRYPT_MODE,rsaPrivateKey);
		//byte[] bts = Hex.decodeHex(encrypted.toCharArray());

		byte[] messageDecrypte = blockCipher(decryptCipher,data,Cipher.DECRYPT_MODE);
		//byte[] messageDecrypte = decryptCipher.doFinal(data);
		//byte[] messageDecrypte = decryptCipher.doFinal(data);
		return messageDecrypte;
	}
	
	private static byte[] blockCipher(Cipher cipher,byte[] bytes, int mode) throws IllegalBlockSizeException, BadPaddingException{
		// string initialize 2 buffers.
		// scrambled will hold intermediate results
		byte[] scrambled = new byte[0];

		// toReturn will hold the total result
		byte[] toReturn = new byte[0];
		// if we encrypt we use 100 byte long blocks. Decryption requires 128 byte long blocks (because of RSA)
		int length = (mode == Cipher.ENCRYPT_MODE)? 100 : 128;

		// another buffer. this one will hold the bytes that have to be modified in this step
		byte[] buffer = new byte[length];

		for (int i=0; i< bytes.length; i++){

			// if we filled our buffer array we have our block ready for de- or encryption
			if ((i > 0) && (i % length == 0)){
				//execute the operation
				scrambled = cipher.doFinal(buffer);
				// add the result to our total result.
				toReturn = append(toReturn,scrambled);
				// here we calculate the length of the next buffer required
				int newlength = length;

				// if newlength would be longer than remaining bytes in the bytes array we shorten it.
				if (i + length > bytes.length) {
					 newlength = bytes.length - i;
				}
				// clean the buffer array
				buffer = new byte[newlength];
			}
			// copy byte into our buffer.
			buffer[i%length] = bytes[i];
		}

		// this step is needed if we had a trailing buffer. should only happen when encrypting.
		// example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
		scrambled = cipher.doFinal(buffer);

		// final step before we can return the modified data.
		toReturn = append(toReturn,scrambled);

		return toReturn;
	}
	
	private static byte[] append(byte[] prefix, byte[] suffix){
		byte[] toReturn = new byte[prefix.length + suffix.length];
		for (int i=0; i< prefix.length; i++){
			toReturn[i] = prefix[i];
		}
		for (int i=0; i< suffix.length; i++){
			toReturn[i+prefix.length] = suffix[i];
		}
		return toReturn;
	}
	
	static Cipher createCipher(int mode) throws Exception {
	    PBEKeySpec keySpec = new PBEKeySpec("ece6102groupA".toCharArray());
	    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
	    SecretKey key = keyFactory.generateSecret(keySpec);
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update("input".getBytes());
	    byte[] digest = md.digest();
	    byte[] salt = new byte[8];
	    for (int i = 0; i < 8; ++i)
	      salt[i] = digest[i];
	    PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 20);
	    Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
	    cipher.init(mode, key, paramSpec);
	    return cipher;
	  }
	static void applyCipher(String inFile, String outFile, Cipher cipher) throws Exception {
	    CipherInputStream in = new CipherInputStream(new FileInputStream(inFile), cipher);
	    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
	    int BUFFER_SIZE = 8;
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int numRead = 0;
	    do {
	      numRead = in.read(buffer);
	      if (numRead > 0)
	        out.write(buffer, 0, numRead);
	    } while (numRead == 8);
	  }

	 /*

    public static void main(String args[])
    {
    	
	    
    	Login("teamA");
    	initiate("127.0.0.1",10001);
    	Hashtable test = new Hashtable();
    	try {
    		
    		//Cipher cipher = createCipher(Cipher.ENCRYPT_MODE);
    	    //applyCipher("groupA.p8", "groupA.p8.encrypt", cipher);
    	    
    	    //cipher = createCipher(Cipher.DECRYPT_MODE);
		    //applyCipher("groupA.p8.encrypt", "groupA.p8.decrypt", cipher);
    		
			byte[] result = EncryptData("test".getBytes());
			byte[] t = Decrypt(result);
			System.out.println(new String(t));
			
			

		    //cipher = createCipher(Cipher.DECRYPT_MODE);
		    //applyCipher("file_to_decrypt", "decrypted_file", cipher);
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//sendMsg(test);
    } */
}
