package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Texture;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glider;
import org.anism.lotw.Glob;

public class Multiplier extends Goody {
	public Multiplier (Glob g, int x, float speed, Texture t) {
		super(g, x, speed, t);
		points = 0;
		radius = G.powerSize / 2;
		lit = false;
	}

	/*
	public void draw () {
		ShapeRenderer sr = G.sr;

		sr.begin(ShapeType.Line);
		sr.setColor(Color.BLACK);
		sr.circle(position.x, position.y, radius);

		float r = (float) (radius / 2.5);
		sr.line(position.x - r, position.y - r,
				position.x + r, position.y + r);
		sr.line(position.x + r, position.y - r,
				position.x - r, position.y + r);
		sr.end();
	}
	*/

	public boolean collide (Glider glider) {
		glider.multiplierUp();
		glider.doEffect(color);

		return true;
	}
}
