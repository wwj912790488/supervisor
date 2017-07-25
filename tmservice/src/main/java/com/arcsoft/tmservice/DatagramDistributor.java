package com.arcsoft.tmservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract interface DataOutputIf {
	public OutputStream GetOutStream();
	public int DistributeData(BufferPacket bufPacket);
	public long SendoutProcedure();
	public long sendoutProcedureAsync();
}

final class BufferPacket {
    byte[] buf;
    int length;
    int bufSize;
    
    BufferPacket(int size) {
    	try
    	{
    		this.buf = new byte[size];
        	this.bufSize = size;
    	}
    	catch (Error err)
    	{
    		this.buf = null;
        	this.bufSize = 0;
    	}
    	this.length = 0;
    }
    BufferPacket(byte[] b, int offset, int len) {
    	try
    	{
	    	this.buf = new byte[len];
			System.arraycopy(b, offset, this.buf, 0, len);
			this.bufSize = this.length = len;
    	}
    	catch (Error err)
    	{
    		this.buf = null;
        	this.bufSize = this.length = 0;
    	}
		catch (Exception e)
		{
    		this.buf = null;
        	this.bufSize = this.length = 0;
		}
    }
    BufferPacket CopyMe()
    {
    	BufferPacket bufPacket = null;
		if (this.buf != null && this.length > 0)
		{
	    	try
	    	{
		    	bufPacket = new BufferPacket(this.length);
				System.arraycopy(this.buf, 0, bufPacket.buf, bufPacket.length, this.length);
				bufPacket.length += this.length;
	    	}
	    	catch (Error err)
	    	{
	    		bufPacket = null;
	    	}
			catch (Exception e)
			{
	    		bufPacket = null;
			}
		}
    	return bufPacket;
    }
    int Append(byte[] b, int offset, int len) {
    	if (this.length + len > this.bufSize) {
    		len = this.bufSize - this.length;
    	}
    	if (len > 0)
    	{
    		System.arraycopy(b, offset, this.buf, this.length, len);
    		this.length += len;
    		return len;
    	}
    	else
    	{
    		return 0;
    	}
    }
    boolean IsReady()
    {
    	return (length==bufSize);
    }
}

final class DataDispatcher {
	static private Logger logger = Logger.getLogger(DataDispatcher.class.getName());
	
	private static final int PacketSize = 10528; // 0x8000; // 0xF8E0;

	private int m_readTimeout = NativeFunction.ReadTimeout; // milliseconds
	private DatagramSocket m_srcSocket = null;
	
	private String m_srcHeaderFilePath = null;
	private String m_srcCacheFilePath0 = null;
	private String m_srcCacheFilePath1 = null;
	private int m_indexFileSource = 0;	// the index of file source currently in use
	private FileInputStream m_streamFileSource = null;
	
	private ArrayList<BufferPacket> m_listPacket = null;
	private ArrayList<DataOutputIf> m_listOutput = null;
	
	private int m_nState = 0; // 1: source socket closed, 2: timeout as receiving data from source socket, 100: aborted by user.  
	private Thread m_sendThread = null;
	private Thread m_recvThread = null;
	private BufferPacket m_bufPacket = new BufferPacket(PacketSize);
	private Lock m_lockBuffer = new ReentrantLock();
	private Condition m_condDataGot = m_lockBuffer.newCondition(); 
	private Lock m_lockOutput = new ReentrantLock();
	
	public DataDispatcher(DatagramSocket s, String uriSource) {
		m_readTimeout = NativeFunction.ReadTimeout;
		m_srcSocket = s;
    	if (m_srcSocket != null)
    	{
    		try 
    		{
    			m_srcSocket.setSoTimeout(m_readTimeout);
    		}
    		catch (SocketException e)
    		{
    		}
    	}
    	
		m_srcHeaderFilePath = GetMediaHeaderPath(uriSource);
		if (m_srcHeaderFilePath != null)
		{
			m_srcCacheFilePath0 = m_srcHeaderFilePath + "_0";
			m_srcCacheFilePath1 = m_srcHeaderFilePath + "_1";
		}
		
		m_listPacket = new ArrayList<BufferPacket>();
		m_listOutput = new ArrayList<DataOutputIf>();
	}

