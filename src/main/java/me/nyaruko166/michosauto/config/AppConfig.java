package me.nyaruko166.michosauto.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AppConfig {

    private String discordToken;

    private String guildId;

    private String channelId;

    private List<AccountConfig> accounts;

}
