package org.anism.lotw;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;

import org.anism.lotw.Glider;
import org.anism.lotw.Settings;
import org.anism.lotw.goodies.Goodies;
import org.anism.lotw.LOTW;
import org.anism.lotw.Const;

public class Glob {
	final LOTW lotw;
	public Const constants;
	public int width, height;
	public Glider glider;
	public int ngliders;
	public Settings settings;
	public Goodies goodies;
	public int tracers = 0;
	public float t = 0;
	float currentX = 0;
	public float lastGoodyT = 0;
	public float dpi;
	public int size;
	public int lightSize;
	public int powerSize;
	public String sizeName;
	public float unit;
	public boolean paused;
	public boolean pausing;
	public boolean backing;

	// fonts:
	public BitmapFont roboto16;
	public BitmapFont roboto24;
	public BitmapFont roboto96;

	// renderers:
	public SpriteBatch sb;
	public ShapeRenderer sr;

	// textures:
	public Texture goodyTexture;
	public Texture baddyTexture;
	public Texture multiplierTexture;
	public Texture shieldTexture;
	public Texture lightTexture;
	public Texture starTexture;
	public TextureRegion starRegion;
	public Texture gliderTexture;
	public Texture skyTexture;
	public Texture dotTexture;
	public TextureRegion dotRegion;
	public Texture donutTexture;
	public TextureRegion donutRegion;
	public Texture indicatorTexture;
	int numSnows = 6;
	public Texture[] snowFlakes;
	// icons:
	public Texture backIcon;
	public Texture playIcon;
	public Texture leaderIcon;
	public Texture achieveIcon;
	public Texture controllerIcon;
	public Texture circleIcon;
	public Texture dashIcon;
	public Texture infoIcon;
	// logo:
	public Texture leafLogo;
	public Texture playWrite;

	public Stats stats;

	public Color bgColor;
	public class Colors {
		public Color gold = new Color(0xf9e77177);
		public Color blue = new Color(0x3498db77);
		public Color grey = new Color(0x88888877);
		public Color green = new Color(0x2ecc7177);
		public Color red = new Color(0xe74c3c77);

		public Color goodyGold = new Color(0xf9e771ff);
		public Color goodyBlue = new Color(0x3498dbff);
		public Color goodyGrey = new Color(0x888888ff);
		public Color goodyGreen = new Color(0x2ecc71ff);
		public Color goodyRed = new Color(0xe74c3cff);
	}

	public Colors colors = new Colors();

	public void resetGlider () {
		glider = new Glider(this, 0);
		vx = 0;
		goodies.clear();
	}

	public Glob (LOTW l) {
		lotw = l;
		dpi = lotw.getDpi();

		if (dpi <= 1) {
			size = 16;
			powerSize = 20;
			lightSize = 32;
			sizeName = "s";
		} else if (dpi <= 1.5) {
			size = 24;
			powerSize = 30;
			lightSize = 48;
			sizeName = "m";
		} else if (dpi <= 2) {
			size = 32;
			powerSize = 40;
			lightSize = 64;
			sizeName = "l";
		} else {
			size = 48;
			powerSize = 60;
			lightSize = 96;
			sizeName = "xl";
		}

		//size = (int) Math.pow(2, Math.ceil(Math.log(dpi * 16) / Math.log(2)));
		unit = ((float) size) / 16f;
		constants = new Const();
		ngliders = 1;
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		sr = new ShapeRenderer();
		settings = new Settings();
		settings.read();
		updateBgColor();
		sb = new SpriteBatch();
		paused = false;
		pausing = false;
		backing = false;

		initTextures();

		goodies = new Goodies(this);

		initFonts();

		initStats();

		resetGlider();

		Gdx.input.setCatchBackKey(true);
	}

	public FileHandle imageFile (String img) {
		return Gdx.files.internal("images/" + sizeName + "/" + img);
	}

	public Texture image (String img) {
		return new Texture(imageFile(img));
	}

