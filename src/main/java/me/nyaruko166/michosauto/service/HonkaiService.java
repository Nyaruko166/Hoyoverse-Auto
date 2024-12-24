package me.nyaruko166.michosauto.service;

import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HonkaiService {

    private final static String ACT_ID = "e202110291205111";
    private final static String SIGN_INFO_API = "https://sg-public-api.hoyolab.com/event/mani/info?act_id=%s".formatted(ACT_ID);
    private final static String SIGN_API = "https://sg-public-api.hoyolab.com/event/mani/sign?act_id=%s".formatted(ACT_ID);

    public String getSignInfo(String cookie) {
        Headers headers = HttpUtil.headersBuilder(List.of(
                "Cookie: %s".formatted(cookie)
        ));

        return HttpUtil.getRequest(SIGN_INFO_API, headers);
    }

    public String signIn(String cookie) {
        Headers headers = HttpUtil.headersBuilder(List.of(
                "Cookie: %s".formatted(cookie)
        ));
        return HttpUtil.postRequest(SIGN_API, HttpUtil.requestBodyBuilder(""), headers);
    }

}
