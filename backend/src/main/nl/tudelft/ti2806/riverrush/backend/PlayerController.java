package nl.tudelft.ti2806.riverrush.backend;

import nl.tudelft.ti2806.riverrush.domain.entity.Player;
import nl.tudelft.ti2806.riverrush.domain.event.*;
import nl.tudelft.ti2806.riverrush.network.Server;
import nl.tudelft.ti2806.riverrush.network.event.JumpEvent;

public class PlayerController implements Controller {

    private final Player player;
    private final EventDispatcher dispatcher;
    private final Server server;
    private final HandlerLambda onGameStateChangeLambda = this::onGameStateChange;
    private final HandlerLambda onJumpLambda = this::onJump;


    public PlayerController(final EventDispatcher dispatcher, final Server server) {
        this.player = new Player();
        this.dispatcher = dispatcher;
        this.server = server;

        dispatcher.attatch(JumpEvent.class, onJumpLambda);
        dispatcher.attatch(GameAboutToStartEvent.class, onGameStateChangeLambda);
        dispatcher.attatch(GameStartedEvent.class, onGameStateChangeLambda);
        dispatcher.attatch(GameStoppedEvent.class, onGameStateChangeLambda);
        dispatcher.attatch(GameFinishedEvent.class, onGameStateChangeLambda);
        dispatcher.attatch(GameWaitingEvent.class, onGameStateChangeLambda);
    }

    @Override
    public void onSocketMessage(final Event event) {
        event.setPlayer(this.player);
        this.dispatcher.dispatch(event);
    }

    @Override
    public void detatch() {
        this.dispatcher.detatch(JumpEvent.class, onJumpLambda);
        this.dispatcher.detatch(GameWaitingEvent.class, onGameStateChangeLambda);
        this.dispatcher.detatch(GameStartedEvent.class, onGameStateChangeLambda);
        this.dispatcher.detatch(GameAboutToStartEvent.class, onGameStateChangeLambda);
        this.dispatcher.detatch(GameStoppedEvent.class, onGameStateChangeLambda);
        this.dispatcher.detatch(GameFinishedEvent.class, onGameStateChangeLambda);
    }

    private void onGameStateChange(final Event event) {
        server.sendEvent(event, this);
    }

    private void onJump(final Event event) {
        JumpEvent jumpEvent = (JumpEvent) event;
        server.sendEvent(jumpEvent, this);
    }

}
