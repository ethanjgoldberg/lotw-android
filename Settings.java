package org.anism.lotw;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import org.anism.lotw.Mode;
import org.anism.lotw.goodies.*;
import org.anism.lotw.Season;

public class Settings extends Object {
	enum ControlScheme {
		TWO_TOUCH, TILT, ONE_TOUCH;
	}
	public boolean setSeason (Season s) { 
		if (season == s) return false;
		oldSeason = season;
		season = s;
		String val = s.toString();
		seasonVal = Seasons.valueOf(val.toUpperCase());
		if (unlock && seasonIndex(seasonVal) > seasonLock) {
			seasonLock = seasonIndex(seasonVal);
		}
		save();
		return true;
	}
	public void setSeasonStart () {
		if (! unlock) return;
		setSeason(seasons[seasonIndex(Seasons.SUMMER)]);
	}
	public Season getSeason () { return season; }
	public Star newStar (int x) { return season.newStar(x); }
	public Star newStar (Vector2 p) { return season.newStar(p); }
	public float getStarChance () { return season.getStarChance(); }
	public float getStarVel () { return season.getStarVel(); }
	public Color getColor () { return season.getColor(); }
	public Color getStarColor () { return season.getStarColor(); }
	public Color getGliderColor () { return season.getGliderColor(); }
	public void drawBackground () { season.drawBackground(); }
	public Camera getCam () { return season.getCam(); }
	public void tickStars (float dt, float dx) { season.tickStars(dt, dx); }
	public void saltStars () { season.saltStars(); }
	public void drawStars () { season.drawStars(); }
	public void addStar (Star s) { season.addStar(s); }
	class Space implements Season {
		float starChance;
		float starVel;
		Color color;
		Color starColor;
		Color gliderColor;
		// name should be the enum val, with possibly different capitalization.
		String name;
		Texture icon;
		float starAlpha;
		public int numStars;
		public Star[] stars;
		Stack<Integer> blankStars;

		int makeExtra = 4;
		int extraWidth = makeExtra * 2 + 1;

		public void init () {
			starChance = 0.05f;
			starVel = 0.65f;
			color = new Color(0x000022ff);
			starColor = new Color(0xffffffff);
			gliderColor = new Color(G.lightColor);
			name = "Space";
			icon = G.spaceIcon;
		}

		public Space () {
			init();
			saltStars();
		}

		public float getStarChance () { return starChance; }
		public float getStarVel () { return starVel; }
		public Color getColor () { return color; }
		public Color getStarColor () { return starColor; }
		public Color getGliderColor () { return gliderColor; }
		public Texture getIcon () { return icon; }
		public String getName () { return name; }
		public Camera getCam () { return null; }
		public void drawStars () {
			Color c = new Color(getStarColor());
			c.a *= G.sb.getColor().a;
			G.sb.setColor(c);
			for (int i = 0; i < numStars; i++) {
				Star s = stars[i];
				if (s == null) continue;
				s.draw();
			}
		}
		public void saltStars () {
			blankStars = new Stack<Integer>();
			numStars = (int) ((8 / getStarVel()) * G.height * getStarChance() * extraWidth);
			for (int i = 0; i < numStars; i++) {
				blankStars.push(i);
			}
			stars = new Star[numStars];
			//stars = new Star[(int) (2048 * G.unit * G.unit)];

			saltArea(-makeExtra * G.width, (makeExtra + 1) * G.width);
		}
		public void saltArea (int xMin, int xMax) {
			float starChance = getStarChance();
			float wide = xMax - xMin;
			float numToAdd = (4 / getStarVel()) * G.height * starChance * wide / G.width;

			while (numToAdd > 1 || Math.random() < numToAdd) {
				//stars.add(
				addStar(newStar(new Vector2((int) (wide * Math.random()) + xMin,
								(int) (G.height * Math.random()))));
				numToAdd--;
			}
		}
		public void addStar (Star s) {
			int n = blankStars.pop();
			stars[n] = s;
			if (s == null) {
				blankStars.push(n);
				return;
			}
			s.setIndex((float) n / numStars);
		}
		public void tickStars (float dt, float dx) {
			int integerDx = -(int) dx;
			if (dx < 0) integerDx++;
			if (integerDx > 0) {
				saltArea(G.width * (makeExtra+1), G.width * (makeExtra+1) + integerDx);
			} else if (integerDx < 0) {
				saltArea(-G.width * makeExtra + integerDx, -G.width * makeExtra);
			}

			for (int i = 0; i < numStars; i++) {
				Star s = stars[i];
				if (s == null) continue;
				if (s.tick(dt, dx)) {
					stars[i] = null;
					blankStars.push(i);
				}
			}

			double chance = extraWidth * getStarChance();
			while (Math.random() < chance) {
				chance--;
				try {
					addStar(newStar());
				} catch (EmptyStackException e) {}
			}
		}

