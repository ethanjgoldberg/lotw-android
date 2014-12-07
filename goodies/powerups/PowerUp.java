package org.anism.lotw.goodies.powerups;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class PowerUp extends Goody {
	int t = 0;
	int life = 400;

	public PowerUp (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);
		radius = G.powerSize / 2;
	}

	public void powerUp (Glider glider) {
	}
	public void powerDown (Glider glider) {
	}

	public void collide (Glider glider) {
		glider.givePower(this);
		powerUp(glider);
	}

	public void decay (Glider glider) {
		life--;
		if (life <= 0) {
			powerDown(glider);
			glider.losePower(this);
		}
	}

	public boolean tick (float dt, float dx) {
		t++;
		if (t >= 120) t -= 120;
		return super.tick(dt, dx);
	}

	/*
	public void draw () {
		G.sr.begin(ShapeType.Line);
		G.sr.setColor(Color.BLACK);
		G.sr.circle(position.x, position.y, radius);
		if (t < 60) {
			int r = radius * t / 60;
			G.sr.circle(position.x, position.y, r);
		}
		G.sr.end();
	}
	*/
}
