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
public class GameData {

    @SerializedName("game_id")
    private Integer gameId;

    @SerializedName("game_name")
    private String gameName;

    //In game UID
    @SerializedName("game_role_id")
    private String gameRoleId;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("level")
    private Integer level;

    @SerializedName("region")
    private String region;

    @SerializedName("region_name")
    private String regionName;
}
