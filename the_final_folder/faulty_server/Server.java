import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Random;

public class Server {

	private static String group;
	private static int faultCode;

	public static Hashtable processRequest(Hashtable recvTable) {
		String command = (String) recvTable.get("cmd");
		group = readGroup((String) recvTable.get("group"));
		Hashtable sendTable = new Hashtable();
		
		faultCode = generateFaultCode();
		if (faultCode == 2) {
			System.out.println("Byz Fault: Return success on failure");
			sendTable.put("response", 1);
			return sendTable;
		}		

		int cmd = 0;
		if (command.equals("get")) {
			cmd = 1;
		} else if (command.equals("put")) {
			cmd = 2;
		} else if (command.equals("ls")) {
			cmd = 3;
		} else if (command.equals("mkdir")) {
			cmd = 4;
		} else if (command.equals("rmdir")) {
			cmd = 5;
		} else if (command.equals("rm")) {
			cmd = 6;
		}

		switch (cmd) {
		case 1:
			sendTable = get(recvTable);
			break;
		case 2:
			sendTable = put(recvTable);
			break;
		case 3:
			sendTable = ls(recvTable);
			break;
		case 4:
			sendTable = mkdir(recvTable);
			break;
		case 5:
			sendTable = rmdir(recvTable);
			break;
		case 6:
			sendTable = rm(recvTable);
			break;
		default:
			sendTable.put("response", -2);
		}
		return sendTable;
	}

	private static Hashtable get(Hashtable table) {
		String localPath = (String) table.get("get");
		Hashtable sendTable = new Hashtable();

		localPath = group + localPath;
		if (!checkPath(localPath)) {
			System.out.println("Invalid Path. Quit trying to hack the server");
			sendTable.put("response", -3);
			return sendTable;
		}

		System.out.println("Server: Attempting to get. Remote path is ");

		try {
			File localFile = new File("data/" + localPath);
			System.out.println(localPath);
			int fileSize = (int) localFile.length();
			FileInputStream fisFile = new FileInputStream(localFile);
			BufferedInputStream bisFile = new BufferedInputStream(fisFile);

			byte[] readBuffer = new byte[fileSize];
			bisFile.read(readBuffer, 0, fileSize);
			if (faultCode == 4) {
				System.out.println("Byz Fault: File corrupted on read/write");
				for (int i = 0; i < fileSize; i++) {
					readBuffer[i]++;
				}
			}
			sendTable.put("file", readBuffer);

			File timestamp = new File("time/" + localPath + ".timestamp");
			int timeSize = (int) timestamp.length();
			FileInputStream fisTime = new FileInputStream(timestamp);
			BufferedInputStream bisTime = new BufferedInputStream(fisTime);

			byte[] timeBuffer = new byte[timeSize];
			bisTime.read(timeBuffer, 0, timeSize);
			sendTable.put("timestamp", timeBuffer);

			if (faultCode == 3) {
				System.out.println("Byz Fault: Return failure on success");
				sendTable.put("response", -1);
			}
			else {
				sendTable.put("response", 1);
			}
			
			fisFile.close();
			bisFile.close();
			fisTime.close();
			bisTime.close();
		} catch (IOException e) {
			System.out.println("Server encountered IOException");
			sendTable.put("response", -1);
		}

		return sendTable;
	}

	private static Hashtable put(Hashtable table) {
		String localPath = (String) table.get("put");
		Hashtable sendTable = new Hashtable();

		localPath = group + localPath;
		if (!checkPath(localPath)) {
			System.out.println("Invalid Path. Quit trying to hack the server");
			sendTable.put("response", -3);
			return sendTable;
		}

		System.out.println("Server: Attempting to put. Remote path is "
				+ localPath);
		try {
			FileOutputStream fosFile = new FileOutputStream("data/" + localPath);
			System.out.println(localPath);
			BufferedOutputStream bosFile = new BufferedOutputStream(fosFile);

			byte[] fileBuffer = (byte[]) table.get("file");
			if (faultCode == 4) {
				System.out.println("Byz Fault: File corrupted on read/write");
				for (int i = 0; i < fileBuffer.length; i++) {
					fileBuffer[i]++;
				}
			}
			bosFile.write(fileBuffer, 0, fileBuffer.length);

			String timestamp = new String((byte[]) table.get("timestamp"));
			FileWriter time = new FileWriter("time/" + localPath + ".timestamp");
			BufferedWriter out = new BufferedWriter(time);
			out.write(timestamp);

			bosFile.flush();
			bosFile.close();
			fosFile.flush();
			fosFile.close();
			out.flush();
			out.close();

			if (faultCode == 3) {
				System.out.println("Byz Fault: Return failure on success");
				sendTable.put("response", -1);
			}
			else {
				sendTable.put("response", 1);
			}
			
		} catch (IOException e) {
			System.out.println("Server encountered IOException");
			sendTable.put("response", -1);
		}

		return sendTable;
	}

