package com.arcsoft.tmservice;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataOutputStream implements DataOutputIf 
{
	static private Logger logger = Logger.getLogger(Content.class.getName());
	private static final int MaxSize4IFrame = 0x500000;
	private static final int MaxSize4Buffer = 10*0x100000; // 10MB
	private static final int MaxSleepInterval = 15012;
	
	private int m_nQuit = 0;
	private ArrayList<BufferPacket> m_listPacket = null;
	private Lock m_lockBuffer = new ReentrantLock();
	private Condition m_condDataGot = m_lockBuffer.newCondition(); 
	
	private OutputStream m_outStream = null;
	private String	m_uriSource = null;
	private int m_nHeaderDeal = 0;
	private byte[] m_lastBuffer = null;
	private int m_lenLastBuffer = 0;
	private int m_lenFirstIFrame = 0;
	private int totalSent = 0;
	private int m_writeCount = 0;
	
	DataOutputStream(OutputStream outStream, String uriSource) 
	{
		m_listPacket = new ArrayList<BufferPacket>();
		this.m_outStream = outStream;
		this.m_uriSource = uriSource;
	}
	
	public OutputStream GetOutStream()
	{
		return m_outStream;
	}
	
	public int DistributeData(BufferPacket bufPacket)
	{
		// bufPacket==null indicates source socket closed, trigger quit signal!
		if (bufPacket==null)
		{
			if (m_nQuit==0)
			{
				m_nQuit = -1;
				logger.info("change m_nQuit = " + m_nQuit);
				m_lockBuffer.lock();
				try
				{
			    	m_condDataGot.signal();
				}
				catch (Exception e)
				{
					logger.log(Level.SEVERE, e.toString(), e);
				}
				finally
				{
					m_lockBuffer.unlock();
				}
			}
			return 0;
		}
		
		// if output stream is closed, trigger quit signal! 
		if (this.m_outStream==null || this.m_uriSource==null)
		{
			if (m_nQuit==0)
			{
				m_nQuit = -2;
				logger.info("change m_nQuit = " + m_nQuit);
				m_lockBuffer.lock();
				try
				{
			    	m_condDataGot.signal();
				}
				catch (Exception e)
				{
					logger.log(Level.SEVERE, e.toString(), e);
				}
				finally
				{
					m_lockBuffer.unlock();
				}
			}
			return -1;
		}

		int bytesOutput = 0;
		if (bufPacket != null && bufPacket.buf != null && bufPacket.length > 0)
		{
			m_lockBuffer.lock();
			try
			{
				if (m_listPacket.size()*bufPacket.length > MaxSize4Buffer)
				{
					m_listPacket.clear();
				}
				BufferPacket bp = bufPacket.CopyMe();
				if (bp != null)
				{
					m_listPacket.add(bp);
					bytesOutput = bufPacket.length;
			    	m_condDataGot.signal();
				}
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, e.toString(), e);
			}
	    	catch (Error err)
	    	{
	    		logger.log(Level.SEVERE, err.toString(), err);
	    	}
			finally
			{
				m_lockBuffer.unlock();
			}
		}
		
		return bytesOutput;
	}
	
	public long SendoutProcedure()
	{
		logger.info("SendoutProcedure begin");
    	//long totalSent = 0;
    	BufferPacket bufPacket;
    	int nSentout;
    	
		while (m_nQuit==0) 
		{
	    	bufPacket = null;
    		m_lockBuffer.lock();
    		try
    		{
        		// wait for packet received!
    	    	while (m_nQuit==0 && m_listPacket.isEmpty())
    	    	{
    	    		m_condDataGot.awaitNanos(1000000000);
    	    		//m_condDataGot.await();
    	    	}
    	    	if (m_nQuit==0 && !m_listPacket.isEmpty())
    	    	{
    	    		bufPacket = m_listPacket.remove(0);
    	    	}
    		}
			catch (InterruptedException e)
			{
				logger.log(Level.SEVERE, e.toString(), e);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, e.toString(), e);
			}
    		finally
    		{
    			m_lockBuffer.unlock();
    		}
    		
    		if (m_nQuit==0 && bufPacket != null && bufPacket.length > 0)
    		{
    			nSentout = 0;
		
		    	if (m_nHeaderDeal==0)
		    	{
			    	byte[] header = null;
					int lenHeader = 0;
					String mediaHeaderPath = DataDispatcher.GetMediaHeaderPath(m_uriSource);
					if (mediaHeaderPath != null)
					{
						try
						{
							File fileHeader = new File(mediaHeaderPath);
							FileInputStream streamHeader = new FileInputStream(fileHeader);
							lenHeader = (int)fileHeader.length(); 
					    	header = new byte[lenHeader];
							lenHeader = streamHeader.read(header, 0, lenHeader);
							streamHeader.close();
							streamHeader = null;
							fileHeader = null;
						}
						catch (Exception e) // FileNotFoundException, IOException
						{
							logger.log(Level.SEVERE, e.toString(), e);
					    	header = null;
							lenHeader = 0;
							m_nQuit = 3;
							m_nHeaderDeal = -1;
						}
				    	catch (Error e)
				    	{
				    		logger.log(Level.SEVERE, e.toString(), e);
					    	header = null;
							lenHeader = 0;
							m_nQuit = 10;
							m_nHeaderDeal = -10;
				    	}
						mediaHeaderPath = null;
					}
					
					if (m_nHeaderDeal==0)
					{
						if (header != null && lenHeader > 0)
						{
							try
							{
								if (m_lastBuffer==null)
								{
									m_lastBuffer = new byte[2*bufPacket.length];
								}
								if (m_lenLastBuffer + bufPacket.length > m_lastBuffer.length)
								{
									byte[] newData = new byte[m_lenLastBuffer + 2*bufPacket.length];
						    		if (m_lenLastBuffer > 0)
						    		{
						    			System.arraycopy(m_lastBuffer, 0, newData, 0, m_lenLastBuffer);
						    		}
						    		m_lastBuffer = newData;
								}
					    		System.arraycopy(bufPacket.buf, 0, m_lastBuffer, m_lenLastBuffer, bufPacket.length);
								m_lenLastBuffer += bufPacket.length;
								
						    	NativeFunction nf = new NativeFunction();
						    	int[] retOffset = {-1, 0};
//						    	System.out.println("get in AdaptFlvHeaderData: data len = " + m_lenLastBuffer);
						    	nf.AdaptFlvHeaderData(header, lenHeader, m_lastBuffer, 0, m_lenLastBuffer, retOffset);
//						    	System.out.println("get off AdaptFlvHeaderData: offset0 = " + retOffset[0] + ", offset1 = " + retOffset[1] + ", First I-Frame len = " + m_lenFirstIFrame);
						    	if (retOffset[0] >= 0)
						    	{
						    		m_nHeaderDeal = 1;
									m_outStream.write(header, 0, lenHeader);
									m_writeCount++;
									nSentout += lenHeader;
							    	if (retOffset[0] >= 0)
							    	{
							    		m_lenLastBuffer -= retOffset[0];
							    		m_outStream.write(m_lastBuffer, retOffset[0], m_lenLastBuffer);
							    		m_writeCount++;
							    		nSentout += m_lenLastBuffer;
							    	}
						    	}
						    	else if (retOffset[1] > 0)
					    		{
						    		m_lenFirstIFrame += retOffset[1];
						    		if (m_lenFirstIFrame > MaxSize4IFrame)
						    		{
										m_nHeaderDeal = -2;
						    		}
						    		else
						    		{
							    		m_lenLastBuffer -= retOffset[1];
							    		if (m_lenLastBuffer > 0)
							    		{
							    			System.arraycopy(m_lastBuffer, retOffset[1], m_lastBuffer, 0, m_lenLastBuffer);
							    		}
						    		}
					    		}
						    	nf = null;
							}
							catch (IOException e)
							{
								logger.log(Level.SEVERE, e.toString(), e);
								// output stream is closed, quit!
								m_nQuit = 1;
								m_nHeaderDeal = -3;
							}
							catch (Exception e)
							{
								logger.log(Level.SEVERE, e.toString(), e);
								// other exception, quit!
								m_nQuit = 2;
								m_nHeaderDeal = -4;
							}
					    	catch (Error e)
					    	{
					    		logger.log(Level.SEVERE, e.toString(), e);
					    		// error happens, quit!
								m_nQuit = 11;
								m_nHeaderDeal = -11;
					    	}
						}
						else
						{
							m_nHeaderDeal = 2;
						}
					}
					
					if (m_nHeaderDeal != 0)
					{
				    	m_lastBuffer = null;
				    	m_lenLastBuffer = 0;
					}
		    	}
		    	else
		    	{
					try
					{
						//Sometimes m_outStream.write will take 7mins to throw ClientAbortException
						//ClientAbortException:  java.net.SocketException: Connection timed out
						//ClientAbortException:  java.net.SocketException: Broken pipe
						m_outStream.write(bufPacket.buf, 0, bufPacket.length);
						m_writeCount++;
						nSentout += bufPacket.length;
					}
					catch (IOException e)
					{
						logger.log(Level.SEVERE, e.toString(), e);
						// output stream is closed, quit!
						m_nQuit = 1;
					}
					catch (Exception e)
					{
						logger.log(Level.SEVERE, e.toString(), e);
						// other exception, quit!
						m_nQuit = 2;
					}
			    	catch (Error e)
			    	{
			    		logger.log(Level.SEVERE, e.toString(), e);
			    		// error happens, quit!
						m_nQuit = 12;
			    	}
		    	}
				
		    	if (nSentout > 0)
		    	{
		    		totalSent += nSentout;
		    	}
    		}
		}
    	
		if (m_listPacket != null)
		{
			m_listPacket.clear();
		}
		
		logger.info("SendoutProcedure end");
    	return totalSent;
	}
	
	//use async send, so watch dog can know when the data is blocked. 
	public long sendoutProcedureAsync() {
		logger.info("sendoutProcedureAsync begin");
		
		//thread for sendoutProcedure
		Thread sendoutThread = new Thread(new Runnable() {
	        public void run() {
	        	SendoutProcedure();
	        }
		});
		sendoutThread.start();
		
		int writeCount = 0;
		while(m_nQuit == 0) {
			try {
				//m_outStream.write should be called once in 2s.
				Thread.sleep(MaxSleepInterval);
				//logger.info("m_writeCount= " + m_writeCount);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, e.toString(), e);
				m_nQuit = 20;
			}
			
			if(m_writeCount == writeCount) {
				logger.info("m_writeCount is not change m_writeCount=" + m_writeCount);
				m_nQuit = 21;
			}
			
			writeCount = m_writeCount;
		}
		
		logger.info("sendoutProcedureAsync end m_nQuit=" + m_nQuit);
		return this.totalSent;
	}
}
