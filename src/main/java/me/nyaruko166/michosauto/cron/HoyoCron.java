package me.nyaruko166.michosauto.cron;

import me.nyaruko166.michosauto.model.AccountData;
import me.nyaruko166.michosauto.service.HoyoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class HoyoCron {

    @Autowired
    private HoyoService hoyoService;

    @Scheduled(cron = "0 0 3 * * ?", zone = "GMT+7")
    public void runOnDailyReset() {
//        List<AccountData> lstAccount = hoyoService.getAccountInfo();
    }

}
