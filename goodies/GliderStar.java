package org.anism.lotw.goodies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class GliderStar extends Star {
	Vector2 rotation;
	Vector2 gravity;

	float z_level;
	float rotationSpeed;
	float halfHeight;

	public GliderStar (Glob G) {
		super(G, 0, 0, G.starTexture);

		gravity = new Vector2(0.f, -0.1f);
		velocity = new Vector2();
		rotation = new Vector2(1, 0);

		z_level = -(float) (3 * Math.random() + 1);
		rotationSpeed = 0.05f;

		radius = -15 / z_level;
		size = 2 * radius;
		halfHeight = 4 / z_level;
	}

	public GliderStar (Glob G, int x) {
		this(G);
		position.set(x, G.height + size);
	}

	public GliderStar (Glob G, Vector2 p) {
		this(G);
		position.set(p);
	}

	float currentTurn = 0;

	float control () {
		if (velocity.y < 0 && currentTurn == 0 && Math.random() < 0.01) {
			currentTurn -= Math.signum(rotation.angle());
			return currentTurn;
		}
		if (Math.random() > 0.01) return currentTurn;
		currentTurn += (Math.random() < 0.5? 1: -1) * (Math.random() < 0.5? 2: 1);
		currentTurn = (float) Math.signum(currentTurn);
		return currentTurn;
	}

	Vector2 vel = new Vector2();

	public boolean tick (float dt, float dx) {
		position.x -= dx * (1.5f / z_level);

		Vector2 wind = G.glider.getWindAt(position);
		Vector2 rot = new Vector2(rotation);
		rot.rotate90(1);

		vel.set(velocity);
		position.add(vel.scl(-3 / z_level));
		velocity.add(gravity);

		Vector2 effectiveWind = wind.add(velocity);
		float windMagnitude = effectiveWind.dot(rot);
		velocity.sub(rot.scl(windMagnitude));

		rotation.rotateRad(control() * rotationSpeed);

		offScreen = (position.x < -size || position.x > G.width + size);

		return position.y < -G.height/2 - size 
			|| position.x < -4 * G.width 
			|| position.x > 5 * G.width;
	}

	public void draw () {
		if (offScreen) return;

		G.sb.draw(G.dotTexture, position.x, position.y, 0, 0,
				size, halfHeight, 1, 1, rotation.angle(),
				0, 0, 1, 1, false, false);
	}
}
