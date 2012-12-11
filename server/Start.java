package Server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import Encryption.Skip;

public class Start {

	private static char[] serverPassword = "ece6102".toCharArray();
	private static String keystoreFile = "../keyStoreFileServer.bin";
	private static String serverAlias = "server";
	private static PublicKey pubKey;
	private static PrivateKey privKey;
	private static Hashtable<String, SecretKey> privatekeys = new Hashtable<String,SecretKey>();
	private static SecretKey caSecret;
	private static SealedObject so;
	private static X509Certificate serverCert;
	private static ServerSocket serverSocket;
	private static Socket socket,caSocket;
	private static FileOutputStream ksos = null;
	private ObjectOutputStream caOut;
	private ObjectInputStream caIn;
	private static Cipher ecipher = null;
	private static Cipher dcipher = null;
	
	
	public void GetKeys() throws Exception
	{
		FileInputStream input = new FileInputStream(keystoreFile);
	    KeyStore keyStore = KeyStore.getInstance("JKS");
	    //KeyStore ks = KeyStore.getInstance("JCEKS");
	    keyStore.load(input, serverPassword);
	    input.close();
	    privKey = (PrivateKey) keyStore.getKey(serverAlias, serverPassword);
	    java.security.cert.Certificate serverCert = keyStore.getCertificate(serverAlias);
	    pubKey = serverCert.getPublicKey();
	}
	public void listen()
	{
		try{
		serverSocket = new ServerSocket(10001,20);
		GetKeys();    
		ksos = new FileOutputStream("keystoreAuthenticateFile");
		KeyStore ks = KeyStore.getInstance("JCEKS");
		
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		socket = serverSocket.accept();
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		X509Certificate cert = null;
		while(in.available() <= 0 && !socket.isClosed())
	    {	    	
	    		synchronized(this)
	    		{
	    			Hashtable request = new Hashtable();
	    			  request = (Hashtable)in.readObject();
	    			  
	    			if(request.containsKey("authenticate"))
	    			  {
	    				byte[] clientKey = (byte[])request.get("authenticate");
	    				cert = (X509Certificate)request.get("cert");
	    				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
	    			    keyGen.initialize(Skip.sDHParameterSpec);
	    			    KeyPair   keypair = keyGen.genKeyPair();
	    			 
	    		    // Get the generated public and private keys
	    		       PrivateKey diffiePriv = keypair.getPrivate();
	    		       PublicKey  diffiePub = keypair.getPublic();
	    		       caSocket = new ServerSocket(1001,20).accept();
	    		       caOut = new ObjectOutputStream(caSocket.getOutputStream());
	    		       caIn = new ObjectInputStream(caSocket.getInputStream());

	    				Hashtable table = new Hashtable();
	    				table.put("authenticate", diffiePub.getEncoded());
	    				table.put("cert", serverCert);
	    				caOut.writeObject(table);
	    				caOut.flush();
	    				table = (Hashtable)caIn.readObject();
	 				   if(table.containsKey("authenticateResponse"))
	 				   {
	 					  byte[] caKey = (byte[])request.get("authenticate");
	 					  caSecret = InitiateProcess(caKey,diffiePriv,diffiePub);
	 					  Hashtable hsend = new Hashtable();
							hsend.put("queryId", 2);
							ecipher = Cipher.getInstance("DES");
							ecipher.init(Cipher.ENCRYPT_MODE, caSecret);
							
							so = new SealedObject(hsend, ecipher);
							request = new Hashtable();
					        request.put("message", so);
					        request.put("cert", serverCert);
					        out.flush();
					        out.writeObject(request);
					        
					        hsend = (Hashtable)caIn.readObject();
					        dcipher.init(Cipher.DECRYPT_MODE, caSecret);
					        so = (SealedObject)hsend.get("response");
					        
					        cert = (X509Certificate)so.getObject(dcipher);
					        caOut.flush();
					        caOut.close();
					        caIn.close();
					        if(cert == null)
					        {
					        	out.reset();
					        	in.reset();
					        	out.close();
					        	in.close();
					        }
					        else{
	    				   keypair = keyGen.genKeyPair();

	    				    // Get the generated public and private keys
	    				    diffiePriv = keypair.getPrivate();
	    				    diffiePub = keypair.getPublic();
	    			      SecretKey key = InitiateProcess(clientKey,diffiePriv,diffiePub);
	    			      privatekeys.put(new String(cert.getPublicKey().getEncoded(),"UTF-8"), key);
	    			         table = new Hashtable<String,byte[]>();
	    			         table.put("authenticateResponse", serverCert);
	    			         out.writeObject(table);
	    			         out.flush();
	    			         
	    			         continue;
					        }
	    			  } 
	    		}
		}}}
		catch(Exception ex)
		{
		}
		
	}
	public static SecretKey InitiateProcess(byte[] key, PrivateKey diffiePriv, PublicKey diffiePub)
    {
    	
	try {
		
       
	    // Get the generated public and private keys
		FileInputStream input = new FileInputStream(keystoreFile);
	    KeyStore keyStore = KeyStore.getInstance("JKS");
	    keyStore.load(input, serverPassword);

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
	
}
