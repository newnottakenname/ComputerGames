package nl.tudelft.ti2806.Graphics;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Martijn on 9-5-2015.
 */
public class ProgressBar {

    public static ProgressBar staticStatus;

    public static ProgressBar getStatus() {
        if (staticStatus == null) {
            staticStatus = new ProgressBar();
        }
        return staticStatus;
    }

    public static void discard() {
        staticStatus = null;
    }

    private double team1 = 0;
    private double team2 = 0;

    public void draw(ShapeRenderer shapemaker) {
        shapemaker.line(840, 0, 840, 1080);
        shapemaker.line(1080, 0, 1080, 1080);

        this.drawTeam1(shapemaker);
        this.drawTeam2(shapemaker);
    }

    private void drawTeam1(ShapeRenderer shapemaker) {
        int percent = (int) (this.team1 / 100 * 480);

        shapemaker.line(920, 300, 920, 780);
        shapemaker.circle(920, 300 + percent, 10);
    }

    private void drawTeam2(ShapeRenderer shapemaker) {
        int percent = (int) (this.team2 / 100 * 480);
        shapemaker.line(1000, 300, 1000, 780);
        shapemaker.circle(1000, 300 + percent, 10);
    }

    public void setPointsTeam1(double percentage) {
        this.team1 = percentage;
        if (percentage > 100) {
            this.team1 = 100;
        }

    }

    public void setPointsTeam2(double percentage) {
        this.team2 = percentage;
        if (percentage > 100) {
            this.team2 = 100;
        }
    }
}