import java.io.*;
import java.net.*;
import java.sql.*;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.mina.core.*;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.*;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;

class ftp{
	public static void main(String args[]){
		File kstore = new File("src/ftp.jks");
		FtpServerFactory serverfactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		
		factory.setPort(10000);

		SslConfigurationFactory ssl = new SslConfigurationFactory();
		
		ssl.setKeystoreFile(kstore);
		ssl.setKeystorePassword("password");
		
		factory.setSslConfiguration(ssl.createSslConfiguration());
		factory.setImplicitSsl(true);
		
		
		serverfactory.addListener("Default", factory.createListener());
		PropertiesUserManagerFactory usermgrfactory = new PropertiesUserManagerFactory();
		usermgrfactory.setFile(new File("src/user.props"));
		serverfactory.setUserManager(usermgrfactory.createUserManager());
		
		FtpServer server = serverfactory.createServer();
		
		try{
		server.start();
		}
		
		catch(Exception e){
		System.err.println(e.toString());	
		}
		}
	}
