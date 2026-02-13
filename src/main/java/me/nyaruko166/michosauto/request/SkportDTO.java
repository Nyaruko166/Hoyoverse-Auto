package me.nyaruko166.michosauto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.nyaruko166.michosauto.model.EndfieldReward;
import okhttp3.Headers;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SkportDTO {

    private String skGameRole;

    private EndfieldReward lastReward;

    private Headers headers;

    private Boolean hasCheckIn;

}
