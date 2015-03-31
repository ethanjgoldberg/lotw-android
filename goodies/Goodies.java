package org.anism.lotw.goodies;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import org.anism.lotw.goodies.*;
import org.anism.lotw.goodies.powerups.*;
import org.anism.lotw.Glob;
import org.anism.lotw.Glider;

public class Goodies extends Object {
	// class should hold all the goodies, and know how to update them and collisions
	
	Glob G;

	List<Goody> goodies;
	//List<Star> stars;

	int makeExtra = 4;
	int extraWidth = makeExtra * 2 + 1;

	public Goodies (Glob g) {
		G = g;

		clear();
	}

	public void tick (float dt) {
		tick(dt, true);
	}

	public void tick (float dt, boolean spawn) {
		tick(dt, spawn, 0);
	}

	public void tick (float dt, boolean spawn, float dx) {
		/*
		List<Star> starsTmp = new ArrayList<Star>();
		for (Iterator it = stars.iterator(); it.hasNext(); ) {
			Star s = (Star) it.next();
			if ( ! s.tick(dt, dx) ) {
				starsTmp.add(s);
			}
		}
		stars = new ArrayList<Star>(starsTmp);
		*/

		List<Goody> goodiesTmp = new ArrayList<Goody>();
		for (Iterator it = goodies.iterator(); it.hasNext(); ) {
			Goody g = (Goody) it.next();
			boolean d = false;
			if (g.tick(dt, dx)) {
				d = true;

				if (g.points > 0) {
					if (G.settings.heaven()) {
						G.glider.damage();
					}
				}
			}
			if (G.glider.collideWith(g, true)) {
				if (g.collide(G.glider))
					d = true;
			}
			if ( ! d ) {
				goodiesTmp.add(g);
			}
		}
		goodies = new ArrayList<Goody>(goodiesTmp);

		if (spawn) {
			double chance = extraWidth * G.newGoodyChance();
			while (Math.random() < chance) {
				Goody g = newGoody();
				goodies.add(g);
				G.goodyAdded();
				chance--;
			}
		}
		/*
		   if (Math.random() < 0.01) {
		   Star s = newShootingStar();
		   stars.add(s);
		}
		*/
	}

	public void clear () {
		goodies = new ArrayList<Goody>();
	}

	public PowerUp newPowerUp (int x, float vy) {
		return new Shields(G, x, vy, G.shieldTexture);
	}

	public Star newShootingStar () {
		int x = (int) Math.floor(Math.random() * (G.width - G.size)) + G.size / 2;
		int y = (int) Math.floor(Math.random() * (G.height - 16)) + 8;

		return new ShootingStar(G, x, y);
	}

	public Goody newGoody () {
		int x = (int) Math.floor(Math.random() * (extraWidth * G.width - G.lightSize)) + G.lightSize/2 - makeExtra * G.width;
		int vy = (int) Math.floor(Math.random() * 3) + 1;
		vy = -vy;

		// hellMarker:
		if (G.settings.hell() && G.timeForHellMarker())
			return new HellMarker(G);

		// extra life:
		if (Math.random() < G.glider.livesChance())
			return new OneUp(G, x, vy, G.goodyTexture);

		// snitch:
		if (G.settings.normal() && Math.random() < 0.001)
			return new Snitch(G, x, G.goodyTexture);

		// goal:
		/*
		if (Math.random() < 0.05) {
			Goal g = new Goal(G, x, -1, null);
			float w = g.getWidth();
			goodies.add(g);
			goodies.add(new Baddy(G, x, -1, -1, g));
			if (Math.random() < 0.3) {
				Baddy b = new Baddy(G, x + (int) (w / 2), -1, null);
				b.getPosition().y += (int) (w / 2);
				goodies.add(b);

				g.setPoints(10);
			}
			return new Baddy(G, x + (int) w, -1, 1, g);
		}
		*/

		// power up (shield):
		if (!G.settings.heaven() && Math.random() < 0.015)
			return newPowerUp(x, vy);

		// multiplier:
		if (!G.settings.heaven() && Math.random() < G.glider.multiplierChance())
			return new Multiplier(G, x, vy, G.multiplierTexture);

		// goody:
		if (G.settings.heaven() || (!G.settings.hell() && Math.random() < G.greenChance()))
			return new Goody(G, x, vy, G.goodyTexture);

		// baddy:
		return new Baddy(G, x, vy, G.goodyTexture);
	}

	public void draw () {
		G.sb.begin();
		for (Iterator it = goodies.iterator(); it.hasNext(); ) {
			Goody g = (Goody) it.next();
			g.draw();
		}
		G.sb.end();

		/*
		float h = (float) (G.newGoodyChance() * G.height);
		h *= 4;
		G.sr.begin(ShapeType.Line);
		G.sr.line(0, h, 20, h);
		G.sr.end();
		*/
	}

	/*
	public void save () {
		Preferences prefs = new Preferences("org.anism.lotw.goodies");
		prefs.putInteger("goodies.size", goodies.size());
		int i = 0;
		for (Iterator it = goodies.iterator(); it.hasNext(); ) {
			Goody g = (Goody) it.next();
			g.save(i);
			i++;
		}
		prefs.flush();
	}

	public Goody restoreGoody (int i) {

	}

	public void restore () {
		Preferences prefs = new Preferences("org.anism.lotw.goodies");
		int size = prefs.getInteger("goodies.size", 0);
		for (int i = 0; i < size; i++) {
			Goody g = new Goody();
			g.restore(i);
			goodies.add(g);
		}
	}
	*/
}
