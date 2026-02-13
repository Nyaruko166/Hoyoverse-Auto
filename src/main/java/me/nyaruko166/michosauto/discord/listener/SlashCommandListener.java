package me.nyaruko166.michosauto.discord.listener;

import lombok.extern.slf4j.Slf4j;
import me.nyaruko166.michosauto.model.EndfieldReward;
import me.nyaruko166.michosauto.model.SkportAccount;
import me.nyaruko166.michosauto.request.SkportDTO;
import me.nyaruko166.michosauto.service.SkportService;
import me.nyaruko166.michosauto.util.GeneralUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class SlashCommandListener extends ListenerAdapter {

    @Autowired
    private SkportService skportService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        log.info("Slash command '{}' invoked by '{}'", event.getName(), event.getUser().getAsTag());
        switch (event.getName()) {
            case "add" -> {
                String subCommandName = event.getSubcommandName();
                if (subCommandName.equals("skport")) {
                    String encodedCred = event.getOption("account_token").getAsString();
                    String decodedCred = GeneralUtil.decodeUrl(encodedCred);
                    String skGameRole = event.getOption("sk_game_role").getAsString();
                    String authorTag = event.getUser().getAsTag();
                    String authorId = event.getUser().getId();
                    SkportAccount skportAccount = SkportAccount.builder()
                                                               .id(null)
                                                               .cred(decodedCred)
                                                               .SkGameRole(skGameRole)
                                                               .ownerDiscordId(authorId)
                                                               .ownerName(authorTag)
                                                               .build();
                    if (skportService.addAccount(skportAccount).getId() != null) {
                        log.info("Endfield account uid: {} saved successfully by {}.", GeneralUtil.getUid(skportAccount.getSkGameRole()), authorTag);
                        event.reply("Endfield account saved successfully.").setEphemeral(true).queue();
                    } else {
                        log.error("Failed to save endfield account by {}.", authorTag);
                        event.reply("Failed to save endfield account.").setEphemeral(true).queue();
                    }
                }
            }
            case "claim" -> {
                String gameType = event.getOption("game").getAsString();
                String authorId = event.getUser().getId();
                switch (gameType) {
                    case "all" -> {
                    }
                    case "endfield" -> {
                        List<SkportAccount> lstAccounts = skportService.getAccountsByDiscordId(authorId);
                        deleteOriginalMessage(event);
                        for (SkportAccount account : lstAccounts) {
                            String uid = GeneralUtil.getUid(account.getSkGameRole());
                            log.info("Starting manual check-in for account uid: {}", uid);
                            SkportDTO skportDTO = skportService.attendanceCheck(account);
                            if (skportDTO.getHasCheckIn()) {
                                log.info("Stopped manual check-in since Account uid: {} already checked in today.", uid);
                                sendEmbedMessageToChannel(event, new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setFooter(GeneralUtil.getDiscordTimeStamp(event))
                                        .setTitle("You already check in today !!")
                                        .setThumbnail(SkportService.endfieldIcon)
                                        .addField("Check in rewards:", "%s x%s"
                                                .formatted(skportDTO.getLastReward().getRewardName(),
                                                        skportDTO.getLastReward().getRewardCount()), false)
                                        .setImage(skportDTO.getLastReward().getRewardIcon())
                                        .build());
                            } else {
                                List<EndfieldReward> rewards = skportService.claimAttendance(skportDTO);
                                List<MessageEmbed> lstEmbed = new ArrayList<>();

                                for (EndfieldReward reward : rewards) {
                                    lstEmbed.add(new EmbedBuilder()
                                            .setColor(Color.GREEN)
                                            .setFooter(GeneralUtil.getDiscordTimeStamp(event))
                                            .setTitle(rewards.indexOf(reward) == 0 ?
                                                    "Manual check in for account uid: %s completed !!"
                                                            .formatted(uid) : "Gift: #%s".formatted(rewards.indexOf(reward) + 1))
                                            .setThumbnail(SkportService.endfieldIcon)
                                            .addField("Check in rewards:", "%s x%s"
                                                    .formatted(reward.getRewardName(), reward.getRewardCount()), false)
                                            .setImage(reward.getRewardIcon())
                                            .build());
                                }
                                sendEmbedMessageToChannel(event, lstEmbed);
                                log.info("Manual check-in for account uid: {} completed", uid);
                            }
                        }
                    }
                    case "zzz" -> {
                    }
                    case "hsr" -> {
                    }
                    case "hi3" -> {
                    }
                }
            }
            case "guide" -> {
                String platform = event.getOption("platform").getAsString();
                if (platform.equalsIgnoreCase("skport")) {
                    MessageEmbed credEb = new EmbedBuilder()
                            .setColor(Color.CYAN)
                            .setFooter(GeneralUtil.getDiscordTimeStamp(event))
                            .setTitle("Guide to get required information to add skport account.")
                            .setDescription("""
                                    To get account_token of your account:
                                    
                                    1. Log in to the [SKPort Endfield Portal](https://game.skport.com/endfield/sign-in)
                                    2. Open **Developer Tools** (F12) or (Ctrl Shift C) → **Application** → **Cookies**
                                    3. Find `ACCOUNT_TOKEN` and copy its value (If doesn't exist, refresh the page)
                                    4. Check the URL decode box if needed (replace `%2F` with `/`)
                                    5. Add to /add skport as `account_token`
                                    """)
                            .setImage("https://i.postimg.cc/JhpMH4VG/accounttoken.png")
                            .build();
                    MessageEmbed skroleEb = new EmbedBuilder()
                            .setColor(Color.CYAN)
                            .setFooter(GeneralUtil.getDiscordTimeStamp(event))
                            .setTitle("Guide to get required information to add skport account.")
                            .setDescription("""
                                    To get sk_game_role of your account:
                                    
                                    1. Log in to the [SKPort Endfield Portal](https://game.skport.com/endfield/sign-in)
                                    2. Open **Developer Tools** (F12) or (Ctrl Shift C) → **Network**
                                    3. Find request named `attendance` and look for sk-game-role in the headers
                                    4. Add to `/add skport` as `sk_game_role`
                                    
                                    Or just put your in game uid in "3_uid_2"
                                    "2" is the id of Asia server idk about other servers.
                                    """)
                            .setImage("https://i.postimg.cc/4xJ3Y07D/skgamerole.png")
                            .build();
                    event.replyEmbeds(credEb, skroleEb)
                         .setEphemeral(true).queue();
                } else {
                    //Michos case
                }
            }
            case "help" -> {
                event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setFooter(GeneralUtil.getDiscordTimeStamp(event))
                        .setTitle("Gacha Slave - Help")
                        .setDescription("Simple bot that make your gacha life daily easier.")
                        .addField(
                                "/add skport",
                                """
                                        Register your SKPort Endfield account. (Also should use in private message, don't use on public channel)
                                        
                                        Options:
                                        • `account_token` – Your SKPort account token
                                        • `sk_game_role` – Your game role (example: 3_your-in-game-uid_your-server-id)
                                        
                                        Example:
                                        `/add skport account_token:<token> sk_game_role:<3_xxxxxxxxxxxxx_2>`
                                        """,
                                false
                        )
                        .addField(
                                "/claim",
                                """
                                        Manually claim daily check in rewards
                                        for all accounts linked to your Discord.
                                        
                                        Options:
                                        • `game` (example: Endfield)
                                        """,
                                false
                        )
                        .addField(
                                "/guide",
                                "Guide to get required information for adding account.",
                                false
                        )
                        .addField(
                                "/help",
                                "Show this useless help message.",
                                false
                        )
                        .setImage("https://i.postimg.cc/qqS7hBKc/endmin-thumb-up.png")
                        .build()).setEphemeral(true).queue();
            }
            default -> {
                return;
            }
        }
    }

    private void sendEmbedMessageToChannel(SlashCommandInteractionEvent event, MessageEmbed embedMessage) {
        event.getChannel().sendMessageEmbeds(embedMessage).queue();
    }

    private void sendEmbedMessageToChannel(SlashCommandInteractionEvent event, Collection<MessageEmbed> embedMessage) {
        event.getChannel().sendMessageEmbeds(embedMessage).queue();
    }

    private void deleteOriginalMessage(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            hook.deleteOriginal().queue();
        });
    }
}
