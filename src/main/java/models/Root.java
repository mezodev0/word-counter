package models;

import java.util.List;

public class Root {
    public List<Guild> guilds;

    public Root() {

    }

    public Root(List<Guild> guilds) {
        this.guilds = guilds;
    }

    public Guild getGuildById(long guild_id) {
        for (Guild g : guilds) {
            if (g.getGuildId() == guild_id) {
                return g;
            }
        }
        return null;
    }
}