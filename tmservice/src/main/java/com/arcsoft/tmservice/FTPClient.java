package com.arcsoft.tmservice;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.StringTokenizer;


/**
 * @author Morgan Wang
 * 
 */
public class FTPClient {

	public static final int ERROR_NONE		= 0; // no error 
	public static final int ERROR_RESTART	= -101; // possibly not supported! 
	public static final int ERROR_PASSIVE	= -102; 
	public static final int ERROR_RETRIEVE	= -103; 
	public static final int ERROR_ABORTED	= -104;	// Connection closed; transfer aborted. 
	public static final int ERROR_TIMEOUT	= -105;	// Read/Write Timeout. 
	public static final int ERROR_TRANSFER	= -106; // Other transfer error
	
	private static final int Block_Size	= 0x10000; // 0x1000; 
	private static final int Default_Port = 21; 
	
	private boolean epsvWork = false;
	private String ctrlHost = null;
	private Socket socket = null;
	private BufferedReader reader = null;
	private BufferedWriter writer = null;

	/**
	 * Create an instance of FTPClient.
	 */
	public FTPClient() {

	}

	/**
	 * Create an instance of FTPClient.
	 */
	public FTPClient(URL url) throws IOException {
	    String username = "", password = "";
	    String userInfo = url.getUserInfo();
	    if (userInfo != null)
	    {
			int index = userInfo.indexOf(':');
			if (index >= 0)
			{
				username = userInfo.substring(0, index);
				password = userInfo.substring(index+1);
			}
			else
			{
				username = userInfo;
			}
			try
			{
				if (!username.isEmpty())
				{
					username = URLDecoder.decode(username, "UTF-8");
				}
			}
			catch (Exception e)
			{
			}
			catch (Error err)
			{
			}
			try
			{
				if (!password.isEmpty())
				{
					password = URLDecoder.decode(password, "UTF-8");
				}
			}
			catch (Exception e)
			{
			}
			catch (Error err)
			{
			}
	    }
		
	    // Connect to the FTP server.
	    ctrlHost = url.getHost();
	    this.connect(ctrlHost, url.getPort(), username, password);
	    
	    //try {epsvWork = this.extPassiveAll();} catch(Exception e) {epsvWork = false;}
	    
	    // Set binary mode.
	    this.binType();
	}

	/**
	 * Connects to the default port of an FTP server and logs in as
	 * anonymous/anonymous.
	 */
	public synchronized void connect(String host) throws IOException {
		connect(host, Default_Port);
	}

	/**
	 * Connects to an FTP server and logs in as anonymous/anonymous.
	 */
	public synchronized void connect(String host, int port) throws IOException {
		connect(host, port, "anonymous", "anonymous");
	}

	/**
	 * Connects to an FTP server and logs in with the supplied username and
	 * password.
	 */
	public synchronized void connect(String host, int port, String user, String pass) throws IOException {
		if (socket != null) {
			throw new IOException("FTPClient is already connected. Disconnect first.");
		}
		if (port < 0) {
			port = Default_Port;
		}
		socket = new Socket(host, port);
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		String response = readLine();
		if (!response.startsWith("220 ")) {
			throw new IOException("FTPClient received an unknown response when connecting to the FTP server: " + response);
		}

		sendLine("USER " + user);

		response = readLine();
		if (!response.startsWith("331 ")) {
			throw new IOException("FTPClient received an unknown response after sending the user: " + response);
		}

		sendLine("PASS " + pass);

		response = readLine();
		if (!response.startsWith("230 ")) {
			throw new IOException("FTPClient was unable to log in with the supplied password: " + response);
		}

		// Now logged in.
	}

	/**
	 * Disconnects from the FTP server.
	 */
	public synchronized void disconnect() throws IOException {
		try {
			sendLine("QUIT");
		} finally {
			socket = null;
		}
	}

	/**
	 * Enter extended passive mode for sending binary files.
	 */
	public synchronized boolean extPassiveAll() throws IOException {
		sendLine("EPSV ALL");
		String response = readLine();
		return (response.startsWith("200 ") || response.startsWith("229 "));
	}

