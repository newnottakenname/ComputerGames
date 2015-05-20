package nl.tudelft.ti2806.riverrush.desktop;

import java.net.URISyntaxException;

import nl.tudelft.ti2806.riverrush.CoreModule;
import nl.tudelft.ti2806.riverrush.controller.Controller;
import nl.tudelft.ti2806.riverrush.controller.RenderController;
import nl.tudelft.ti2806.riverrush.domain.event.BasicEventDispatcher;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.domain.event.GameAboutToStartEvent;
import nl.tudelft.ti2806.riverrush.domain.event.GameStartedEvent;
import nl.tudelft.ti2806.riverrush.game.Game;
import nl.tudelft.ti2806.riverrush.network.Client;
import nl.tudelft.ti2806.riverrush.network.event.JumpEvent;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MainDesktop extends CoreModule {

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    private final Injector injector;
    private final Client client;

    public static void main(String[] arg) throws URISyntaxException {
        new MainDesktop();

    }

    public MainDesktop() throws URISyntaxException {
        this.injector = Guice.createInjector(this);

        this.client = new Client("localhost", this.configureRendererProtocol(),
                this.injector.getInstance(EventDispatcher.class),
                this.injector.getInstance(Controller.class));

        this.setupGraphics();
        this.client.connect();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.injector.getInstance(EventDispatcher.class).dispatch(
                new GameAboutToStartEvent());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.injector.getInstance(EventDispatcher.class).dispatch(
                new GameStartedEvent());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.injector.getInstance(EventDispatcher.class).dispatch(
                new JumpEvent());
    }

    private void setupGraphics() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = 0;
        config.width = WIDTH;
        config.height = HEIGHT;
        // config.fullscreen = true;

        Game game = this.injector.getInstance(Game.class);
        new LwjglApplication(game, config);
    }

    /**
     * Method is called when creating an {@link Injector}. It configures all
     * dependencies specific to the desktop application.
     */
    @Override
    protected void configure() {
        super.configure();
        this.bind(AssetManager.class).toInstance(new AssetManager());
        this.bind(Controller.class).to(RenderController.class);
    }

    @Override
    protected EventDispatcher configureEventDispatcher() {
        return new BasicEventDispatcher();
    }
}
