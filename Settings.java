package org.anism.lotw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;

import org.anism.lotw.Mode;

public class Settings extends Object {
	enum ControlScheme {
		TWO_TOUCH, TILT, ONE_TOUCH;
	}
	enum Seasons {
		SPACE, WINTER;
	}
	public ControlScheme controlScheme;
	public Seasons season;
	public int difficulty = 7;
	public int diff = 1;
	public Mode mode = Mode.NORMAL;
	public float speed = 1;
	public boolean colorblind = false;
	public boolean bottomOut = true;
	public boolean autologin = true;
	public boolean ninjaMode = false;

	public Settings (ControlScheme cs) {
		controlScheme = cs;
	}

	public Settings () {
		controlScheme = ControlScheme.TWO_TOUCH;
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
		if (mode == Mode.NORMAL) {
			speed = 1;
			return;
		} 
		if (mode == Mode.HELL) {
			speed = 0.5f;
			return;
		}
		speed = 0.25f;
	}

	public boolean winter () {
		return season == Seasons.WINTER;
	}
	public boolean space () {
		return season == Seasons.SPACE;
	}
	public float starVel () {
		if (winter()) return 2.5f;
		if (space()) return 0.65f;
		return 0.3f;
	}
	Color winterColor = new Color(0x88cceeff);
	Color spaceColor = new Color(0x000022ff);
	public Color bgColor () {
		if (winter()) return winterColor;
		if (space()) return spaceColor;
		return spaceColor;
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

	public void save () {
		Preferences prefs = Gdx.app.getPreferences("org.anism.lotw.settings");
		prefs.putBoolean("colorblind", colorblind);
		prefs.putBoolean("ninjaMode", ninjaMode);
		prefs.putBoolean("autologin", autologin);
		prefs.putBoolean("bottomOut", bottomOut);
		prefs.putString("controls", controlScheme.name());
		prefs.putString("season", season.name());
		prefs.flush();
	}

	public void read () {
		Preferences prefs = Gdx.app.getPreferences("org.anism.lotw.settings");
		colorblind = prefs.getBoolean("colorblind", false);
		ninjaMode = prefs.getBoolean("ninjaMode", false);
		autologin = prefs.getBoolean("autologin", true);
		bottomOut = prefs.getBoolean("bottomOut", true);
		controlScheme = ControlScheme.valueOf(prefs.getString("controls", "TWO_TOUCH"));
		season = Seasons.valueOf(prefs.getString("season", "WINTER"));
	}
}
