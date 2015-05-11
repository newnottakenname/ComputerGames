package nl.tudelft.ti2806.riverrush.network.event;

import java.util.Map;

import nl.tudelft.ti2806.riverrush.domain.event.Event;
import nl.tudelft.ti2806.riverrush.network.protocol.Protocol;

/**
 * Indicates that a client wants to join the game.
 */
public class JoinEvent implements Event {

    @Override
    public String serialize(Protocol protocol) {
        return "";
    }

    /**
     * A join request has no parameters. Thus, {@code keyValuePairs} is ignored.
     *
     * @param keyValuePairs
     *            - Ignored.
     * @return Just {@code this}.
     */
    @Override
    public Event deserialize(final Map<String, String> keyValuePairs) {
        return this;
    }
}