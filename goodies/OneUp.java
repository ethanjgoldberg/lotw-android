package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class OneUp extends Goody {
	public OneUp (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);
		color = G.colors.goodyBlue;
	}

	public boolean collide (Glider glider) {
		glider.oneUp();
		glider.doEffect(color);

		return true;
	}
}
