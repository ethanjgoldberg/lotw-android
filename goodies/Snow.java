package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.anism.lotw.goodies.Star;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Snow extends Star {
	float rotation;
	float spin;

	public Snow (Glob G, int x, float vy) {
		super(G, x, vy);
		if (vy > 0) vy = -vy;
		texture = G.snowFlake();

		radius = -(float) vy * (float) (1.5 + Math.random());
		size = 2 * radius;
		drawTrail = false;

		// six fold rotational symmetry...
		rotation = 60 * (float) Math.random();
		spin = (float) Math.random() - 0.5f;

		color = new Color(1, 1, 1, 1);
		velocity = new Vector2(0, vy / 4);
	}

	public Snow (Glob G, Vector2 p) {
		this(G, (int) p.x);
		position.set(p);
	}

	public Snow (Glob G, int x) {
		this(G, x, (float) Math.random() + 2);
	}

	public boolean tick (float dt, float dx) {
		//super.tick(dt, -dx * velocity.y);
		position.x -= dx * velocity.y;
		position.add(velocity);
		velocity.x += (Math.random() - 0.5f) / 16;
		spin += (Math.random() - 0.5f) / 32;
		offScreen = (position.x < -size 
				|| position.x > G.width + size);
		rotation += spin;

		return position.y < -radius;
	}

	public void draw () {
		if (offScreen) return;

		float px = position.x - radius;
		float py = position.y - radius;

		G.sb.draw(texture, px, py,
				0, 0, size, size, 1, 1, rotation, 
				0, 0, texture.getWidth(), texture.getHeight(),
				false, false);
	}
}
