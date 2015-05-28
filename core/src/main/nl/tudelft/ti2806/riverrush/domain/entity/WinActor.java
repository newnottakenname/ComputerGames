package nl.tudelft.ti2806.riverrush.domain.entity;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Win game object that represents the winning screen state.
 */
public class WinActor extends EndActor {

    private static final int END_REGIONX = 313;
    private static final int END_REGIONY = 232;

    /**
     * Creates an win game object that presents itself on the screen to the winning party.
     *
     * @param assetManager enables the object to retrieve its assets.
     */
    public WinActor(final AssetManager assetManager) {
        super();
        Texture tex = assetManager.get("assets/data/win.png", Texture.class);
        this.setRegion(new TextureRegion(tex, 0, 0, END_REGIONX, END_REGIONY));

    }

}