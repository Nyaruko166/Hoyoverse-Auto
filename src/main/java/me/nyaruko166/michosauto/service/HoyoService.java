package me.nyaruko166.michosauto.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.nyaruko166.michosauto.model.HoyoAccount;
import me.nyaruko166.michosauto.model.CookieInfo;
import me.nyaruko166.michosauto.model.GameData;
import me.nyaruko166.michosauto.repository.HoyoAccountRepository;
import me.nyaruko166.michosauto.request.AccountRequest;
import me.nyaruko166.michosauto.util.CookieUtil;
import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HoyoService {

    private final String GAME_INFO_API = "https://bbs-api-os.hoyolab.com/game_record/card/wapi/getGameRecordCard?uid=%s";
    private final String HOYOLAB_INFO_API = "https://webapi-os.account.hoyoverse.com/Api/fetch_cookie_accountinfo";

    private Gson gson = new Gson();
    private Logger log = LogManager.getLogger(HoyoService.class);

    @Autowired
    private HoyoAccountRepository hoyoAccountRepository;

    @Autowired
    private ZenlessService zenlessService;

//    @EventListener(ApplicationReadyEvent.class)
//    public void checkIn() {
//        List<Account> accounts = accountRepository.findByType("multiple");
//        accounts.forEach(account -> {
//
//        });
//    }

    public void redeemCode(Map<String, String> params) {
        //Todo act according to game type
        //Todo logging
        HoyoAccount account = hoyoAccountRepository.findById(Integer.valueOf(params.get("id"))); //dummy later change to smt else
        List<GameData> lstGameData = getGameInfo(account.getCookie());
        String refreshedCookies = CookieUtil.getNewCookie(account);
        account.setCookie(refreshedCookies);

        hoyoAccountRepository.save(account);

        for (GameData gameData : lstGameData) {
            Integer gameId = gameData.getGameId();
//            if (gameId == 1 && params.get("type").equalsIgnoreCase(GameType.Honkai3rd))
            if (gameId == 8) {
                String res = zenlessService.redeemCode(gameData, refreshedCookies, params.get("code"));
                System.out.println(res);
            }
        }
    }

    public HoyoAccount addAccount(AccountRequest accountRequest) {
        CookieInfo cookieInfo = getHoyoAccountInfo(accountRequest.getCookie());

        HoyoAccount account = HoyoAccount.builder().id(null).hoyoUid(cookieInfo.getHoyoUid()).hoyoName(cookieInfo.getHoyoName()).accountName(cookieInfo.getAccountName()).codeRedeem(accountRequest.getRedeem()).cookie(accountRequest.getCookie()).type(accountRequest.getType()).build();

        return hoyoAccountRepository.save(account);
    }

    private CookieInfo getHoyoAccountInfo(String cookie) {
        log.info("Getting hoyolab account info");
        Headers headers = HttpUtil.headersBuilder(HttpUtil.HoyoLab, List.of("Cookie: %s".formatted(cookie)));

        JsonObject jsonRes = gson.fromJson(HttpUtil.getRequest(HOYOLAB_INFO_API, headers), JsonObject.class);
        if (jsonRes.get("code").getAsInt() != 200) {
            log.error("Failed to fetch hoyolab account info!! \n {}", jsonRes);
            return null;
        }
        return gson.fromJson(jsonRes.get("data").getAsJsonObject().get("cookie_info").getAsJsonObject(), CookieInfo.class);
    }

    public List<GameData> getGameInfo(String cookie) {
        List<GameData> lstGameData = new ArrayList<>();

        log.info("Getting account info...");
        String uid = CookieUtil.getCookieValue(cookie, CookieUtil.UID);
        Headers headers = HttpUtil.headersBuilder(HttpUtil.HoyoLab, List.of("Cookie: %s".formatted(cookie), "x-rpc-language: en-us"));
        String res = HttpUtil.getRequest(GAME_INFO_API.formatted(uid), headers);
        JsonObject jsonRes = gson.fromJson(res, JsonObject.class);

        if (jsonRes.get("retcode").getAsInt() != 0) {
            log.error("Something went wrong when getting account info.");
            log.error(res);
            return null;
        }

        JsonArray dataArr = jsonRes.get("data").getAsJsonObject().get("list").getAsJsonArray();
        dataArr.forEach(data -> lstGameData.add(gson.fromJson(data, GameData.class)));

        return lstGameData;
    }

    public List<HoyoAccount> getAllAccount() {
        return hoyoAccountRepository.findAll();
    }

    public HoyoAccount updateAcount(HoyoAccount account) {
        return hoyoAccountRepository.save(account);
    }
}
