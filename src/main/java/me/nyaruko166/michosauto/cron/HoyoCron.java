package me.nyaruko166.michosauto.cron;

import de.marhali.json5.Json5Object;
import me.nyaruko166.michosauto.config.Config;
import me.nyaruko166.michosauto.service.HoyoService;
import me.nyaruko166.michosauto.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableScheduling
public class HoyoCron {

    @Autowired
    private HoyoService hoyoService;

    @Scheduled(cron = "0 0 */2 * * *")
    public void runEvery2Hours(){

    }

    @Scheduled(cron = "0 0 3 * * ?", zone = "GMT+7")
    public void runOnDailyReset() {
//        List<AccountData> lstAccount = hoyoService.getAccountInfo();
    }

    private void updateCookie(){
        Json5Object configJson = Config.getRawProperty();
        configJson.get("accounts").getAsJson5Array().forEach(accounts -> {
            if (accounts.getAsJson5Object().get("active").getAsBoolean()){
                accounts.getAsJson5Object().get("data").getAsJson5Array().forEach(data ->{
                    if (!data.getAsJson5Object().get("cookie").getAsString().isBlank()){
                        String oldCookie = data.getAsJson5Object().get("cookie").getAsString();
                        Map<String,String> refreshCookie =
                                CookieUtil.getNewCookie(oldCookie);
//                        data.
                    }
                });
            };
        });
    }
}