	public static String GetMediaHeaderPath(String srcPath)
	{
		String fullName = null;
		int index = (srcPath != null) ? srcPath.indexOf("mediaformat=") : -1;
		if (index >= 0)
		{
			index += 12;
			if (srcPath.substring(index, index+3).compareToIgnoreCase("flv")==0)
			{
				index = srcPath.indexOf("://");
				index = srcPath.indexOf('/', index+3);
				if (index < 0)
				{
					index = srcPath.indexOf('\\', index+3);
				}
				if (index < 0)
				{
					index = srcPath.indexOf('?', index+3);
				}
				fullName = (index < 0) ? srcPath.substring(0) : srcPath.substring(0, index);
				fullName = fullName.replace(':', '_');
				fullName = fullName.replace('/', '_');
				fullName = NativeFunction.strTempCachePath + fullName;
			}
		}
		return fullName;
	}
	
	protected int ReadDataFromCacheFile(byte[] buf, int offset, int length) throws IOException, Exception, Error
	{
		if (buf==null || offset < 0 || length <= 0)
		{
			return 0;
		}
		
		if (m_srcHeaderFilePath==null || m_srcCacheFilePath0==null)
		{
			return -1000;
		}
		else if (!new File(m_srcHeaderFilePath).exists())
		{
			return -1001;
		}
		
		int len = 0;
		
		if (m_streamFileSource==null)
		{
			File file0 = null, file1 = null;
			FileInputStream stream0 = null, stream1 = null;
			
			try
			{
				file0 = new File(m_srcCacheFilePath0);
				stream0 = new FileInputStream(file0);
			}
			catch (Exception e) // FileNotFoundException, IOException
			{
				file0 = null;
				stream0 = null;
			}
			
			if (m_srcCacheFilePath1 != null)
			{
				try
				{
					file1 = new File(m_srcCacheFilePath1);
					stream1 = new FileInputStream(file1);
				}
				catch (Exception e) // FileNotFoundException, IOException
				{
					file1 = null;
					stream1 = null;
				}
			}
			
			try 
			{
				if (stream0 != null && stream1 != null)
				{
					if (file0.length() < file1.length())
					{
						m_indexFileSource = 0;
						stream1.close();
						stream1 = null;
					}
					else
					{
						m_indexFileSource = 1;
						stream0.close();
						stream0 = null;
					}
				}
				else if (stream0 != null)
				{
					m_indexFileSource = 0;
				}
				else if (stream1 != null)
				{
					m_indexFileSource = 1;
				}
				else
				{
					m_indexFileSource = -1;
				}
				
				long nSkip = 0;
				if (m_indexFileSource==0)
				{
					m_streamFileSource = stream0;
					nSkip = file0.length();
				}
				else if (m_indexFileSource==1)
				{
					m_streamFileSource = stream1;
					nSkip = file1.length();
				}
				else
				{
					m_streamFileSource = null;
				}
				if (m_streamFileSource != null)
				{
					nSkip -= NativeFunction.FileCacheAdvance;
					if (nSkip < 0) { nSkip = 0; }
					m_streamFileSource.skip(nSkip);
				}
			}
			catch (Exception e) // IOException
			{
				m_streamFileSource = null;
			}
		}
		
		if (m_streamFileSource != null)
		{
			try
			{
				len = m_streamFileSource.read(buf, offset, length);
				if (len < 0)
				{
					// there is no more data because the end of the file has been reached.
					try
					{
						File file = null;
						if (m_indexFileSource==0)
						{
							file = new File(m_srcCacheFilePath0);
							if (file.length() < NativeFunction.FileCacheMaxSize)
							{
								// wait for more data
								len = 0;
							}
							else
							{
								len = m_streamFileSource.read(buf, offset, length);
								if (len < 0)
								{
									file = new File(m_srcCacheFilePath1);
									m_indexFileSource = 1;
								}
							}
						}
						else if (m_indexFileSource==1)
						{
							file = new File(m_srcCacheFilePath1);
							if (file.length() < NativeFunction.FileCacheMaxSize)
							{
								// wait for more data
								len = 0;
							}
							else
							{
								len = m_streamFileSource.read(buf, offset, length);
								if (len < 0)
								{
									file = new File(m_srcCacheFilePath0);
									m_indexFileSource = 0;
								}
							}
						}
						if (len < 0 && file != null)
						{
							if (m_streamFileSource != null)
							{
								m_streamFileSource.close();
								m_streamFileSource = null;
							}
							int msWait = 0;
							do 
							{
								try
								{
									m_streamFileSource = new FileInputStream(file);
								}
								catch (Exception e)
								{
									Thread.sleep(50);
									msWait += 50;
								}
							} while (m_streamFileSource==null && m_nState==0 && msWait < m_readTimeout && new File(m_srcHeaderFilePath).exists());
							if (m_streamFileSource==null)
							{
								len = -1004;
							}
							else
							{
								len = m_streamFileSource.read(buf, offset, length);
								if (len < 0)
								{
									// wait for more data
									len = 0;
								}
							}
						}
					}
					catch (Exception e) // IOException
					{
					}
				}
			}
			catch (Exception e) // IOException
			{
				len = 0;
			}
		}
		else
		{
			len = -1002;
		}
		
		return len;
	}
	
