package nl.tudelft.ti2806.riverrush.domain.event;

import nl.tudelft.ti2806.riverrush.domain.entity.Animal;
import nl.tudelft.ti2806.riverrush.network.protocol.Protocol;

import java.util.Map;

/**
 * Event raised when the game stops.
 */
public class GameStoppedEvent implements Event {

    @Override
    public String serialize(final Protocol protocol) {
        return "";
    }

    @Override
    public Event deserialize(final Map<String, String> keyValuePairs) {
        return this;
    }

    @Override
    public void setAnimal(Animal aAnimal) {
        // Has to be empty
    }

    @Override
    public Animal getAnimal() {
        // Has to be empty
        return null;
    }
}
