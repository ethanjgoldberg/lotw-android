package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class Goal extends Goody {
	float width;
	boolean got = false;
	boolean left = true;
	boolean right = true;
	float angle;
	float dangle;

	public Goal (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);

		angle = 0;
		dangle = 0;

		points = 5;

		width = 64 * G.unit;

		color = new Color(0, 0, 0, 0);
	}

	public boolean tick (float dt, float dx) {
		if (!got && G.glider.intersects(position, width) && left && right) {
			got = true;
			G.glider.givePoints(points);
		}

		if (left ^ right) {
			dangle -= Math.signum(angle + (right? -90: 90)) / 4;
		}

		dangle *= 0.99;
		angle += dangle;

		return super.tick(dt, dx);
	}

	public void draw () {
		if (!got)
			G.sb.setColor(0, 0, 0, 0.5f);
		else
			G.sb.setColor(G.colors.goodyGreen);

		if (right && !left) {
			G.sb.draw(G.dotTexture, position.x + width, position.y, 0, 0,
					-width, 2, 1, 1, angle,
					0, 0, 1, 1, false, false);
		} else {
			G.sb.draw(G.dotTexture, position.x, position.y, 0, 0,
					width, 2, 1, 1, angle,
					0, 0, 1, 1, false, false);
		}
	}

	public void invalidate (int leftOrRight) {
		if (leftOrRight < 0)
			left = false;
		if (leftOrRight > 0)
			right = false;
	}

	public boolean collide (Glider glider) {
		return false;
	}

	public float getWidth () {
		return width;
	}
}
