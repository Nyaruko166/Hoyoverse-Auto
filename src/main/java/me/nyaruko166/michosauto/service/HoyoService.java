package me.nyaruko166.michosauto.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.nyaruko166.michosauto.model.Account;
import me.nyaruko166.michosauto.model.CookieInfo;
import me.nyaruko166.michosauto.model.GameData;
import me.nyaruko166.michosauto.repository.AccountRepository;
import me.nyaruko166.michosauto.request.AccountRequest;
import me.nyaruko166.michosauto.util.CookieUtil;
import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HoyoService {

    private final String GAME_INFO_API = "https://bbs-api-os.hoyolab.com/game_record/card/wapi/getGameRecordCard?uid=%s";
    private final String HOYOLAB_INFO_API = "https://webapi-os.account.hoyoverse.com/Api/fetch_cookie_accountinfo";

    private Gson gson = new Gson();
    private Logger log = LogManager.getLogger(HoyoService.class);

    @Autowired
    private AccountRepository accountRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void checkIn(){
        List<Account> accounts = accountRepository.findByType("multiple");
        accounts.forEach(account -> {

        });
    }

    public Account addAccount(AccountRequest accountRequest) {
        CookieInfo cookieInfo = getHoyoAccountInfo(accountRequest.getCookie());

        Account account = Account.builder()
                                 .id(null)
                                 .accountId(cookieInfo.getAccountId())
                                 .codeRedeem(accountRequest.getRedeem())
                                 .cookie(accountRequest.getCookie())
                                 .type(accountRequest.getType())
                                 .build();

        return accountRepository.save(account);
    }

    public CookieInfo getHoyoAccountInfo(String cookie) {
        log.info("Getting hoyolab account info");
        Headers headers = HttpUtil.headersBuilder(List.of(
                "Cookie: %s".formatted(cookie)
        ));

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
        Headers headers = HttpUtil.headersBuilder(List.of("Cookie: %s".formatted(cookie), "x-rpc-language: en-us"));
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

//    public static void main(String[] args) {
//
//        AccountConfig multipleGameAccount = Config.getProperty().getAccounts()
//                                                  .stream().filter(account -> account.isActive() && account.getType().equalsIgnoreCase("multiple"))
//                                                  .findAny().orElse(null);
//
//        CookieInfo cookieInfo = new HoyoService().getHoyoAccountInfo(multipleGameAccount.getData().get(0).cookie());
//
//        System.out.println(cookieInfo.toString());
//    }
}
