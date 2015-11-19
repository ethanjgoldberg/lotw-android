package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.anism.lotw.goodies.Star;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Seed extends Star {
	float rotation;
	float spin;

	float phase;
	float z;

	public Seed (Glob G, int x, float vy) {
		super(G, x, vy);
		if (vy > 0) vy = -vy;
		texture = G.lightTexture;

		radius = -.8f * (float) vy * (float) (1.5 + Math.random());
		size = 2 * radius;
		drawTrail = false;

		rotation = 0;
		spin = (float) (Math.random() - 0.5f) * 2;

		phase = (float) (Math.random() * Math.PI);

		Color[] colors = new Color[] {
			new Color(.5f, .2f, 0, 1),
			    new Color(.2f, .5f, 0, 1),
			    new Color(.35f, .35f, 0, 1)
		};
		color = colors[(int) (Math.random() * 3)];
		velocity = new Vector2(0, vy / 8);
		z = vy / 3;
	}

	public Seed (Glob G, Vector2 p) {
		this(G, (int) p.x);
		position.set(p);
	}

	public Seed (Glob G, int x) {
		this(G, x, (float) Math.random() + 2);
	}

	Vector2 accel = new Vector2(0, -0.01f);

	float center (float angle) {
		while (angle > 180) angle -= 360;
		while (angle < 180) angle += 360;
		return angle;
	}

	Vector2 vel = new Vector2();

	public boolean tick (float dt, float dx) {
		//super.tick(dt, -dx * velocity.y);
		position.x -= dx * z;
		vel.set(velocity).scl(G.settings.speed);
		position.add(vel);
		offScreen = (position.x < -size * 2 
				|| position.x > G.width + size * 2);

		accel.x += ((Math.random() > 0.5? 1: -1) - Math.signum(accel.x) * 0.2) / 512;
		accel.y += ((Math.random() > 0.5? 1: 0) / 1024);
		velocity.add(accel);
		velocity.scl(0.99f);

		float vAngle = center(-center(velocity.angle() - 90) - 180);
		if (rotation > vAngle)
			spin -= 0.1;
		if (rotation < vAngle)
			spin += 0.1;
		spin *= 0.99;
		rotation += spin;
		rotation = center(rotation);

		return position.y < -radius
			|| position.x < -4 * G.width
			|| position.x > 5 * G.width
			|| position.y > 2 * G.height + size && velocity.y > 0;
	}

	Vector2 oldPos = new Vector2();
	public void setAngle (Vector2 angle) {
		oldPos.set(position);
		position.add(angle);
	}
	public void unsetAngle () {
		position.set(oldPos);
	}

	public void draw () {
		Color tmp = G.sb.getColor();
		draw(tmp.a);
		G.sb.setColor(tmp);
	}

	public void draw (float a) {
		if (offScreen) return;

		float px = position.x;
		float py = position.y;

		/*
		G.sb.setColor(0, 0, 0, .25f);
		G.sb.draw(G.dotTexture, 
				px - size / 16, py, 0, 0,
				size / 8, -2 * size/3, 1, 1, rotation,
				0, 0, 1, 1, false, false);
				*/
		G.sb.setColor(1, 1, 1, .25f * a);
		G.sb.draw(G.lightTexture, px - size/2, py - size/2, size, size);
	}

	public void setRotation (float r) {
		rotation = r;
	}

	public void setZ (float _z) {
		z = _z;
	}
}
