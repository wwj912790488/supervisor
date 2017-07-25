package com.arcsoft.supervisor.utils.app;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * A utility class for expire date checker.
 *
 * @author zw.
 */
public abstract class SystemExpireChecker {

    private static long expiredTime;

    private static final String DATE_KEY = "date";

    private static final String KEY = "~!@#supervisor**";

    private static final String DEFAULT_EVALUATE_FILE = "system.properties";

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final Logger LOG = LoggerFactory.getLogger(SystemExpireChecker.class);

    public static void initialize() throws URISyntaxException, SystemConfigurationNotFoundException,
            IllegalExpireDateException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(DEFAULT_EVALUATE_FILE);
        initialize(url);
    }

    public static void initialize(URL url) throws URISyntaxException, SystemConfigurationNotFoundException,
            IllegalExpireDateException {
        if (url == null) {
            throw new SystemConfigurationNotFoundException("Can't found system configuration");
        }
        initialize(url.toURI().getPath());
    }

    public static void initialize(String filePath) throws SystemConfigurationNotFoundException,
            IllegalExpireDateException {
        Objects.requireNonNull(filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new SystemConfigurationNotFoundException("Can't found system configuration");
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            Properties p = new Properties();
            p.load(fis);

            String encryptedStr = p.getProperty(DATE_KEY);
            if (StringUtils.isEmpty(encryptedStr)) {
                throw new IllegalExpireDateException("Illegally system expire date");
            }

            try {
                String actualDateStr = decrypt(encryptedStr.substring(4));
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
                expiredTime = sdf.parse(actualDateStr).getTime();
                LOG.info("Currently expired date: " + actualDateStr);
            } catch (Exception e) {
                throw new IllegalExpireDateException("Illegally system expire date", e);
            }
        } catch (IOException e) {
            throw new SystemConfigurationNotFoundException("Can't found system configuration");
        }
    }

    public static boolean isExpired() {
        return new Date().getTime() >= expiredTime;
    }

    public static String encrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new String(new Base64().encode(cipher.doFinal(str.getBytes())));
    }

    public static String decrypt(String encrypt) throws Exception {
        byte[] arr = new Base64().decode(encrypt.getBytes());
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedArr = cipher.doFinal(arr);
        return new String(decryptedArr);
    }

    /**
     * An exception thrown when system configuration not found.
     *
     */
    public static class SystemConfigurationNotFoundException extends RuntimeException {

        public SystemConfigurationNotFoundException() {
            super();
        }

        public SystemConfigurationNotFoundException(String message) {
            super(message);
        }

        public SystemConfigurationNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * An exception thrown when the expire date of system is illegality.
     */
    public static class IllegalExpireDateException extends RuntimeException {
        public IllegalExpireDateException() {
            super();
        }

        public IllegalExpireDateException(String message) {
            super(message);
        }

        public IllegalExpireDateException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
