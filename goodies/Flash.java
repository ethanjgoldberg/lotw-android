package org.anism.lotw.goodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Flash extends Star {
	int alive = 6;
	Vector3 pos = new Vector3();
	Vector2 drawAt = new Vector2();


	public Flash (Glob G, ShaderProgram s) {
		super(G, 0, 0, G.starTexture);

		/*
		if (screenUtils == null)
			screenUtils = new ScreenUtils();
		if (shader == null)
			shader = s;
			*/

		/*
		if (sunPos.z < 0) {
			alive = 0;
			return;
		}

		pos.set(sunPos);
		pos.z = -(0.1f - pos.z);
		pos.scl(0.1f / pos.z);
		pos.z = 0;

		pos.y += Math.random() - 0.5;
		
		float which = (float) Math.random();
		float count;
		if (which < 0.5) {
			count = pos.y / 0.005f;
			color = new Color(1, 1, 1, 1);
		} else if (which < 0.7) {
			count = pos.y / 0.04f;
			color = new Color(1, 1, 0, 1);
		} else {
			count = pos.y / 0.1f;
			color = new Color(0xee6644ff);
		}

		for (int i = 0; i < 4 * count; i++) {
			pos.x *= (i + 1);
			pos.x += Math.random() * 2 - 1;
			pos.x /= (i + 2);
		}

		radius = 6;
		size = 2 * radius;

		G.settings.getCam().project(pos);
		drawAt.set(pos.x, pos.y);
		*/
	}

	public boolean tick (float dt, float dx) {
		alive--;
		return alive <= 0;
	}

	public void draw () {
		float px = drawAt.x - radius;
		float py = drawAt.y - radius;

		if (px < -radius || px > G.width + radius) return;

		Color c = G.sb.getColor();
		G.sb.setColor(color);
		G.sb.draw(texture, px, py, size, radius);
		G.sb.setColor(c);
	}
}
