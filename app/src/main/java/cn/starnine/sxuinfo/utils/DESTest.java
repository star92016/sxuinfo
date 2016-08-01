package cn.starnine.sxuinfo.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by licheng on 16-8-1.
 */
public class DESTest {
    public static void initkey(Context context){
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        uniqueId=uniqueId.toUpperCase();
        uniqueId=uniqueId.replaceAll("-","");
        byte [] b=Hex2byte(uniqueId);
        byte[]bytes=new byte[]{0,0,0,0};
        for(int i=0;i<b.length;i++){
            bytes[i%4]^=b[i];
        }
        key=byte2Hex(bytes);
    }
    private static String key="";
    public static String encrypt(String content) {

        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            byte[] result = cipher.doFinal(content.getBytes());
            return byte2Hex(result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private final static char[] hexarray="0123456789ABCDEF".toCharArray();
    private static String byte2Hex(byte[]b){
        char[] hex=new char[b.length*2];
        for(int j=0;j<b.length;j++){
            int v=b[j]&0xff;
            hex[j*2]=hexarray[v>>>4];
            hex[j*2+1]=hexarray[v&0x0f];
        }
        return new String(hex);
    }
    private static byte[] Hex2byte(String s){
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String decrypt(String s) {
        try {
            byte []content=Hex2byte(s);
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            byte[] result = cipher.doFinal(content);
            return new String(result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
