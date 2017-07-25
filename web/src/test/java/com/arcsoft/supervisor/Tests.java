package com.arcsoft.supervisor;

import com.arcsoft.supervisor.utils.app.SystemExpireChecker;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * @author zw.
 */
public class Tests {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tests.class);

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, ParseException {
        System.out.println(String.format("%x", 135347));
    }


    @Test
    public void testDynamicChangeLevelOfLog4j() {
        org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
        rootLogger.setLevel(Level.OFF);
        Enumeration enumeration = rootLogger.getLoggerRepository().getCurrentLoggers();
        while (enumeration.hasMoreElements()) {
            org.apache.log4j.Logger log = (org.apache.log4j.Logger) enumeration.nextElement();
            log.setLevel(Level.OFF);
        }
        LOGGER.error("xxxx", new IllegalArgumentException("Xxxx is illegal."));
    }

    @Test
    public void testArrayUtils() {
        String[] fields = {"1", "2"};
        String[] copyedFields = ArrayUtils.add(fields, "3");
        Assert.assertArrayEquals(fields, copyedFields);
    }

    @Test
    public void testPaths() throws IOException {
        File file = new File("../tomato/Downloads/transcoder.tpl");
        System.out.println(file.getCanonicalPath());
    }

    @Test
    public void testAes() throws Exception {
        String encryptionKey = "~!@#supervisor**";
        String plaintext = "2015-08-20";

        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] entrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
        System.out.println(entrypted.length);
        String encodeStr = new String(new Base64().encode(entrypted));
        System.out.println(encodeStr);

        byte[] en = new Base64().decode(encodeStr.getBytes());
        Cipher cipher2 = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher2.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] kkk = cipher2.doFinal(en);

        System.out.println(new String(kkk));

    }

    @Test
    public void testEvaluteExpire() throws Exception {
        String date = "2999-08-20 23:59:59";
        String data = SystemExpireChecker.encrypt(date);
        System.out.println(data);

        String dataaa = "abcd" + data + "dcba";
        System.out.println(dataaa);
        System.out.println(dataaa.substring(4, dataaa.length() - 4));

        String data2 = SystemExpireChecker.decrypt(data);
        Assert.assertEquals(date, data2);
    }

    @Test
    public void testEvaluateExpireChecker() {
        try {
            SystemExpireChecker.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
