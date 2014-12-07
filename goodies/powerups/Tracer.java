package org.anism.lotw.goodies.powerups;

import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.powerups.PowerUp;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class Tracer extends PowerUp {
	public Tracer (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);
	}

	public void powerUp (Glider glider) {
		G.tracers++;
	}

	public void powerDown (Glider glider) {
		G.tracers--;
	}
}
