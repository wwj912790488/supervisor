package com.arcsoft.supervisor.service.device;

import ch.ethz.ssh2.*;

import com.arcsoft.supervisor.model.domain.server.SSHConnectInfo;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by wwj on 2017/3/23.
 */
@Service
public class RemoteShellExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(RemoteShellExecutorService.class);
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static final int Block_Size	= 0xF8E0;

    @PreDestroy
    private void uninit() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.error("Force shutdown the RemoteShellExecutorService after wait 5 seconds timeout");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Failed to wait RemoteShellExecutorService shutdown");
        }
    }

    public boolean uploadFile(SSHConnectInfo sshConnectInfo, String localFile, String remoteTargetDirectory, String mode) {
        boolean flag = false;
        Connection connection = null;
        try {
            logger.info(sshConnectInfo + ",localFile:" + localFile + ",remoteTargetDirectory:" + remoteTargetDirectory + ",mode:" + mode);
            connection = new Connection(sshConnectInfo.getIp());
            connection.connect();
            flag = connection.authenticateWithPassword(sshConnectInfo.getUser(), sshConnectInfo.getPassword());

            SCPClient scpClient = connection.createSCPClient();
            scpClient.put(localFile, remoteTargetDirectory, mode);

            flag = true;
        } catch (IOException e) {
            logger.error("uploadFile error : {}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return flag;
    }

    public boolean getSSHFile(SSHConnectInfo sshConnectInfo, String localTargetDirectory, String remoteFile) {
        boolean flag = false;
        Connection connection = null;
        try {
            logger.info(sshConnectInfo + ",remoteFile:" + remoteFile + ",localTargetDirectory:" + localTargetDirectory);
            connection = new Connection(sshConnectInfo.getIp());
            connection.connect();
            flag = connection.authenticateWithPassword(sshConnectInfo.getUser(), sshConnectInfo.getPassword());

            SCPClient scpClient = connection.createSCPClient();
            scpClient.get(remoteFile,localTargetDirectory);
            flag = true;
        } catch (IOException e) {
            logger.error("getSSHFile error : {}", e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return flag;
    }

    public boolean login(SSHConnectInfo sshConnectInfo)  {
        Connection connection  = new Connection(sshConnectInfo.getIp(),sshConnectInfo.getPort());
        try {
            connection.connect(); // 连接
            return connection.authenticateWithPassword(sshConnectInfo.getUser(), sshConnectInfo.getPassword()); // 认证
        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }

    }

    public boolean execCommand(SSHConnectInfo sshConnectInfo, String cmds) throws Exception {
        boolean result = false;
        Connection connection = null;
        InputStream stdOut = null;
        InputStream stdErr = null;
        List<String> outStringList;
        List<String> outErrorStringList;
        Session session = null;
        try {
            connection = new Connection(sshConnectInfo.getIp(), sshConnectInfo.getPort());
            connection.connect();
            connection.authenticateWithPassword(sshConnectInfo.getUser(), sshConnectInfo.getPassword());
            session = connection.openSession();
            session.execCommand(cmds);
            stdOut = new StreamGobbler(session.getStdout());
            stdErr = new StreamGobbler(session.getStderr());
            Future<List<String>> stdOutFuture = createStreamFuture(stdOut);
            Future<List<String>> stdErrorFuture = createStreamFuture(stdErr);
            outStringList = stdOutFuture.get();
            logger.info(sshConnectInfo + ",shell result:" + outStringList);
            outErrorStringList = stdErrorFuture.get();
            logger.info(sshConnectInfo + ",shell error:" + outErrorStringList);
            result = true;
        } catch (Exception e) {
            logger.error("run agent.sh fail", e);
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
            IOUtils.closeQuietly(stdOut);
            IOUtils.closeQuietly(stdErr);
        }
        return result;
    }

    private List<String> processStream(final InputStream in) throws IOException {
        final List<String> stringList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                stringList.add(line);
            }
        } catch (IOException e) {
            throw e;
        }
        return stringList;
    }

    private Future<List<String>> createStreamFuture(final InputStream stdStream) {
        Future<List<String>> stdFuture = executorService.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() {
                try {
                    return processStream(stdStream);
                } catch (IOException e) {
                    logger.error("{}", e);
                }
                return null;
            }
        });
        return stdFuture;
    }

    public long readFileContent(HttpServletRequest request, HttpServletResponse response, File file) throws IOException
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
        response.setHeader("Content-disposition", "attachment;filename=" +file.getName());

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
               // e.printStackTrace();
            }
            catch (IOException e)
            {
                // input or output stream is closed
               // e.printStackTrace();
            }
            catch (Exception e)
            {
                // other exception
               // e.printStackTrace();
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
