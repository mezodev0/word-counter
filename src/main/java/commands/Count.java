package commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import models.Channel;
import models.Guild;
import models.Message;
import models.Root;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Count extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] messageArray = event.getMessage().getContentRaw().split("\\s+", 2);
        if (messageArray[0].equals(".count")) {
            AtomicInteger counter = new AtomicInteger();
            if (messageArray.length != 2) {
                event.getChannel().sendMessage(".count [search]").queue();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            String jsonAsString = null;

            try {
                jsonAsString = Files.asCharSource(new File("src/output.json"), Charsets.UTF_8).read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonAsString == null) {
                event.getChannel().sendMessage("There was an error reading the JSON file").queue();
                return;
            }

            Root rootJson = null;

            try {
                try {
                    rootJson = mapper.readValue(jsonAsString, Root.class);
                } catch (MismatchedInputException e) {
                    rootJson = new Root();
                    // e.printStackTrace();
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if (rootJson == null) {
                event.getChannel().sendMessage("There was an error while trying to create the json object").queue();
                return;
            }

            boolean guildExists = false;

            if (rootJson.guilds == null) {
                rootJson.guilds = new ArrayList<>();
            }

            for (Guild guild : rootJson.guilds) {
                if (guild.getGuildId() == event.getGuild().getIdLong()) {
                    guildExists = true;
                    break;
                }
            }

            if (!guildExists) {
                rootJson.guilds.add(new Guild(event.getGuild().getIdLong()));
            }

            List<TextChannel> channels = event.getGuild().getTextChannels();

            for (TextChannel channel : channels) {
                Guild g = rootJson.getGuildById(event.getGuild().getIdLong());
                if (Arrays.asList(g.getBlacklist() == null ? new String[]{""} : g.getBlacklist()).contains(channel.getName())) {
                    continue;
                }

                if (g.getChannels() == null) {
                    g.setChannels(new ArrayList<>());
                }

                if (g.getChannelById(channel.getIdLong()) == null) {
                    g.getChannels().add(new Channel(channel.getIdLong()));
                }

                Channel c = g.getChannelById(channel.getIdLong());
                AtomicBoolean dateSet = new AtomicBoolean(false);

                String cacheDate = c.getLastMessageReceivedDate() != null ? c.getLastMessageReceivedDate() : "";
                if (c.getLastMessageReceivedDate() == null) {
                    c.setLastMessageReceivedDate("NOT SET");
                }

                AtomicReference<String> tempDate = new AtomicReference<>("");

                if (c.getMessages() == null) {
                    c.setMessages(new ArrayList<>());
                }

                channel.getIterableHistory().cache(false).forEachAsync(msg -> {

                    if (!msg.getAuthor().isBot()) {
                        if (!dateSet.get()) {
                            tempDate.set(msg.getTimeCreated().toString());
                            dateSet.set(true);
                        }

                        if (!msg.getTimeCreated().toString().equals(cacheDate)) {
                            c.getMessages().add(new Message(msg.getIdLong()));
                            Message m = c.getMessageById(msg.getIdLong());
                            m.setContent(msg.getContentRaw());
                            m.setAuthor(msg.getAuthor().getIdLong());
                            counter.getAndIncrement();
                        } else {
                            c.setLastMessageReceivedDate(tempDate.get());
                            return false;
                        }
                    }

                    return true;
                }).join();

                if (c.getLastMessageReceivedDate().equals("NOT SET")) {
                    c.setLastMessageReceivedDate(tempDate.get());
                }
            }


            try {
                mapper.writeValue(Paths.get("src/output.json").toFile(), rootJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            EmbedBuilder eb = evaluate(event, rootJson, messageArray[1]);
            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    public EmbedBuilder evaluate(GuildMessageReceivedEvent event, Root r, String query) {
        Guild g = r.getGuildById(event.getGuild().getIdLong());
        TreeMap<Long, Integer> authorMap = new TreeMap<>();

        for (Channel channel : g.getChannels()) {
            for (Message message : channel.getMessages()) {
                if (!authorMap.containsKey(message.getAuthor())) {
                    authorMap.put(message.getAuthor(), 0);
                }
                if (message.getContent().contains(query)) {
                    authorMap.put(message.getAuthor(), authorMap.get(message.getAuthor()) + 1);
                }
            }
        }

        EmbedBuilder eb = new EmbedBuilder();
        int counter = 1;
        eb.setTitle("Leaderboard for '" + query + "'");
        for (Map.Entry<Long, Integer> entry : authorMap.entrySet()) {
            Long key = entry.getKey();
            Integer value = entry.getValue();
            User u = Objects.requireNonNull(event.getGuild().getMemberById(key)).getUser();
            eb.addField("#" + counter + " " + u.getName() + "#" + u.getDiscriminator(), value + " occurrences", false);
        }

        return eb;
    }

}