		public Star newStar () {
			int x = (int) Math.floor(Math.random() * (extraWidth * G.width - G.size)) + G.size / 2 - makeExtra * G.width;

			return newStar(x);
		}

		public Star newStar (int x) {
			return new Star(G, x);
		}

		public Star newStar (Vector2 p) {
			return new Star(G, p);
		}

		public void drawBackground () {
			Color tmp = G.sb.getColor();
			G.sb.setColor(1, 1, 1, 0.1f * tmp.a);
			G.sb.draw(G.skyTexture, 0, -60, G.width, G.height + 60);
			G.sb.setColor(tmp);
		}

		public String toString () {
			return name;
		}
	}
	class ComputerDream extends Space {
		float mag;
		float size;
		Color c;

		public void init () {
			starChance = .0002f;
			// times two because the stars are so huge
			starVel = 1.5f / 64;
			color = new Color(0x5156DFFF);
			starColor = new Color(1, 1, 1, .5f);
			gliderColor = new Color(G.darkColor);
			name = "Computer Dream";
			icon = G.summerIcon;

			mag = Math.max(G.width, G.height) * 2;
			size = mag * 4;
			c = new Color(0, .2f, 1, 1);
		}

		public ComputerDream () {
			super();
		}

		public Star newStar (int x) {
			return new Cloud(G, x);
		}

		public Star newStar (Vector2 p) {
			return new Cloud(G, p);
		}
	}
	class Summer extends Space {
		public void init () {
			starChance = .05f;
			starVel = 2.5f;
			color = new Color(0x5156DFFF);
			starColor = new Color(1, 1, 1, 1);
			gliderColor = new Color(G.darkColor);
			name = "Summer";
			icon = G.summerIcon;
		}

		public Summer () {
			super();
		}

		public Star newStar (int x) {
			return new Seed(G, x);
		}

		public Star newStar (Vector2 p) {
			if (Math.random() < 0.05)
				return new Dandelion(G, (int) p.x);
			return new Seed(G, p);
		}
	}
	class Fall extends Space {
		public void init () {
			starChance = 0.001f;
			starVel = 0.5f;
			color = new Color(0x993322ff);
			starColor = new Color(0, 0, 0, 0.5f);
			gliderColor = new Color(G.lightColor);
			name = "Fall";
			icon = G.fallIcon;
		}

		public Fall () {
			super();
		}

		public Star newStar (int x) {
			return new GliderStar(G, x);
		}

		public Star newStar (Vector2 p) {
			return new GliderStar(G, p);
		}
	}
	class Winter extends Space {
		public void init () {
			starChance = 0.05f;
			starVel = 2.5f;
			color = new Color(0x88cceeff);
			starColor = new Color(0xffffffff);
			gliderColor = new Color(G.darkColor);
			name = "Winter";
			icon = G.winterIcon;
		}

		public Winter () {
			super();
		}

		public Star newStar (int x) {
			return new Snow(G, x);
		}

