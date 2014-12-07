package org.anism.lotw.goodies;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import org.anism.lotw.goodies.*;
import org.anism.lotw.goodies.powerups.*;
import org.anism.lotw.Glob;

public class Goodies extends Object {
	// class should hold all the goodies, and know how to update them and collisions
	
	Glob G;

	List<Goody> goodies;
	List<Goody> forDeletion;
	//List<Star> stars;
	Star[] stars;
	Stack<Integer> blankStars;

	float starChance = 0.05f;
	int makeExtra = 4;
	int extraWidth = makeExtra * 2 + 1;
	int numStars;

	public Goodies (Glob g) {
		G = g;

		saltStars();

		clear();
	}

	public void saltStars () {
		//List<Star> startStars = new ArrayList<Star>();
		numStars = (int) ((8 / G.settings.starVel()) * G.height * starChance * extraWidth);
		blankStars = new Stack<Integer>();

		for (int i = 0; i < numStars; i++) {
			blankStars.push(i);
		}

		stars = new Star[numStars];
		for (int i = 0; i < numStars / 2; i++) {
			//stars.add(
			stars[blankStars.pop()] = newStar(
					new Vector2((int) (extraWidth * G.width * 
							Math.random()) - makeExtra * G.width,
						(int) (G.height * Math.random())));
		}
		/*
		int size = 2 * (int) Math.pow(2, Math.floor(Math.log(numStars) / Math.log(2)));
		stars = new stars[size];
		STARS = 0;
		for (Iterator it = startStars.iterator(); it.hasNext(); ) {
			stars[++STARS] = it.next();
		}
		*/
	}

	public void tick (float dt) {
		tick(dt, true);
	}

	public void tick (float dt, boolean spawn) {
		tick(dt, spawn, 0);
	}

	public void tick (float dt, boolean spawn, float dx) {
		for (int i = 0; i < numStars; i++) {
			Star s = stars[i];
			if (s == null) continue;
			if (s.tick(dt, dx)) {
				stars[i] = null;
				blankStars.push(i);
			}
		}

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
				//forDeletion.add(g);

				if (g.points > 0) {
					if (G.settings.heaven()) {
						G.glider.damage();
					}
				}

				if (G.settings.hell() && g.points < 0) {
					G.glider.givePoints(-g.points);
				}
			}
			if (G.glider.collideWith(g)) {
				g.collide(G.glider);
				d = true;
				//forDeletion.add(g);
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

		double chance = extraWidth * starChance;
		while (Math.random() < chance) {
			chance--;
			stars[blankStars.pop()] = newStar();
		}
		/*
		if (Math.random() < 0.01) {
			Star s = newShootingStar();
			stars.add(s);
		}
		*/
	}

	public void clean () {
		for (Iterator it = forDeletion.iterator(); it.hasNext(); ) {
			Goody g = (Goody) it.next();
			goodies.remove(g);
		}
		forDeletion = new ArrayList<Goody>();
	}

	public void clear () {
		goodies = new ArrayList<Goody>();
		forDeletion = new ArrayList<Goody>();
	}

	public PowerUp newPowerUp (int x, float vy) {
		return new Shields(G, x, vy, G.shieldTexture);
	}

	public Star newShootingStar () {
		int x = (int) Math.floor(Math.random() * (G.width - G.size)) + G.size / 2;
		int y = (int) Math.floor(Math.random() * (G.height - 16)) + 8;

		return new ShootingStar(G, x, y);
	}

	public Star newStar () {
		int x = (int) Math.floor(Math.random() * (extraWidth * G.width - G.size)) + G.size / 2 - makeExtra * G.width;

		if (G.settings.space())
			return new Star(G, x);
		if (G.settings.winter())
			return new Snow(G, x);
		return new Star(G, x);
	}

	public Star newStar (Vector2 p) {
		if (G.settings.space())
			return new Star(G, p);
		if (G.settings.winter())
			return new Snow(G, p);
		return new Star(G, p);
	}

	public Goody newGoody () {
		int x = (int) Math.floor(Math.random() * (extraWidth * G.width - G.lightSize)) + G.lightSize/2 - makeExtra * G.width;
		int vy = (int) Math.floor(Math.random() * 3) + 1;
		vy = -vy;

		// extra life:
		if (Math.random() < G.glider.livesChance())
			return new OneUp(G, x, vy, G.goodyTexture);

		// snitch:
		if (G.settings.normal() && Math.random() < 0.001)
			return new Snitch(G, x, G.goodyTexture);

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

	public void drawStars () {
		G.sb.begin();
		G.sb.setColor(1, 1, 1, 1);
		for (int i = 0; i < numStars; i++) {
			Star s = stars[i];
			if (s == null) continue;
			s.draw();
		}
		/*
		for (Iterator it = stars.iterator(); it.hasNext(); ) {
			Star s = (Star) it.next();
			s.draw();
		}
		*/
		G.sb.end();
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
