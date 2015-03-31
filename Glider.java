package org.anism.lotw;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;

import org.anism.lotw.Glob;
import org.anism.lotw.goodies.Goody;
import org.anism.lotw.goodies.powerups.PowerUp;

public class Glider extends Object {
	class Effect {
		float width = 0;
		float size;
		float position;
		float maxWidth;
		Color color;

		public Effect (float p, float m, Color c) {
			position = p / m;
			maxWidth = m;
			size = m * 2;
			color = G.settings.ninjaMode? new Color(0, 0, 0, 1): new Color(c);
		}

		public void draw (float halfWidth, float halfHeight, Color clear, Affine2 mat) {
			Color c = new Color(color);
			c.a *= clear.a;
			G.sb.setColor(c);

			float center = position * halfWidth;
			float left = Math.max(-halfWidth, center - width);
			float right = Math.min(halfWidth, center + width);

			Affine2 m = new Affine2(mat);
			m.translate(left + halfWidth, 0);
			G.sb.draw(G.dotRegion, right - left, 2*halfHeight, m);

			if (width < 2 * maxWidth) {
				float boxWidth = 2 * halfHeight;
				m.translate(0, -boxWidth/2 + halfHeight);
				G.sb.setColor(1, 1, 1, 1);
				if (left > -halfWidth)
					G.sb.draw(G.dotRegion, boxWidth, boxWidth, m);
				if (right < halfWidth) {
					m.translate(right - left - boxWidth, 0);
					G.sb.draw(G.dotRegion, boxWidth, boxWidth, m);
				}
			}
		}

		float speed = 0;

		public boolean tick (Color clear) {
			speed += 0.15f;
			width += speed;
			if (width > 3 * maxWidth)
				color.a *= 0.98f;
			return (color.a < 0.01);
		}
	}

	List<Effect> effects;

	int history = 16;

	Glob G;
	public Vector2 position;
	List<Vector2> pHistory;
	Vector2 velocity;
	Vector2 rotation;
	List<Vector2> rHistory;
	Vector2 gravity;
	public int halfWidth;
	float halfHeight;
	boolean shields = false;
	float shieldEffect = 0;
	float shieldEffectStep = 0;
	int multiplier = 1;
	int invulnerable = 0;
	boolean active = true;
	float rotationSpeed;
	int index;

	Color gliderColor = new Color(1, 1, 1, 1);
	Color trailColor = new Color(0, 0, 0, 0.5f);
	Color effectColor = new Color(1, 1, 1, 1);
	Color HUDColor;

	List<PowerUp> powersUp;

	public int score = 0;
	int lives = 3;
	int damages = 0;
	int snitches = 0;
	int combo = 0;
	int maxCombo = 0;

	BitmapFontCache scoreCache;
	BitmapFontCache multiplierCache; 
	FrameBuffer fbo;
	TextureRegion HUDRegion;
	float scoreWidth, scoreHeight;
	float multiplierWidth, multiplierHeight;
	float HUDLeft;
	float HUDMiddle;
	float HUDx;

	public void logVec (String name, Vector2 v) {
		Gdx.app.log(name, v.x + " " + v.y);
	}

	public Vector2 getWindAt(Vector2 loc) {
		// y coord relative to zero wind
		float rely = G.height - 60 * G.unit - loc.y;

		return new Vector2(0f, 
				(float) (-0.01 * 
					Math.sqrt(Math.abs(rely)) * Math.signum(rely)));
	}

	public void resetLives () {
		lives = 3;
	}

	public void reset () {
		// starting position
		position = new Vector2(G.width / 2, G.height - 60 * G.dpi);
		velocity = new Vector2();
		// starting rotation, and defines glider size:
		rotation = new Vector2(1, 0);

		pHistory = new ArrayList<Vector2>();
		rHistory = new ArrayList<Vector2>();

		effects = new ArrayList<Effect>();
	}

