/**
 * 
 */
package com.arcsoft.tmservice;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author Morgan
 *
 */
public class NativeFunction {

    public native int AdjustFlvHeaderData(byte[] header, int lenHeader, byte[] data, int offsetData, int lenData, int[] retOffset);    

    public static String strTranscoderPath = null;
    public static String strTempCachePath = null;
    public static long FileCacheMaxSize = 300*1024*1024; // default value if no setting in ASCodec.ini
    public static long FileCacheAdvance = 128*1024;
    public static int ChunkedTransfer = 1;	// 0: chunked transfer is not allowed, 1: allowed.
    public static long PresumedTotalBytes = Long.MAX_VALUE; // default value if no setting in ASCodec.ini
    public static int ReadTimeout = 15000; // milliseconds
    
    private static int s_nNativeLibLoaded = 0; // 0: not loaded yet, 1: loaded successfully, -1: failed to load
	
    static {
    	InitializePath();
    	GetConfigParameters();
    }
    
	private static void InitializePath()
	{
    	try
    	{
    		Properties pps = new Properties();
    		pps.load(NativeFunction.class.getResourceAsStream("/tmservice.properties"));
    		
			String OS = null;
			try
			{
				OS = System.getProperty("os.name").toLowerCase();
			}
	    	catch (Exception e)
	    	{
	    	}
    		if (OS != null && OS.indexOf("win") >= 0)
    		{
    			strTranscoderPath = "F:\\TMPlayer\\Output\\Run-VS2010\\UnicodeDebug\\";
    	    	strTempCachePath = "D:\\Media\\flv\\";
    		}
    		else
    		{
    			strTranscoderPath = pps.getProperty("transcoder_path");
				//strTranscoderPath = "/usr/local/arcvideo/live/transcoder/";
    			
    			//strTempCachePath comes from ini
    			strTempCachePath = "/usr/local/arcvideo/live/tmpdir/flvoverhttpcache/";
    			
    			/*
    			try 
    			{
        			Properties theProps = new Properties();
        			try
        			{
	    				String cfgPath = System.getProperty("arcvideo.application.config");
	    				if (cfgPath==null)
	    				{
	    					cfgPath = System.getenv("arcvideo.application.config");
	    				}
	    				if (cfgPath != null)
	    				{
	    					FileInputStream fis = new FileInputStream(cfgPath);
	    					theProps.load(fis);
	    					fis.close();
	    					fis = null;
	    				}
        			}
        			catch (Exception e)
        			{
        			}
					String txPath = theProps.getProperty("transcoder_path");
    				if (txPath==null)
    				{
    					if (!theProps.isEmpty())
    					{
    						theProps.clear();
    					}
    					theProps.load(NativeFunction.class.getResourceAsStream("/config.properties"));
    					txPath = theProps.getProperty("transcoder_path");
    				}
    				theProps = null;
					if (txPath != null)
					{
    					int index = txPath.lastIndexOf('/');
    					if (index < 0)
    					{
        					index = txPath.lastIndexOf('\\');
    					}
    					if (index >= 0)
    					{
    						strTranscoderPath = txPath.substring(0, index + 1);
    					}
    					else
    					{
    						strTranscoderPath = txPath + "/";
    					}
					}
    			} 
    			catch (Exception e) 
    			{
    			}
    			*/
    		}
    	}
    	catch (Exception e)
    	{
    		System.out.println(e);
    	}
	}
	