    Runnable ReceiveDataProc = new Runnable() {
        public void run() {
        	logger.info("ReceiveDataProc run begin");
            // receive data from DatagramPacket
    		byte[] buf = new byte[PacketSize];
    		DatagramPacket p = new DatagramPacket(buf, buf.length);
    		int n, msWait, offset = 0, len = 0;
    		
    		while (m_nState==0) {
    			try {
    				if (!m_listOutput.isEmpty())
    				{
    					if (m_srcSocket != null) {
		    				m_srcSocket.receive(p);
		    				offset = p.getOffset();
		    				len = p.getLength();
    					}
    					else {
    						// read data from cache file
    						offset = 0;
    						msWait = 0;
    						do 
    						{
    							len = ReadDataFromCacheFile(buf, offset, buf.length-offset);
        						if (len==0)
        						{
        							Thread.sleep(50);
        							msWait += 50;
        							if (msWait > m_readTimeout)
        							{
        								//throw new IOException();
            							if (m_nState==0)
            							{
            								m_nState = 1;
            								logger.info("change m_nState = " + m_nState);
            							}
            		    				break;
        							}
        						}
    						} while (len==0 && m_nState==0);
    						if (len < 0)
    						{
    							if (m_nState==0)
    							{
    								m_nState = len;
    								logger.info("change m_nState = " + m_nState);
    							}
    						}
    					}
    					
	    				while (len > 0) {
	    					n = m_bufPacket.Append(buf, offset, len);
	    					if (m_bufPacket.IsReady()) {
	    						m_lockBuffer.lock();
	    						try
	    						{
	    							m_listPacket.add(m_bufPacket);
	        				    	m_bufPacket = new BufferPacket(PacketSize);
	        				    	m_condDataGot.signal();
	    						}
	    		    			catch (Exception e)
	    		    			{
	    		    				logger.log(Level.SEVERE, e.toString(), e);
	    		    			}
	    		    			catch (Error e)
	    		    			{
	    		    				logger.log(Level.SEVERE, e.toString(), e);
	    		    			}
	    		    			finally
	    		    			{
	    		    				m_lockBuffer.unlock();
	    		    			}
	    					}
	    					offset += n;
	    					len -= n;
	    				}
    				}
    			}
    			catch (SocketTimeoutException e) {
    				logger.log(Level.SEVERE, e.toString(), e);
    				// timeout to receive source packet
    				if (m_nState==0)
    				{
    					m_nState = 2;
    				}
    				break;
    			}
    			catch (IOException e) {
    				logger.log(Level.SEVERE, e.toString(), e);
    				// source socket/file is closed, quit!
    				if (m_nState==0)
    				{
    					m_nState = 1;
    				}
    				break;
    			}
    			catch (Exception e) {
    				logger.log(Level.SEVERE, e.toString(), e);
    				// other exception, quit!
    				if (m_nState==0)
    				{
    					m_nState = -1;
    				}
    				break;
    			}
    			catch (Error e) {
    				logger.log(Level.SEVERE, e.toString(), e);
    				// error happens, quit!
    				if (m_nState==0)
    				{
    					m_nState = -10;
    				}
    				break;
    			}
    		}
    		
    		p = null;
    		buf = null;
    		
			if (m_nState==0) {
				m_nState = -2;
				logger.info("change m_nState = " + m_nState);
			}
			
			m_lockBuffer.lock();
			try {
		    	m_condDataGot.signal();
			}
			catch (Exception e) {
				logger.log(Level.SEVERE, e.toString(), e);
			}
			finally {
				m_lockBuffer.unlock();
				//Thread.interrupt is not safe.
				/*
	    		if (m_sendThread != null)
	    		{
	    			m_sendThread.interrupt();
	    		}*/
			}
			
			logger.info("ReceiveDataProc run end.");
			logger.info(String.format("ReceiveDataProc: m_nState=%d, m_listPacket.size=%d, m_listOutput.size=%d", 
					m_nState, m_listPacket.size(), m_listOutput.size()));
        }
    };
    