	public Glider (Glob g, int i) {
		G = g;

		index = i;
		HUDx = G.width * (index + 1) / (G.ngliders + 1);
		HUDColor = new Color(G.getAntiColor());

		reset();

		gravity = new Vector2(0.f, -0.1f);
		rotationSpeed = 0.05f;

		if (G.settings.heaven()) halfWidth = 40;
		else halfWidth = 20;
		halfWidth *= G.unit;

		halfHeight = 2f * G.unit;

		powersUp = new ArrayList<PowerUp>();

		scoreCache = new BitmapFontCache(G.roboto96);
		scoreCache.setColor(0, 0, 0, .5f);
		multiplierCache = new BitmapFontCache(G.roboto24);
		multiplierCache.setColor(0, 0, 0, .5f);

		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, G.width, G.height, false);
		HUDRegion = new TextureRegion();
		updateHUD();
	}

	public boolean keyLeft () {
		return (Gdx.input.isKeyPressed(Keys.J));
	}
	public boolean keyRight () {
		return (Gdx.input.isKeyPressed(Keys.K));
	}

	int lastTouchPosition = 0;
	float deltaTouch = 0;

	public void touchDown (int x) {
		lastTouchPosition = x;
		deltaTouch = 0;
	}

	public void touchDragged (int x) {
		deltaTouch = x - lastTouchPosition;
		lastTouchPosition = x;
	}

	public void touchUp () {
		deltaTouch = 0;
	}

	public float control () {
		if (keyLeft()) return 1;
		if (keyRight()) return -1;

		if (G.settings.twoTouch()) {
			int c = 0;
			for (int i = 0; i < 20; i++) {
				if (Gdx.input.isTouched(i)) {
					if (Gdx.input.getX(i) < G.width / 2)
						c += 1;
					else
						c -= 1;
				}
			}
			return Math.signum(c);
		}

		float t = 0;

		if (G.settings.tilt()) {
			t = -Gdx.input.getAccelerometerY();
		}

		if (G.settings.oneTouch()) {
			t = -deltaTouch / G.width;
			t *= 1024;
			deltaTouch = 0;
		}

		return Math.signum(t) * Math.min(Math.abs(t), 1);
	}

	public boolean fellOffTheBottom () {
		if (G.settings.bottomOut && position.y < -G.width * 3) {
			damage();
			reset();
			return true;
		}
		return false;
	}

	Vector2 vel = new Vector2();
	Vector2 grav = new Vector2();

	public float tick (float dt) {
		Vector2 wind = getWindAt(position);
		Vector2 rot = new Vector2(rotation);
		rot.rotate90(1);

		// update position and velocity
		vel.set(velocity).scl(G.settings.speed);
		position.add(vel);
		grav.set(gravity).scl(G.settings.speed);
		velocity.add(grav);

		// keep position in bounds
		/*
		while (position.x > G.width) position.x -= G.width;
		while (position.x < 0) position.x += G.width;
		*/

		// update velocity due to wind
		Vector2 effectiveWind = wind.add(velocity);
		float windMagnitude = effectiveWind.dot(rot);
		velocity.sub(rot.scl(windMagnitude * G.settings.speed));

		// control the glider
		rotation.rotateRad(control() * rotationSpeed * G.settings.speed);

		// decay the powers
		List<PowerUp> iterList = new ArrayList<PowerUp>(powersUp);
		for (Iterator it = iterList.iterator(); it.hasNext(); ) {
			PowerUp p = (PowerUp) it.next();
			p.decay(this);
		}
		if (invulnerable > 0) invulnerable--;

		pHistory.add(0, new Vector2(position));
		if (pHistory.size() > history) pHistory.remove(pHistory.size() - 1);
		rHistory.add(0, new Vector2(rotation));
		if (rHistory.size() > history) rHistory.remove(rHistory.size() - 1);

		List<Effect> overEffects = new ArrayList<Effect>();
		for (Iterator it = effects.iterator(); it.hasNext(); ) {
			Effect e = (Effect) it.next();
			if (e.tick(gliderColor)) overEffects.add(e);
		}
		for (Iterator it = overEffects.iterator(); it.hasNext(); ) {
			effects.remove(it.next());
		}

		effectColor.lerp(gliderColor, 0.02f);
		Color hc = new Color(G.getAntiColor());
		hc.a = 0.5f;
		HUDColor.lerp(hc, 0.02f);

		if (shields && shieldEffect < 1) {
			shieldEffectStep += 0.005f;
			shieldEffect += shieldEffectStep;
		}

		return G.width/2 - pHistory.get(pHistory.size() - 1).x;
	}

	public boolean closeToTheEdge () {
		return position.x > G.width*5/6 || position.x < G.width/6;

	}

	public void scroll (float dx) {
		for (Iterator it = pHistory.iterator(); it.hasNext(); ) {
			Vector2 v = (Vector2) (it.next());
			v.add(new Vector2(dx, 0));
		}

		position.x += dx;
	}

	public float getTheta () {
		return rotation.angle();
	}
	public Vector2 getVelocity () {
		return velocity;
	}
	public Vector2 getPosition () {
		return position;
	}

	Vector2 pos = new Vector2();
	Vector2 gPos = new Vector2();

	public boolean collideWithAt (int offset, Goody goody, boolean fx) {
		Vector2 gPosTmp = goody.getPosition();

		pos.set(position);

		gPos.set(gPosTmp);
		gPos.sub(pos);

		if (Math.abs(gPos.x) > goody.getRadius() + halfWidth + 3) return false;
		if (Math.abs(gPos.y) > goody.getRadius() + halfWidth + 3) return false;

		gPos.rotate(-getTheta());
		float gx = Math.abs(gPos.x);
		float gy = Math.abs(gPos.y);

		boolean doEffect = fx && (!shields || goody.getPoints() >= 0);
		if (halfWidth > gx && goody.getRadius() + halfHeight > gy) {
			if (doEffect)
				effects.add(new Effect(gPos.x, halfWidth, goody.getColor()));
			return true;
		}
		if (halfHeight > gy && goody.getRadius() + halfWidth > gx) {
			if (doEffect)
				effects.add(new Effect(Math.signum(gPos.x) * halfWidth,
							halfWidth, goody.getColor()));
			return true;
		}
		if (gPos.dst2(new Vector2(halfWidth, (float) halfHeight)) < goody.getRadius2()) {
			if (doEffect)
				effects.add(new Effect(Math.signum(gPos.x) * halfWidth,
							halfWidth, goody.getColor()));
			return true;
		}

		return false;
	}

	public boolean collideWith (Goody goody, boolean fx) {
		if (!active) return false;
		if (invulnerable > 0) return false;

		return collideWithAt(0, goody, fx);

		/*
		if (position.x < halfWidth && collideWithAt(G.width, goody)) return true;
		if (G.width - position.x < halfWidth && collideWithAt(-G.width, goody))
			return true;
			*/
	}

	public boolean below (Vector2 p) {
		if (position.y > p.y) return false;
		if (rotation.x == 0) {
			return position.x + halfHeight > p.x 
				&& position.x - halfHeight < p.x;
		}

		float rx = Math.abs(rotation.x);
		if (position.x + halfWidth * rx < p.x) return false;
		if (position.x - halfWidth * rx > p.x) return false;

		float slope = rotation.y / rotation.x;
		float rise = slope * (p.x - position.x);
		return position.y + rise < p.y;
	}

	public boolean intersects (Vector2 p, float width) {
		if (rotation.y == 0) return false;
		float dy = (p.y - position.y) / rotation.y;
		if (Math.abs(dy) > Math.abs(rotation.y) * halfWidth) return false;
		float x = position.x + dy * rotation.x;
		if (x < p.x + width && x > p.x) {
			Gdx.app.log("x", "" + x);
			return true;
		}
		return false;
	}

	public void drawAt (Vector2 loc, float zoom, Color color, boolean light) {
		drawAt(loc, rotation, zoom, color, light);
	}

	public void drawLightAt (Vector2 loc, float zoom) {
		loc.sub(velocity);
		float size = 3 * halfWidth * zoom;
		G.sb.begin();
		G.sb.setColor(1, 1, 1, 0.125f);
		G.sb.draw(G.lightTexture, loc.x - size/2, loc.y - size/2,
				size, size);
		G.sb.end();
		G.fixTransparency();
		loc.add(velocity);
	}

	public void drawAt (Vector2 loc, Vector2 r, float zoom, Color color, boolean light) {
		/*
		G.sb.begin();
		Color white = new Color(1, 1, 1, 1);
		white.a = color.a;
		G.sb.setColor(white);
		G.sb.setTransformMatrix(new Matrix4()
				.trn(new Vector3(loc, 0))
				.rotate(new Vector3(0, 0, 1), r.angle())
				.scl(zoom)
				);
		float w = G.gliderTexture.getWidth();
		float h = G.gliderTexture.getHeight();
		G.sb.draw(G.gliderTexture, - w/2, - h/2, w, h);
		G.sb.end();
		G.sb.setTransformMatrix(new Matrix4().idt());
		*/

		//if (light) drawLightAt(loc, zoom);

		/*
		Vector2 rot = new Vector2(r);
		Vector2 edge1 = new Vector2(loc);
		Vector2 edge2 = new Vector2(loc);

		rot.scl(halfWidth);
		edge1.add(rot);
		edge2.sub(rot);
		*/

		/*
		G.sr.setColor(color);
		G.sr.translate(loc.x, loc.y, 0);
		G.sr.scale(zoom, zoom, 1);
		G.sr.rotate(0, 0, 1, r.angle());
		G.sr.rect(-halfWidth, -halfHeight, 2*halfWidth, 2*halfHeight);
		*/
		//G.sr.rectLine(edge1, edge2, 2 * halfHeight);

		Affine2 mat = new Affine2().translate(loc).rotate(r.angle())
			.scale(zoom, zoom).translate(-halfWidth, -halfHeight);

		G.sb.setColor(color);
		G.sb.draw(G.dotRegion, 2*halfWidth, 2*halfHeight, mat);

		if (light) {
			for (Iterator it = effects.iterator(); it.hasNext(); ) {
				Effect e = (Effect) it.next();
				e.draw(halfWidth, halfHeight, color, mat);
			}
		}

		// draw shields
		if (light && shields) {
			Color c = new Color(G.colors.blue);
			c.a = 0.7f;
			G.sb.setColor(c);
			float wm = 3 * shieldEffect;
			float hm = 12 - (float) Math.random();
			G.sb.draw(G.donutRegion, wm * halfWidth, hm * halfHeight, 
					mat.translate(-(wm/2 - 1) * halfWidth, 
						-(hm/2 - 1) * halfHeight));

			/*
			G.sr.begin(ShapeType.Filled);
			G.sr.translate(loc.x, loc.y, 0);
			G.sr.rotate(0, 0, 1, getTheta());
			Color bluish = new Color(G.colors.blue);
			bluish.a = 2 * color.a / 3;
			Color clear = new Color(G.colors.blue);
			clear.a = 0;
			G.sr.triangle(-halfWidth, 0, halfWidth, 0, 0, halfWidth / 2,
					bluish, bluish, clear);
			G.sr.triangle(-halfWidth, 0, halfWidth, 0, 0, -halfWidth / 2,
					bluish, bluish, clear);
			G.sr.identity();
			G.sr.end();
			*/
		}

	}

	public void draw (Color color) {
		for (int i = pHistory.size() - 1; i >= 0; i--) {
			Vector2 pos = pHistory.get(i);
			Vector2 rot = rHistory.get(i);
			Color c = new Color(color);
			if (i > 0) {
				c = new Color(color);
				if (!G.settings.ninjaMode)
					c.a = 1f / ((float) i + 4);
			}
			drawAt(pos, rot, 1, c, i == 0);
			/*
			if (pos.x < halfWidth) 
				drawAt(new Vector2(pos.x + G.width, pos.y), rot, 1, c, i == 0);
			if (pos.x > G.width - halfWidth)
				drawAt(new Vector2(pos.x - G.width, pos.y), rot, 1, c, i == 0);
				*/
		}

		if (position.y < -halfWidth) {
			G.sb.setColor(color);
			int width = 12 * (int) G.unit;
			G.sb.draw(G.indicatorTexture,
					position.x - width / 2, 2 * G.unit,
					width, width/2);

			float factor = 200 / (-position.y + 200);
			drawAt(new Vector2(position.x, 50.f), factor, color, true);
		} else if (position.y > G.height + halfWidth) {
			G.sb.setColor(color);
			int width = 12 * (int) G.unit;
			G.sb.draw(G.indicatorTexture,
					position.x - width / 2, G.height - 2 * G.unit,
					width, -width/2);
			float factor = 200 / (position.y - G.height + 200);
			drawAt(new Vector2(position.x, G.height - 50.f), factor, color, true);
		}
	}

	public void draw () {
		//draw(gliderColor);
		/*
		float bc = 1 - G.bgColor.r * G.bgColor.g * G.bgColor.b;
		Color c = new Color(bc, bc, bc, 1);
		*/

		Color c;
		c = G.getGliderColor();
		c.a = 1;

		G.sb.begin();
		draw(c);
		G.sb.end();
	}

	public void updateHUD () {
		float x = HUDx;

		String scoreString = Integer.toString(score);
		TextBounds scoreBounds = G.roboto96.getBounds(scoreString);
		scoreWidth = scoreBounds.width;
		scoreHeight = scoreBounds.height;

		String multiplierString = "x" + multiplier;
		TextBounds multiplierBounds = G.roboto24.getBounds(multiplierString);
		multiplierWidth = multiplierBounds.width;
		multiplierHeight = multiplierBounds.height;

		HUDLeft = x - (scoreWidth + multiplierWidth) / 2;
		HUDMiddle = x - (multiplierWidth - scoreWidth) / 2;

		/*
		scoreCache.setText(scoreString, HUDLeft, 4 + scoreHeight);
		multiplierCache.setText(multiplierString, HUDMiddle, 4 + multiplierHeight);
		*/

		fbo.begin();

		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		G.sb.begin();
		G.sb.setColor(1, 1, 1, 1);
		for (int i = 0; i < lives; i++) {
			float y = scoreHeight - 4 * i * G.unit;
			G.sb.draw(G.dotTexture, HUDMiddle, y, multiplierWidth, 2 * G.unit);
		}

		G.sb.setColor(1, 1, 1, 1);
		G.roboto96.draw(G.sb, scoreString, HUDLeft, 4 + scoreHeight);
		G.roboto24.draw(G.sb, multiplierString, HUDMiddle, 4 + multiplierHeight);

		G.sb.end();
		fbo.end();
		HUDRegion.setRegion(fbo.getColorBufferTexture());
		HUDRegion.flip(false, true);
	}

	public void drawHUD () {
		G.sb.begin();
		G.sb.setColor(HUDColor);
		G.sb.draw(HUDRegion, 0, 0);
		G.sb.end();
		/*
		float x = HUDx;

		G.sr.begin(ShapeType.Filled);
		G.sr.setColor(0, 0, 0, .5f);
		for (int i = 0; i < lives; i++) {
			float y = scoreHeight - 4 * i;
			G.sr.rect(HUDMiddle, y, multiplierWidth, 2);
		}
		G.sr.end();

		G.sb.begin();

		scoreCache.draw(G.sb);
		multiplierCache.draw(G.sb);

		G.sb.end();
		*/
	}

	public void givePower (PowerUp p) {
		powersUp.add(p);
	}
	public void losePower (PowerUp p) {
		powersUp.remove(p);
	}

	public void doEffect (Color c) {
		HUDColor = new Color(c);
		HUDColor.a = 0.5f;
		//effectColor = c;
	}

	public void givePoints (int points) {
		if (points < 0 && shields) return;
		score += points * multiplier;
		if (G.settings.normal() && points > 0) comboUp();
		if (score < 0) score = 0;
		updateHUD();
	}

	public void missCombo () {
		combo = 0;
	}

	public void comboUp () {
		combo++;
		if (combo >= 3) {
			G.toastCombo(combo + " combo");
		}
		if (combo == 25)
			G.unlock(G.constants.quarterCombo);
		if (combo == 100)
			G.unlock(G.constants.dollarCombo);

		maxCombo = Math.max(maxCombo, combo);
	}

	public void multiplierUp () {
		multiplier++;
		if (multiplier == 6)
			G.unlock(G.constants.sixEx);
		if (multiplier == 12)
			G.unlock(G.constants.deusEx);
		updateHUD();
	}

	public float livesChance () {
		if (G.settings.heaven())
			return (float) (0.4 / (lives + 1));
		return (float) (0.04 / (lives + 1));
	}

	public float multiplierChance () {
		return (float) (0.1 / multiplier);
	}

	public void shieldsUp () {
		shieldsUp(true);
	}
	public void shieldsUp (boolean trueShield) {
		if (!shields) {
			shields = true;
			shieldEffect = 0;
			shieldEffectStep = 0;
			return;
		}

		if (! trueShield) return;

		givePoints(10);
	}

	public void oneUp () {
		lives++;
		updateHUD();
	}

	public void catchSnitch () {
		snitches++;
		G.unlock(G.constants.seeker);
		if (score == 0)
			G.unlock(G.constants.patient);
		score += 100;
		updateHUD();
	}

	public void damage () {
		if (shields) {
			shields = false;
			return;
		}

		missCombo();
		lives--;
		damages++;

		multiplier = 1;

		updateHUD();
	}

	public boolean dead () {
		return lives < 0;
	}

	/*
	public void save () {
		Preferences prefs = new Preferences("org.anism.lotw.glider");
		prefs.putFloat("position.x", position.x);
		prefs.putFloat("position.y", position.y);
		prefs.putFloat("velocity.x", position.x);
		prefs.putFloat("velocity.y", position.y);
		prefs.putFloat("rotation.x", rotation.x);
		prefs.putFloat("rotation.y", rotation.y);
		prefs.putInteger("index", index);
		prefs.putInteger("score", score);
		prefs.putInteger("lives", lives);
		prefs.putInteger("combo", combo);
		prefs.putInteger("maxCombo", maxCombo);
		prefs.putInteger("damages", damages);
		prefs.putInteger("multiplier", multiplier);
		prefs.putBoolean("shields", shields);
		prefs.flush();
	}

	public void restore () {
		Preferences prefs = new Preferences("org.anism.lotw.glider");
		position.x = prefs.getFloat("position.x");
		position.y = prefs.getFloat("position.y");
		velocity.x = prefs.getFloat("velocity.x");
		velocity.y = prefs.getFloat("velocity.y");
		rotation.x = prefs.getFloat("rotation.x");
		rotation.y = prefs.getFloat("rotation.y");
		index = prefs.getInteger("index");
		score = prefs.getInteger("score");
		lives = prefs.getInteger("lives");
		combo = prefs.getInteger("combo");
		maxCombo = prefs.getInteger("maxCombo");
		damages = prefs.getInteger("damages");
		multiplier = prefs.getInteger("multiplier");
		shields = prefs.getBoolean("shields");
	}
	*/
}
