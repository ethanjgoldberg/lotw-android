package org.anism.lotw.goodies.powerups;

import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.powerups.PowerUp;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class SpeedUp extends PowerUp {
	public SpeedUp (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);
	}

	public void powerUp (Glider glider) {
		G.settings.speed *= 2;
	}

	public void powerDown (Glider glider) {
		G.settings.speed /= 2;
	}
}
