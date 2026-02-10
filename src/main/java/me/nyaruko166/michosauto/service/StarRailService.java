package me.nyaruko166.michosauto.service;

import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StarRailService {

    private final String ACT_ID = "e202303301540311";
    private final String SIGN_INFO_API = "https://sg-public-api.hoyolab.com/event/luna/os/info?act_id=%s".formatted(ACT_ID);
    private final String SIGN_API = "https://sg-public-api.hoyolab.com/event/luna/os/sign?act_id=%s".formatted(ACT_ID);
    private final String REDEEM_API = "https://sg-hkrpg-api.hoyoverse.com/common/apicdkey/api/webExchangeCdkeyRisk";

    //{"retcode":0,"message":"OK","data":{"total_sign_day":15,"today":"2024-12-16","is_sign":true,"is_sub":true,"region":"prod_official_asia","sign_cnt_missed":1,"short_sign_day":0,"send_first":false}}
    public String getSignInfo(String cookie) {
        Headers headers = HttpUtil.headersBuilder(HttpUtil.HoyoLab, List.of(
                "Cookie: %s".formatted(cookie),
                "x-rpc-signgame: hkrpg"
        ));

        return HttpUtil.getRequest(SIGN_INFO_API, headers);
    }

    //{"data":null,"message":"You've already checked in today, Trailblazer~","retcode":-5003} if already checked in
    public String signIn(String cookie) {
        Headers headers = HttpUtil.headersBuilder(HttpUtil.HoyoLab, List.of(
                "Cookie: %s".formatted(cookie),
                "x-rpc-signgame: hkrpg"
        ));
        return HttpUtil.postRequest(SIGN_API, HttpUtil.requestBodyBuilder(""), headers);
    }

    public String redeemCode(String cookie, String redeemCode) {
        return null;
    }
}
