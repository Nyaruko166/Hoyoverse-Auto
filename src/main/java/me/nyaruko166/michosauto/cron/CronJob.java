package me.nyaruko166.michosauto.cron;

import lombok.extern.log4j.Log4j2;
import me.nyaruko166.michosauto.discord.Bot;
import me.nyaruko166.michosauto.model.EndfieldReward;
import me.nyaruko166.michosauto.model.SkportAccount;
import me.nyaruko166.michosauto.service.HoyoService;
import me.nyaruko166.michosauto.service.SkportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
@Log4j2
public class CronJob {

    @Autowired
    private HoyoService hoyoService;

    @Autowired
    private SkportService skportService;

    @Autowired
    private Bot bot;

//    @Scheduled(cron = "0 0 */2 * * *")
//    public void runEvery2Hours(){
//
//    }

    @Scheduled(cron = "0 0 7 * * ?", zone = "GMT+7")
    public void runOnDailyReset() {
//        List<AccountData> lstAccount = hoyoService.getGameInfo();
        log.info("Starting daily check-in for endfield account");
        List<SkportAccount> lstSkAccount = skportService.getAllAccount();
        for (SkportAccount account : lstSkAccount) {
            List<EndfieldReward> lstReward = skportService.claimAttendance(account);
            bot.sendRewardInfo(lstReward, account.getOwnerDiscordId(), account.getSkGameRole());
            log.info("Daily check-in for account uid: {} completed", account.getSkGameRole().split("_")[1]);
        }
    }

//    private void updateCookie(){
//        List<HoyoAccount> lstAccounts = hoyoService.getAllAccount();
//        for(HoyoAccount account : lstAccounts){
//            String refreshedCookie = CookieUtil.getNewCookie(account);
//            account.setCookie(refreshedCookie);
//            hoyoService.updateAcount(account);
//            log.info("Cookie for {} updated");
//        }
//    }
}
