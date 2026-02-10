package me.nyaruko166.michosauto.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AppConfig {

    private String discord_token;

    public static AppConfig configTemplate() {
        return AppConfig.builder()
                        .discord_token("")
                        .build();
    }
}