		public Star newStar (Vector2 p) {
			return new Snow(G, p);
		}
	}
	class Spring extends Space {
		float offset = 0;
		Color defColor;
		Color lightningColor;
		float lightning;

		public void init () {
			starChance = 0.4f;
			starVel = 8.5f;
			starColor = new Color(0x00000033);
			gliderColor = new Color(1, 1, 1, .5f);
			name = "Spring";
			icon = G.springIcon;

			defColor = new Color(0x447766ff);
			color = defColor;
			lightningColor = new Color(0xffffffff);
			lightning = 0;
		}

		public Spring () {
			super();
		}

		public Star newStar (int x) {
			return new Rain(G, x);
		}

		public Star newStar (Vector2 p) {
			return new Rain(G, p);
		}

		public Color getGliderColor () {
			return gliderColor;
		}

		public void drawBackground () {
			// lightning:
			if (lightning > 0) lightning -= 0.0001;
			if (Math.random() < 0.001) lightning = 0.02f;
			if (Math.random() < lightning) {
				color = new Color(lightningColor);
				gliderColor = new Color(0, 0, 0, 0.5f);
			} else {
				color.lerp(defColor, 0.02f);
				gliderColor.lerp(new Color(1, 1, 1, 0.5f), 0.02f);
			}

			// hills:
			if (! G.paused) offset -= G.vx / 4;
			Color c = new Color(color);
			Color tmp = G.sb.getColor();
			Color target = new Color(0, 0.2f, 0, tmp.a);
			for (int j = 4; j > 0; j--) {
				float o = offset / j + j * 70;
				int w = (int) (96 * 4 / j);
				int start = (int) (o % w);
				int height = 48;
				float bottom = 96 - height/j - 64 / j;
				if (o < 0)
					start += w;
				c.lerp(target, 0.3f);
				c.a *= tmp.a;
				G.sb.setColor(c);
				G.sb.draw(G.dotTexture, 0, 0, G.width, bottom);
				for (int i = -start; i < G.width; i += w) {
					G.sb.draw(G.sineTexture, i, bottom,
							w, height / j);
				}
			}
			G.sb.setColor(tmp);
		}
	}
	class Unlock extends Space {
		public Unlock () {
			name = "Unlock";
			icon = G.unlockIcon;
		}
	}
	public Season unlockSeason;
	Season[] seasons;
	public Season[] getSeasons () {
		return seasons;
	}
	public Season[] getUnlockedSeasons () {
		if (unlockSeason == null) unlockSeason = new Unlock();
		Season[] ret = new Season[seasonLock + 1];
		ret[0] = unlockSeason;
		for (int i = 1; i < seasonLock + 1; i++) {
			ret[i] = seasons[i-1];
		}
		return ret;
	}
	enum Seasons {
		SUMMER, FALL, WINTER, SPRING, SPACE;
	}
	Glob G;
	public ControlScheme controlScheme;
	public Seasons seasonVal;
	public Season season;
	public Season oldSeason;
	public int difficulty = 7;
	public int diff = 1;
	public Mode mode = Mode.NORMAL;
	public float speed = 1;
	public boolean colorblind = false;
	public boolean bottomOut = true;
	public boolean autologin = true;
	public boolean ninjaMode = false;

	public Settings (Glob g, ControlScheme cs) {
		G = g;
		controlScheme = cs;
	}

	public Settings (Glob g) {
		this(g, ControlScheme.TWO_TOUCH);
	}

	public void initSeasons () {
		seasons = new Season[] {
			new Summer(), 
			    new Fall(),
			    new Winter(),
			    new Spring(), 
			    new Space(),
		};
	}

	public void setMode (int m) {
		if (m < 0) {
			setMode(Mode.HELL);
		} else if (m == 0) {
			setMode(Mode.NORMAL);
		} else if (m > 0) {
			setMode(Mode.HEAVEN);
		}
	}

