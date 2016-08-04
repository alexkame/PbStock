package com.pengbo.mhdzq.tools;

import java.security.MessageDigest;

/**
 * 
 * @author pobo
 * @created 2015-05-29
 */

public class KeyTool {
    /**
     * Gets the requested finger print of the certificate.
     * "MD5" "SHA1" "SHA-256"
     */
    public String getCertFingerPrint(String mdAlg, byte[] encCertInfo)
        throws Exception
    {
        MessageDigest md = MessageDigest.getInstance(mdAlg);
        byte[] digest = md.digest(encCertInfo);
        return toHexString(digest);
    }
    
    /**
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
    
    /**
     * Converts a byte array to hex string
     */
    private String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len-1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }
}