	public void initTextures () {
		lightTexture = image("light.png");
		goodyTexture = image("lit_goody.png");
		initBaddy();
		multiplierTexture = image("flat_multiplier.png");
		shieldTexture = image("flat_shield.png");
		starTexture = image("star.png");
		starTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		starRegion = new TextureRegion(starTexture);
		gliderTexture = image("glider.png");

		backIcon = image("icons/back.png");
		playIcon = image("icons/play.png");
		leaderIcon = image("icons/leaderboard.png");
		achieveIcon = image("icons/achievements.png");
		controllerIcon = image("icons/controller.png");
		circleIcon = image("icons/circle.png");
		dashIcon = image("icons/dash.png");
		infoIcon = image("icons/info.png");

		leafLogo = new Texture(Gdx.files.internal("images/leaf_logo_bright.png"));
		leafLogo.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		playWrite = new Texture(Gdx.files.internal("images/playwrite.png"));
		playWrite.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skyTexture = new Texture(Gdx.files.internal("images/sky.png"));
		skyTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
		dotTexture = new Texture(Gdx.files.internal("images/1x1.png"));
		dotTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		dotRegion = new TextureRegion(dotTexture);
		donutTexture = new Texture(Gdx.files.internal("images/donut.png"));
		donutTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		donutRegion = new TextureRegion(donutTexture);
		indicatorTexture = new Texture(Gdx.files.internal("images/indicator.png"));
		indicatorTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		snowFlakes = new Texture[numSnows];
		for (int i = 0; i < numSnows; i++) {
			snowFlakes[i] = image("snow/" + String.format("%02d", i) + ".png");
			snowFlakes[i].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		}
	}

	public void initFonts () {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = (int) (16 * unit + 0.5f);
		roboto16 = generator.generateFont(parameter);
		parameter.size = (int) (24 * unit + 0.5f);
		roboto24 = generator.generateFont(parameter);
		parameter.size = (int) (96 * unit + 0.5f);
		roboto96 = generator.generateFont(parameter);
		generator.dispose();
	}

	public void initStats () {
		stats = new Stats(this);
		stats.read();
	}

	public void writeStats () {
		stats.write();
	}

	public void initBaddy () {
		if (settings.colorblind) {
			baddyTexture = image("lit_goody_minus.png");
			goodyTexture = image("lit_goody_plus.png");
		} else {
			goodyTexture = image("lit_goody.png");
			baddyTexture = goodyTexture;
		}
	}

	public void toggleColorblind () {
		settings.colorblind = !settings.colorblind;
		initBaddy();
		settings.save();
	}

	public void toggleBottomOut () {
		settings.bottomOut = !settings.bottomOut;
		settings.save();
	}

	public void toggleNinja () {
		settings.ninjaMode = !settings.ninjaMode;
		settings.save();
	}

	public void toggleAutologin () {
		settings.autologin = !settings.autologin;
		settings.save();
	}

	float vx = 0;

	public void tick () {
		tick(true);
	}

	public void tick (boolean spawn) {
		if (!paused) {
			float dt = Gdx.graphics.getDeltaTime();
			t += dt;
			float deltaTime = dt / 0.015f;
			float dx = glider.tick(deltaTime);
			float sign = Math.signum(dx - vx);
			if (glider.closeToTheEdge()) {
				//don't push me
				vx = dx;
			} else {
				vx += sign * Math.min(1, Math.abs(dx - vx));
			}
			if (glider.fellOffTheBottom()) {
				vx = 0;
			}
			glider.scroll(vx);
			goodies.tick(deltaTime, spawn, vx);
		}
	}