	/**
	 * Enter binary mode for sending binary files.
	 */
	public synchronized boolean binType() throws IOException {
		sendLine("TYPE I");
		String response = readLine();
		return (response.startsWith("200 "));
	}

	/**
	 * Enter ASCII mode for sending text files. This is usually the default
	 * mode. Make sure you use binary mode if you are sending images or other
	 * binary data, as ASCII mode is likely to corrupt them.
	 */
	public synchronized boolean asciiType() throws IOException {
		sendLine("TYPE A");
		String response = readLine();
		return (response.startsWith("200 "));
	}

	/**
	 * Sends a raw command to the FTP server.
	 */
	private void sendLine(String line) throws IOException {
		if (socket == null) {
			throw new IOException("FTPClient is not connected.");
		}
		try {
			writer.write(line + "\r\n");
			writer.flush();
		} catch (IOException e) {
			socket = null;
			throw e;
		}
	}

	private String readLine() throws IOException {
		String line = reader.readLine();
		if (line==null) {
			line = "";
		}
		return line;
	}
	
	/**
	 * Requests the server to listen on a data port (which is not its default data port) 
	 * 	and to wait for a connection rather than initiate one upon receipt of a transfer command.
	 * 	The response to this command includes the host and port address this server is listening on.
     * @param host: a reference of string array to store host address  
	 * Return the port for data connection.
	 */
	public synchronized int passive(String host[]) throws IOException {
		
		String ip = null;
		int port = -1;
		
		if (this.epsvWork)
		{
			sendLine("EPSV");
			String response = readLine();
			if (!response.startsWith("229 ")) {
				throw new IOException("FTPClient could not request extended passive mode: " + response);
			}
			
			int opening = response.indexOf('(');
			int closing = response.indexOf(')', opening + 1);
			if (closing > 0) {
				String dataLink = response.substring(opening + 1, closing);
				StringTokenizer tokenizer = new StringTokenizer(dataLink, "|");
				try {
					port = Integer.parseInt(tokenizer.nextToken());
				} 
				catch (Exception e) {
					throw new IOException("FTPClient received bad data link information: " + response);
				}
			}
		}
		else
		{
			sendLine("PASV");
			String response = readLine();
			if (!response.startsWith("227 ")) {
				throw new IOException("FTPClient could not request passive mode: " + response);
			}
			
			int opening = response.indexOf('(');
			int closing = response.indexOf(')', opening + 1);
			if (closing > 0) {
				String dataLink = response.substring(opening + 1, closing);
				StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
				try {
					ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
							+ tokenizer.nextToken() + "." + tokenizer.nextToken();
					port = Integer.parseInt(tokenizer.nextToken()) * 256
							+ Integer.parseInt(tokenizer.nextToken());
				} 
				catch (Exception e) {
					throw new IOException("FTPClient received bad data link information: " + response);
				}
			}
		}

		if (host != null)
		{
			host[0] = ip;
		}
		return port;
	}
	
	/**
	 * Requests the server to skip over the file to the specified data checkpoint.
	 * 	This command shall be immediately followed by the appropriate FTP service command 
	 *	which shall cause file transfer to resume.
     * @param startPos: the position(bytes) at which file transfer is to be restarted.  
	 * Return the error code.
	 */
	public synchronized int restart(long startPos) throws IOException {
		int error = ERROR_NONE;
		if (startPos < 0) {
			startPos = 0;
		}
		try {
			sendLine("REST " + startPos);
			String response = readLine();
			if (!response.startsWith("350 ")) {
				error = ERROR_RESTART;
			}
		}
		catch (Exception e) {
			error = ERROR_RESTART;
		}
		return error;
	}
	
	/**
	 * Returns the working directory of the FTP server it is connected to.
	 */
	public synchronized String pwd() throws IOException {
		sendLine("PWD");
		String dir = null;
		String response = readLine();
		if (response.startsWith("257 ")) {
			int firstQuote = response.indexOf('\"');
			int secondQuote = response.indexOf('\"', firstQuote + 1);
			if (secondQuote > 0) {
				dir = response.substring(firstQuote + 1, secondQuote);
			}
		}
		return dir;
	}

