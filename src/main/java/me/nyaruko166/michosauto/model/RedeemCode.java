package me.nyaruko166.michosauto.model;

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
public class RedeemCode {

    private List<Code> activeCode;

    private List<Code> inactiveCode;

    public record Code(String cdkey, List<String> rewards){}

}
