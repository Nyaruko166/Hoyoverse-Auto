package me.nyaruko166.michosauto.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.nyaruko166.michosauto.model.EndfieldReward;
import me.nyaruko166.michosauto.model.SkportAccount;
import me.nyaruko166.michosauto.repository.SkportAccountRepository;
import me.nyaruko166.michosauto.request.SkportDTO;
import me.nyaruko166.michosauto.util.GeneralUtil;
import me.nyaruko166.michosauto.util.HttpUtil;
import okhttp3.Headers;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Log4j2
@Service
public class SkportService {

    public static String platform = "3";
    public static String vName = "1.0.0";
    static String signInUrl = "/web/v1/game/endfield/attendance";
    static String baseUrl = "https://zonai.skport.com";
    static String grantTokenUrl = "https://as.gryphline.com/user/oauth2/v2/grant";
    static String grantCredUrl = "https://zonai.skport.com/web/v1/user/auth/generate_cred_by_code";
    public static String endfieldIcon = "https://play-lh.googleusercontent.com/l6FVNa293RykBWy88TqEhUakIcGSC8bRygSnKOBgztln48JX-WzMWnrBAETrKZsxDNC4HhwCsvfle_UI7rBE=s48-rw";

    @Autowired
    private SkportAccountRepository skportAccountRepository;

    private Gson gson = new Gson();

    public SkportAccount addAccount(SkportAccount skportAccount) {
        return skportAccountRepository.save(skportAccount);
    }

    public List<SkportAccount> getAccountsByDiscordId(String discordId) {
        return skportAccountRepository.findAllByOwnerDiscordId(discordId);
    }

    public List<SkportAccount> getAllAccount() {
        return skportAccountRepository.findAll();
    }

    public List<EndfieldReward> claimAttendance(SkportDTO skportDTO) {
        if (skportDTO.getHasCheckIn()) {
            return List.of(skportDTO.getLastReward());
        }
        String res = HttpUtil.postRequest(baseUrl + signInUrl, HttpUtil.requestBodyBuilder(""), skportDTO.getHeaders());
        JsonObject jsonRes = gson.fromJson(res, JsonObject.class);
        if (jsonRes.get("code").getAsInt() == 0) {
            JsonObject resData = jsonRes.get("data").getAsJsonObject();
            JsonArray rewardsArr = resData.get("awardIds").getAsJsonArray();
            JsonObject resourceMap = resData.get("resourceInfoMap").getAsJsonObject();
            List<EndfieldReward> rewards = new ArrayList<>();
            for (int i = 0; i < rewardsArr.size(); i++) {
                String awardId = rewardsArr.get(i).getAsJsonObject().get("id").getAsString();
                rewards.add(gson.fromJson(resourceMap.get(awardId).toString(), EndfieldReward.class));
            }
            return rewards;
        }
        return null;
    }

    public SkportDTO attendanceCheck(SkportAccount skportAccount) {
        Map<String, String> mapData = oAuthFlow(skportAccount.getCred());
        String timeStamp = GeneralUtil.getTimestamp();
        String sign = generateSignV2(timeStamp, mapData.get("token"));
        List<String> headerStr = HttpUtil.endFieldHeaders(skportAccount.getSkGameRole(), mapData.get("grantCred"), timeStamp, sign);
        Headers endfieldHeader = HttpUtil.headersBuilder(HttpUtil.Skport, headerStr);
        String res = HttpUtil.getRequest(baseUrl + signInUrl, endfieldHeader);
        JsonObject jsonRes = gson.fromJson(res, JsonObject.class);
        if (jsonRes.get("code").getAsInt() == 0) {
            Boolean hasCheckIn = jsonRes.get("data").getAsJsonObject().get("hasToday").getAsBoolean();
            JsonArray calendar = jsonRes.getAsJsonObject("data").getAsJsonArray("calendar");
            String todayRewardId = "";
            for (int i = calendar.size() - 1; i >= 0; i--) {
                JsonObject reward = calendar.get(i).getAsJsonObject();
                if (reward.get("done").getAsBoolean()) {
                    todayRewardId = reward.get("awardId").getAsString();
                    break;
                }
            }
            JsonObject resourceInfoMap = jsonRes.getAsJsonObject("data").getAsJsonObject("resourceInfoMap");
            EndfieldReward reward = gson.fromJson(resourceInfoMap.getAsJsonObject(todayRewardId).toString(), EndfieldReward.class);
            return SkportDTO.builder().hasCheckIn(hasCheckIn).headers(endfieldHeader).skGameRole(skportAccount.getSkGameRole()).lastReward(reward).build();
        }
        return null;
    }

    private Map<String, String> oAuthFlow(String cred) {
        String code = grantCode(cred);
        return grantCred(code);
    }

    private String grantCode(String cred) {
        RequestBody body = HttpUtil.requestBodyBuilder("{\"token\":\"%s\",\"appCode\":\"6eb76d4e13aa36e6\",\"type\":0}"
                .formatted(cred));
        Headers headers = HttpUtil.headersBuilder(null, List.of(
                "Content-Type: application/json", "Accept: application/json"
        ));
        String res = HttpUtil.postRequest(grantTokenUrl, body, headers);
        JsonObject jsonRes = gson.fromJson(res, JsonObject.class);
        if (jsonRes.get("status").getAsInt() == 0) {
            return jsonRes.get("data").getAsJsonObject().get("code").getAsString();
        }
        log.error("Failed to get token from grant token url: {}", jsonRes);
        return null;
    }

    private Map<String, String> grantCred(String code) {
        RequestBody body = HttpUtil.requestBodyBuilder(("{\"code\":\"%s\", \"kind\":1}".formatted(code)));
        Headers headers = HttpUtil.headersBuilder(null, List.of("Content-Type: application/json",
                "Accept: application/json",
                "platform: %s".formatted(platform),
                "Referer: https://www.skport.com/",
                "Origin: https://www.skport.com"));
        String res = HttpUtil.postRequest(grantCredUrl, body, headers);
        JsonObject jsonRes = gson.fromJson(res, JsonObject.class);
        if (jsonRes.get("code").getAsInt() == 0) {
            Map<String, String> mapData = new HashMap<>();
            String grantCred = jsonRes.get("data").getAsJsonObject().get("cred").getAsString();
            String token = jsonRes.get("data").getAsJsonObject().get("token").getAsString();
            mapData.put("grantCred", grantCred);
            mapData.put("token", token);
            return mapData;
        }
        log.error("Failed to get cred from grant cred url: {}", jsonRes);
        return null;
    }

    private String generateSignV2(String timestamp, String token) {
        String headerJson = "{\"platform\":\"%s\",\"timestamp\":\"%s\",\"dId\":\"\",\"vName\":\"%s\"}"
                .formatted(platform, timestamp, vName);
        String str = signInUrl + timestamp + headerJson;
        String hmacStr = GeneralUtil.hmacSha256Hex(str, token);
        return GeneralUtil.hashMD5(hmacStr);
    }

    private String generateSignV1(String timestamp, String cred) {
        String hashStr = "timestamp=%s&cred=%s".formatted(timestamp, cred);
        return GeneralUtil.hashMD5(hashStr);
    }
}
