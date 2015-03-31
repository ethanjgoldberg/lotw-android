package org.anism.lotw.goodies.powerups;

import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.powerups.PowerUp;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class Shields extends PowerUp {
	public Shields (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);
		lit = false;
	}

	public boolean collide (Glider glider) {
		glider.shieldsUp();

		return true;
	}
}
