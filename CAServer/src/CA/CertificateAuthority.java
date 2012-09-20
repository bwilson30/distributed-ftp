package CA;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CertificateAuthority {

	private static String keystoreFile = "../keyStoreFile.bin";
	
	public static X509Certificate ValidateCertificate(String certpath) throws Exception
	{
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
	    List mylist = new ArrayList();
	    FileInputStream in = new FileInputStream(certpath);
	    Certificate c = cf.generateCertificate(in);
	    mylist.add(c);
        
	    CertPath cp = cf.generateCertPath(mylist);

	    Certificate trust = cf.generateCertificate(in);
	    TrustAnchor anchor = new TrustAnchor((X509Certificate) trust, null);
	    PKIXParameters params = new PKIXParameters(Collections.singleton(anchor));
	    params.setRevocationEnabled(false);
	    CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
	    PKIXCertPathValidatorResult result = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
	    
	    if (trust instanceof X509Certificate) {
            X509Certificate x509cert = (X509Certificate)trust;
            return x509cert;
        }
	    
	    System.out.println(result);
	   return null;
	}
	public static void LoadCACertificate() throws Exception
	{
		 FileOutputStream ksos = new FileOutputStream(keystoreFile);
         
		 // The certificate files, to be added to keystore
		 //FileInputStream certRootFile = new FileInputStream("../ca.crt");
		 FileInputStream certFile1 = new FileInputStream("../sa.crt");
		 FileInputStream certFile2 = new FileInputStream("../sb.crt");
		 FileInputStream certFile3 = new FileInputStream("../sds.crt");

		 CertificateFactory cf = CertificateFactory.getInstance("X.509");
       
		 // Read the 3 certificates into memory
		 //Certificate certRoot = cf.generateCertificate(certRootFile);
		 Certificate cert1 = cf.generateCertificate(certFile1);
		 Certificate cert2 = cf.generateCertificate(certFile2);
		 Certificate cert3 = cf.generateCertificate(certFile3);
         
		 KeyStore ks = KeyStore.getInstance("JKS");
		 
		 
		 //KeyStore ks = KeyStore.getInstance("PKCS12");
		 char[] password = "ece6238".toCharArray();

		 //ks.load(ksis, password);
		 ks.load(null, password);

		 // Add certificates to keystore
		 //ks.setCertificateEntry("ROOT_ALIAS", certRoot);
		 ks.setCertificateEntry("C1_ALIAS", cert1);
		 ks.setCertificateEntry("C2_ALIAS", cert2);
		 ks.setCertificateEntry("C3_ALIAS", cert3);
		 
		 
		 // Write modified keystore to file system
		 ks.store(ksos, password);

		 ksos.close();

	}
}
