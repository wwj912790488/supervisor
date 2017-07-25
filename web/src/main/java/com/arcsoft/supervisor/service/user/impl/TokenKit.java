
package com.arcsoft.supervisor.service.user.impl;


/**
 */
public class TokenKit
{
    private static TokenKit token = new TokenKit();
    
    /**
     * 
     * @return
     */
    public static TokenKit getInstance()
    {
        return token;
    }
    
    /**
     * 
     */
    protected TokenKit()
    {
        
    }
    
    /**
     * 
     * @return
     */
    public synchronized String generateTokenString(long userid)
    {
        return System.currentTimeMillis() + "a" + DESPlus.getInstance().encrypt(userid + "");
    }    
    
    /**
     * 
     * @param str
     */
    public synchronized long getUserId(String str)
    {
        int index = str.indexOf("a");
        if (index > 0)
        {
            return Long.parseLong(DESPlus.getInstance().decrypt(str.substring(index + 1)));
        }
        return 1;
    }
}
