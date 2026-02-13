package me.nyaruko166.michosauto.discord;

import jakarta.annotation.PostConstruct;
import me.nyaruko166.michosauto.config.Config;
import me.nyaruko166.michosauto.discord.listener.SlashCommandListener;
import me.nyaruko166.michosauto.model.EndfieldReward;
import me.nyaruko166.michosauto.model.SkportAccount;
import me.nyaruko166.michosauto.service.SkportService;
import me.nyaruko166.michosauto.util.GeneralUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
public class Bot {

    static Logger log = LogManager.getLogger(Bot.class);

    private static final String DISCORD_TOKEN = Config.getProperty().getDiscord_token();
    public static JDA jda;

    private final SlashCommandListener slashCommandListener;

    public Bot(SlashCommandListener slashCommandListener) {
        this.slashCommandListener = slashCommandListener;
    }

    @PostConstruct
    public void runBot() {
        log.info("Bot is starting...");
        jda = JDABuilder.createLight(DISCORD_TOKEN, EnumSet.of(GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT))
                        .setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "Just a random bot passing through."))
                        .addEventListeners(slashCommandListener)
                        .build();

        SubcommandData skportCommand = new SubcommandData("skport", "Add a new endfield account to the bot.")
                .addOption(OptionType.STRING, "account_token", "The account token of the account.")
                .addOption(OptionType.STRING, "sk_game_role", "The game role (3_UID_SERVER-ID) of the account.");


        CommandListUpdateAction commands = jda.updateCommands();
        commands.addCommands(
                Commands.slash("help", "Show useless help message."),
                Commands.slash("add", "Add a new account to the bot.")
                        .addSubcommands(skportCommand),
                Commands.slash("claim", "Start check in game manually.")
                        .addOptions(new OptionData(OptionType.STRING, "game", "Choose the game to check in.", true)
                                .addChoice("All", "all")
                                .addChoice("Endfield", "endfield")
                                .addChoice("Zenless Zone Zero", "zzz")
                                .addChoice("Honkai Star Rail", "hsr")
                                .addChoice("Honkai Impact 3", "hi3")
                                .addChoice("Genshin Impact", "gi")
                        ),
                Commands.slash("guide", "Guide to get required information for adding account.")
                        .addOptions(new OptionData(OptionType.STRING, "platform", "Choose the platform to get help.", true)
                                .addChoice("Hoyoverse | Michos | Cognosphere", "michos")
                                .addChoice("Skport", "skport")
                        )
        ).queue();
    }

    public void sendRewardInfo(List<EndfieldReward> lstReward, SkportAccount account) {
        jda.openPrivateChannelById(account.getOwnerDiscordId()).queue(privateChannel -> {
            List<MessageEmbed> lstEmbed = new ArrayList<>();
            for (EndfieldReward reward : lstReward) {
                lstEmbed.add(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setFooter(GeneralUtil.getDiscordTimeStamp(jda))
                        .setTitle(lstReward.indexOf(reward) == 0 ?
                                "Daily check in for account uid: %s completed !!"
                                        .formatted(account.getSkGameRole().split("_")[1]) : "Gift: #%s"
                                .formatted(lstReward.indexOf(reward) + 1))
                        .setThumbnail(SkportService.endfieldIcon)
                        .addField("Check in rewards:", "%s x%s"
                                .formatted(reward.getRewardName(), reward.getRewardCount()), false)
                        .setImage(reward.getRewardIcon())
                        .build());
            }
            privateChannel.sendMessageEmbeds(lstEmbed).queue();
        });
    }

    public void sendEmbedMessage(EndfieldReward reward, SkportAccount account) {
        jda.openPrivateChannelById(account.getOwnerDiscordId()).queue(privateChannel -> {
            privateChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setFooter(GeneralUtil.getDiscordTimeStamp(jda))
                    .setTitle("Stopped auto check-in since you already check in today !!")
                    .setThumbnail(SkportService.endfieldIcon)
                    .addField("Check in rewards:", "%s x%s"
                            .formatted(reward.getRewardName(),
                                    reward.getRewardCount()), false)
                    .setImage(reward.getRewardIcon())
                    .build()).queue();
        });
    }
}
