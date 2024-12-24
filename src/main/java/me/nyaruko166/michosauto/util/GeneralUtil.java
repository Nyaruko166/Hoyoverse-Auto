package me.nyaruko166.michosauto.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class GeneralUtil {

    private static final String DS_SALT = "6s25p5ox5y14umn1p61aqyyvbvvl3lrt";

    public static String generateDS() {
        // Get the current Unix timestamp in seconds
        long time = System.currentTimeMillis() / 1000;

        // Generate a random string
        String random = generateRandomString();

        // Create the hash using the format: salt + time + random
        String data = "salt=" + DS_SALT + "&t=" + time + "&r=" + random;
        String hash = hashData(data);

        // Return the DS string: "time,random,hash"
        return time + "," + random + "," + hash;
    }

    private static String generateRandomString() {
        int length = 6; // Length of the random string
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }

        return result.toString();
    }

    private static String hashData(String data) {
        try {
            // Use MD5 hashing algorithm
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

}
