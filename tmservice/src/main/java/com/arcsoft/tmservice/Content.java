/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.arcsoft.tmservice;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.channels.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Morgan Wang
 */
@WebServlet(name="Content", urlPatterns={"/tms.content"}, asyncSupported=true)
public class Content extends HttpServlet 
{
	static private Logger logger = Logger.getLogger(Content.class.getName());
	private static final long serialVersionUID = 540589854571155890L;
	
	private static String SourceParamName	= "url";
	private static String ActionParamName	= "action";

	private static final int Action_Auto	= 0; // no action specified
	private static final int Action_List	= 1; // action="list"
	private static final int Action_Read	= 2; // action="read"
	private static final int Action_Write	= 4; // action="write"
	
	private static final int Source_None	= 0; // local file/folder
	private static final int Source_File	= 1; // local file/folder
	private static final int Source_Http	= 2; // http resource, not supported for now!
	private static final int Source_Udp	 	= 3; // udp resource
	private static final int Source_Ftp		= 4; // ftp resource
	
	private static final int Block_Size	= 0xF8E0; // 0x10000; // 0x2000; // 0x2000000 for native transferTo(java.nio.channels.FileChannel.transferTo)
	
	private static DatagramDistributor g_udpDistributor = new DatagramDistributor();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Content() {
        super();
        // TODO Auto-generated constructor stub
    }

    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	logger.info("doGet begin");
    	
    	int action = Action_Auto;
    	int type = Source_None;
    	String path = null;
    	URL url = null;
    	File file = null;
    	
        try
        {
        	path = request.getParameter(SourceParamName);
        	logger.info(path);
        	if (path != null)
        	{
        		path = java.net.URLDecoder.decode(path, "UTF-8");
        		try
        		{
        			url = new URL(path);
	        		String protocol = url.getProtocol();
	        		protocol.toLowerCase();
	        		if (protocol.startsWith("http"))
	        		{
	        			type = Source_Http;
	        		}
	        		else if (protocol.startsWith("udp"))
	        		{
	        			type = Source_Udp;
	        		}
	        		else if (protocol.startsWith("ftp"))
	        		{
	        			type = Source_Ftp;
	        		}
	        		else
	        		{
	        			type = Source_File;
	        		}
        		}
        		catch (Exception e) // MalformedURLException
        		{
        			if (path.startsWith("udp://"))
        			{
	        			type = Source_Udp;
        			}
        			else
        			{
        				type = Source_File;
        			}
        		}
        		if (type==Source_File)
        		{
        			file = new File(path);
        			if (file==null || file.exists()==false)
        			{
        				type = Source_None;
        			}
        		}
        	}
        }
		catch (Exception e) 
        {
			file = null;
			type = Source_None;
		}
    
        try 
        {
        	String strAction = request.getParameter(ActionParamName);
            if (strAction != null)
            {
            	if (strAction.equalsIgnoreCase("list"))
            	{
            		action = Action_List;
            	}
            	else if (strAction.equalsIgnoreCase("read"))
            	{
            		action = Action_Read;
            	}
            }
        }
		catch (Exception e) 
        {
		}
   		if (action==Action_Auto)
   		{
   			if (type==Source_Http || type==Source_Udp || type==Source_Ftp)
   			{
   				action = Action_Read;
   			}
   			else if (file==null || file.exists()==false)
   			{
   				action = Action_List;
   			}
   			else if (file.isDirectory())
   			{
   				action = Action_List;
   			}
   			else // if (file.isFile())
   			{
   				action = Action_Read;
   			}
    	}

