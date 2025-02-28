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

    public static String getNewCookie(String cookie) {
//        String coc = "mi18nLang=en-us; _HYVUUID=b2e51a3b-89f6-43b3-afc4-8fa7c0870e96; HYV_LOGIN_PLATFORM_OPTIONAL_AGREEMENT={%22content%22:[]}; _MHYUUID=20d40f4e-be50-4b74-b286-733e34112e5b; cookie_token_v2=v2_CAQSDGM5b3FhcTNzM2d1OBokYjJlNTFhM2ItODlmNi00M2IzLWFmYzQtOGZhN2MwODcwZTk2ILOzh74GKPb1loIGMOfQPkILYmJzX292ZXJzZWE.s9nBZwAAAAAB.MEUCIQCvv1437w0DrOnYCWVnKkF8Bi9wHYTRSp4uktoJOn7hpgIgHQJZhWkbtic-llq8i55tssF6-hR0YKKvpJQxSpzZZl8; account_mid_v2=1bxz59ie9u_hy; account_id_v2=1026151; ltoken_v2=v2_CAISDGM5b3FhcTNzM2d1OBokYjJlNTFhM2ItODlmNi00M2IzLWFmYzQtOGZhN2MwODcwZTk2ILOzh74GKI2bgcMCMOfQPkILYmJzX292ZXJzZWE.s9nBZwAAAAAB.MEUCIDAq0Sg5Kxynh6cpWFJunGV_4OmgT1QZlcd0aMwUXw7_AiEAhjawIa2PZVtW5gP7Z631_Nb3rEz79F78_7mmt9ADmkI; ltmid_v2=1bxz59ie9u_hy; ltuid_v2=1026151; HYV_LOGIN_PLATFORM_LIFECYCLE_ID={%22value%22:%2253f1c0d1-e121-4320-9b9e-7cb967484e47%22}; HYV_LOGIN_PLATFORM_LOAD_TIMEOUT={}; HYV_LOGIN_PLATFORM_TRACKING_MAP={}";
        Headers headers = HttpUtil.headersBuilderEmpty(List.of(
                "Host: webapi-os.account.hoyoverse.com",
                "Cookie: %s".formatted(cookie)));
        JsonObject jsonRes = gson.fromJson(HttpUtil.getRequest(WEB_API, headers), JsonObject.class);
        if (jsonRes.get("code").getAsInt() != 200) {
            log.error("Fail to get new cookie token");
            log.error(jsonRes);
            return null;
        }
        String cookie_token = jsonRes.get("data").getAsJsonObject()
                                     .get("cookie_info").getAsJsonObject()
                                     .get("cookie_token").getAsString();
        String account_id = jsonRes.get("data").getAsJsonObject()
                                   .get("cookie_info").getAsJsonObject()
                                   .get("account_id").getAsString();

        return "%s; cookie_token=%s; account_id=%s".formatted(cookie, cookie_token, account_id);
    }

    public static final String UID = "ltuid_v2";
    public static final String ACCOUNT_ID = "account_id"; //Same as UID?
    public static final String COOKIE_TOKEN = "cookie_token";
    public static final String COOKIE_TOKEN_V2 = "cookie_token_v2";
    public static final String ACCOUNT_MID_V2 = "account_mid_v2";
    public static final String ACCOUNT_ID_V2 = "account_id_v2";

}
