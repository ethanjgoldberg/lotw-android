package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

import org.anism.lotw.goodies.Star;
import org.anism.lotw.goodies.Clouds;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Clouds extends Star {
	Cloud[] clouds;
	int numClouds;

	/*
	public Clouds (Glob G, int x, float vy, Texture t) {
		super(G, x, vy, t);
	}
	*/

	public Clouds (Glob G, int x, int y, float vy) {
		super(G, x, vy, G.starTexture);

		if (vy > 0) vy = -vy;

		velocity.set(-(float) Math.random() / 32, vy/16);

		size = Math.max(G.width, G.height) / 4;
		position.set(x - size / 2, y - size / 2);

		numClouds = (int) Math.floor(Math.random() * 6) + 3;
		clouds = new Cloud[numClouds];
		int atCloud = 0;

		for (int i = 0; i < numClouds; i++) {
			// init the clouds

			float s = (float) Math.random() * 64 + 64;
			Vector2 p = new Vector2((float) Math.random() * size, 
					(float) Math.random() * size);

			int z = -64;

			for (int j = 0; j < atCloud; j++) {
				if (clouds[j].cloudCollide(p, s)) {
					z = (int) Math.max(z, clouds[j].z + clouds[j].size);
				}
			}

			//Cloud n = new Cloud(G, p.add(position), s, z);
			//clouds[i] = n;
			atCloud++;
		}
	}

	public Clouds (Glob G, Vector2 p) {
		this(G, (int) p.x, (int) p.y, (float) (1 + Math.random()));
	}

	public Clouds (Glob G, int x) {
		this(G, x, G.height, (float) (1 + Math.random()));
	}

	public boolean tick (float dt, float dx) {
		position.add(velocity);

		offScreen = (position.x < -size || position.x > G.width + size);

		for (int i = 0; i < numClouds; i++) {
			clouds[i].tick(dt, dx);
			clouds[i].offset(velocity);
		}

		return position.y < -size 
				|| position.x < -4 * G.width - size
				|| position.x > 5 * G.width + size;
	}

	public void draw () {
		for (int i = 0; i < numClouds; i++) {
			clouds[i].draw();
		}
	}
}
