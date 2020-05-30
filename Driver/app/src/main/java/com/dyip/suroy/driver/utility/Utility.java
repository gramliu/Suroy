package com.dyip.suroy.driver.utility;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utility {

    public static void log(String tag, Object message) {
        Log.println(Log.ASSERT, tag, message.toString());
    }

    public static String padInt(int num, int width) {

        String str = String.valueOf(num);
        if (str.length() >= width) {
            return str;
        } else {
            int diff = width - str.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < diff; i++) {
                sb.append(0);
            }
            sb.append(num);
            return sb.toString();
        }
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2) {
                    h.insert(0, "0");
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
