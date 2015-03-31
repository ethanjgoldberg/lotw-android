package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.anism.lotw.goodies.Star;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Rain extends Star {
	float magnitude;
	float angle;
	Vector2 offset;

	public Rain (Glob G, int x, float vy) {
		super(G, x, vy);
		if (vy > 0) vy = -vy;

		velocity = new Vector2((float) Math.random(), vy);

		magnitude = velocity.len();
		angle = velocity.angle();

		offset = new Vector2((float) Math.random() * 60 + 30,
				-(float) Math.random() * 60 + 30);
	}

	public Rain (Glob G, Vector2 p) {
		this(G, (int) p.x);
		position.set(p);
	}

	public Rain (Glob G, int x) {
		this(G, x, 16 * (float) Math.random() + 8);
	}

	Vector2 vel = new Vector2();

	public boolean tick (float dt, float dx) {
		//super.tick(dt, -dx * velocity.y);
		position.x -= dx * velocity.y / 32;
		vel.set(velocity).scl(G.settings.speed);
		position.add(vel);
		offScreen = (position.x < -magnitude
				|| position.x > G.width + magnitude);

		return position.y < -magnitude
			|| position.x < -4 * G.width
			|| position.x > 5 * G.width;
	}

	public void draw () {
		int px = (int) position.x;
		int py = (int) position.y;

		for (int i = 0; i < 20; i++) {
			px += (int) offset.x;
			py += (int) offset.y;
			if (px > G.width || py < -magnitude) break;
			if (px < 0 || py > G.height) continue;
			G.sb.draw(G.dotTexture,
					px, py,
					0, 0, magnitude, 1, 1, 1, angle, 
					0, 0, 1, 1, false, false);
		}
	}
}
