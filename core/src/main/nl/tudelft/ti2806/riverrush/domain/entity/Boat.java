package nl.tudelft.ti2806.riverrush.domain.entity;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.inject.Inject;

/**
 * Represents a boat that the animals row on.
 */
public class Boat extends AbstractGroup {

  private AssetManager manager;
  private static final float MONKEY_WIDTH = 144;
  private static final float MONKEY_HEIGHT = 81;
  private static final float HALF = 2;
  private static final int REGION_ENDX = 584;
  private static final int REGION_ENDY = 1574;

  /**
   * Creates an boat object with a given graphical representation.
   *
   * @param assetManager
   *          enables the object to retrieve its assets
   * @param xpos
   *          represents the position of the boat on the x axis
   * @param ypos
   *          represents the position of the boat on the y axis
   * @param width
   *          represents the width of the boat object
   * @param height
   *          represents the height of the boat object
   */
  @Inject
  public Boat(AssetManager assetManager, float xpos, float ypos, float width, float height) {
    this.manager = assetManager;
    this.setX(xpos);
    this.setY(ypos);
    this.setWidth(width);
    this.setHeight(height);
    // Size is based on viewport: 1920, 1080 is full sized, 100, 100 wont
    // give equivalent height/width
    Monkey monk = new Monkey(this.manager, (this.getX() + (this.getWidth() / HALF))
        - (MONKEY_WIDTH / 2), (this.getY() + (this.getHeight() / HALF)) - (MONKEY_HEIGHT / 2),
        MONKEY_WIDTH, MONKEY_HEIGHT);
    this.addActor(monk);
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    Texture tex = this.manager.get("assets/data/shipv2.png", Texture.class);
    TextureRegion region = new TextureRegion(tex, 0, 0, REGION_ENDX, REGION_ENDY);
    batch.enableBlending();
    batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

    Color color = this.getColor();
    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

    batch.draw(region, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(),
        this.getWidth(), this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation());
    batch.setColor(Color.WHITE);
    this.drawChildren(batch, parentAlpha);
    batch.disableBlending();

  }

  @Override
  public void act(float delta) {
    super.act(delta);

  }

}
