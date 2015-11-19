package org.anism.lotw;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Camera;

import org.anism.lotw.goodies.Star;

public interface Season {
	public Star newStar (int x);
	public Star newStar (Vector2 p);
	public float getStarChance();
	public float getStarVel();
	public Color getColor();
	public Color getStarColor();
	public Color getGliderColor();
	public void drawBackground();
	public Texture getIcon();
	public String getName();

	public Camera getCam();

	public void drawStars();
	public void saltStars();
	public void tickStars(float dt, float dx);
	public void addStar(Star s);

    public void playMusic();
}
