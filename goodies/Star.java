package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Star extends Goody {
	boolean oddColor;

	public Star(Glob G, int x, float vy, Texture t) {
		super(G, x, vy, t);
	}

	public Star(Glob G, int x, float vy) {
		super(G, x, vy, G.starTexture);

		radius = -3 * (float) vy * (float) (0.5 + Math.random());
		size = 2*radius;
		drawTrail = false;

		float r = (float) Math.random();

		Color[] colors = {
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(1, 1, 1, 1),
			new Color(G.colors.goodyGreen),
			//new Color(G.colors.red),
			new Color(G.colors.goodyGold),
			//new Color(G.colors.blue)
		};

		color = colors[(int) Math.floor(Math.random() * colors.length)];
		color.a = ((-(float) vy) + (float) Math.random()) / 2;

		oddColor = color.r < 1 || color.g < 1 || color.b < 1;

		velocity = new Vector2(0, vy / 4);
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

		return position.y < -radius;
	}


	public void draw () {
		if (offScreen) return;

		float px = position.x - radius;
		float py = position.y - radius;

		if (oddColor) G.sb.setColor(color);

		G.sb.draw(texture, px, py, size, size);

		if (oddColor) G.sb.setColor(1, 1, 1, 1);
	}

	public void collide (Glider glider) {
	}
}
