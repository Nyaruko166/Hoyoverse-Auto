package me.nyaruko166.michosauto.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CookieInfo {

    @SerializedName("account_id")
    private String accountId;

    @SerializedName("account_name")
    private String accountName;

    @SerializedName("cookie_token")
    private String cookieToken;

    @SerializedName("cur_time")
    private Long currentTime;

    @SerializedName("snoy_name")
    private String hoyoName;
}
