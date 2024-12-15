package me.nyaruko166.michosauto.service;

import me.nyaruko166.michosauto.util.CookieUtil;
import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZenlessService {

    private final String ACT_ID = "e202406031448091";
    private final String SIGN_INFO_API = "https://sg-public-api.hoyolab.com/event/luna/zzz/os/info?act_id=%s".formatted(ACT_ID);
    private final String SIGN_API = "https://sg-public-api.hoyolab.com/event/luna/zzz/os/sign?act_id=%s".formatted(ACT_ID);
    private final String REDEEM_API = "https://public-operation-nap.hoyoverse.com/common/apicdkey/api/webExchangeCdkey";

    @Autowired
    private HoyoService hoyoService;

    //{"retcode":0,"message":"OK","data":{"total_sign_day":14,"today":"2024-12-14","is_sign":true,"is_sub":false,"region":"","sign_cnt_missed":0,"short_sign_day":0,"send_first":false}}
    public String getSignInfo(String cookie) {
        Headers headers = HttpUtil.headersBuilder(List.of(HttpUtil.APPLICATION_JSON,
                "Cookie: %s".formatted(cookie),
                HttpUtil.USER_AGENT,
                "x-rpc-signgame: zzz"));
        return HttpUtil.getRequest(SIGN_INFO_API, headers);
    }

    //Return {"retcode":0,"message":"OK","data":{"code":"","risk_code":0,"gt":"","challenge":"","success":0,"is_risk":false}}
    public String signIn(String cookie) {
        Headers headers = HttpUtil.headersBuilder(List.of(HttpUtil.APPLICATION_JSON,
                "Cookie: %s".formatted(cookie),
                HttpUtil.USER_AGENT,
                "x-rpc-signgame: zzz"));
        return HttpUtil.postRequest(SIGN_API, HttpUtil.requestBodyBuilder(""), headers);
    }

    public String redeemCode(String cookie, String redeemCode) {
        String parsedCookie = CookieUtil.parseCookie(cookie, List.of(
                CookieUtil.COOKIE_TOKEN_V2,
                CookieUtil.ACCOUNT_MID_V2,
                CookieUtil.ACCOUNT_ID_V2,
                CookieUtil.COOKIE_TOKEN,
                CookieUtil.ACCOUNT_ID
        ), true);

        Map<String, String> params = new HashMap<>();
        params.put("t", String.valueOf(System.currentTimeMillis()));
        params.put("lang", "en");
        params.put("game_biz", "nap_global");
        params.put("uid", CookieUtil.getCookieValue(cookie, CookieUtil.UID));
        params.put("region", "prod_gf_jp");
        params.put("cdkey", redeemCode);

        Headers headers = HttpUtil.headersBuilder(List.of("Cookie: %s".formatted(parsedCookie)));

        String url = HttpUtil.urlParamBuilder(REDEEM_API, params);
        return HttpUtil.getRequest(url, headers);
    }

//    public static void main(String[] args) {
//
//        String res = new ZenlessService().redeemCode(CookieUtil.cookie, "YANAGIGIFTALL");
//        System.out.println(res);
//
//    }

}
