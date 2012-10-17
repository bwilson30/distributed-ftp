import java.io.IOException;

public class Messaging {

	private static Server server;

	public static void main(String args[]) throws IOException {
		int port = 5010;
		int dataport = -1;

		if (args.length > 1) {
			port = (new Integer(args[0])).intValue();
			if (args.length > 2)
				dataport = (new Integer(args[1])).intValue();
		}

		System.out.println("Server, listening on port " + port
				+ ", datagram port " + dataport);
		server = new Server(port, dataport);

		while (true) {
			System.out.println("Server, waiting for connection...");
			server.Connect();

			int result;
			String command = server.RecvString('^');

			switch (command) {
			case "get":
				result = get();
				if (result == 0) {
					// get successful
					server.Close();
				} 
				else {
					// Is this necessary?
					server.Close();
				}
				break;
			case "put":
				result = put();
				if (result == 0) {
					// put successful
					server.Close();
				} 
				else {
					// Is this necessary?
					server.Close();
				}
				break;
			case "ls":
				result = ls();
				if (result == 0) {
					// ls successful
					server.Close();
				} 
				else {
					// Is this necessary?
					server.Close();
				}
				break;
			case "mkdir":
				result = mkdir();
				if (result == 0) {
					// mkdir successful
					server.Close();
				} 
				else {
					// Is this necessary?
					server.Close();
				}
				break;
			case "rmdir":
				result = rmdir();
				if (result == 0) {
					// rmdir successful
					server.Close();
				} 
				else {
					// Is this necessary?
					server.Close();
				}
				break;
			}
		}
	}

	private static int get() throws IOException {
		String localPath = server.RecvString('^');

		server.SendFile(localPath);

		return 0;
	}

	private static int put() throws IOException {
		String localPath = server.RecvString('^');

		int fileSize[] = new int[1];
		server.RecvInts(fileSize, 1);

		server.RecvFile(localPath, fileSize[0]);

		return 0;
	}

	private static int ls() throws IOException {
		String localPath = server.RecvString('^');

		server.SendFile(localPath + "/ls.txt");

		return 0;
	}

	private static int mkdir() throws IOException {
		String localPath = server.RecvString('^');

		// How do I create a directory?
		// Make a system call?
		// Move the command up to the server level?

		return 0;
	}

	private static int rmdir() throws IOException {
		String localPath = server.RecvString('^');

		// How do I remove a directory?
		// Make a system call?
		// Move the command up to the server level?

		return 0;
	}
}
