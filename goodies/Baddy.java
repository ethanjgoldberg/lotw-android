package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.goodies.Goal;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class Baddy extends Goody {
	public Baddy (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);
		points *= -1;
		color = G.colors.goodyRed;
		texture = G.baddyTexture;
	}

	public Baddy (Glob g, Color c) {
		this(g, 0, 0, null);
		color = c;
	}

	int side;
	Goal net;

	public Baddy (Glob g, int x, float speed, int s, Goal n) {
		this(g, x, speed, null);
		side = s;
		net = n;
	}

	public boolean collide (Glider glider) {
		super.collide(glider);
		glider.damage();

		if (net != null) {
			net.invalidate(side);
		}

		return true;
	}
}
