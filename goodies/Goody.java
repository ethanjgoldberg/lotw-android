package org.anism.lotw.goodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.Batch;

import org.anism.lotw.Glob;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class Goody extends Actor {
	protected Glob G;

	protected Vector2 position;
	Vector2 velocity;

	protected int points;
	protected float radius;
	protected float size;
	protected boolean offScreen;
	protected boolean drawTrail = true;
	protected boolean lit = true;

	protected Color color;

	Texture texture;

	public Goody (Glob g, float x, float y, Color c) {
		G = g;
		radius = G.size / 2;
		size = G.lightSize;
		position = new Vector2(G.width/2 + x*G.dpi - radius/2,
				G.height/2 + y*G.dpi - radius/2);
		velocity = new Vector2();
		points = 0;
		texture = G.goodyTexture;
		color = c;
	}

	public Goody (Glob g, int x, float speed, Texture t) {
		G = g;

		radius = G.size / 2;
		size = G.lightSize;

		color = new Color(G.colors.goodyGreen);

		position = new Vector2(x, G.height + 20 * G.unit + size);
		velocity = new Vector2(0, speed * G.unit);
		velocity.scl(G.settings.speed);

		points = -Math.round(speed);

		texture = t;
	}

	public boolean tick (float dt, float dx) {
		position.x += dx;

		offScreen = (position.x < -size || position.x > G.width + size);

		position.add(velocity);

		return position.y < -G.height;
	}

	public boolean collide (Glider glider) {
		glider.givePoints(points);
		glider.doEffect(color);

		return true;
	}

	public void drawLight () {
		drawLight(G.sb);
	}

	public void drawLight (Batch b) {
		Color c = new Color(color);
		c.a /= 2;
		float size = G.lightSize;
		size -= Math.random() * 2;
		b.setColor(c);
		b.draw(G.lightTexture,
				position.x - size/2, position.y - size/2,
				size, size);
	}

	public int getPoints () {
		return points;
	}

	public void setPoints (int p) {
		points = p;
	}

	@Override
	public Color getColor () {
		return color;
	}

	@Override
	public void setPosition (float x, float y) {
		position.set(x, y);
	}

	@Override
	public void moveBy (float x, float y) {
		position.add(new Vector2(x, y));
	}

	@Override
	public void setBounds (float x, float y, float w, float h) {
		setPosition(x, y);
	}

	@Override
	public void draw (Batch b, float parentAlpha) {
		float oldAlpha = color.a;
		color.a *= parentAlpha;
		draw(b);
		color.a = oldAlpha;
	}

	public void draw () {
		draw(G.sb);
	}

	public void draw (Batch b) {
		if (offScreen) return;
		if (position.y < -size) return;

		b.setColor(color);
		if (lit) {
			b.draw(texture, position.x - size/2, position.y - size/2, size, size);
		} else {
			b.draw(texture, position.x - radius, 
					position.y - radius,
					radius*2, radius*2);
			if (drawTrail) drawLight(b);
		}

		/*
		ShapeRenderer sr = G.sr;

		sr.begin(ShapeType.Filled);
		sr.setColor(color);
		sr.circle(position.x, position.y, radius);
		sr.end();
		*/
	}

	public Vector2 getPosition () {
		return position;
	}

	public float getRadius () {
		return radius;
	}

	public float getRadius2 () {
		return radius * radius;
	}

	public void setVelocity (Vector2 v) {
		velocity.set(v);
	}
}