	private static Hashtable ls(Hashtable table) {
		String localPath = (String) table.get("ls");
		Hashtable sendTable = new Hashtable();

		localPath = group + localPath;
		if (!checkPath(localPath)) {
			System.out.println("Invalid Path. Quit trying to hack the server");
			sendTable.put("response", -3);
			return sendTable;
		}

		System.out.println("Attempting ls: Remote path is " + localPath);

		File localDir = new File("data/" + localPath);
		if (!localDir.isDirectory()) {
			sendTable.put("response", -1);
		} 
		else {
			Runtime runtime = Runtime.getRuntime();
			Process p;
			try {
				p = runtime.exec("ls -F " + "data/" + localPath);
				InputStream in = p.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(in);

				int i = 0;
				int nextByte;
				byte[] lsBuffer = new byte[10000];
				do {
					nextByte = bis.read();
					if ((nextByte != -1) && (i < 10000)) {
						lsBuffer[i] = (byte) nextByte;
						i++;
					}
				} while (nextByte != -1);

				sendTable.put("length", i);
				if (faultCode == 4) {
					System.out.println("Byz Fault: File corrupted on read/write");
					for (int j = 0; j < i; j++) {
						lsBuffer[j]++;
					}
				}
				sendTable.put("file", lsBuffer);
				
				if (faultCode == 3) {
					System.out.println("Byz Fault: Return failure on success");
					sendTable.put("response", -1);
				}
				else {
					sendTable.put("response", 1);
				}
				
			} catch (IOException e) {
				System.out.println("Server encountered IOException");
				sendTable.put("response", -1);
			} // Do I need ./ here?
		}

		return sendTable;
	}

	private static Hashtable mkdir(Hashtable table) {
		String localPath = (String) table.get("mkdir");
		Hashtable sendTable = new Hashtable();

		localPath = group + localPath;
		if (!checkPath(localPath)) {
			System.out.println("Invalid Path. Quit trying to hack the server");
			sendTable.put("response", -3);
			return sendTable;
		}
		System.out.println("Attempting to make (mkdir) directory " + localPath);

		File localDir = new File("data/" + localPath);
		File timeDir = new File("time/" + localPath);
		if (localDir.isDirectory()) {
			// Directory already exists. Return "success"
			sendTable.put("response", 1);
		}
		else {
			if (localDir.mkdir()) {
				if (!timeDir.isDirectory()) {
					timeDir.mkdir();
				}
				if (faultCode == 3) {
					System.out.println("Byz Fault: Return failure on success");
					sendTable.put("response", -1);
				}
				else {
					sendTable.put("response", 1);
				}
			}
			else {
				System.out.println("Server encountered IOException");
				sendTable.put("response", -1);
			}
		}

		return sendTable;
	}

	private static Hashtable rmdir(Hashtable table) {
		String localPath = (String) table.get("rmdir");
		Hashtable sendTable = new Hashtable();

		localPath = group + localPath;
		if (!checkPath(localPath)) {
			System.out.println("Invalid Path. Quit trying to hack the server");
			sendTable.put("response", -3);
			return sendTable;
		}
		
		File requestPath = new File("data/" + localPath);
		File rootPath = new File("data/" + group);
		String requestStr = requestPath.getAbsolutePath();
		String rootStr = rootPath.getAbsolutePath();
		
		if (requestStr.equals(rootStr)) {
			System.out.println("Unable to remove group directory");
			sendTable.put("response", -1);
		} else {
			System.out.println("Attempting to remove (rmdir) directory "
					+ localPath);

			if (rmdirRecursive("data/" + localPath)
					&& rmdirRecursive("time/" + localPath)) {
				if (faultCode == 3) {
					System.out.println("Byz Fault: Return failure on success");
					sendTable.put("response", -1);
				}
				else {
					sendTable.put("response", 1);
				}
			} else {
				System.out.println("Server unable to complete rmdir request");
				sendTable.put("response", -1);
			}
		}

		return sendTable;
	}

