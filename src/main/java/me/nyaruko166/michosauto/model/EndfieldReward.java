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
public class EndfieldReward {

    @SerializedName("name")
    private String rewardName;

    @SerializedName("count")
    private String rewardCount;

    @SerializedName("icon")
    private String rewardIcon;

}
