import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class Server {

	public static Hashtable processRequest(Hashtable recvTable) {
		String command = (String) recvTable.get("cmd");
		Hashtable sendTable = new Hashtable();

		switch (command) {
		case "get":
			sendTable = get(recvTable);
			break;
		case "put":
			sendTable = put(recvTable);
			break;
		case "ls":
			sendTable = ls(recvTable);
			break;
		case "mkdir":
			sendTable = mkdir(recvTable);
			break;
		case "rmdir":
			sendTable = rmdir(recvTable);
			break;
		case "rm":
			sendTable = rm(recvTable);
			break;
		}
		return sendTable;
	}

	private static Hashtable get(Hashtable table) {
		String localPath = (String) table.get("get");
		Hashtable sendTable = new Hashtable();

		try {
			File localFile = new File(localPath);
			int fileSize = (int) localFile.length();
			FileInputStream fisFile = new FileInputStream(localFile);
			BufferedInputStream bisFile = new BufferedInputStream(fisFile);

			byte[] readBuffer = new byte[fileSize];
			bisFile.read(readBuffer, 0, fileSize);
			sendTable.put("file", readBuffer);

			File timestamp = new File(localPath + ".timestamp");
			int timeSize = (int) timestamp.length();
			FileInputStream fisTime = new FileInputStream(timestamp);
			BufferedInputStream bisTime = new BufferedInputStream(fisTime);

			byte[] timeBuffer = new byte[timeSize];
			bisTime.read(timeBuffer, 0, timeSize);
			sendTable.put("timestamp", timeBuffer);

			sendTable.put("response", fileSize);

			fisFile.close();
			bisFile.close();
			fisTime.close();
			bisTime.close();
		} catch (IOException e) {
			sendTable.put("response", -1);
		}

		return sendTable;
	}

	private static Hashtable put(Hashtable table) {
		String localPath = (String) table.get("put");
		Hashtable sendTable = new Hashtable();

		try {
			FileOutputStream fosFile = new FileOutputStream(localPath);
			BufferedOutputStream bosFile = new BufferedOutputStream(fosFile);

			byte[] fileBuffer = (byte[]) table.get("file");
			bosFile.write(fileBuffer, 0, fileBuffer.length);

			FileOutputStream fosTime = new FileOutputStream(localPath
					+ ".timestamp");
			BufferedOutputStream bosTime = new BufferedOutputStream(fosTime);

			byte[] readBuffer = (byte[]) table.get("timestamp");
			bosTime.write(readBuffer, 0, readBuffer.length);

			bosFile.flush();
			bosFile.close();
			bosTime.flush();
			bosTime.close();
			fosFile.flush();
			fosFile.close();
			fosTime.flush();
			fosTime.close();
			
			sendTable.put("response", 1);
		} catch (IOException e) {
			sendTable.put("response", -1);
		}
		
		return sendTable;
	}

	private static Hashtable ls(Hashtable table) {
		String localPath = (String) table.get("ls");
		Hashtable sendTable = new Hashtable();
		
		Runtime runtime = Runtime.getRuntime();
		Process p;
		try {
			p = runtime.exec("ls " + localPath);
			InputStream in = p.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(in);
			
			int i = 0;
			int nextByte;
			byte[] lsBuffer = new byte[10000];
			do {
				nextByte = bis.read();
				if ((nextByte != -1) && (i < 10000)) {
					lsBuffer[i] = (byte)nextByte;
					i++;
				}
			} while (i != -1);
			
			sendTable.put("length", i);
			sendTable.put("file", lsBuffer);
			sendTable.put("response", 1);
		} catch (IOException e) {
			sendTable.put("response", -1);
		} // Do I need ./ here?
		
		return sendTable;
	}

	private static Hashtable mkdir(Hashtable table) {
		String localPath = (String) table.get("mkdir");
		Hashtable sendTable = new Hashtable();
		
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("mkdir " + localPath);
			sendTable.put("response", 1);
		} catch (IOException e) {
			sendTable.put("response", -1);
		}		
		
		return sendTable;
	}

	private static Hashtable rmdir(Hashtable table) {
		String localPath = (String) table.get("rmdir");
		Hashtable sendTable = new Hashtable();
		
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("rm -rf " + localPath);
			sendTable.put("response", 1);
		} catch (IOException e) {
			sendTable.put("response", -1);
		}		
		
		return sendTable;
	}

	private static Hashtable rm(Hashtable table) {
		String localPath = (String) table.get("rm");
		Hashtable sendTable = new Hashtable();
		
		Runtime runtime = Runtime.getRuntime();
		try {
			Process p = runtime.exec("rm " + localPath);
			sendTable.put("response", 1);
		} catch (IOException e) {
			sendTable.put("response", -1);
		}
		
		return sendTable;
	}
}
