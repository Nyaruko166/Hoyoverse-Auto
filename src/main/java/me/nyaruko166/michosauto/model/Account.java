package me.nyaruko166.michosauto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "hoyo_uid", length = 50)
    private String hoyoUid;

    @Column(name = "hoyo_name", length = 50)
    private String hoyoName;

    @Column(name = "account_name", length = 50)
    private String accountName;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "code_redeem")
    private Boolean codeRedeem;

    @Column(name = "cookie")
    private String cookie;

    @Column(name = "discord_id")
    private String discordId;
}
