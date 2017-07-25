package com.arcsoft.supervisor.commons;

/**
 *
 * A class that defines all of constants for system usage.
 *
 * @author zw.
 */
public class SupervisorDefs {

    /**
     * All of constants should be defines in here.
     */
    public interface Constants{
        // login user info
        String LOGIN_USER_INFO = "login_userinfo";

        /**
         * The size of each page.
         */
        int PAGE_SIZE = 10;
        /**
         * The attribute name of page.
         */
        String PAGER = "pager";
        /**
         * The attribute name of query params.
         */
        String QUERY_PARAMS = "q";

        /**
         * A string of json header with charset=utf-8.
         */
        String PRODUCT_JSON_UTF8 = "application/json;charset=utf-8";

    }



    /**
     * Defines all of modules.
     */
    public enum Modules{

        CHANNEL((byte)0),
        GRAPHICS((byte)1),
        DEVICE((byte)2),
        ALARM((byte)3);

        private final byte module;

        Modules(byte module) {
            this.module = module;
        }

        public byte getModule() {
            return module;
        }
    }
}
