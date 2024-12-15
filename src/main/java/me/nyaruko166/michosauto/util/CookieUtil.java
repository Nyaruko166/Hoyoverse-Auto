package me.nyaruko166.michosauto.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CookieUtil {

    public static String parseCookie(String cookieString, List<String> keys, boolean isWhitelist) {
        // Split the cookie string into individual key-value pairs
        String[] pairs = cookieString.split("; ");
        Map<String, String> cookieMap = new HashMap<>();

        // Populate the map with cookie key-value pairs
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                cookieMap.put(keyValue[0], keyValue[1]);
            }
        }

        // Filter keys based on whitelist/blacklist logic
        Set<String> filterKeys = new HashSet<>(keys);
        Map<String, String> filteredCookies = cookieMap.entrySet()
                                                       .stream()
                                                       .filter(entry -> isWhitelist == filterKeys.contains(entry.getKey()))
                                                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Reconstruct the cookie string
        return filteredCookies.entrySet()
                              .stream()
                              .map(entry -> entry.getKey() + "=" + entry.getValue())
                              .collect(Collectors.joining("; "));
    }

    public static String getCookieValue(String cookieString, String key) {
        // Split the cookie string into individual key-value pairs
        String[] pairs = cookieString.split("; ");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue[0].equalsIgnoreCase(key)) {
                return keyValue[1];
            }
        }
        // Return null if the key is not found
        return null;
    }

    public static final String UID = "ltuid_v2";
    public static final String ACCOUNT_ID = "account_id"; //Same as UID?
    public static final String COOKIE_TOKEN = "cookie_token";
    public static final String COOKIE_TOKEN_V2 = "cookie_token_v2";
    public static final String ACCOUNT_MID_V2 = "account_mid_v2";
    public static final String ACCOUNT_ID_V2 = "account_id_v2";



}
