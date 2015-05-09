package nl.tudelft.ti2806.monkeyrush.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import nl.tudelft.ti2806.monkeyrush.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.x = 0;
        config.width = 1920;
        config.height = 1080;
//        config.fullscreen = true;
        LwjglApplication frame = new LwjglApplication(new Main(), config);

	}
}