    Runnable DistributeDataProc = new Runnable() {
        public void run() {
        	logger.info("DistributeDataProc run begin");
            // distribute data to all outputs
        	DataOutputIf out = null;
    		Iterator<DataOutputIf> iter = null;
        	BufferPacket bufPacket = null;
        	while (true) {
	    		if (m_nState != 0) {
	    			// Notify all DataOutput to quit because of source socket closed.
	        		m_lockOutput.lock();
	    			try {
	    	    		iter = m_listOutput.iterator();
	    	    		while(iter.hasNext()) {
	    	    			out = iter.next();
	    	    			out.DistributeData(null);
	    	    		}
	    			}
	        		finally {
	        			m_lockOutput.unlock();
	        		}
	    			break;
	    		}
	    		else {
	    			bufPacket = null;
	    			
	        		m_lockBuffer.lock();
	        		try {
	            		// wait for packet received!
		    	    	while (m_nState==0 && m_listPacket.isEmpty()) {
		    	    		m_condDataGot.awaitNanos(1000000000);
		    	    		//m_condDataGot.await();
		    	    	}
		    	    	
		    	    	if (m_nState==0 && !m_listPacket.isEmpty()) {
		    	    		bufPacket = m_listPacket.remove(0);
		    	    	}
	        		}
	    			catch (InterruptedException e) {
	    				logger.log(Level.SEVERE, e.toString(), e);
	    			}
	    			catch (Exception e) {
	    				logger.log(Level.SEVERE, e.toString(), e);
	    			}
	    			catch (Error e) {
	    				logger.log(Level.SEVERE, e.toString(), e);
	    			}
	        		finally {
	        			m_lockBuffer.unlock();
	        		}
	        		
	        		if (bufPacket != null)
	        		{
	        			m_lockOutput.lock();
		        		try
		        		{
		    	    		iter = m_listOutput.iterator();
		    	    		while(iter.hasNext())
		    	    		{
			    	    		if (m_nState != 0)
			    	    		{
			    	    			break;
			    	    		}
		    	    			out = iter.next();
		    	    			out.DistributeData(bufPacket);
		    	    		}
		    	    	}
		        		catch(Exception e) {
		        			logger.log(Level.SEVERE, e.toString(), e);
		        		}
		        		finally
		        		{
		        			m_lockOutput.unlock();
		        		}
	        		}
	    		}
        	}
        	
        	logger.info("DistributeDataProc run end");
        	logger.info(String.format("DistributeDataProc: m_nState=%d, m_listPacket.size=%d, m_listOutput.size=%d", m_nState, m_listPacket.size(), m_listOutput.size()));
        }
    };

    public DataOutputIf GetOutputIf(OutputStream outStream) {
    	DataOutputIf outIf = null;
		m_lockOutput.lock();
		try
		{
			DataOutputIf out;
    		Iterator<DataOutputIf> iter = m_listOutput.iterator();
    		while(iter.hasNext())
    		{
    			out = iter.next();
    			if (out != null && out.GetOutStream()==outStream)
    			{
    				outIf = out;
    				break;
    			}
    		}
		}
		finally
		{
			m_lockOutput.unlock();
		}
    	return outIf;
    }
    
	public boolean RegisterDataOutput(DataOutputIf out) {
		boolean bAdd = false;
		m_lockOutput.lock();
		try
		{
			bAdd = m_listOutput.add(out);
		}
		finally
		{
			m_lockOutput.unlock();
		}
		if (bAdd)
		{
			if (m_recvThread==null)
			{
				m_recvThread = new Thread(ReceiveDataProc);
				m_recvThread.start();
				logger.info("m_recvThread started");
			}
			if (m_sendThread==null)
			{
				m_sendThread = new Thread(DistributeDataProc);
				m_sendThread.start();
				logger.info("m_sendThread started");
			}
		}
		return bAdd;
	}

