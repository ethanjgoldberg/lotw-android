package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

import org.anism.lotw.goodies.Star;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Cloud extends Star {
	float z;
	float effective;

	public void setIndex (float n) {
		super.setIndex(n);
		z = n * 128 - 128;
	}

	public boolean cloudCollide (Vector2 p, float s) {
		if (p.x + s < position.x) return false;
		if (p.x > position.x + size) return false;
		if (p.y + s/2 < position.y) return false;
		if (p.y > position.y + size/2) return false;
		return true;
	}

	public Cloud (Glob G, Vector2 p) {
		super(G, 0, 0, G.starTexture);

		float s = (float) Math.random() * 64 + 64;

		position.set(p);

		effective = (float) Math.pow(2, z / 128);

		size = s * effective;
		radius = size / 16;
	}

	public Cloud (Glob G, int x) {
		this(G, new Vector2(x, G.height));
	}

	public boolean tick (float dt, float dx) {
		position.x += dx * effective;
		offScreen = (position.x < -size*2 || position.x > G.width + size*2);

		return position.y < -size * effective
			|| position.x < -4 * G.width
			|| position.x > 5 * G.width;
	}

	public void offset (Vector2 o) {
		position.x += o.x * effective;
		position.y += o.y * effective;
	}

	public float dx (float x) {
		return -size * (x / G.width - 0.5f) / 4;
	}

	public float dy (float y) {
		return -size * (y / G.height - 0.5f) / 8;
	}

	public void draw () {
		if (offScreen) return;

		float s = size * effective;

		float bottom = position.y;
		float top = position.y + s / 2;
		float left = position.x;
		float right = position.x + s;

		float width = right - left;
		float height = top - bottom;

		float dtop = dy(top);
		float dbottom = dy(bottom);
		float dleft = dx(left);
		float dright = dx(right);

		// middle:
		G.sb.setColor(1, 1, 1, 1);
		G.sb.draw(G.dotTexture, left, bottom, width, height);

		float l = Math.max(left, left + dleft);
		float r = Math.min(right, right + dright);

		// top:
		if (dtop > 0) {
			G.sb.setColor(.7f, .7f, .7f, 1);
			G.sb.draw(G.dotTexture, l, top, r - l, dtop);
			if (l > left)
				G.sb.draw(G.angleTexture, l, top, 
						-dleft, dtop);
			else
				G.sb.draw(G.angleTexture, l, top + dtop, 
						dleft, -dtop);
			if (r < right)
				G.sb.draw(G.angleTexture, r, top, 
						-dright, dtop);
			else
				G.sb.draw(G.angleTexture, r, top + dtop, 
						dright, -dtop);
		}

		// bottom:
		if (dbottom < 0) {
			G.sb.setColor(.3f, .3f, .3f, 1);
			G.sb.draw(G.dotTexture, l, bottom, r - l, dbottom);
			if (l > left)
				G.sb.draw(G.angleTexture, l, bottom, 
						-dleft, dbottom);
			else
				G.sb.draw(G.angleTexture, l, bottom + dbottom, 
						dleft, -dbottom);
			if (r < right)
				G.sb.draw(G.angleTexture, r, bottom, 
						-dright, dbottom);
			else
				G.sb.draw(G.angleTexture, r, bottom + dbottom, 
						dright,-dbottom);
		}

		float t = Math.min(top, top + dtop);
		float b = Math.max(bottom, bottom + dbottom);
		G.sb.setColor(.5f, .5f, .5f, 1);
		// left:
		if (dleft < 0) {
			G.sb.draw(G.dotTexture, left, b, dleft, t - b);
			if (b > bottom)
				G.sb.draw(G.angleTexture, left, 
						b, dleft, -dbottom);
			else
				G.sb.draw(G.angleTexture, left + dleft, 
						b, -dleft, dbottom);
			if (t < top)
				G.sb.draw(G.angleTexture, left, 
						t, dleft, -dtop);
			else
				G.sb.draw(G.angleTexture, left + dleft, 
						t, -dleft, dtop);
		}

		// right:
		if (dright > 0) {
			G.sb.draw(G.dotTexture, right, b, dright, t - b);
			if (b > bottom)
				G.sb.draw(G.angleTexture, right, 
						b, dright, -dbottom);
			else
				G.sb.draw(G.angleTexture, right + dright, 
						b, -dright, dbottom);
			if (t < top)
				G.sb.draw(G.angleTexture, right, 
						t, dright, -dtop);
			else
				G.sb.draw(G.angleTexture, right + dright, 
						t, -dright, dtop);
		}
	}
}
