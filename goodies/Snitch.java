package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Snitch extends Goody {
	public Snitch (Glob g, int x, Texture t) {
		super(g, x, -4, t);
		points = 100;
		color = G.colors.goodyGold;
	}

	public boolean collide (Glider glider) {
		glider.catchSnitch();
		glider.doEffect(color);

		return true;
	}
}