	public boolean UnregisterDataOutput(DataOutputIf out) {
		boolean bRemove = false;
		m_lockOutput.lock();
		try
		{
			bRemove = m_listOutput.remove(out);
		}
		finally
		{
			m_lockOutput.unlock();
		}
		return bRemove;
	}
	
	public boolean IsOutputEmpty() {
		return m_listOutput.isEmpty();
	}
	
	public void Destroy() {
		logger.info("Destroy begin");
		try
		{
			if (m_nState==0)
			{
				m_nState = 100;
			}
			if (m_recvThread != null)
			{
				m_recvThread.interrupt();
			}
			if (m_sendThread != null)
			{
				m_sendThread.interrupt();
			}
			int msWait = 0;
			while (msWait < 5000 && ((m_recvThread != null && m_recvThread.isAlive()) || 
				(m_sendThread != null && m_sendThread.isAlive())))
			{
				Thread.sleep(50);
				msWait += 50;
			}
		}
		catch (InterruptedException e)
		{
			logger.log(Level.SEVERE, e.toString(), e);
		}
		finally
		{
			m_recvThread = null;
			m_sendThread = null;
			if (m_srcSocket != null)
			{
				m_srcSocket.close();
				m_srcSocket = null;
			}
			if (m_streamFileSource != null)
			{
				try
				{
					m_streamFileSource.close();
				}
				catch (IOException e)
				{
				}
				m_streamFileSource = null;
			}
			if (m_listPacket != null)
			{
				m_listPacket.clear();
				m_listPacket = null;
			}
			if (m_listOutput != null)
			{
				m_listOutput.clear();
				m_listOutput = null;
			}
		}
	}
}

public class DatagramDistributor {
	private static Logger logger = Logger.getLogger(Content.class.getName());
	private HashMap<String, DataDispatcher> g_mapDispatcher = new HashMap<String, DataDispatcher>();
	private Lock m_lockDispatcher = new ReentrantLock();
	
	public DatagramDistributor() {
	}
	
	
	public boolean RegisterOutput(String path, OutputStream out) {
		boolean bReg = false;
		m_lockDispatcher.lock();
		try
		{
			DataDispatcher dgd = g_mapDispatcher.get(path);
			if (dgd != null)
			{
				DataOutputIf outIf = new DataOutputStream(out, path);
				bReg = dgd.RegisterDataOutput(outIf);
				if (!bReg)
				{
					outIf = null;
				}
			}
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		finally
		{
			m_lockDispatcher.unlock();
		}
		return bReg;
	}
	
	public boolean UnregisterOutput(String path, OutputStream out) {
		DataOutputIf outIf = null;
		m_lockDispatcher.lock();
		try
		{
			DataDispatcher dgd = g_mapDispatcher.get(path);
			if (dgd != null)
			{
				outIf = dgd.GetOutputIf(out);
			}
		}
		finally
		{
			m_lockDispatcher.unlock();
		}
		return (outIf != null) ? UnregisterOutputIf(path, outIf) : false;
	}
	
	public DataOutputIf RegisterOutputIf(String path, OutputStream out, DatagramSocket s) {
		DataOutputIf outIf = null;
		m_lockDispatcher.lock();
		try
		{
			DataDispatcher dgd = g_mapDispatcher.get(path);
			if (dgd==null)
			{
				dgd = new DataDispatcher(s, path);
				g_mapDispatcher.put(path, dgd);
			}
			else
			{
				outIf = dgd.GetOutputIf(out);
			}
			if (outIf==null)
			{
				outIf = new DataOutputStream(out, path);
				if (!dgd.RegisterDataOutput(outIf))
				{
					outIf = null;
				}
			}
		}
		finally
		{
			m_lockDispatcher.unlock();
		}
		return outIf;
	}
	
	public boolean UnregisterOutputIf(String path, DataOutputIf outIf) {
		boolean bDone = false;
		m_lockDispatcher.lock();
		try
		{
			DataDispatcher dgd = g_mapDispatcher.get(path);
			if (dgd != null)
			{
				bDone = dgd.UnregisterDataOutput(outIf);
				if (dgd.IsOutputEmpty())
				{
					g_mapDispatcher.remove(path);
					dgd.Destroy();
					dgd = null;
				}
			}
		}
		finally
		{
			m_lockDispatcher.unlock();
		}
		return bDone;
	}
}
