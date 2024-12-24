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
public class AccountConfig {

    private boolean active;

    private String type;

    private List<data> data;

    public record data(String cookie, boolean checkIn, boolean redeemCode) {
    }

}