	private static int ParseLineForParameter(String strLine)
	{
		int n = strLine.indexOf("//");
		if (n < 0)
		{
			n = strLine.indexOf("/*");
		}
		if (n >= 0)
		{
			strLine = strLine.substring(0, n);
		}
		strLine = strLine.trim();
		if (strLine.isEmpty())
		{
			return 0;
		}
		
		int ret = 0;
		n = strLine.indexOf('=');
		if (n >= 0)
		{
			String name = strLine.substring(0, n).trim();
			String value = strLine.substring(n+1).trim();
			if (!name.isEmpty() && !value.isEmpty())
			{
				if (name.equals("CachePath"))
				{
					int end = value.length() - 1;
					if (end >= 0 && value.charAt(end) != '/' && value.charAt(end) != '\\')
					{
						strTempCachePath = value + "/";
					}
					else
					{
						strTempCachePath = value;
					}
					ret = 1;
				}
				else if (name.equals("CacheSize"))
				{
					long mb = Long.parseLong(value);
					if (mb > 0)
					{
						FileCacheMaxSize = mb*1024*1024;
						ret = 1;
					}
				}
				else if (name.equals("CacheAdvance"))
				{
					long kb = Long.parseLong(value);
					if (kb > 0)
					{
						FileCacheAdvance = kb*1024;
						ret = 1;
					}
				}
				else if (name.equals("ChunkedTransfer"))
				{
					if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("false"))
					{
						ChunkedTransfer = 0;
					}
					else if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("true"))
					{
						ChunkedTransfer = 1;
					}
					else
					{
						ChunkedTransfer = Integer.parseInt(value);
					}
					ret = 1;
				}
				else if (name.equals("PresumedTotalBytes"))
				{
					long totalBytes = Long.parseLong(value);
					if (totalBytes > 0)
					{
						PresumedTotalBytes = totalBytes;
						ret = 1;
					}
				}
				else if (name.equals("ReadTimeout"))
				{
					int msTimeout = Integer.parseInt(value);
					if (msTimeout >= 0)
					{
						ReadTimeout = msTimeout;
					}
					else
					{
						ReadTimeout = Integer.MAX_VALUE;
					}
					ret = 1;
				}
			}
		}
		else
		{
			int n1 = strLine.indexOf('[');
			int n2 = strLine.indexOf(']');
			if (n1 >= 0 && n2 >= 0 && n1 < n2)
			{
				ret = -1;
			}
		}
		
		return ret;
	}
	
	private static void GetConfigParameters()
	{
		if (strTranscoderPath==null)
		{
			return;
		}
		
		try
		{
			String codecIniPath = strTranscoderPath + "ASCodec.ini";
			File file = new File(codecIniPath);
			int len = (int)file.length();
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[len];
			len = fis.read(buf, 0, len);
			fis.close();
			fis = null;
			file = null;
			String str = new String(buf, 0, len, "UTF-8");
			buf = null;
			
			//[FLVOVERHTTP]
			//CachePath=/usr/local/arcsoft/arcvideo/tmpdir/flvoverhttpcache/
			//CacheSize=100 //MB
			//ChunkedTransfer=0
			//PresumedTotalBytes=9223372036854775807
			int idxEntry = str.indexOf("[FLVOVERHTTP]");
			if (idxEntry >= 0)
			{
				idxEntry += 13;
			}
			else
			{
				idxEntry = 0;
			}
			
			char ch;
			int i = idxEntry;
			int lineBegin = i;
			do
			{
				try
				{
					ch = str.charAt(i);
				}
				catch (Exception e)
				{
					ch = 0;
				}
				if (ch==0 || ch=='\r' || ch=='\n')
				{
					if (i > lineBegin)
					{
						if (ParseLineForParameter(str.substring(lineBegin, i)) < 0)
						{
							break;
						}
					}
					try
					{
						if (ch != 0 && str.charAt(i+1)=='\n')
						{
							++i;
						}
					}
					catch (Exception e)
					{
					}
					lineBegin = i+1;
				}
				++i;
			} while (ch != 0);
		}
    	catch (Exception e)
    	{
    	}
    }        

	private static void LoadNativeLibrary() throws SecurityException, UnsatisfiedLinkError, NullPointerException
	{
		s_nNativeLibLoaded = -1;
		if (strTranscoderPath != null)
		{
	    	try
	    	{
				String libPath = strTranscoderPath;
				String OS = null;
				try
				{
					OS = System.getProperty("os.name").toLowerCase();
				}
		    	catch (Exception e)
		    	{
		    	}
	    		if (OS != null && OS.indexOf("win") >= 0)
	    		{
	    			libPath += "MediaHelper.dll";
	    		}
	    		else
	    		{
    				libPath += "libMediaHelper.so";
    			}
    			System.load(libPath);
    			s_nNativeLibLoaded = 1;
	    	}
	    	catch (Error err)
	    	{
	    	}
	    	catch (Exception e)
	    	{
	    	}
		}
		if (s_nNativeLibLoaded == -1)
		{
			System.loadLibrary("MediaHelper");
   			s_nNativeLibLoaded = 1;
		}
	}
	
	public NativeFunction()
	{
		if (s_nNativeLibLoaded==0)
		{
			try
			{
				LoadNativeLibrary();
			}
	    	catch (Error err)
	    	{
	    	}
	    	catch (Exception e)
	    	{
	    	}
		}
	}
	
    public int AdaptFlvHeaderData(byte[] header, int lenHeader, byte[] data, int offsetData, int lenData, int[] retOffset) {
    	return AdjustFlvHeaderData(header, lenHeader, data, offsetData, lenData, retOffset);
    }
    
    public static void main(String[] args) {
    	
    	int Packet_Size = 0x100000;	// 0x10000
    	
    	try {
    		
			File fileHeader = new File("D:\\Media\\flv\\flv.header");
			int lenHeader = (int)fileHeader.length(); 
	    	byte[] header = new byte[lenHeader];
			FileInputStream streamHeader = new FileInputStream(fileHeader);
			lenHeader = streamHeader.read(header, 0, lenHeader);
			streamHeader.close();
			fileHeader = null;
	
			try
			{
				File fileData = new File("D:\\Media\\flv\\flv.data");
				FileInputStream streamData = new FileInputStream(fileData);
				streamData.skip(123456);
				
				byte[] data = new byte[Packet_Size];
		    	NativeFunction nf = new NativeFunction();
		    	int lenDone = 0, lenData = 0;
		    	int[] retOffset = {-1, 0};
		    	while (retOffset[0] < 0)
		    	{
		    		if (retOffset[1] > 0)
		    		{
			    		lenDone += retOffset[1];
			    		if (lenDone > 0x500000)
			    		{
			    			break;
			    		}
			    		lenData -= retOffset[1];
			    		System.arraycopy(data, retOffset[1], data, 0, lenData);
		    		}
					lenData = streamData.read(data, 0, Packet_Size);
					if (lenData > 0)
					{
				    	nf.AdaptFlvHeaderData(header, lenHeader, data, 0, lenData, retOffset);
					}
		    	}
		    	nf = null;
		    	data = null;
		    	
				streamData.close();
				fileData = null;
			}
			catch (Exception e)
			{
			}
    	}
    	catch (Exception e)
    	{
    	}
    	
    	return;
    }
}
