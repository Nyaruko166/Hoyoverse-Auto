package me.nyaruko166.michosauto.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.nyaruko166.michosauto.model.AccountData;
import me.nyaruko166.michosauto.util.CookieUtil;
import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HoyoService {

    private final String ACCOUNT_INFO_API = "https://bbs-api-os.hoyolab.com/game_record/card/wapi/getGameRecordCard?uid=%s";

    Logger log = LogManager.getLogger(HoyoService.class);

    public List<AccountData> getAccountInfo(String cookie) {
        Gson gson = new Gson();
        List<AccountData> lstAccountData = new ArrayList<>();

        log.info("Getting account info...");
        String uid = CookieUtil.getCookieValue(cookie, CookieUtil.UID);
        Headers headers = HttpUtil.headersBuilder(List.of("Cookie: %s".formatted(cookie), "x-rpc-language: en-us"));
        String res = HttpUtil.getRequest(ACCOUNT_INFO_API.formatted(uid), headers);
        JsonObject jsonRes = gson.fromJson(res, JsonObject.class);

        if (jsonRes.get("retcode").getAsInt() != 0) {
            log.error("Something went wrong when getting account info.");
            log.error(res);
            return null;
        }

        JsonArray dataArr = jsonRes.get("data").getAsJsonObject().get("list").getAsJsonArray();
        dataArr.forEach(data -> lstAccountData.add(gson.fromJson(data, AccountData.class)));

        return lstAccountData;
    }
}
