package nl.tudelft.ti2806.riverrush;

import com.google.inject.AbstractModule;
import nl.tudelft.ti2806.riverrush.domain.event.BasicEventDispatcher;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.domain.event.GameAboutToStartEvent;
import nl.tudelft.ti2806.riverrush.domain.event.GameFinishedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.GameStartedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.GameStoppedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.GameWaitingEvent;
import nl.tudelft.ti2806.riverrush.domain.event.PlayerAddedEvent;
import nl.tudelft.ti2806.riverrush.domain.event.PlayerJumpedEvent;
import nl.tudelft.ti2806.riverrush.network.event.JumpCommand;
import nl.tudelft.ti2806.riverrush.network.protocol.BasicProtocol;
import nl.tudelft.ti2806.riverrush.network.protocol.Protocol;

/**
 * Configures dependency injection.
 */

public abstract class CoreModule extends AbstractModule {

    public static final int RENDER_PORT_NUMBER = 51337;
    public static final int CLIENT_PORT_NUMBER = 41337;

    @Override
    protected void configure() {
        this.bind(EventDispatcher.class).toInstance(this.configureEventDispatcher());
    }

    /**
     * Creates instances of EventDispatcher
     * Override for the ability to pre-attach any listeners.
     *
     * @return A fresh dispatcher.
     */
    protected EventDispatcher configureEventDispatcher() {
        return new BasicEventDispatcher();
    }

    /**
     * Configure the renderer protocol by registering all valid messages that can be
     * sent.
     *
     * @return The fully configured protocol.
     */
    protected Protocol configureRendererProtocol() {
        Protocol protocol = new BasicProtocol(RENDER_PORT_NUMBER);
        // Register available network actions
        // protocol.registerNetworkMessage(...);

        this.registerStateMessages(protocol);

        return protocol;
    }

    private void registerStateMessages(Protocol protocol) {
        protocol.registerNetworkMessage(GameWaitingEvent.class, GameWaitingEvent::new);
        protocol.registerNetworkMessage(GameAboutToStartEvent.class, GameAboutToStartEvent::new);
        protocol.registerNetworkMessage(GameStartedEvent.class, GameStartedEvent::new);
        protocol.registerNetworkMessage(GameFinishedEvent.class, GameFinishedEvent::new);
        protocol.registerNetworkMessage(GameStoppedEvent.class, GameStoppedEvent::new);
        protocol.registerNetworkMessage(PlayerAddedEvent.class, PlayerAddedEvent::new);
        protocol.registerNetworkMessage(PlayerJumpedEvent.class, PlayerJumpedEvent::new);
    }

    /**
     * Configure the client protocol by registering all valid messages that can be
     * sent.
     *
     * @return The fully configured protocol.
     */
    protected Protocol configureClientProtocol() {
        Protocol protocol = new BasicProtocol(CLIENT_PORT_NUMBER);
        // Register available network actions
        // protocol.registerNetworkMessage(...);

        protocol.registerNetworkMessage(JumpCommand.class, JumpCommand::new);
        registerStateMessages(protocol);

        return protocol;
    }

}
