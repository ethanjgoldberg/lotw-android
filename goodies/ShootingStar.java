package org.anism.lotw.goodies;

import com.badlogic.gdx.math.Vector2;

import org.anism.lotw.Glob;
import org.anism.lotw.goodies.Star;

public class ShootingStar extends Star {
	int age = 0;
	int maxAge;
	float width;
	float vy;
	float alpha = 1;
	float pct;

	public ShootingStar (Glob g, int x, int y) {
		super(g, x);

		velocity.set((float) Math.random()-.5f, (float) Math.random()/2-.25f).nor();
		position.set(x, y);

		maxAge = Math.min(40 * (int) (1 / Math.random()), 800);
		width = (float) (2 * Math.log(maxAge));
		pct = width / (2 * (float) Math.log(800));

		vy = -pct / 4;

	}

	public void draw () {
		Vector2 p = new Vector2(position);
		G.sb.setColor(1, 1, 1, alpha * pct);
		for (int i = 0; i < age; i++) {
			float size = width * (float) Math.min(i, maxAge - i) / (float) maxAge;
			size = (float) Math.sqrt(size);
			size = Math.max(size, 1);
			G.sb.draw(G.lightTexture, position.x + i * velocity.x - size/2,
					position.y + i * velocity.y - size/2,
					size, size);
		}
	}

	public boolean tick (float dt) {
		position.y += vy;
		age += 8;
		if (age >= maxAge) {
			alpha -= 0.017;
			age = maxAge;
		}
		return alpha <= 0;
	}
}
