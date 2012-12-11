package Encryption;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.Certificate;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Hashtable;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import Encryption.Skip;

public class Encrypt {
	private static String keystoreFile = "../keyStoreFileClient.bin";
	private static String keyPub = "pub";
	private static String keyPriv = "priv";
	private static String clientAlias = "client";
	private static String caClientAlias = "caClient";
	private static String clientPassword = "ece6102";
	private static PublicKey pubKey;
	private static PrivateKey privKey;
	private static X509Certificate clientCert;
	private static Socket socket;
	private static Socket serverSocket;
	private static ObjectOutputStream out;
	private static ObjectInputStream in;
	private static ObjectOutputStream sout = null;
	private static ObjectInputStream sin = null;
	private static Hashtable<String, byte[]> response;
	private static SecretKey caSecret, serverSecret;
	private static X509Certificate cert;
	private static SealedObject so;
	private static Cipher ecipher = null;
	private static Cipher dcipher = null;
	private static KeyStore keyStore;
	 static String ipAddress;
	 static String username;
	 static String pwd;
	 static String serverIp;
	public String a;
	public double b;
	public Encrypt(String x, double y)
	{
		a = x;
		b = y;
	}
	
	public static native void initialize();
	
	
	
	public static void main(String[] args)
	{
		Encrypt[] staff = new Encrypt[3];
		 staff[0] = new Encrypt("Harry Hacker", 35000);
	      staff[1] = new Encrypt("Carl Cracker", 75000);
	      staff[2] = new Encrypt("Tony Tester", 38000);
	}
	public static void GetKeys()
	{
		try{
		{
			
	       FileInputStream input = new FileInputStream(keystoreFile);
	       keyStore = KeyStore.getInstance("JKS");
	       keyStore.load(input, clientPassword.toCharArray());
	       input.close();
	       
	       privKey = (PrivateKey) keyStore.getKey(clientAlias, clientPassword.toCharArray());
		   clientCert = (X509Certificate) keyStore.getCertificate(clientAlias);
		   pubKey = clientCert.getPublicKey();
	       
	       if(keyStore.isCertificateEntry(caClientAlias))
	           cert = (X509Certificate) keyStore.getCertificate(caClientAlias);
	       
	       dcipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		}
		
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
	}
	
	public static void initiate()
	{
		GetKeys();
		try{   
				serverSocket = new Socket(ipAddress,10001);
				sout = new ObjectOutputStream(serverSocket.getOutputStream());
		        sin = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()));
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
						if(cert == null)
						{
							socket = new Socket(serverIp,1001);
					        out = new ObjectOutputStream(socket.getOutputStream());
					        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
					        table.put("authenticate", diffiePub.getEncoded());
							table.put("cert", clientCert);
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
						}
						if(cert != null)
						{
						    table.put("authenticate", diffiePub.getEncoded());
						    table.put("cert", cert);
						    sout.writeObject(table);
						    sout.flush();
						    table = (Hashtable)sin.readObject();
			 				   if(table.containsKey("authenticateResponse"))
			 				   {
			 					  byte[] sKey = (byte[])table.get("authenticate");
				    			  cert = (X509Certificate)table.get("cert");
				    			  if(caSecret == null || socket == null || !socket.isConnected())
				    			  {
									socket = new Socket(serverIp,1001);
							        out = new ObjectOutputStream(socket.getOutputStream());
							        in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
							        table = new Hashtable();
							        table.put("authenticate", diffiePub.getEncoded());
									table.put("cert", clientCert);
									out.writeObject(table);
									out.flush();
									byte []key = response.get("authenticateResponse");
									caSecret = InitiateProcess(key, diffiePriv, diffiePub);
				    			  }
							        
							        Hashtable hsend = new Hashtable();
									hsend.put("queryId", 2);
									ecipher = Cipher.getInstance("DES");
									ecipher.init(Cipher.ENCRYPT_MODE, caSecret);
									
									so = new SealedObject(hsend, ecipher);
									table = new Hashtable();
									table.put("message", so);
									table.put("cert", cert);
							        out.flush();
							        out.writeObject(table);
							        
							        hsend = (Hashtable)in.readObject();
							        dcipher.init(Cipher.DECRYPT_MODE, caSecret);
							        so = (SealedObject)hsend.get("response");
							        X509Certificate scert = (X509Certificate)so.getObject(dcipher);
							        if(scert != null)
							        {
							        	serverSecret = InitiateProcess(sKey, diffiePriv, diffiePub);
							        }
			 				   }
					        
						}
					
				   }
				
			}
			
		}
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
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
	
    public static SecretKey InitiateProcess(byte[] key, PrivateKey diffiePriv, PublicKey diffiePub)
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
    static
	   {
	      System.loadLibrary("Quorum/Encryption");
	   }
}