	private static Hashtable rm(Hashtable table) {
		String localPath = (String) table.get("rm");
		Hashtable sendTable = new Hashtable();

		localPath = group + localPath;
		if (!checkPath(localPath)) {
			System.out.println("Invalid Path. Quit trying to hack the server");
			sendTable.put("response", -3);
			return sendTable;
		}
		System.out.println("Attempting to remove (rm) file " + localPath);

		File localFile = new File("data/" + localPath);
		File timestamp = new File("time/" + localPath + ".timestamp");
		if (localFile.isFile()) {
			if (localFile.delete()) {
				if (timestamp.isFile()) {
					timestamp.delete();
				}
				if (faultCode == 3) {
					System.out.println("Byz Fault: Return failure on success");
					sendTable.put("response", -1);
				}
				else {
					sendTable.put("response", 1);
				}
			}
			else {
				System.out.println("Server encountered IOException");
				sendTable.put("response", -1);
			}
		}
		else {
			sendTable.put("response", -1);
		}

		return sendTable;
	}

	private static String readGroup(String CAgroup) {
		int CNIndex = CAgroup.indexOf("O=Group");
		String ret;
		ret = "group" + CAgroup.charAt(CNIndex + 8);

		File groupDir = new File("data/" + ret);
		if (!groupDir.isDirectory()) {
			groupDir.mkdir();
		}
		File timeDir = new File("time/" + ret);
		if (!timeDir.isDirectory()) {
			timeDir.mkdir();
		}

		return ret;
	}
	
	/* This function generates a random number 0-9 corresponding to a fault
	 * 0) infinite loop
	 * 1) create exception
	 * 2) return success on error
	 * 3) return failure on success
	 * 4) Create faults during reading/writing data
	 * 5-9) No fault
	 * */
	private static int generateFaultCode() {
		Random randGen = new Random();
		int rand = randGen.nextInt(5);
		System.out.println("Fault Code is: " + rand);
		if (rand == 0) {
			infiniteLoop();
		}
		if (rand == 1) {
			createException();
		}
		return rand;
	}
	
	private static void infiniteLoop() {
		System.out.println("Byz Fault: Entering infinite loop");
		while (true) {
			
		}
	}
	
	private static void createException() {
		System.out.println("Byz Fault: Creating exception to crash server");
		throw new NullPointerException();
	}
	
	private static boolean rmdirRecursive(String dirPath) {
		File directory = new File(dirPath);
		if (directory.isDirectory()) {
			File[] dirFiles = directory.listFiles();
			if (dirFiles.length == 0) {
				return directory.delete();
			} else {
				for (int i = 0; i < dirFiles.length; i++) {
					if (dirFiles[i].isFile()) {
						if (!dirFiles[i].delete()) {
							System.out
									.println("Server encountered file that cannot be removed.");
						}
					} else {
						if (!rmdirRecursive(dirFiles[i].toString())) {
							System.out
									.println("Server encountered file or dir that cannot be removed.");
						}
					}
				}
				return directory.delete();
			}
		} else {
			if (directory.isFile()) {
				return directory.delete();
			}
		}
		return false;
	}
	
	private static boolean checkPath(String requestedPath) {
		File requestPath = new File("data/" + requestedPath);
		File rootPath = new File("data/" + group);
		
		String requestStr = requestPath.getAbsolutePath();
		String rootStr = rootPath.getAbsolutePath();
		
		if (rootStr.equals(requestStr)) {
			return true;
		}
		else if (requestStr.contains(rootStr)) {
			if (!requestStr.contains("../")) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}	
	
}
