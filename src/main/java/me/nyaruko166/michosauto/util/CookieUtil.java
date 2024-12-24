package me.nyaruko166.michosauto.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CookieUtil {

    private static final String WEB_API = "https://webapi-os.account.hoyoverse.com/Api/fetch_cookie_accountinfo";
    private static final Gson gson = new Gson();
    private static final Logger log = LogManager.getLogger(CookieUtil.class);

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

    public static Map<String, String> getNewCookie(String cookie) {
        Map<String, String> mapCookie = new HashMap<>();
        Headers headers = HttpUtil.headersBuilder(List.of("Cookie: %s".formatted(cookie)));
        JsonObject jsonRes = gson.fromJson(HttpUtil.getRequest(WEB_API, headers), JsonObject.class);
        if (jsonRes.get("code").getAsInt() != 200) {
            log.error("Fail to get new cookie token");
            return null;
        }
        String cookie_token = jsonRes.get("data").getAsJsonObject()
                                     .get("cookie_info").getAsJsonObject()
                                     .get("cookie_token").getAsString();
        String account_id = jsonRes.get("data").getAsJsonObject()
                                   .get("cookie_info").getAsJsonObject()
                                   .get("account_id").getAsString();

        mapCookie.put("cookie_token", cookie_token);
        mapCookie.put("account_id", account_id);
        return mapCookie;
    }

    public static final String UID = "ltuid_v2";
    public static final String ACCOUNT_ID = "account_id"; //Same as UID?
    public static final String COOKIE_TOKEN = "cookie_token";
    public static final String COOKIE_TOKEN_V2 = "cookie_token_v2";
    public static final String ACCOUNT_MID_V2 = "account_mid_v2";
    public static final String ACCOUNT_ID_V2 = "account_id_v2";

}
