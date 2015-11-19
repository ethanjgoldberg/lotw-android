package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.goodies.Goal;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class HellMarker extends Goody {
	public HellMarker (Glob g) {
		super(g, 0, 0, (Texture) null);
		color = G.colors.green;

		velocity.set(0, 0);
		position.set(0, G.height);
	}

	public boolean tick (float dt, float dx) {
		velocity.y -= 0.04;
		position.y += velocity.y;

		if (position.y < 0) {
			G.glider.givePoints(1);
			return true;
		}
		return false;
	}

	public void draw () {
		G.sb.setColor(color);
		G.sb.draw(G.dotTexture, 0, position.y, G.width, 2);
	}

	public boolean collide (Glider glider) {
		return false;
	}
}
