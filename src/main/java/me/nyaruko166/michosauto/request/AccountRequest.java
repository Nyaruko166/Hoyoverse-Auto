package me.nyaruko166.michosauto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AccountRequest {

    @NotNull
    private String cookie;

    @NotNull
    private String type;

    @NotNull
    private Boolean redeem;

    private String discordId;
}
