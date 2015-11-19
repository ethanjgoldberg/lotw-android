package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.anism.lotw.goodies.Star;
import org.anism.lotw.goodies.Seed;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Dandelion extends Star {
	Seed[] seeds;
	int sixthSeeds;
	int numSeeds;

	boolean withSeed;

	Vector2 angle = new Vector2(0, 1);
	float dangle = 0;

	boolean giveShield;

	float theta (int i) {
		if (i == numSeeds - 1)
			return 0.25f;
		if (i >= 5 * sixthSeeds)
			return ((float) i - 5 * sixthSeeds + 0.25f) / sixthSeeds;
		if (i >= 3 * sixthSeeds)
			return ((float) i - 3 * sixthSeeds + 0.25f) / (2 * sixthSeeds);
		return (float) i / (3 * sixthSeeds);
	}
	float radial (int i) {
		if (i == numSeeds - 1)
			return 0;
		if (i >= 5 * sixthSeeds)
			return 0.33f;
		if (i >= 3 * sixthSeeds)
			return 0.66f;
		return 1;
	}

	public Dandelion (Glob G, int x) {
		super(G, x, 0);

		position.set(x, 0);
		angle.scl(60 + 240 * (float) Math.random());
		withSeed = true;

		radius = 8;
		size = 2 * radius;

		// numSeeds should be a multiple of six:
		sixthSeeds = 4;
		numSeeds = 6 * sixthSeeds + 1;

		seeds = new Seed[numSeeds];
		for (int i = 0; i < numSeeds; i++) {
			float t = theta(i);
			float r = size * radial(i);

			Vector2 n = new Vector2(position);
			n.add((float) (Math.cos(t * Math.PI * 2) * r),
					(float) (Math.sin(t * Math.PI * 2) * r));
			seeds[i] = new Seed(G, n);
			seeds[i].setRotation(-90+t*360);
			seeds[i].setSize(12);
			seeds[i].setZ(-1);
		}

		color = new Color(.4f, .4f, 0, .5f);
		giveShield = false;
		if (Math.random() < 0.02) {
			color = G.colors.blue;
			giveShield = true;
		}
	}

	public boolean tick (float dt, float dx) {
		position.x += dx;
		for (int i = 0; i < numSeeds; i++) {
			if (seeds[i] == null) continue;
			seeds[i].addX(dx);
		}

		float difference = angle.angle() - 90;
		if (difference > 0)
			dangle -= Math.min(0.5, difference);
		if (difference < 0)
			dangle -= Math.max(-0.5, difference);
		dangle *= 0.95;
		angle.rotate(dangle * G.settings.speed);

		offScreen = (position.x < -size * 2 
				|| position.x > G.width + size * 2);

		return position.x < -4 * G.width
			|| position.x > 5 * G.width;
	}

	public void loseSeed (Vector2 v, int i) {
		float r = radial(i) * ((float) Math.random() + 1) / 2;
		float t = theta(i) * (float) Math.PI * 2;
		seeds[i].setVelocity(v.x + (float) Math.cos(t) * r,
				v.y + (float) Math.sin(t) * r);
		seeds[i].addPosition(angle);
		G.settings.getSeasons()[0].addStar(seeds[i]);
		seeds[i] = null;
		G.dandelionFX(v.len() / 100);
	}

	public void draw () {
		Color tmp = G.sb.getColor();
		draw(tmp.a);
		G.sb.setColor(tmp);
	}

	Vector2 save = new Vector2();
	public void draw (float alpha) {
		if (offScreen) return;

		float px = angle.x + position.x - radius;
		float py = angle.y + position.y - radius;

		G.sb.setColor(.4f, .4f, 0, .5f * alpha);
		G.sb.draw(G.dotTexture, px + radius - 1, py + radius, 0, 0,
				-2, angle.len(), 1, 1, angle.angle() + 90,
				0, 0, 1, 1, false, false);
		Color col = new Color(color);
		col.a *= alpha;
		G.sb.setColor(col);
		G.sb.draw(G.lightTexture, px, py, size, size);

		Vector2 v = G.glider.getVelocity();
		Vector2 p = G.glider.getPosition();

		if (withSeed) {
			for (int i = 0; i < numSeeds; i++) {
				if (seeds[i] == null) continue;
				seeds[i].setAngle(angle);
				seeds[i].draw(alpha);
				boolean c = G.glider.collideWith(seeds[i], false);
				seeds[i].unsetAngle();
				if (c) {
					loseSeed(v, i);
				}
			}
		}

		save.set(position);
		position.add(angle);

		if (G.glider.collideWith(this, false)) {
			if (withSeed && giveShield) {
				G.sb.end();
				G.glider.oneUp();
				G.sb.begin();
			}

			dangle = -v.x;

			if (withSeed) {
				G.glider.hitDandelion();
				Vector2 real = new Vector2();
				for (int i = 0; i < numSeeds; i++) {
					if (seeds[i] == null) continue;
					loseSeed(v, i);
				}

				withSeed = false;
			}
		} else if (p.y > 0 && Math.abs(angle.angle() - 90) < .1
				&& G.glider.below(position)) {
		    G.twangFX(v.len() / 100);
			dangle = -v.x * p.y / angle.len();
			if (withSeed) {
				for (int i = 0; i < numSeeds; i++) {
					if (seeds[i] == null) continue;
					if (Math.random() < Math.abs(dangle) / G.width * 8) {
						loseSeed(v, i);
					}
				}
			}
		}
		position.set(save);
	}

	public boolean isStatic () {
		return true;
	}
}