   		if (type==Source_None)
   		{
   			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
   		}
   		else
   		{
	        switch (action)
	        {
	        case Action_Read:
	        	switch(type)
	        	{
	        	case Source_Http:
	            	readHttpContent(request, response, url);
	        		break;
	        		
	        	case Source_Ftp:
	            	readFtpContent(request, response, url);
	        		break;
	        		
	        	case Source_Udp:
        			try
        			{
	        			String host = null, srcAddress = null, localIf = null;
	        			int port = -1, index;
		        		if (url != null)
		        		{
		        			host = url.getHost();
		        			port = url.getPort();
		        			srcAddress = url.getUserInfo();
		        		}
		        		else
		        		{
		        			// "udp://srcAddress@destAddress:portNumber"
	        				host = path.substring(6);
		        			index = host.indexOf('/');
		        			if (index < 0)
		        			{
			        			index = host.indexOf('?');
		        			}
		        			if (index >= 0)
		        			{
		        				host = host.substring(0, index);
		        			}
		                    int at = host.lastIndexOf('@');
		                    if (at >= 0) {
		                    	srcAddress = host.substring(0, at);
		                        host = host.substring(at+1);
		                    }
		        			index = host.indexOf(':');
		        			if (index >= 0)
		        			{
		        				port = Integer.parseInt(host.substring(index+1));
		        				host = host.substring(0, index);
		        			}
		        			else
		        			{
		        				port = Integer.parseInt(host);
		        				host = "";
		        			}
	        			}
		        		index = path.indexOf("localaddr=");
		        		if (index > 0)
		        		{
		        			localIf = path.substring(index+10);
		        			index = localIf.indexOf('&');
		        			if (index >= 0)
		        			{
		        				localIf = localIf.substring(0, index);
		        			}
		        		}
	        			readUdpContent(request, response, host, port, srcAddress, localIf, path);
	        		}
        			catch (Exception e)
        			{
        				logger.log(Level.SEVERE, e.toString(), e);
        			}
	        		break;
	        		
	        	case Source_File:
	        	default:
	            	readFileContent(request, response, file);
	        		break;
	        	}
	        	break;
	        	
	        case Action_List:
	        default:
	        	listFolderContent(request, response, file);
		        break;
	        }
   		}
    } 

    /** 
    * Handles the HTTP <code>PUT</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	int type = Source_None;
    	String path = null;
    	URL url = null;
    	
        try
        {
        	path = request.getParameter(SourceParamName);
        	if (path != null)
        	{
        		path = java.net.URLDecoder.decode(path, "UTF-8");
        		try
        		{
        			url = new URL(path);
	        		String protocol = url.getProtocol();
	        		protocol.toLowerCase();
	        		if (protocol.startsWith("http"))
	        		{
	        			type = Source_Http;
	        		}
	        		else if (protocol.startsWith("udp"))
	        		{
	        			type = Source_Udp;
	        		}
	        		else if (protocol.startsWith("ftp"))
	        		{
	        			type = Source_Ftp;
	        		}
	        		else
	        		{
	        			type = Source_File;
	        		}
        		}
        		catch (Exception e) // MalformedURLException
        		{
        			if (path.startsWith("udp://"))
        			{
	        			type = Source_Udp;
        			}
        			else
        			{
        				type = Source_File;
        			}
        		}
        	}
        }
		catch (Exception e) 
        {
			type = Source_None;
		}

		if (type==Source_File)
		{
			writeFileContent(request, response, new File(path));
		}
		else
		{
			writeFileContent(request, response, null);
		}
    }
    
    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	int action = Action_Auto;
    	String strAction = request.getParameter(ActionParamName);
    	if (strAction != null && (strAction.equalsIgnoreCase("write") || strAction.equalsIgnoreCase("w")))
    	{
   			action = Action_Write;
    	}
    	if (action==Action_Auto)
    	{
    		doGet(request, response);
    	}
    	else
    	{
    		doPut(request, response);
    	}
    }
    
    /** 
    * Read the http content
    * @param response servlet response
    * @param url resource
    */
    protected long readHttpContent(HttpServletRequest request, HttpServletResponse response, URL url) throws IOException 
    {
    	// not supported for now!
    	return -1;
    }
    
    /** 
    * Read the ftp content
    * @param response servlet response
    * @param url resource
    */
    protected long readFtpContent(HttpServletRequest request, HttpServletResponse response, URL url) throws IOException 
    {
    	FTPClient ftp = new FTPClient(url);
    	
    	String filePath = url.getPath();
    	if (filePath.charAt(0)=='/')
    	{
    		filePath = filePath.substring(1);
    	}
    	
    	// Get the file size
    	long length = ftp.size(filePath);
    	
    	int restSupported = 1;
		long start = 0, end = length;
    	long pos2[] = new long[2];
    	if (ParseHttpRangeHeader(request.getHeader("Range"), pos2) != 0)
    	{
    		if (pos2[0] > 0)
    		{
    			start = pos2[0];
    		}
    		if (pos2[1] > 0)
    		{
    			end = pos2[1];
    		}
    		if (start > 0 && ftp.restart(start) < 0) {
    			// it is not supported to restart at some position!
    			start = 0;
    	    	restSupported = 0;
    			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
    		}
    		else { 
	    		if (end > start)
	    		{
	    			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
	    		}
	    		else
	    		{
	    			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
	    		}
	    		String bytesRange = String.format("bytes %d-%d/%d", start, end-1, length);
	    		response.addHeader("Content-Range", bytesRange);
    		}
    	}
    	else
    	{
    		response.setStatus(HttpServletResponse.SC_OK);
    	}
		response.addHeader("Accept-Ranges", (restSupported==0)? "none" : "bytes");
		response.addHeader("Content-Length", Long.toString(end-start));
		// Don't set MIME type according to file extension, because file extension may be incorrect!
		/*
		try
		{
			String mimeType = request.getServletContext().getMimeType(filePath);
			if (mimeType != null)
			{
				response.setContentType(mimeType);
			}
		}
		catch (Exception e)
		{
		}
		*/
		
		long lenSent = 0;
		try 
		{
		    // Transfer the file to the output.
		    OutputStream output = response.getOutputStream();
		    lenSent = ftp.transfer(filePath, end-start, output);
		    output.close();
		}
		catch (Exception e)
		{
			// other exception
			if (lenSent >= 0) {
				lenSent = -1;
			}
		}
		finally
		{
		    // Quit from the FTP server.
		    ftp.disconnect();
		}
	    
    	return lenSent;
    }
    
    /**
     * Get a local network interface that is in the same network segment as netAddr
     */
    static private NetworkInterface getNetworkInterface(InetAddress netAddr)
    {
    	NetworkInterface niResult = null;
    	try
    	{
    		if (netAddr != null)
    		{
	        	byte[] addrBytes = netAddr.getAddress();
	        	if (addrBytes != null && addrBytes.length >= 4)
	        	{
		        	long l0 = (addrBytes[0] >= 0) ? addrBytes[0] : (addrBytes[0]+256);
		        	long l1 = (addrBytes[1] >= 0) ? addrBytes[1] : (addrBytes[1]+256);
		        	long l2 = (addrBytes[2] >= 0) ? addrBytes[2] : (addrBytes[2]+256);
		        	long l3 = (addrBytes[3] >= 0) ? addrBytes[3] : (addrBytes[3]+256);
		        	long lNetAddr = (l0<<24) | (l1<<16) | (l2<<8) | l3;
		        	long lAddr;
		        	short maskLength;
		        	InetAddress addr;
		        	InterfaceAddress iAddr;
		        	NetworkInterface ni;
		        	Iterator<InterfaceAddress> iter;
		        	ArrayList<NetworkInterface> niList = new ArrayList<NetworkInterface>();
		    		Enumeration<NetworkInterface> nin = NetworkInterface.getNetworkInterfaces();
		    		while (nin.hasMoreElements())
		    		{
		    			try
		    			{
		    				ni = nin.nextElement();
		    				iter = ni.getInterfaceAddresses().iterator();
		    				while (iter.hasNext())
		    				{
		        				iAddr = iter.next();
		        				addr = iAddr.getAddress();
		        				maskLength = iAddr.getNetworkPrefixLength();
		        				if (maskLength > 0 && maskLength < 32 && (addr instanceof Inet4Address))
		        				{
		        					addrBytes = addr.getAddress();
		        		        	if (addrBytes != null && addrBytes.length >= 4)
		        		        	{
			        		        	l0 = (addrBytes[0] >= 0) ? addrBytes[0] : (addrBytes[0]+256);
			        		        	l1 = (addrBytes[1] >= 0) ? addrBytes[1] : (addrBytes[1]+256);
			        		        	l2 = (addrBytes[2] >= 0) ? addrBytes[2] : (addrBytes[2]+256);
			        		        	l3 = (addrBytes[3] >= 0) ? addrBytes[3] : (addrBytes[3]+256);
			        		        	lAddr = (l0<<24) | (l1<<16) | (l2<<8) | l3;
			        					if ((lAddr>>(32-maskLength))==(lNetAddr>>(32-maskLength)))
			        					{
			        						niList.add(ni);
			        					}
		        		        	}
		        				}
		    				}
		    			}
		    			catch (Exception e)
		    			{
		    			}
		    		}
		    		if (niList.size() > 0)
		    		{
		    			Random rand = new Random();
		    			niResult = niList.get(rand.nextInt(niList.size()));
		    			rand = null;
		    		}
		    		niList = null;
		    		nin = null;
	        	}
    		}
    		if (niResult==null)
    		{
    			niResult = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
    		}
    	}
    	catch (Exception e)
    	{
    	}
    	return niResult;
    }

    /** 
    * Transfer udp data to output stream
    * return the bytes transfered out.
    */
    protected long transferUdpData(DatagramSocket s, OutputStream out, String path) // throws IOException 
    {
    	long bytesSent = -1;
    	if (out != null && path != null)
    	{
			DataOutputIf outIf = g_udpDistributor.RegisterOutputIf(path, out, s);
			if (outIf != null)
			{
				//SendoutProcedure will block in output.write
				//bytesSent = outIf.SendoutProcedure();
				bytesSent = outIf.sendoutProcedureAsync();
				g_udpDistributor.UnregisterOutputIf(path, outIf);
				
				outIf = null;
			}
    	}
		return bytesSent;
    }
    
    /** 
    * Read the udp content
    * @param response servlet response
    */
    protected long readUdpContent(HttpServletRequest request, HttpServletResponse response, String udpHost, int udpPort, String srcAddress, String localIf, String path) throws IOException 
    {
    	long bytesSent = 0;
    	
    	if (udpPort < 0)
    	{
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    		return -1;
    	}
    	
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("Accept-Ranges", "none");
		if (NativeFunction.ChunkedTransfer==0)
		{
			response.addHeader("Content-Length", Long.toString(NativeFunction.PresumedTotalBytes));
		}
		ServletOutputStream servletOutputStream = response.getOutputStream();
		
		boolean bDone = false;
		try
		{
			if (g_udpDistributor.RegisterOutput(path, servletOutputStream)==false && path.indexOf("mediaformat=flv") < 0)
			{
		    	if (udpHost==null || udpHost.isEmpty() || udpHost.compareTo("*")==0)
		    	{
		    		udpHost = "0.0.0.0";
		    	}
		    	if (localIf==null || localIf.isEmpty() || localIf.compareTo("*")==0)
		    	{
		    		localIf = "0.0.0.0";
		    	}
		    	InetAddress addr = InetAddress.getByName(udpHost);
		    	if (addr.isMulticastAddress())
		    	{
			    	InetAddress loAddr = InetAddress.getByName(localIf); 
		    		SocketAddress mcSockAddr = new InetSocketAddress(addr, udpPort);
		    		SocketAddress loSockAddr = new InetSocketAddress(loAddr, udpPort);
		    		NetworkInterface loNetIf = NetworkInterface.getByInetAddress(loAddr);
		    		InetAddress srcInetAddress = null;
		    		if (srcAddress != null && srcAddress.length() > 0)
		    		{
		    			try
		    			{
		    				srcInetAddress = InetAddress.getByName(srcAddress);
		    			}
		    			catch (Exception e)
		    			{
		    				logger.log(Level.SEVERE, e.toString(), e);
		    			}
		    		}
		    		
		    		if (srcInetAddress != null)
		    		{
		    			MembershipKey key = null;
		    			DatagramChannel dc = null;
		    			try
		    			{
							dc = DatagramChannel.open(StandardProtocolFamily.INET)
								.setOption(StandardSocketOptions.SO_REUSEADDR, true);
							if (dc != null)
							{
								try
								{
									dc.bind(mcSockAddr);
								}
				    			catch (Exception e)
				    			{
									dc.bind(loSockAddr);
				    			}
								key = dc.join(addr, 
									(loNetIf==null) ? getNetworkInterface(srcInetAddress) : loNetIf, 
									srcInetAddress);
							}
		    			}
		    			catch (Exception e)
		    			{
		    				logger.log(Level.SEVERE, e.toString(), e);
		    			}
		    			finally
		    			{
							if (key != null && dc != null)
							{
					    		bytesSent = transferUdpData(dc.socket(), servletOutputStream, path);
								bDone = true;
							}
							else if (dc != null)
							{
								dc.close();
								dc = null;
							}
		    			}
		    		}
		    		if (bDone==false)
		    		{
	    				MulticastSocket s = null;
		    			try
		    			{
		    				try
		    				{
		    					s = new MulticastSocket(mcSockAddr);
		    				}
		    				catch (Exception e)
		    				{
		    					s = new MulticastSocket(loSockAddr);
		    				}
		    				if (s != null)
		    				{
					    		if (loNetIf==null)
					    		{
					    			s.joinGroup(addr);
					    		}
					    		else
					    		{
					    			try
					    			{
					    				s.joinGroup(mcSockAddr, loNetIf);
					    			}
					    			catch (Exception e)
					    			{
						    			s.joinGroup(addr);
					    			}
					    		}
		    				}
		    			}
		    			catch (Exception e)
		    			{
		    				if (s != null)
		    				{
		    					s.close();
		    					s = null;
		    				}
		    			}
		    			finally
		    			{
			    			if (s != null)
			    			{
					    		bytesSent = transferUdpData(s, servletOutputStream, path);
								bDone = true;
			    			}
		    			}
		    		}
		    	}
		    	else
		    	{
		    		DatagramSocket s = new DatagramSocket(udpPort, addr);
		    		bytesSent = transferUdpData(s, servletOutputStream, path);
					bDone = true;
		    	}
			}
			else
			{
	    		bytesSent = transferUdpData(null, servletOutputStream, path);
				bDone = true;
			}
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			logger.log(Level.SEVERE, e.toString(), e);
		}
    	
		try
		{
			if (!bDone)
			{
				g_udpDistributor.UnregisterOutput(path, servletOutputStream);
			}
			servletOutputStream.close();
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			logger.log(Level.SEVERE, e.toString(), e);
		}
		
    	return bytesSent;
    }
    
    /** 
    * Read the file content and output to servlet response
    * @param request servlet request
    * @param response servlet response
    */
    protected long readFileContent(HttpServletRequest request, HttpServletResponse response, File file) throws IOException 
    {
		long length = (file==null) ? -1 : file.length();
		if (length <= 0)
		{
			return length;
		}
		
		long start = 0, end = length;
    	long pos2[] = new long[2];
    	if (ParseHttpRangeHeader(request.getHeader("Range"), pos2) != 0)
    	{
    		if (pos2[0] > 0)
    		{
    			start = pos2[0];
    		}
    		if (pos2[1] > 0)
    		{
    			end = pos2[1];
    		}
    		if (end > start)
    		{
    			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
    		}
    		else
    		{
    			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
    		}
    		String bytesRange = String.format("bytes %d-%d/%d", start, end-1, length);
    		response.addHeader("Content-Range", bytesRange);
    	}
    	else
    	{
    		response.setStatus(HttpServletResponse.SC_OK);
    	}
		response.addHeader("Accept-Ranges", "bytes");
		response.addHeader("Content-Length", Long.toString(end-start));
		// Don't set MIME type according to file extension, because file extension may be incorrect!
		/*
		try
		{
			String mimeType = request.getServletContext().getMimeType(file.getAbsolutePath());
			if (mimeType != null)
			{
				response.setContentType(mimeType);
			}
		}
		catch (Exception e)
		{
		}
		*/
    	
		long pos = start;
    	if (start < end)
    	{
    		FileInputStream input = null;
    		ServletOutputStream output = null;
			try 
			{
				input = new FileInputStream(file);
				if (pos != 0)
				{
					input.skip(pos);
				}
		        output = response.getOutputStream();
				byte[] buffer = new byte[Block_Size];
				int bytesRead = 0;
				while (pos < end && (bytesRead=input.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
					pos += bytesRead;
				}
				output.flush();
				buffer = null;
			}
			catch (SocketTimeoutException e)
			{
				// timeout to read/write data
				//e.printStackTrace();
			}
			catch (IOException e)
			{
				// input or output stream is closed
				//e.printStackTrace();
			}
			catch (Exception e)
			{
				// other exception
				//e.printStackTrace();
			}
			finally
			{
				if (input != null)
				{
					input.close();
					input = null;
				}
				if (output != null)
				{
					output.close();
					output = null;
				}
			}
    		/*
	        FileChannel fileChannel = null;
			WritableByteChannel outChannel = null;
			try 
			{
		        fileChannel = new FileInputStream(file).getChannel();
				outChannel = Channels.newChannel(response.getOutputStream());
				while (pos < end)
				{
					pos += fileChannel.transferTo(pos, (((end-pos) > Block_Size) ? Block_Size : (end-pos)), outChannel);
				}
			}
			catch (Exception e) // EOFException, ClosedChannelException and so on
			{
				//e.printStackTrace();
			}
			finally
			{
				if (fileChannel != null)
				{
					fileChannel.close();
					fileChannel = null;
				}
				if (outChannel != null)
				{
					outChannel.close();
					outChannel = null;
				}
			}
			*/
    	}

		return (pos-start);
    }
    
    /** 
    * Write the file content from servlet request
    * @param request servlet request
    * @param response servlet response
    */
    protected long writeFileContent(HttpServletRequest request, HttpServletResponse response, File file)
    throws IOException {
    	
    	int error = 0;
		long pos = 0;
		if (file != null)
		{
			long end;
	    	String strLength = request.getHeader("Content-Length");
			if (strLength != null && (end=Long.parseLong(strLength)) > pos)
			{
		        FileChannel fileChannel = null;
		        ReadableByteChannel inputChannel = null;
				try 
				{
			        fileChannel = new FileOutputStream(file).getChannel();
			        inputChannel = Channels.newChannel(request.getInputStream());
					while (pos < end)
					{
						pos += fileChannel.transferFrom(inputChannel, pos, (((end-pos) > Block_Size) ? Block_Size : (end-pos)));
					}
				}
				catch (SecurityException e)
				{
					error = -1;
				}
				catch (FileNotFoundException e)
				{
					error = -2;
				}
				catch (Exception e) // EOFException, ClosedChannelException and so on
				{
					//e.printStackTrace();
					if (pos < end)
					{
						error = -9;
					}
				}
				finally
				{
					if (fileChannel != null)
					{
						fileChannel.close();
						fileChannel = null;
					}
					if (inputChannel != null)
					{
						inputChannel.close();
						inputChannel = null;
					}
				}
			}
			else
			{
	    		error = -20;
			}
		}
		else
		{
    		error = -21;
		}
		
		switch (error)
		{
		case 0:
    		response.setStatus(HttpServletResponse.SC_OK);
			break;
		case -1:
    		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			break;
		case -2:
    		response.setStatus(HttpServletResponse.SC_CONFLICT);
			break;
		case -9:
    		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			break;
		case -20:
    		response.setStatus(HttpServletResponse.SC_LENGTH_REQUIRED);
			break;
		case -21:
    		response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
			break;
		default:
    		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			break;
		}
		
		PrintWriter writer = null;
		try 
		{
			String strResult = Integer.toString(error);
			if (strResult != null)
			{
				response.addHeader("Content-Length", Integer.toString(strResult.length()));
				writer = response.getWriter();
				writer.print(strResult);
			}
		}
		catch (Exception e)
		{
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
				writer = null;
			}
		}
    	
		return pos;
    }
    
    /** 
    * List the folder contents
    * @param response servlet response
    */
    protected void listFolderContent(HttpServletRequest request, HttpServletResponse response, File file)
    throws IOException {
        String templetFilePath = null;
        try {
            //templetFilePath = Content.class.getResource("ContentList.xsl").getFile();
        }
		catch (Exception e) 
        {
			templetFilePath = null;
		}
    	response.setContentType("text/xml;charset=UTF-8");
    	ServletOutputStream out = response.getOutputStream();
        if (out != null)
        {
	        try 
	        {
	            UtilHelper.outputXml2Response(genContentsDocument(file, request.getRequestURL().toString()), templetFilePath, out);
	        }
	        finally 
	        { 
	        	out.close();
	        }
        }
    }
    
    protected String getStringValue(Element elemParent, String tagName)
    {
        NodeList nodeList;
        Node  node0, node;
        String str = null;

        nodeList = elemParent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0)
        {
            node0 = nodeList.item(0);
            if (node0 != null)
            {
                node = node0.getFirstChild();
                if (node != null)
                {
                    str = node.getNodeValue();
                }
            }
        }
        
        return str;
    }

	protected Document genContentsDocument(File file, String reqUrl) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Node node = genContentsNode(doc, file, reqUrl);
			if (node != null) doc.appendChild(node);
			return doc;
		}
		catch (Exception e) {
			return null;
		}
	}

	protected Node genContentsNode(Document ownerDoc, File file, String reqUrl) {
		try 
        {
			Element rootElem = null;
    		File[] fs = null;
    		
    		try 
    		{
	    		if (file != null)
	    		{
	    			fs = file.listFiles();
	    			if (fs != null)
	    			{
		    			Arrays.sort(fs, new Comparator<File>(){
		    				@Override
		    				public int compare(File f1, File f2) {
		    					if (f1.isFile() && f2.isDirectory())
		    						return -1;
		    					else if (f1.isDirectory() && f2.isFile())
		    						return 1;
		    					else
		    						return f1.getName().compareToIgnoreCase(f2.getName());
		    				}				
		    			});
	    			}
	    		}
    		}
    		catch (Exception e) 
            {
    		}
    		
    		if (fs != null)
    		{
                Element itemElem, elem;
                String  strName, strType, strUri;
                String preUri = String.format("%s?%s=", reqUrl, SourceParamName);
                
    			rootElem = ownerDoc.createElement("contents");
    			rootElem.setAttribute("type", "1");
	            for (int i = 0; i < fs.length; ++i)
	            {
	            	strType = fs[i].isFile() ? "0" : "1"; // 0 indicates file, 1 indicates folder
	                strName = fs[i].getName();
	                strUri = preUri + fs[i].getAbsolutePath();
	                
	                itemElem = ownerDoc.createElement("item");
	                rootElem.appendChild(itemElem);
	                
	                itemElem.setAttribute("type", strType);
	                
	                elem = ownerDoc.createElement("name");
	                elem.appendChild(ownerDoc.createTextNode(strName));
	                itemElem.appendChild(elem);
	                
	                elem = ownerDoc.createElement("uri");
	                elem.appendChild(ownerDoc.createTextNode(strUri));
	                itemElem.appendChild(elem);
	            }
    		}
    		else
    		{
    			rootElem = ownerDoc.createElement("error");
                rootElem.setAttribute("code", "-1");
                rootElem.appendChild(ownerDoc.createTextNode("file/folder doesn't exist!"));
    		}
           
			return rootElem;
		}
		catch (Exception e) 
        {
			return null;
		}
	}
	
    /** 
    * Parse the http Range header
    * @param strRange: the header string of http range
    * @param pos: a reference of long array to store start, end position.
    * return 0 if no http Range header, else return 1. 
    */
	protected int ParseHttpRangeHeader(String strRange, long pos[])
	{
    	if (strRange != null)
    	{
    		String strStart = null, strEnd = null;
    		int index = strRange.indexOf("bytes");
    		if (index >= 0)
    		{
    			index += 6;
    			int dashIndex = strRange.indexOf("-");
    			if (dashIndex >= index)
    			{
	    			strStart = strRange.substring(index, dashIndex);
	    			strEnd = strRange.substring(dashIndex+1);
    			}
    			else
    			{
	    			strStart = strRange.substring(index);
    			}
    		}
			if (pos != null)
			{
				pos[0] = (strStart==null || strStart.isEmpty()) ? 0 : Long.parseLong(strStart);
				pos[1] = (strEnd==null || strEnd.isEmpty()) ? -1 : Long.parseLong(strEnd)+1;
			}
			return 1;
    	}
    	else
    	{
    		return 0;
    	}
	}
	
}

