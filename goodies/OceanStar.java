package org.anism.lotw.goodies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import org.anism.lotw.goodies.Goody;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class OceanStar extends Star {
	float theta;
	float gamma;
	Vector3 pos;
	Vector3 pole;
	Vector2 drawAt = new Vector2();

	static Quaternion quat;

	public OceanStar(Glob G, Quaternion q) {
		super(G, 0, 0, G.starTexture);

		position.set(0, 0);

		float theta = (float) (2 * Math.PI * Math.random());
		float gamma = (float) Math.acos(2 * Math.random() - 1);

		pos = new Vector3((float) (Math.cos(theta) * Math.sin(gamma)),
				(float) (Math.sin(theta) * Math.sin(gamma)),
				(float) Math.cos(gamma));

		pole = new Vector3(1, 1, 1);
		if (quat == null)
			quat = q;

		radius = 3 * (float) (0.5 + Math.random());
		size = 2*radius;

		offScreen = true;
	}

	Vector3 tmp = new Vector3();

	public boolean tick (float dt, float dx) {
		//super.tick(dt, -dx * velocity.y);
		
		pos.mul(quat);
		
		tmp.set(pos);
		G.settings.getCam().project(tmp);
		drawAt.set(tmp.x, tmp.y);

		offScreen = (pos.y < 0 
				|| drawAt.x < -size || drawAt.x > G.width + size
				|| drawAt.y > G.height + size || drawAt.y < G.height/2 - size);

		return false;
	}

	public void draw () {
		if (offScreen) return;

		float px = drawAt.x - radius;
		float py = drawAt.y - radius;

		G.sb.draw(texture, px, py, size, size);
	}
}
