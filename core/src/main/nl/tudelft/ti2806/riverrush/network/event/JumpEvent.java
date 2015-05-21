package nl.tudelft.ti2806.riverrush.network.event;

import nl.tudelft.ti2806.riverrush.domain.entity.Player;
import nl.tudelft.ti2806.riverrush.domain.event.Event;
import nl.tudelft.ti2806.riverrush.network.protocol.Protocol;

import java.util.Map;

/**
 * This event is sent from the device to the server.
 */
public class JumpEvent implements Event {


    private Player player;

    @Override
    public String serialize(final Protocol protocol) {
        return "player" + protocol.getKeyValueSeperator() + player.getId();
    }

    @Override
    public Event deserialize(final Map<String, String> keyValuePairs) {
        this.player = new Player(Long.parseLong(keyValuePairs.get("player")));
        return this;
    }

    public void setPlayer(final Player aPlayer) {
        this.player = aPlayer;
    }

    public Player getPlayer() {
        return player;
    }
}
