package nl.tudelft.ti2806.riverrush.domain.event;


import nl.tudelft.ti2806.riverrush.domain.entity.Player;
import nl.tudelft.ti2806.riverrush.network.protocol.Protocol;

import java.util.Map;

/**
 * Domain event.
 */
public interface Event {

    /**
     * Serialize the current event to it's string representation.
     *
     * @param protocol - The protocol to use.
     * @return - A mesage that can be sent over sockets.
     */
    String serialize(Protocol protocol);

    /**
     * From key-value pairs, initialize this event's fields with the appropriate
     * values.
     *
     * @param keyValuePairs - A map containing a value for each field of this Event.
     * @return The fully initialized event, ready for dispatch.
     */
    Event deserialize(Map<String, String> keyValuePairs);

    void setPlayer(Player player);

    Player getPlayer();
}
