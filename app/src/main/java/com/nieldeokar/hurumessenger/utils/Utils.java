package com.nieldeokar.hurumessenger.utils;

import android.icu.text.DateFormat;
import android.os.Build;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static int networkEndianTwoBytesIntoUnsignedInteger(byte[] bytes) {
        if (bytes == null || bytes.length != 2) throw new InvalidParameterException();
        return ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);

    }

    public static byte[] unsignedShortIntegerIntoTwoBytes(int integer) {
        if (integer < 0 || integer > 65535) throw new InvalidParameterException();
        byte[] data = new byte[2]; // <- assuming "in" value in 0..65535 range and we can use 2 bytes only
        data[1] = (byte) (integer & 0xFF);
        data[0] = (byte) ((integer >> 8) & 0xFF);
        return data;
    }

    public static byte[] unsignedShortIntegerIntoOneByte(int integer){
        if (integer < 0 || integer > 255) throw new InvalidParameterException();

        byte[] data = new byte[1]; // <- assuming "in" value in 0..255 range and we can use 1 byte only
        data[0] = (byte) (integer & 0xff);
        return data;
    }

    public static int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }


    public static String formatTime(long time){
        Date date = new Date(time);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
            return df.format(date);
        }else {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            return sdf.format(date);
        }
    }
}