	public void input (boolean pauseable) {
		if (pauseable) {
			int count = 0;
			boolean nPausing = false;
			for (int i = 0; i < 20; i++) {
				if (Gdx.input.isTouched(i)) count++;
				if (count >= 3 || Gdx.input.isKeyPressed(Keys.R)) {
					pause();
					nPausing = true;
					break;
				}
			}
			pausing = nPausing;
		}

		boolean nBacking = false;
		if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.N)) {
			nBacking = true;
			if (!backing) {
				lotw.back();
			}
		}
		backing = nBacking;
	}

	public void pause () {
		if (!pausing) {
			paused = !paused;
			lotw.togglePauseButtons();
		}
	}

	public void clear () {
		updateBgColor();
		Color bg = bgColor;
		Gdx.gl.glClearColor(bg.r, bg.g, bg.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		sb.begin();
		sb.setColor(1, 1, 1, .1f);
		sb.draw(skyTexture, 0, -60, width, height + 60);
		sb.end();

		/*
		sr.begin(ShapeType.Filled);
		sr.setColor(1, 1, .9f, 0.5f);
		sr.rect(0, 0, width, height);
		sr.end();
		*/
	}

	public void fixTransparency () {
		Gdx.gl.glEnable(GL20.GL_BLEND);
	}

	public void draw () {
		fixTransparency();
		goodies.drawStars();
		fixTransparency();
		glider.draw();
		fixTransparency();
		goodies.draw();
		fixTransparency();
		glider.drawHUD();
		fixTransparency();
		
		if (paused) {
			drawPaused();
		}
	}

	public void drawPaused () {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		sr.begin(ShapeType.Filled);
		sr.setColor(new Color(0, 0, 0, 0.25f));
		sr.rect(width / 4 - 48*dpi, height / 2 - 144*dpi, width / 2 + 96*dpi, 288 * dpi);
		sr.end();

		TextBounds bounds = roboto24.getBounds("Paused");
		sb.begin();
		roboto24.draw(sb, "Paused", width/2 - bounds.width/2,
				48*dpi + height/2 + bounds.height/2);
		sb.end();
	}

	public void toastCombo (String t) {
		lotw.toast(lotw.game, t, 0, 0);
	}

	public void clean () {
		goodies.clean();
	}

	public void unlock (String id) {
		lotw.actionResolver.unlockAchievementGPGS(id);
	}

	public void gameOver () {
		if (paused) {
			pause();
		}

		stats.update(glider);
		if (stats.totalScore() > 100000)
			unlock(constants.forever);
		if (stats.totalScore() > 1000000)
			unlock(constants.eternity);

		goodies.clear();
		lotw.gameOver();
	}

	public void updateBgColor () {
		bgColor = settings.bgColor();
	}
		
	/*
	public Color calcBgColor () {
		Color[] bgColors = {
			new Color(0x000022FF)
		};
		int timeStep = 20;
		int index = (int) t / timeStep;
		while (index >= bgColors.length) index -= bgColors.length;
		float pct = (t % timeStep) / timeStep;
		Color ci = new Color(bgColors[index]);
		Color cn = new Color(bgColors[(index+1) % bgColors.length]);
		ci.mul(1 - pct);
		cn.mul(pct);
		ci.add(cn);
		return ci;
	}
	*/

	public boolean dark () {
		return (bgColor.r + bgColor.g + bgColor.b) < 1.5f;
	}
	Color lightColor = new Color(1, 1, 1, 0.5f);
	Color darkColor = new Color(0.2f, 0.2f, 0.2f, 0.75f);
	public Color antiColor () {
		if (dark()) return lightColor;
		return darkColor;
	}

	public Texture snowFlake () {
		return snowFlakes[(int) (Math.random() * numSnows)];
	}

	public double newGoodyChance () {
		return Math.max((Math.sin(t / 2) + 1.5)
			* settings.diff 
			* (1 - settings.difficulty / Math.log(Math.max(glider.score, 40) + 
						(Math.pow(Math.E,
							  settings.difficulty + 0.01)))),
			(t - lastGoodyT)/(glider.score > 100? 150: 250));
	}

	public double greenChance () {
		return 1 / (1 + Math.log(1 + (float) glider.score/20));
	}

	public void goodyAdded () {
		lastGoodyT = t;
	}

	/*
	public void save () {
		glider.save();
		goodies.save();

		Preferences prefs = new Preferences("org.anism.lotw.global");
		prefs.putFloat("t", t);
		prefs.flush();
	}

	public void restore () {
		glider.restore();
		goodies.restore();

		Preferences prefs = new Preferences("org.anism.lotw.global");
		t = prefs.getFloat("t", 0);
	}
	*/
}
