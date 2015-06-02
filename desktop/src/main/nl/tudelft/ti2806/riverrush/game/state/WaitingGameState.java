package nl.tudelft.ti2806.riverrush.game.state;

import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.domain.event.GameAboutToStartEvent;
import nl.tudelft.ti2806.riverrush.domain.event.HandlerLambda;
import nl.tudelft.ti2806.riverrush.game.Game;
import nl.tudelft.ti2806.riverrush.screen.WaitingScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

/**
 * State for a waiting game.
 */
public class WaitingGameState extends AbstractGameState {

    private final WaitingScreen screen;
    private static final int DELAY = 5;
    // private final HandlerLambda<AnimalAddedEvent> animalHandler = (e) ->
    // this.addAnimalHandler(e);
    private final HandlerLambda<GameAboutToStartEvent> timerHandler = (e) -> this.startTimer();

    /**
     * The state of the game that indicates that the game is waiting for players. In this state the
     * game can be started when enough players have connected.
     *
     * @param eventDispatcher
     *            the dispatcher that is used to handle any relevant events for the game in this
     *            state.
     * @param assetManager
     *            has all necessary assets loaded and available for use.
     * @param gm
     *            refers to the game that this state belongs to.
     */
    public WaitingGameState(final EventDispatcher eventDispatcher, final AssetManager assetManager,
            final Game gm) {
        super(eventDispatcher, assetManager, gm);

        this.dispatcher.attach(GameAboutToStartEvent.class, this.timerHandler);
        // this.dispatcher.attach(AnimalAddedEvent.class, this.animalHandler);
        this.screen = new WaitingScreen(assetManager, eventDispatcher);
        Gdx.app.postRunnable(() -> WaitingGameState.this.game
                .setScreen(WaitingGameState.this.screen));
    }

    /**
     * Starts the timer of 5 seconds.
     */
    private void startTimer() {
        this.screen.startTimer(DELAY);
    }

    @Override
    public void dispose() {
        this.dispatcher.detach(GameAboutToStartEvent.class, this.timerHandler);
        // this.dispatcher.detach(AnimalAddedEvent.class, this.animalHandler);
        this.screen.dispose();
    }

    @Override
    public GameState start() {
        this.dispose();
        return new PlayingGameState(this.dispatcher, this.assets, this.game);
    }

    @Override
    public GameState stop() {
        this.dispose();
        return new StoppedGameState(this.dispatcher, this.assets, this.game);
    }

    @Override
    public GameState finish() {
        return this;
    }

    @Override
    public GameState waitForPlayers() {
        return this;
    }

}