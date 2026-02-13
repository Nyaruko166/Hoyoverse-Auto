package me.nyaruko166.michosauto.model;

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

    private String rewardName;

    private String rewardCount;

    private String rewardIcon;

}
