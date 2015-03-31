package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Star extends Goody {
	float index;

	public Star(Glob G, int x, float vy, Texture t) {
		super(G, x, vy, t);
	}

	public Star(Glob G, int x, float vy) {
		super(G, x, vy, G.starTexture);

		if (vy > 0) vy = -vy;

		radius = -3 * (float) vy * (float) (0.5 + Math.random());
		size = 2*radius;
		drawTrail = false;

		float r = (float) Math.random();

		velocity = new Vector2(0, vy / 4);
		velocity.scl(G.settings.speed);
	}

	public Star (Glob G, Vector2 p) {
		this(G, (int) p.x, -(float) Math.max(0.3, Math.random()));
		position.set(p);
	}

	public Star (Glob G, int x) {
		this(G, x, (float) (0.3 + Math.random()));
	}

	public boolean tick (float dt, float dx) {
		//super.tick(dt, -dx * velocity.y);
		position.x -= dx * velocity.y;
		position.add(velocity);
		offScreen = (position.x < -size || position.x > G.width + size);

		return position.y < -radius 
			|| position.x < -4 * G.width 
			|| position.x > 5 * G.width;
	}

	public void draw () {
		if (offScreen) return;

		float px = position.x - radius;
		float py = position.y - radius;

		G.sb.draw(texture, px, py, size, size);
	}

	public boolean collide (Glider glider) {
		return false;
	}

	public void setIndex (float n) {
		index = n;
	}

	public void addX (float dx) {
		position.x += dx;
	}

	public void setVelocity (Vector2 v) {
		velocity.set(v);
	}
	public void setVelocity (float x, float y) {
		velocity.set(x, y);
	}

	public void setSize (float s) {
		radius = s / 2;
		size = s;
	}

	public void addPosition (Vector2 a) {
		position.add(a);
	}

	public boolean isStatic () {
		return false;
	}
}
