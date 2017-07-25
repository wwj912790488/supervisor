package com.arcsoft.supervisor.agent;

import com.arcsoft.supervisor.agent.config.AppConfig;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import com.arcsoft.supervisor.utils.app.Environment;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The class represent the main application of agent side.
 * 
 * @author fjli
 * @author zw
 */
public class Application {

	private static Logger log = Logger.getLogger(Application.class);
	private static final String DEFAULT_LOG4J = "config/log4j.properties";
	private static final int DEFAULT_COMMAND_PORT = 5001;
	private static AbstractApplicationContext context;
	private static MessageServer messageServer;
	private static int port;
    private static final ScheduledExecutorService expiredCheckerPool = Executors.newSingleThreadScheduledExecutor(
            NamedThreadFactory.create("SystemExpiredChecker")
    );
    /**
     * Currently running path.
     */
    private static String workDir = System.getProperty("user.dir");

    /**
     * Currently path of agent folder.
     */
    private static String ROOT_PATH = Paths.get(workDir).getParent().toString();

    private static final String ENVIRONMENT_FILE_PATH = ROOT_PATH + "/conf/" + Environment.ENV_FILE_NAME;

	public static void main(String[] args) {
        // setup log4j configuration.
		configureLog4j();

		// load application configuration.
		try {
			AppConfig.load();
		} catch(IOException e) {
			log.error("load agent configuration failed.", e);
			return;
		}

		// get command port
		port = AppConfig.getInt("command.port", DEFAULT_COMMAND_PORT);

		String command = (args.length > 0) ? args[0] : "start";
		if (command.equalsIgnoreCase("start")) {
			try {
				startAgent();
			} catch (URISyntaxException | IOException e) {
				log.error(e);
			}
		} else {
			sendCommand(command);
		}
        //To response for sign kill of linux
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                sendCommand("stop");
            }
        });
	}

	/**
	 * Start agent.
	 */
	private static void startAgent() throws URISyntaxException, IOException {
        // Initialize environment first
		Environment.initialize(ENVIRONMENT_FILE_PATH);

        // Close jvm if system is expired
        if (isExpired()) {
            System.exit(0);
        }

		// start spring container.
		context = new ClassPathXmlApplicationContext(new String[] {
                "classpath:config/spring_*.xml"
        });

		// start message server.
		messageServer = new MessageServer(port) {
			@Override
			protected void messageReceived(String message, BufferedWriter writer) {
				commandReceived(message, writer);
			}
		};

		try {
			messageServer.start();
		} catch (IOException e) {
			log.error("start message server failed.", e);
		}

        // Do expire date checker every 5 seconds
        expiredCheckerPool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (isExpired()) {
                    sendCommand("stop");
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
	}

    private static boolean isExpired() {
        boolean expired = Environment.getExpireChecker().isExpired();
        if (expired) {
            log.error("Close agent cause by system expire");
        }
        return expired;
    }

	/**
	 * Stop agent.
	 */
	private static void stopAgent() {
		if (context != null) {
			context.close();
			context = null;
		}

        expiredCheckerPool.shutdownNow();

        log.info("Application command server shutdown complete.");
	}

	/**
	 * Configuration log4j.
	 */
	private static void configureLog4j() {
		// setup log4j use the specified file.
		String log4jConfigFile = System.getProperty("agent.log4j");
		if (log4jConfigFile != null) {
			File file = new File(log4jConfigFile);
			if (file.exists()) {
				PropertyConfigurator.configure(log4jConfigFile);
				return;
			}
		}

		// setup log4j using default configuration.
		URL url = Application.class.getClassLoader().getResource(DEFAULT_LOG4J);
		if (url != null) {
			PropertyConfigurator.configure(url);
		}
	}

	/**
	 * Send command to the message server.
	 * 
	 * @param command - the command to be sent
	 */
	private static void sendCommand(String command) {
		Socket socket = new Socket();
		DataOutputStream dos = null;
		BufferedReader reader = null;
		try {
			socket.connect(new InetSocketAddress("127.0.0.1", port), 5000);
			socket.setSoTimeout(30000);
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeUTF(command);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch(IOException e) {

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
				}
			}
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Process received command.
	 * 
	 * @param command - the received command
	 */
	private static void commandReceived(String command, BufferedWriter writer) {
		if ("stop".equalsIgnoreCase(command)) {
            stopAgent();
            try {
                writer.write("OKAY");
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (messageServer != null) {
                messageServer.stop();
                messageServer = null;
            }
		} else {
            try {
                writer.write("FAIL, unknown command: " + command);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
	}

    public static String getWorkDir() {
        return workDir;
    }
}
