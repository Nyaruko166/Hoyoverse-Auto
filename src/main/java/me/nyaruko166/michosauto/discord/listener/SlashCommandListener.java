package me.nyaruko166.michosauto.discord.listener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.nyaruko166.michosauto.model.SkportAccount;
import me.nyaruko166.michosauto.service.SkportService;
import me.nyaruko166.michosauto.util.GeneralUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;

@Component
public class SlashCommandListener extends ListenerAdapter {

    @Autowired
    private SkportService skportService;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        Gson gson = new Gson();
        switch (event.getName()) {
            case "add" -> {
                String subCommandName = event.getSubcommandName();
                if (subCommandName.equals("skport")) {
                    String cred = event.getOption("cred").getAsString();
                    String skGameRole = event.getOption("sk_game_role").getAsString();
                    String authorTag = event.getUser().getAsTag();
                    String authorId = event.getUser().getId();
                    SkportAccount skportAccount = SkportAccount.builder()
                                                               .id(null)
                                                               .cred(cred)
                                                               .SkGameRole(skGameRole)
                                                               .ownerDiscordId(authorId)
                                                               .ownerName(authorTag)
                                                               .build();
                    if (skportService.addAccount(skportAccount).getId() != null) {
                        event.reply("Endfield account saved successfully.").queue();
                    } else {
                        event.reply("Failed to save endfield account.").queue();
                    }
                }
            }
            case "claim" -> {
                String gameType = event.getOption("game").getAsString();
                String authorId = event.getUser().getId();
                if (gameType.equals("all")) {
                } else if (gameType.equals("endfield")) {
                    List<SkportAccount> lstAccounts = skportService.getAccountsByDiscordId(authorId);
                    for (SkportAccount account : lstAccounts) {
                        String resData = skportService.claimAttendance(account);
                        JsonObject jsonRes = gson.fromJson(resData, JsonObject.class);
                        JsonArray awardIds = jsonRes.get("awardIds").getAsJsonArray();
                        JsonObject resourceMap = jsonRes.get("resourceInfoMap").getAsJsonObject();
                        for (int i = 0; i < awardIds.size(); i++) {
                            String awardId = awardIds.get(i).getAsJsonObject().get("id").getAsString();
                            String rewardName = resourceMap.get(awardId).getAsJsonObject().get("name").getAsString();
                            String rewardCount = resourceMap.get(awardId).getAsJsonObject().get("count").getAsString();
                            String rewardIcon = resourceMap.get(awardId).getAsJsonObject().get("icon").getAsString();
                            event.replyEmbeds(
                                    new EmbedBuilder()
                                            .setColor(Color.GREEN)
                                            .setFooter(GeneralUtil.getDiscordTimeStamp(event))
                                            .setTitle("Manual check in for Endfield completed !!")
                                            .setThumbnail(SkportService.endfieldIcon)
                                            .addField("Reward Name: %s".formatted(rewardName), "", false)
                                            .addField("Quantity: %s".formatted(rewardCount), "", true)
                                            .setImage(rewardIcon)
                                            .build()
                            ).queue();
                        }
                    }
                } else if (gameType.equals("zzz")) {
                } else if (gameType.equals("hsr")) {
                } else if (gameType.equals("hi3")) {
                } else {
                } //Genshit impacc
            }
            case "test" -> {
                event.replyEmbeds(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setFooter(GeneralUtil.getDiscordTimeStamp(event))
                        .setTitle("Manual check in for Endfield completed !!")
                        .setThumbnail(SkportService.endfieldIcon)
                        .addField("Reward Name: Intermediate Combat Record", "Intermediate Combat Record", false)
                        .addField("Quantity: %s".formatted("3"), "", true)
                        .setImage("https://static.skport.com/asset/endfield_attendance/0dea0bc0fd87138df322e8a254a6999f.png")
                        .build()).queue();
            }
//            case "leave" -> {
//                event.reply("I'm leaving the server now!").setEphemeral(true) // this message is only visible to the command user
//                     .flatMap(m -> event.getGuild().leave()) // append a follow-up action using flatMap
//                     .queue(); // enqueue both actions to run in sequence (send message -> leave guild)
//            }
            default -> {
                return;
            }
        }
    }

}