	public void setMode (Mode m) {
		mode = m;
		save();
		if (mode == Mode.NORMAL) {
			speed = 0.85f;
			return;
		} 
		if (mode == Mode.HELL) {
			speed = 0.6f;
			return;
		}
		speed = 0.25f;
	}

	public int seasonIndex () {
		return seasonIndex(seasonVal);
	}

	public int seasonIndex (Seasons val) {
		if (val == Seasons.SUMMER)
			return 0;
		if (val == Seasons.FALL)
			return 1;
		if (val == Seasons.WINTER)
			return 2;
		if (val == Seasons.SPRING)
			return 3;
		if (val == Seasons.SPACE)
			return 4;
		return -1;
	}

	public void initSeason () {
		Gdx.app.log("", unlock + " " + seasonVal);
		season = seasons[seasonIndex()];
	}
	public Season nextSeason () {
		if (seasonVal == Seasons.SUMMER)
			seasonVal = Seasons.FALL;
		else if (seasonVal == Seasons.FALL)
			seasonVal = Seasons.WINTER;
		else if (seasonVal == Seasons.WINTER)
			seasonVal = Seasons.SPRING;
		else if (seasonVal == Seasons.SPRING)
			seasonVal = Seasons.SUMMER;
		//initSeason();
		return seasons[seasonIndex()];
	}
	public boolean goToSpace () {
		if (! unlock) return false;
		if (! setSeason(seasons[seasonIndex(Seasons.SPACE)])) {
			seasonLock = seasonIndex(Seasons.SPACE) + 1;
			return false;
		}
		return true;
	}

	public boolean twoTouch () {
		return controlScheme == ControlScheme.TWO_TOUCH;
	}
	public boolean tilt () {
		return controlScheme == ControlScheme.TILT;
	}
	public boolean oneTouch () {
		return controlScheme == ControlScheme.ONE_TOUCH;
	}
	public void setControl (String scheme) {
		controlScheme = ControlScheme.valueOf(scheme);
		save();
	}

	public boolean normal () {
		return mode == Mode.NORMAL;
	}
	public boolean hell () {
		return mode == Mode.HELL;
	}
	public boolean heaven () {
		return mode == Mode.HEAVEN;
	}

	public String modeString () {
		if (mode == Mode.HEAVEN) return "Heaven";
		if (mode == Mode.HELL) return "Hell";
		return "Normal";
	}

	int seasonLock;
	public Boolean unlock;

	public void save () {
		Preferences prefs = Gdx.app.getPreferences("org.anism.lotw.settings");
		prefs.putBoolean("colorblind", colorblind);
		prefs.putBoolean("ninjaMode", ninjaMode);
		prefs.putBoolean("autologin", autologin);
		prefs.putBoolean("bottomOut", bottomOut);
		prefs.putString("controls", controlScheme.name());
		prefs.putString("season", seasonVal.name());
		prefs.putBoolean("unlock", unlock);
		prefs.putString("mode", mode.name());
		prefs.putInteger("seasonLock", seasonLock);

		prefs.flush();
	}

	public void read () {
		Preferences prefs = Gdx.app.getPreferences("org.anism.lotw.settings");
		colorblind = prefs.getBoolean("colorblind", false);
		ninjaMode = prefs.getBoolean("ninjaMode", false);
		autologin = prefs.getBoolean("autologin", true);
		bottomOut = prefs.getBoolean("bottomOut", true);
		controlScheme = ControlScheme.valueOf(prefs.getString("controls", "TWO_TOUCH"));
		seasonVal = Seasons.valueOf(prefs.getString("season", "SUMMER"));
		unlock = prefs.getBoolean("unlock", true);
		if (unlock) seasonVal = Seasons.SUMMER;
		seasonLock = prefs.getInteger("seasonLock", 0);
		mode = Mode.valueOf(prefs.getString("mode", "NORMAL"));

		initSeason();
		setMode(mode);
	}
}
