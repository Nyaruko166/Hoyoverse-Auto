package me.nyaruko166.michosauto.service;

import me.nyaruko166.michosauto.model.GameData;
import me.nyaruko166.michosauto.util.CookieUtil;
import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZenlessService {

    private final String ACT_ID = "e202406031448091";
    private final String SIGN_INFO_API = "https://sg-public-api.hoyolab.com/event/luna/zzz/os/info?act_id=%s".formatted(ACT_ID);
    private final String SIGN_API = "https://sg-public-api.hoyolab.com/event/luna/zzz/os/sign?act_id=%s".formatted(ACT_ID);
    private final String REDEEM_API = "https://public-operation-nap.hoyoverse.com/common/apicdkey/api/webExchangeCdkey";

//    @Autowired
//    private HoyoService hoyoService;

//    public static void main(String[] args) {
//        String url = "https://sg-act-nap-api.hoyolab.com/event/game_record_zzz/api/zzz/note?server=prod_gf_jp&role_id=1300994756";
//        Headers headers = HttpUtil.headersBuilder(List.of(
//                "Cookie: %s".formatted(cookie),
//                "DS: %s".formatted(GeneralUtil.generateDS())
//        ));
//        String res = HttpUtil.getRequest(url, headers);
//        System.out.println(res);
//    }

    //{"retcode":0,"message":"OK","data":{"total_sign_day":14,"today":"2024-12-14","is_sign":true,"is_sub":false,"region":"","sign_cnt_missed":0,"short_sign_day":0,"send_first":false}}
    public String getSignInfo(String cookie) {
        Headers headers = HttpUtil.headersBuilder(HttpUtil.HoyoLab,
                List.of(HttpUtil.APPLICATION_JSON,
                        "Cookie: %s".formatted(cookie),
                        "x-rpc-signgame: zzz"));
        return HttpUtil.getRequest(SIGN_INFO_API, headers);
    }

    //Return {"retcode":0,"message":"OK","data":{"code":"","risk_code":0,"gt":"","challenge":"","success":0,"is_risk":false}}
    public String signIn(String cookie) {
        Headers headers = HttpUtil.headersBuilder(HttpUtil.HoyoLab,
                List.of(HttpUtil.APPLICATION_JSON,
                        "Cookie: %s".formatted(cookie),
                        "x-rpc-signgame: zzz"));
        return HttpUtil.postRequest(SIGN_API, HttpUtil.requestBodyBuilder(""), headers);
    }

    public String redeemCode(GameData gameData, String refreshedCookies, String redeemCode) {
        String parsedCookie = CookieUtil.parseCookie(refreshedCookies, List.of(
                CookieUtil.COOKIE_TOKEN_V2,
                CookieUtil.ACCOUNT_MID_V2,
                CookieUtil.ACCOUNT_ID_V2,
                CookieUtil.COOKIE_TOKEN,
                CookieUtil.ACCOUNT_ID
        ), true);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String gameRoleId = gameData.getGameRoleId();
        String region = gameData.getRegion();

        Headers headers = HttpUtil.headersBuilderEmpty(List.of("Cookie: %s".formatted(parsedCookie)));

        String url = HttpUtil.urlParamBuilder(REDEEM_API, List.of(
                "t: %s".formatted(timestamp),
                "lang: en",
                "game_biz: nap_global",
                "uid: %s".formatted(gameRoleId),
                "region: %s".formatted(region),
                "cdkey: %s".formatted(redeemCode)
        ));
        return HttpUtil.getRequest(url, headers);
    }
}