	/**
	 * Changes the working directory (like cd). Returns true if successful.
	 */
	public synchronized boolean cwd(String dir) throws IOException {
		sendLine("CWD " + dir);
		String response = readLine();
		return (response.startsWith("250 "));
	}

	/**
	 * Sends a file to be stored on the FTP server. Returns true if the file
	 * transfer was successful. The file is sent in passive mode to avoid NAT or
	 * firewall problems at the client end.
	 */
	public synchronized boolean stor(InputStream inputStream, String filename)
			throws IOException {

		String host[] = new String[1];
		int port = this.passive(host);
		if (port < 0)
		{
			return false;
		}
		if (host[0]==null)
		{
			host[0] = this.ctrlHost;
		}
		
		Socket dataSocket = new Socket(host[0], port);
		sendLine("STOR " + filename);
		String response = readLine();
		if (!response.startsWith("150 ") && !response.startsWith("125 ")) {
			dataSocket.close();
			throw new IOException("FTPClient was not allowed to send the file: " + response);
		}

		BufferedInputStream input = new BufferedInputStream(inputStream);
		BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
		byte[] buffer = new byte[Block_Size];
		int bytesRead = 0;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		output.flush();
		output.close();
		input.close();

		response = readLine();
		dataSocket.close();
		return response.startsWith("226 ");
	}
	
	/**
	 * Sends a file to be stored on the FTP server. Returns true if the file
	 * transfer was successful. The file is sent in passive mode to avoid NAT or
	 * firewall problems at the client end.
	 */
	public synchronized boolean upload(File file) throws IOException {
		if (file.isDirectory()) {
			throw new IOException("FTPClient cannot upload a directory.");
		}

		String filename = file.getName();
		return stor(new FileInputStream(file), filename);
	}
	
	/**
	 * Return the size of file on the FTP server.
	 */
	public synchronized long size(String filePath) throws IOException {
		sendLine("SIZE " + filePath);
		String response = readLine();
		if (!response.startsWith("213 ")) {
			throw new IOException("FTPClient could not request file size: " + response);
		}
		return Long.parseLong(response.substring(4));
	}
	
	/**
	 * Transfer file from the FTP server to the output. 
	 *  User may specify the start position by invoking restart method before invoking this method.
	 * Return the bytes transfered if successful, else error code.
	 */
	public synchronized long transfer(String filePath, long len, OutputStream output) throws IOException {

		String host[], response;
		int port = -1;
		
		try {
			host = new String[1];
			port = this.passive(host);
		}
		catch (Exception e) 
		{
			return ERROR_PASSIVE;
		}
		if (port > 0 && host[0]==null)
		{
			host[0] = this.ctrlHost;
		}
		
		int error = ERROR_NONE;
		Socket dataSocket = null;
		try {
			if (port > 0 && host[0] != null)
			{
				dataSocket = new Socket(host[0], port);
			}
			sendLine("RETR " + filePath);
			response = readLine();
			if (!response.startsWith("150 ") && !response.startsWith("125 ")) {
				error = ERROR_RETRIEVE;
			}
		}
		catch (Exception e) {
			error = ERROR_RETRIEVE;
		}

		long ret = error;
		if (error==ERROR_NONE)
		{
			long pos = 0;
			BufferedInputStream input = null;
			try
			{
				input = new BufferedInputStream((dataSocket==null) ? this.socket.getInputStream() : dataSocket.getInputStream());
				byte[] buffer = new byte[Block_Size];
				int bytesRead = 0;
				while ((len < 0 || pos < len) && (bytesRead=input.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
					pos += bytesRead;
				}
				output.flush();
			}
			catch (SocketTimeoutException e)
			{
				// timeout to read/write data
				error = ERROR_TIMEOUT;
			}
			catch (IOException e)
			{
				// input or output stream is closed
				error = ERROR_ABORTED;
			}
			catch (Exception e)
			{
				// other exception
				error = ERROR_TRANSFER;
			}
			finally
			{
				if (input != null)
				{
					input.close();
				}
			}
			response = readLine();
			ret = (response.startsWith("226 ") ? pos : (response.startsWith("426 ") ? ERROR_ABORTED : error));
		}
		
		if (dataSocket != null)
		{
			dataSocket.close();
			dataSocket = null;
		}
		
		return ret;
	}
}
