package com.arcsoft.supervisor.utils.app;

import com.arcsoft.supervisor.utils.ConfigProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Defines utilities fields and properties used for <code>Environment</code>
 *
 * @author zw.
 */
public abstract class Environment {

    private static final Logger LOG = LoggerFactory.getLogger(Environment.class);

    public static final String ENV_FILE_NAME = "env.properties";

    private static ConfigProperties env;

    private static ExpireChecker expireChecker;

    private static Profiler profiler;

    /**
     * Initialize environment.
     *
     * @throws URISyntaxException if failed get resource from classloader
     * @throws IOException        if any I/O errors occurs
     */
    public static void initialize() throws URISyntaxException, IOException {
        URL envURL = Thread.currentThread().getContextClassLoader().getResource(ENV_FILE_NAME);
        if (envURL == null) {
            throw new FileNotFoundException("Can't find environment file with " + ENV_FILE_NAME);
        }
        initialize(Paths.get(envURL.toURI()).toString());
    }

    public static void initialize(String path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)) {
            Properties p = new Properties();
            p.load(reader);
            initialize(p);
        }
    }

    /**
     * Initialize environment from specific properties object.
     *
     * @param p the properties object contains environment configuration
     */
    public static void initialize(Properties p) {
        env = new ConfigProperties(p);
        profiler = new Profiler(env.getString(EnvKey.profiles.name(), StringUtils.EMPTY));
        expireChecker = new ExpireChecker(env.getString(EnvKey.date.name()));
    }

    /**
     * Returns the string value with given key.
     *
     * @param key the key
     * @return the key correspond string value
     */
    public static String getProperty(String key) {
        return env.getProperty(key);
    }

    /**
     * Returns the string value with given key.
     *
     * @param key the key
     * @param defaultStr default value if the key correspond value is {@code null}
     * @return the key correspond string value
     */
    public static String getProperty(String key, String defaultStr) {
        return StringUtils.defaultString(env.getProperty(key), defaultStr);
    }

    /**
     * Returns the {@link ExpireChecker} instance associated to currently environment.
     *
     * @return the {@link ExpireChecker} instance associated to currently environment
     */
    public static ExpireChecker getExpireChecker() {
        return expireChecker;
    }

    /**
     * Returns the {@link Profiler} instance associated to currently environment.
     *
     * @return the {@link Profiler} instance associated to currently environment
     */
    public static Profiler getProfiler() {
        return profiler;
    }


    public enum EnvKey {
        profiles,
        date,
        version
    }

    public static class Profiler {

        public static final String STR_PRODUCTION = "production";
        public static final String STR_SARTF = "sartf";
        public static final String STR_SMS = "sms";
        public static final String STR_VOICE = "voice";
        public static final String STR_WOWZA = "wowza";
        public static final String STR_RTMP = "rtmp";
        public static final String STR_MESSAGE = "message";
        public static final String STR_WG = "wg";
        public static final String STR_MOSAIC = "mosaic";

        private final List<String> profiles;
        private static final String SPRING_ACTIVE_PROFILE_KEY = "spring.profiles.active";

        public Profiler(String profileStr) {
            System.setProperty(SPRING_ACTIVE_PROFILE_KEY, profileStr);
            profiles = Arrays.asList(profileStr.split(","));
            LOG.info("Currently profile: {}", profileStr);
        }

        public boolean isSartf() {
            return profiles.contains(STR_SARTF);
        }

        public boolean isProduction() {
            return profiles.contains(STR_PRODUCTION);
        }

        /**
         * Checks the givens profile is existed in currently environment or not.
         * <p>Synonym method of {@link #hasProfiles(String...)}</p>
         *
         * @param profile profile to be check
         * @return <code>true</code> if givens profile is existed in currently environment
         */
        public boolean hasProfile(String profile) {
            return hasProfiles(profile);
        }

        /**
         * Checks the givens profiles is existed in currently environment or not.
         *
         * @param profiles profile array to be check
         * @return <code>true</code> if givens profiles is existed in currently environment
         */
        public boolean hasProfiles(String... profiles) {
            return this.profiles.containsAll(Arrays.asList(profiles));
        }

        /**
         * Checks the givens profiles has any one existed in currently environment or not.
         *
         * @param profiles profile array to be check
         * @return <code>true</code> if givens profiles is has any one existed in currently environment
         */
        public boolean hasAnyProfile(String... profiles) {
            for (String p : profiles) {
                if (hasProfile(p)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks {@code wowza} profile is enable or not.
         *
         * @return {@code true} enabled otherwise false
         */
        public boolean isEnableWowza() {
            return hasProfile(STR_WOWZA);
        }

        /**
         * Checks {@code rtmp} profile is enable or not.
         *
         * @return {@code true} enabled otherwise false
         */
        public boolean isEnableRtmp() {
            return hasProfile(STR_RTMP);
        }

        public boolean isMosaic(){ return hasProfile(STR_MOSAIC);}
    }

    /**
     * A class used for checks the evaluate date.
     *
     */
    public static class ExpireChecker {

        private long expiredTime;

        private static final String KEY = "~!@#supervisor**";

        private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

        public ExpireChecker(String encryptedStr) {
            decryptExpiredTime(encryptedStr);
        }

        private void decryptExpiredTime(String encryptedStr) {
            if (StringUtils.isEmpty(encryptedStr)) {
                throw new IllegalArgumentException("Illegally system expire date");
            }
            try {
                String actualDateStr = decrypt(encryptedStr.substring(4));
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
                expiredTime = sdf.parse(actualDateStr).getTime();
                LOG.info("Currently expired date: " + actualDateStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Illegally system expire date", e);
            }
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiredTime;
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
    }

}
