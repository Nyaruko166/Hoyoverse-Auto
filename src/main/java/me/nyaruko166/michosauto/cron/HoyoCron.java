package me.nyaruko166.michosauto.cron;

import me.nyaruko166.michosauto.model.HoyoAccount;
import me.nyaruko166.michosauto.service.HoyoService;
import me.nyaruko166.michosauto.util.CookieUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class HoyoCron {

    private Logger log = LogManager.getLogger(HoyoCron.class);

    @Autowired
    private HoyoService hoyoService;

    @Scheduled(cron = "0 0 */2 * * *")
    public void runEvery2Hours(){

    }

    @Scheduled(cron = "0 0 3 * * ?", zone = "GMT+7")
    public void runOnDailyReset() {
//        List<AccountData> lstAccount = hoyoService.getGameInfo();
    }

    private void updateCookie(){
        List<HoyoAccount> lstAccounts = hoyoService.getAllAccount();
        for(HoyoAccount account : lstAccounts){
            String refreshedCookie = CookieUtil.getNewCookie(account);
            account.setCookie(refreshedCookie);
            hoyoService.updateAcount(account);
            log.info("Cookie for {} updated");
        }
    }
}
