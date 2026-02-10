package me.nyaruko166.michosauto.util;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GeneralUtil {

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String DS_SALT = "6s25p5ox5y14umn1p61aqyyvbvvl3lrt";

    public static String generateDS() {
        // Get the current Unix timestamp in seconds
        long time = System.currentTimeMillis() / 1000;

        // Generate a random string
        String random = generateRandomString();

        // Create the hash using the format: salt + time + random
        String data = "salt=" + DS_SALT + "&t=" + time + "&r=" + random;
        String hash = hashMD5(data);

        // Return the DS string: "time,random,hash"
        return time + "," + random + "," + hash;
    }

    public static String getDiscordTimeStamp(SlashCommandInteractionEvent event) {
        return String.format("%s - %s",
                event.getJDA().getSelfUser().getName(),
                LocalDateTime.now().format(timeFormatter));
    }

    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
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

    public static String hashMD5(String data) {
        try {
            // Use MD5 hashing algorithm
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] raw = md.digest(data.getBytes(StandardCharsets.UTF_8));

            return byteToHex(raw);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static String hmacSha256Hex(String data, String salt) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey =
                    new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

            mac.init(secretKey);

            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            return byteToHex(raw);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String byteToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
