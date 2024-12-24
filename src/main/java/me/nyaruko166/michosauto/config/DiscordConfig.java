package me.nyaruko166.michosauto.config;

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
public class DiscordConfig {

    private boolean active;

    private String discordToken;

    private String guildId;

    private String channelId;

}
