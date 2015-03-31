package org.anism.lotw;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import org.anism.lotw.Glob;
import org.anism.lotw.Stats;
import org.anism.lotw.goodies.Goody;
import org.anism.lotw.goodies.Baddy;

public class LOTW extends ApplicationAdapter {
	Glob G;

	class TouchStage extends Stage {
		@Override
		public boolean touchDown (int x, int y, int pointer, int button) {
			super.touchDown(x, y, pointer, button);
			G.glider.touchDown(x);
			return true;
		}

		@Override
		public boolean touchDragged (int x, int y, int pointer) {
			super.touchDragged(x, y, pointer);
			G.glider.touchDragged(x);
			return true;
		}

		@Override
		public boolean touchUp (int x, int y, int pointer, int button) {
			super.touchUp(x, y, pointer, button);
			G.glider.touchUp();
			return true;
		}
	};

	//Stage intro;
	Stage settings;
	Stage colorSettings;
	Stage controlSettings;
	Stage modeSettings;
	Stage loginSettings;
	Stage bottomSettings;

	Stage statistics;
	Stage instructions;
	Stage game;
	Stage over;
	Stage about;

	Stage current;

	ImageButton resumeButton;
	ImageButton exitButton;

	Label.LabelStyle labelStyle;
	Label.LabelStyle labelStyleSmall;

	float fadeDur = 0.5f;
	Color tr = new Color(1, 1, 1, 0);

	ActionResolver actionResolver;

	float dpi;
	float buttonWidth;
	float settingsButtonHeight;

	public LOTW (ActionResolver a) {
		actionResolver = a;
	}

	public boolean playing () {
		return current == game;
	}

	public void togglePauseButtons () {
		resumeButton.setVisible(!resumeButton.isVisible());
		exitButton.setVisible(!exitButton.isVisible());
	}

	public void back () {
		Gdx.app.log("back", "");
		if (current == game) {
			G.pause();
			return;
		}
		if (current == over || current == settings
				|| current == colorSettings
				|| current == controlSettings
				|| current == modeSettings
				|| current == loginSettings
				|| current == bottomSettings) {
			setStage(instructions);
			return;
				}
		if (current == about || current == statistics) {
			setStage(bottomSettings);
			return;
		}
		if (current == instructions) {
			dispose();
			return;
		}
	}

	public void setStage (Stage s) {
		setStage(s, true);
	}
	public void setStage (Stage s, boolean resetGlider) {
		current = s;
		if (current == game) G.startGame();
		if (resetGlider) G.resetGlider();
		else G.glider.resetLives();
		G.resetGoodies();
		Gdx.input.setInputProcessor(current);
	}

	public void putLogo (Stage s) {
		Image goLogo = new Image(G.leafLogo);
		goLogo.addAction(Actions.alpha(0.25f));
		float w = goLogo.getWidth();
		float h = goLogo.getHeight();
		goLogo.setWidth(w * dpi);
		goLogo.setHeight(h * dpi);
		goLogo.setPosition(G.width / 2 - goLogo.getWidth() / 2,
				G.height / 2 - goLogo.getHeight() / 2);
		s.addActor(goLogo);
	}

	public void initGameOver (boolean fade) {
		over.clear();

		//putLogo(over);

		Table goTable = new Table();
		goTable.setFillParent(true);

		goTable.add(new Label("Game Over", labelStyle)).pad(4).colspan(2);
		goTable.row();
		if (G.settings.hell()) {
			goTable.add(new Label("Hell Mode", labelStyleSmall)).pad(4).colspan(2);
			goTable.row();
		}
		goTable.add(new Goody(G, 0, 0, G.colors.goodyGreen)).pad(4);
		goTable.add(new Label(Integer.toString(G.glider.score), labelStyle)).pad(4);
		goTable.row();
		goTable.add(new Goody(G, 0, 0, G.colors.goodyGold)).pad(4);
		goTable.add(new Label(Integer.toString(G.glider.snitches), labelStyle))
			.pad(4);
		goTable.row();
		goTable.add(new Baddy(G, G.colors.goodyRed)).pad(4);
		goTable.add(new Label(Integer.toString(G.glider.damages), labelStyle)).pad(4);
		goTable.row();

		if (G.settings.normal()) {
			goTable.add().pad(4);
			goTable.row();

			goTable.add(new Label(G.glider.maxCombo + " combo", labelStyle))
				.pad(4).colspan(2);
			goTable.row();
		}

		if (fade) {
			goTable.addAction(Actions.alpha(0));
			goTable.addAction(Actions.delay(1, Actions.fadeIn(3)));
		}
		over.addActor(goTable);
		putActionBar(over);
	}
		
	public void gameOver () {
		// first, talk to google:
		if (actionResolver.getSignedInGPGS()) {
			if (G.settings.normal())
				actionResolver.submitScoreGPGS(G.glider.score,
						G.constants.scoreBoard);
			if (G.settings.hell())
				actionResolver.submitScoreGPGS(G.glider.score,
						G.constants.hellBoard);

			if (G.glider.score > 10)
				actionResolver.unlockAchievementGPGS(G.constants.decade);
			if (G.glider.score > 100)
				actionResolver.unlockAchievementGPGS(G.constants.century);
			if (G.glider.score > 1000)
				actionResolver.unlockAchievementGPGS(G.constants.millenium);
		}
		
		// then, make the screen:
		initGameOver(true);

		setStage(over, false);
	}

	public TextButton addButton (Stage src, final Stage dest, String text, float cx, float cy, float w, float h) {
		return addButton(src, dest, text, cx, cy, w, h, G.colors.grey);
	}

	public TextButton addButton (Stage src, final Stage dest, String text, float cx, float cy, float w, float h, Color c) {
		TextButton button = new TextButton(text, textButtonStyle);
		button.setWidth(w * dpi);
		button.setHeight(h * dpi);
		button.setPosition(G.width/2 + cx * dpi - button.getWidth()/2,
				G.height/2 + cy * dpi - button.getHeight()/2);
		button.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				setStage(dest);
			}
		});

		button.setColor(c);

		if (src != null) {
			src.addActor(button);
		}

		return button;
	}

	public ImageButton addIconButton (Stage src, Texture texture, ClickListener cl, float cx, float cy, float w, float h) {
		ImageButton.ImageButtonStyle imgBtnStyle = new ImageButton.ImageButtonStyle(buttonStyle);
		TextureRegion rgn = new TextureRegion(texture);
		TextureRegionDrawable dbl = new TextureRegionDrawable(rgn);
		imgBtnStyle.imageUp = dbl;
		imgBtnStyle.imageDown = dbl;
		imgBtnStyle.imageOver = dbl;
		imgBtnStyle.imageChecked = dbl;
		imgBtnStyle.imageCheckedOver = dbl;
		imgBtnStyle.imageDisabled = dbl;

		ImageButton button = new ImageButton(imgBtnStyle);
		button.setWidth(w * dpi);
		button.setHeight(h * dpi);
		button.setPosition(G.width / 2 + cx * dpi - button.getWidth()/2,
				G.height/2 + cy * dpi - button.getHeight()/2);
		button.addListener(cl);
		Color c = new Color(G.getAntiColor());
		c.a = 1;
		button.getImage().setColor(c);

		if (src != null) {
			src.addActor(button);
		}

		return button;
	}

	public Image addImage (Stage src, Texture texture, Color color, float cx, float cy, float w, float h) {
		Image image = new Image(texture);
		image.setWidth((int) w * dpi);
		image.setHeight((int) h * dpi);
		image.setColor(color);
		image.setPosition((int) G.width/2 + cx * dpi - image.getWidth()/2,
				(int) G.height/2 + cy * dpi - image.getHeight()/2);

		src.addActor(image);

		return image;
	}

	public Goody addDumbBaddy (Stage src, Color color, float cx, float cy) {
		Goody g = new Baddy(G, new Color(color));
		g.setPosition(G.width/2 + cx*dpi - G.size/2, G.height/2 + cy*dpi - G.size/2);
		src.addActor(g);

		return g;
	}

	public Goody addDumbGoody (Stage src, Color color, float cx, float cy) {
		Goody g = new Goody(G, cx, cy, new Color(color));
		src.addActor(g);

		return g;
	}

	public Label addLabel (Stage src, String lbl, float cx, float cy, float h, Color c) {
		Label label = new Label(lbl, new Label.LabelStyle(G.roboto24, G.getAntiColor()));
		TextBounds tb = label.getTextBounds();
		label.setPosition(G.width/2 + cx * dpi - tb.width/2, G.height/2 + cy * dpi - h * dpi/2 - tb.height / 2);
		label.setAlignment(Align.bottom | Align.center);
		label.setColor(c);

		src.addActor(label);

		return label;
	}

	public Label addLabel (Stage src, String lbl, float cx, float cy, float h) {
		return addLabel(src, lbl, cx, cy, h, G.getAntiColor());
	}

	Label toasting;

	public void toast (Stage src, String t, float cx, float cy) {
		if (toasting != null) {
			toasting.addAction(Actions.removeActor());
		}
		toasting = addLabel(src, t, cx, cy, -48, G.getAntiColor());
		toasting.setStyle(new LabelStyle(G.roboto24, G.getAntiColor()));
		toasting.addAction(Actions.delay(1, Actions.fadeOut(1)));
	}

	public void toast (String t) {
		if (current != null) {
			toast(current, t, 0, 0);
		} else {
			Gdx.app.log("TOAST", t);
		}
	}

	public void newBest (int time) {
		addLabel(over, "New " + (time == 0? "Daily": time == 1? "Weekly": "All Time") + "\nHigh Score!", G.width / 4 / dpi, 0, 0)
			.addAction(Actions.forever(Actions.sequence(
							Actions.alpha(0.125f, 2 * fadeDur),
							Actions.fadeIn(2 * fadeDur))));
	}

	public void seeStats () {
		statistics.clear();
		//putLogo(statistics);

		statistics.addActor(G.stats.display(labelStyleSmall));

		addIconButton(statistics, G.backIcon,
				new ClickListener () {
					@Override
					public void clicked(InputEvent e, float x, float y) {
						setStage(bottomSettings);
					}
				},
				0, -G.height/2/dpi + 48,
				buttonWidth/dpi, 88).setColor(G.colors.red);
		setStage(statistics);
	}

	@Override
	public void dispose () {
		G.writeStats();
		super.dispose();
	}

	TextButton loginButton;

	public void login () {
		if (loginButton != null) {
			logButton.setText("Sign Out");
		}
	}

	public void logout () {
		if (loginButton != null) {
			logButton.setText("Sign In");
		}
	}

	TextButton.TextButtonStyle textButtonStyle;
	ButtonStyle buttonStyle;
	ImageTextButton logButton;

	public Table putSettingsMenu (Stage src) {
		Table outTable = new Table();
		outTable.setSize(buttonWidth, G.height - 96 * dpi);
		outTable.setPosition(0, 96 * dpi);
		outTable.left().top();

		outTable.defaults().width(buttonWidth + 8 * dpi)
			.height(settingsButtonHeight);
		Label setLabel = new Label("Settings", labelStyle);
		outTable.add(setLabel).width(setLabel.getWidth()).center();
		outTable.row();

		Table inTable = new Table();
		inTable.defaults().pad(4 * dpi).width(buttonWidth)
			.height(settingsButtonHeight);
		inTable.add(addButton(null, controlSettings, "Controls", 0, 0, 0, 0));
		inTable.row();
		inTable.add(addButton(null, colorSettings, "Display", 0, 0, 0, 0));
		inTable.row();
		inTable.add(addButton(null, modeSettings, "Mode", 0, 0, 0, 0));
		inTable.row();
		inTable.add(addButton(null, loginSettings, "Online", 0, 0, 0, 0));
		inTable.row();
		inTable.add(addButton(null, bottomSettings, "Advanced", 0, 0, 0, 0));

		ScrollPane pane = new ScrollPane(inTable);
		outTable.add(pane).height(G.height - settingsButtonHeight - 96 * dpi);

		if (src != null) {
			src.addActor(outTable);
		}

		return outTable;
	}

	public Table makeSettingsTable (String title) {
		Table table = new Table();
		table.setPosition(buttonWidth, 88 * dpi);
		table.setSize(G.width - buttonWidth, G.height - 88 * dpi);
		table.defaults().height(settingsButtonHeight);
		table.add(new Label(title, labelStyle)).colspan(2);
		table.row();

		return table;
	}
	
	public void putSettingsButton (Table table, Actor button, String text) {
		table.add(button).width(88*dpi).pad(4*dpi);
		Label label = new Label(text, labelStyleSmall);
		label.setWrap(true);
		table.add(label).width(G.width - 384*dpi).pad(4*dpi);
		table.row();
	}

	public Table makeSettings (Stage set, Table right) {
		Table left = putSettingsMenu(null);

		Table out = new Table();
		out.setFillParent(true);
		out.left().top();
		left.setFillParent(false);
		out.add(left);
		out.add(right);

		if (set != null) {
			set.addActor(out);
		}

		return out;
	}

	public Table putActionBar (Stage src) {
		ImageButton leaderButton = addIconButton(null, G.leaderIcon,
				new ClickListener() {
					@Override
					public void clicked (InputEvent e, float x, float y) { 
						actionResolver.getLeaderboardGPGS(G.constants.scoreBoard);
					}
				},
				48, -G.height/2/dpi + 48, 88, 88);
		leaderButton.setColor(G.colors.gold);

		ImageButton achieveButton = addIconButton(null, G.achieveIcon,
				new ClickListener() {
					@Override
					public void clicked (InputEvent e, float x, float y) {
						actionResolver.getAchievementsGPGS();
					}
				},
				-48, -G.height/2/dpi + 48, 88, 88);
		achieveButton.setColor(G.colors.blue);

		ImageButton backButton = addIconButton(null, G.backIcon,
				new ClickListener() {
					@Override
					public void clicked (InputEvent e, float x, float y) {
						setStage(instructions);
					}
				},
				-G.width/2/dpi + 96, -G.height/2/dpi + 48,
				184, 88);
		backButton.setColor(G.colors.red);
		ImageButton playButton = addIconButton(null, G.playIcon,
				new ClickListener () {
					@Override
					public void clicked(InputEvent e, float x, float y) {
						setStage(game);
					}
				},
				G.width/2/dpi - 96, -G.height/2/dpi + 48,
				184, 88);
		playButton.setColor(G.colors.green);

		Table actionBar = new Table();
		actionBar.setFillParent(true);
		actionBar.bottom();
		//actionBar.defaults().pad(4).maxWidth(192).height(88);
		actionBar.defaults().pad(4 * dpi).minWidth(88 * dpi).maxWidth(192 * dpi)
			.expandX().height(88 * dpi);
		actionBar.add(backButton);
		actionBar.add(achieveButton);
		actionBar.add(leaderButton);
		actionBar.add(playButton);

		src.addActor(actionBar);

		return actionBar;
	}

	@Override
	public void pause () {
		//G.save();
	}

	@Override
	public void resume () {
		//G.restore();
	}

	@Override
	public void create () {
		dpi = Gdx.graphics.getDensity();

		if (actionResolver.getSignedInGPGS()) {
		} else {
			actionResolver.loginGPGS();
		}

		G = new Glob(this);
		G.clear();
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		buttonWidth = Math.min(G.width / 4 - 8 * dpi, 196 * dpi);
		settingsButtonHeight = Math.min((G.height - 96 * dpi) / 4 - 8 * dpi, 88 * dpi);
		/*
		Texture upTexture = new Texture(Gdx.files.internal("images/sphere-lightgrey-24.png"));
		Texture downTexture = new Texture(Gdx.files.internal("images/sphere-greylight-24.png"));
		NinePatch buttonPatchUp = new NinePatch(upTexture, 11, 11, 11, 11);
		NinePatch buttonPatchDown = new NinePatch(downTexture, 11, 11, 11, 11);
		*/

		//intro = new Stage();
		settings = new Stage();
		colorSettings = new Stage();
		controlSettings = new Stage();
		modeSettings = new Stage();
		loginSettings = new Stage();
		bottomSettings = new Stage();

		about = new TouchStage();
		instructions = new TouchStage();
		statistics = new TouchStage();
		game = new TouchStage() {
			@Override
			public void draw () {
				G.tick(true, true);
				G.input(true);
				G.clear();
				G.draw();
				super.act();
				super.draw();
				Gdx.gl.glEnable(GL20.GL_BLEND);
			}
		};
		over = new TouchStage();

		initEverything();

		setStage(instructions);
	}

	public void resetEverything () {
		clearEverything();
		initEverything();
		if (current == over)
			initGameOver(false);
	}

	public void clearEverything () {
		settings.clear();
		colorSettings.clear();
		controlSettings.clear();
		modeSettings.clear();
		loginSettings.clear();
		bottomSettings.clear();
		statistics.clear();
		instructions.clear();
		game.clear();
		over.clear();
		about.clear();
	}

	public void initEverything () {
		Color fullAnti = new Color(G.getAntiColor());
		fullAnti.a = 1;

		Texture upTexture = new Texture(Gdx.files.internal("images/grey_button_up.png"));
		Texture downTexture = new Texture(Gdx.files.internal("images/grey_button_down.png"));
		NinePatch buttonPatchUp = new NinePatch(upTexture, 11, 11, 11, 11);
		NinePatch buttonPatchDown = new NinePatch(downTexture, 11, 11, 11, 11);

		NinePatchDrawable npdUp = new NinePatchDrawable(buttonPatchUp);
		NinePatchDrawable npdDown = new NinePatchDrawable(buttonPatchDown);
		textButtonStyle = new TextButton.TextButtonStyle(npdUp, npdDown, npdUp, G.roboto16);
		textButtonStyle.fontColor = new Color(G.getAntiColor());
		textButtonStyle.fontColor.a = 1;
		buttonStyle = new ButtonStyle(npdUp, npdDown, npdUp);
		
		labelStyle = new Label.LabelStyle(G.roboto24, G.getAntiColor());
		labelStyleSmall = new Label.LabelStyle(G.roboto16, G.getAntiColor());
		resumeButton = addIconButton(game, G.playIcon,
				new ClickListener() {
					@Override
					public void clicked(InputEvent e, float x, float y) {
						if (! G.pausing) {
							G.pause();
						}
					}
				},
				buttonWidth/dpi/2 + 4 * dpi, -48, buttonWidth/dpi, 88);
		resumeButton.setColor(G.colors.green);
		exitButton = addIconButton(game, G.backIcon,
				new ClickListener() {
					@Override
					public void clicked(InputEvent e, float x, float y) {
						if (! G.pausing) {
							G.gameOver();
						}
					}
				},
				-buttonWidth/dpi/2 - 4 * dpi, -48, buttonWidth/dpi, 88);
		exitButton.setColor(G.colors.red);
		togglePauseButtons();

		//intro.addActor(logo);
		//putLogo(settings);
		putLogo(instructions);
		//putLogo(statistics);

		/*
		// intro screen:
		addButton(intro, game, "Start Game", 0, 0, 200f, 40f);
		addButton(intro, settings, "Settings", 0, -70, 100f, 40f);
		addButton(intro, instructions, "How to Play", 0, -120, 100f, 40f);
		*/

		// settings screen:
		
		ImageTextButton.ImageTextButtonStyle lbs = new ImageTextButton.ImageTextButtonStyle(textButtonStyle);
		TextureRegionDrawable gamepadDrawable = new TextureRegionDrawable(new TextureRegion(G.controllerIcon));
		lbs.imageChecked = gamepadDrawable;
		lbs.imageCheckedOver = gamepadDrawable;
		lbs.imageDisabled = gamepadDrawable;
		lbs.imageDown = gamepadDrawable;
		lbs.imageOver = gamepadDrawable;
		lbs.imageUp = gamepadDrawable;

		logButton = new ImageTextButton("Sign In", lbs);
		if (actionResolver.getSignedInGPGS()) {
			logButton.setText("Sign Out");
		}
		logButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				if (actionResolver.getSignedInGPGS()) {
					actionResolver.logoutGPGS();
				} else {
					actionResolver.loginGPGS();
				}
				G.toggleAutologin();
			}
		});
		logButton.setWidth(184 * dpi);
		logButton.setHeight(88 * dpi);
		/*logButton.setPosition(G.width/2 - 188 * dpi,
				G.height/2 - 92*dpi);
		*/
		logButton.setColor(G.colors.grey);
		logButton.getImage().setColor(fullAnti);
		//settings.addActor(logButton);

		TextureRegionDrawable cbOn = new TextureRegionDrawable(new TextureRegion(G.dashIcon));
		TextureRegionDrawable cbOff = new TextureRegionDrawable(new TextureRegion(G.circleIcon));
		CheckBoxStyle cbs = new CheckBoxStyle(cbOff, cbOn, G.roboto16, fullAnti);
		cbs.checked = npdUp;
		cbs.checkedOver = npdUp;
		cbs.down = npdDown;
		cbs.over = npdUp;
		cbs.up = npdUp;

		// colorblind settings:
		CheckBox colorButton = new CheckBox("", cbs);
		colorButton.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				G.toggleColorblind();
			}
		});
		colorButton.setWidth(88 * dpi);
		colorButton.setHeight(88 * dpi);
		/*
		colorButton.setPosition(G.width/2 - 188 * dpi,
				G.height/2 + 100*dpi);
				*/
		Table colorTable = makeSettingsTable("Display");
		colorButton.setColor(Math.random() > 0.5? G.colors.green: G.colors.red);
		colorButton.getImage().setColor(fullAnti);
		colorButton.setChecked(G.settings.colorblind);
		ListStyle listStyle = new ListStyle(G.roboto16, G.getAntiColor(), G.getAntiColor(),
				npdDown);
		listStyle.background = npdUp;
		SelectBoxStyle sbs = new SelectBoxStyle(G.roboto16, 
				new Color(0x00000000), npdUp,
				new ScrollPane.ScrollPaneStyle(),
				listStyle);
		final SelectBox<Season> seasonPicker = new SelectBox(sbs);
		seasonPicker.setItems((Season[]) G.settings.getSeasons());
		seasonPicker.setSelected(G.settings.getSeason());
		seasonPicker.getList().setSelected(G.settings.getSeason());
		seasonPicker.setColor(G.colors.grey);
		seasonPicker.getList().setColor(G.colors.grey);
		Stack seasonStack = new Stack();
		final Container seasonImage = new Container(new Image(G.settings.season.getIcon()));
		seasonImage.setSize(32, 32);
		seasonImage.setTouchable(Touchable.disabled);
		seasonImage.getActor().setColor(G.getAntiColor());
		seasonStack.add(seasonPicker);
		seasonStack.add(seasonImage);
		seasonPicker.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent e, Actor a) {
				Season s = seasonPicker.getSelected();
				G.changeSeason(s);
			}
		});
		putSettingsButton(colorTable, seasonStack, "Season.");
		CheckBox ninjaButton = new CheckBox("", cbs);
		ninjaButton.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				G.toggleNinja();
			}
		});
		ninjaButton.setColor(G.colors.grey);
		ninjaButton.getImage().setColor(fullAnti);
		ninjaButton.setChecked(G.settings.ninjaMode);
		putSettingsButton(colorTable, colorButton, "Add markings to distinguish red and green orbs.");
		putSettingsButton(colorTable, ninjaButton, "Ninja mode.");

		// advanced settings:
		CheckBox bottomButton = new CheckBox("", cbs);
		bottomButton.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				G.toggleBottomOut();
			}
		});
		bottomButton.setChecked(G.settings.bottomOut);
		bottomButton.setColor(G.colors.grey);
		bottomButton.getImage().setColor(fullAnti);
		Table bottomTable = makeSettingsTable("Advanced");
		putSettingsButton(bottomTable, bottomButton, "The glider respawns if it falls too far. Experienced players may wish to turn this off.");
		ImageButton statsButton = addIconButton(null, G.image("icons/stats.png"),
				new ClickListener() {
					@Override
					public void clicked (InputEvent e, float x, float y){
						seeStats();
					}
				},
				96, -48, 88, 88);
		statsButton.setColor(G.colors.grey);
		putSettingsButton(bottomTable, statsButton, "View aggregate and record statistics.");
		ImageButton aboutButton = addIconButton(null, G.infoIcon,
				new ClickListener() {
					@Override
					public void clicked(InputEvent e, float x, float y) {
						setStage(about);
					}
		}, 0, 0, 0, 0);
		aboutButton.setColor(G.colors.grey);
		putSettingsButton(bottomTable, aboutButton, "About.");

		// online settings:
		ImageButton leaderButton = addIconButton(null, G.leaderIcon,
				new ClickListener() {
					@Override
					public void clicked(InputEvent e, float x, float y) { 
						actionResolver.getLeaderboardGPGS(G.constants.scoreBoard);
					}
				},
				48, -G.height/2/dpi + 48, 88, 88);
		leaderButton.setColor(G.colors.gold);
		ImageButton achieveButton = addIconButton(null, G.achieveIcon,
				new ClickListener() {
					@Override
					public void clicked (InputEvent e, float x, float y){
						actionResolver.getAchievementsGPGS();
					}
				},
				-48, -G.height/2/dpi + 48, 88, 88);
		achieveButton.setColor(G.colors.blue);
		Table loginTable = makeSettingsTable("Online");
		putSettingsButton(loginTable, achieveButton, "View achievements.");
		putSettingsButton(loginTable, leaderButton, "Access leaderboards.");
		loginTable.add(logButton).width(182*dpi).pad(4*dpi)
			.colspan(2).center();

		// control settings:
		Table controlTable = makeSettingsTable("Controls");
		CheckBox twoTouchControls = new CheckBox("", cbs);
		twoTouchControls.setColor(G.colors.grey);
		twoTouchControls.getImage().setColor(fullAnti);
		twoTouchControls.setChecked(G.settings.twoTouch());
		twoTouchControls.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				G.settings.setControl("TWO_TOUCH");
			}
		});
		CheckBox tiltControls = new CheckBox("", cbs);
		tiltControls.setColor(G.colors.grey);
		tiltControls.getImage().setColor(fullAnti);
		tiltControls.setChecked(G.settings.tilt());
		tiltControls.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				G.settings.setControl("TILT");
			}
		});
		CheckBox dragControls = new CheckBox("", cbs);
		dragControls.setColor(G.colors.grey);
		dragControls.getImage().setColor(fullAnti);
		dragControls.setChecked(G.settings.oneTouch());
		dragControls.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				G.settings.setControl("ONE_TOUCH");
			}
		});
		ButtonGroup controlGroup = new ButtonGroup(twoTouchControls, tiltControls, dragControls);
		controlGroup.setMaxCheckCount(1);
		controlGroup.setMinCheckCount(1);
		controlGroup.setUncheckLast(true);
		putSettingsButton(controlTable, twoTouchControls, "Touch on the left or right.");
		putSettingsButton(controlTable, tiltControls, "Tilt your device.");
		putSettingsButton(controlTable, dragControls, "Touch and drag.");

		// mode settings:
		Table modeTable = makeSettingsTable("Mode");
		CheckBox hellMode = new CheckBox("", cbs);
		hellMode.setColor(G.colors.red);
		hellMode.getImage().setColor(fullAnti);
		hellMode.setChecked(G.settings.hell());
		hellMode.addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent e, float x, float y) {
					G.settings.setMode(-1);
				}
			});
		CheckBox normalMode = new CheckBox("", cbs);
		normalMode.setColor(G.colors.grey);
		normalMode.getImage().setColor(fullAnti);
		normalMode.setChecked(G.settings.normal());
		normalMode.addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent e, float x, float y) {
					G.settings.setMode(0);
				}
			});
		ButtonGroup modeGroup = new ButtonGroup(hellMode, normalMode);
		modeGroup.setMaxCheckCount(1);
		modeGroup.setMinCheckCount(1);
		modeGroup.setUncheckLast(true);
		putSettingsButton(modeTable, normalMode,
				"Normal mode: catch orbs for points.");
		putSettingsButton(modeTable, hellMode, "Hell mode: dodge red orbs.");

		putActionBar(settings);
		putActionBar(colorSettings);
		putActionBar(controlSettings);
		putActionBar(modeSettings);
		putActionBar(loginSettings);
		putActionBar(bottomSettings);

		putSettingsMenu(settings);
		putSettingsMenu(colorSettings);
		colorSettings.addActor(colorTable);
		putSettingsMenu(controlSettings);
		controlSettings.addActor(controlTable);
		putSettingsMenu(loginSettings);
		loginSettings.addActor(loginTable);
		putSettingsMenu(modeSettings);
		modeSettings.addActor(modeTable);
		putSettingsMenu(bottomSettings);
		bottomSettings.addActor(bottomTable);

		Table aboutTable = new Table();
		aboutTable.setSize(G.width, G.height - 88 * dpi);
		aboutTable.setPosition(0, 88 * dpi);
		aboutTable.add(new Label("About", labelStyle));
		aboutTable.row();
		Label aboutTextLabel = new Label("Leaf on the Wind copyright 2014 Ethan Goldberg, all rights reserved.\n\nIcons made by Google and licensed under CC BY 4.0.\n\n\nThank you for playing!", labelStyleSmall);
		aboutTextLabel.setWrap(true);
		aboutTextLabel.setAlignment(Align.center, Align.center);
		aboutTable.add(aboutTextLabel).width(G.width / 2);
		ImageButton aboutBackButton = addIconButton(about, G.backIcon,
				new ClickListener() {
					@Override
					public void clicked (InputEvent e, float x, float y){
						setStage(bottomSettings);
					}
				}, 0, -G.height/2/dpi + 48, buttonWidth/dpi, 88);
		aboutBackButton.setColor(G.colors.red);
		about.addActor(aboutTable);
		about.addActor(aboutBackButton);

		/*
		TextButton statsButton = new TextButton("Statistics", textButtonStyle);
		statsButton.setWidth(192 * dpi);
		statsButton.setHeight(40f * dpi);
		statsButton.setPosition(G.width / 2 - statsButton.getWidth() / 2,
				G.height / 2 - 68f * dpi);
		statsButton.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				seeStats();
				setStage(statistics);
			}
		});
		settings.addActor(statsButton);

		TextButton leaderButton = new TextButton("Leaderboard", textButtonStyle);
		leaderButton.setWidth(192 * dpi);
		leaderButton.setHeight(40f * dpi);
		leaderButton.setPosition(G.width / 2 - leaderButton.getWidth() / 2,
				G.height / 2 - 164f * dpi);
		leaderButton.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				actionResolver.getLeaderboardGPGS(G.constants.scoreBoard);
			}
		});
		settings.addActor(leaderButton);

		TextButton achieveButton = new TextButton("Achievements", textButtonStyle);
		achieveButton.setWidth(192 * dpi);
		achieveButton.setHeight(40f * dpi);
		achieveButton.setPosition(G.width / 2 - leaderButton.getWidth() / 2,
				G.height / 2 - 116f * dpi);
		achieveButton.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				actionResolver.getAchievementsGPGS();
			}
		});
		settings.addActor(achieveButton);
		*/

		/*
		loginButton = new TextButton("Sign In", textButtonStyle);
		loginButton.setWidth(G.width / 3);
		loginButton.setHeight(40f);
		loginButton.setPosition(G.width / 2 - loginButton.getWidth() / 2, 164f);
		loginButton.addListener(new ClickListener () {
			@Override
			public void clicked (InputEvent e, float x, float y) {
				if (actionResolver.getSignedInGPGS()) {
					actionResolver.logoutGPGS();
				} else {
					actionResolver.loginGPGS();
				}
			}
		});
		settings.addActor(loginButton);
		*/

		//addButton(settings, game, "Start Game", 0, -G.height/2 + 88f, G.width/3, 40f);
		//addButton(settings, instructions, "Back", 0, -192f, 192, 40f);
		/*
		final Label modeLabel = addLabel(settings, "Normal: collect and dodge.", 0, -48f, 0f);

		TextButton hellMode = new TextButton("Hell", textButtonStyle);
		hellMode.addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent e, float x, float y) {
					G.settings.setMode(-1);
					modeLabel.setText("Hell: dodge.");
				}
			});
		hellMode.setHeight(40);
		hellMode.setWidth(88);
		hellMode.setPosition(G.width / 2 - 144, G.height / 2 - 20);

		TextButton normalMode = new TextButton("Normal", textButtonStyle);
		normalMode.addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent e, float x, float y) {
					G.settings.setMode(0);
					modeLabel.setText("Normal: collect and dodge.");
				}
			});
		normalMode.setHeight(40);
		normalMode.setWidth(88);
		normalMode.setPosition(G.width / 2 - 48, G.height / 2 - 20);

		TextButton heavenMode = new TextButton("Pokemon", textButtonStyle);
		heavenMode.addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent e, float x, float y) {
					G.settings.setMode(1);
					modeLabel.setText("Pokemon: gotta catch 'em all!");
				}
			});
		heavenMode.setHeight(40);
		heavenMode.setWidth(88);
		heavenMode.setPosition(G.width / 2 + 48, G.height / 2 - 20);

		normalMode.setChecked(true);

		ButtonGroup bg = new ButtonGroup(hellMode, normalMode, heavenMode);
		bg.setMaxCheckCount(1);
		bg.setMinCheckCount(1);
		bg.setUncheckLast(true);

		settings.addActor(hellMode);
		settings.addActor(normalMode);
		settings.addActor(heavenMode);
		*/

		// instructions:
		
		//delay
		float d = 0f;
		addLabel(instructions, "This is your glider.", 0, G.height/2/dpi - 96, 8, tr)
			.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));
		// wait two seconds:
		d += 2.5;
		addLabel(instructions, "Touch the screen", 0, G.height/2/dpi - 136, 8, tr)
			.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));
		d += 1;
		addLabel(instructions, "on the left,", -G.width/4/dpi, G.height/2/dpi-166, 8, tr)
			.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));
		d += 1.5;
		addLabel(instructions, "or the right.", G.width/4/dpi, G.height/2/dpi-166, 8, tr)
			.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));

		d += 3;
		int pickUpOffset = G.size * 2;
		addLabel(instructions, "Catch these,", 
				-G.width/4/dpi, -G.height/6/dpi, G.size*2/dpi, tr)
			.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));
		Goody i;
		d += 1.5;

		Color r = new Color(G.colors.goodyRed);
		r.a = 0;
		Color gr = new Color(G.colors.goodyGreen);
		gr.a = 0;
		Color go = new Color(G.colors.goodyGold);
		go.a = 0;
		Color b = new Color(G.colors.goodyBlue);
		b.a = 0;

		i = addDumbGoody(instructions, b, -G.width/4/dpi - (G.size*2)/dpi,
				-G.height/6/dpi + pickUpOffset);
		i.addAction(Actions.delay(d, Actions.alpha(1)));
		//bounce(i, 0, 8, 0.33f, 8);
		bounce(i, 0, 8, 0.33f, 4);
		d += .5;
		i = addDumbGoody(instructions, gr,
				-G.width/4/dpi, -G.height/6/dpi + pickUpOffset);
		i.addAction(Actions.delay(d, Actions.alpha(1)));
		//bounce(i, 0.25f, 8, 0.33f, 8);
		bounce(i, 0.25f, 8, 0.33f, 4);
		d += .5;
		i = addDumbGoody(instructions, go, -G.width/4/dpi + (G.size*2)/dpi,
				-G.height/6/dpi + pickUpOffset);
		i.addAction(Actions.delay(d, Actions.alpha(1)));
		//bounce(i, 0.5f, 8, 0.33f, 8);
		bounce(i, 0.5f, 8, 0.33f, 4);

		d += 1.5;
		addLabel(instructions, "not these.",
				G.width/4/dpi, -G.height/6/dpi, G.size*2/dpi, tr)
			.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));
		d += 2;
		i = addDumbBaddy(instructions, r,
				G.width/4/dpi, -G.height/6/dpi + pickUpOffset);
		i.addAction(Actions.delay(d, Actions.alpha(1)));
		//bounce(i, 0.75f, 8, 0.33f, 8);
		bounce(i, 0.75f, 8, 0.33f, 4);

		d += 2;

		addIconButton(instructions, G.playIcon,
				new ClickListener () {
					@Override
					public void clicked(InputEvent e, float x, float y) {
						setStage(game);
					}
				},
				G.width/2/dpi - buttonWidth/dpi/2 - 4, -G.height/2/dpi + 48,
				buttonWidth/dpi, 88).setColor(G.colors.green);
		addIconButton(instructions, G.image("icons/settings.png"),
				new ClickListener () {
					@Override
					public void clicked(InputEvent e, float x, float y) {
						setStage(settings);
					}
				},
				-G.width/2/dpi + buttonWidth/2/dpi + 4, -G.height/2/dpi + 48,
				buttonWidth/dpi, 88).setColor(G.colors.grey);

		d += 2;

		Color c = new Color(G.getAntiColor());
		c.a = 0;
		addImage(instructions, G.playWrite, c,
				G.width/2/dpi - 3*buttonWidth/2/dpi - 8, -G.height/2/dpi + 48,
				buttonWidth/2/dpi, 48)
			.addAction(Actions.delay(d, Actions.forever(Actions.sequence(
								Actions.fadeOut(fadeDur),
								Actions.fadeIn(fadeDur)))));

		/*
		TextButton b1 = addButton(instructions, game, "Start", 
				0, -G.height/2/dpi + 88f, 192, 40f);
		TextButton b2 = addButton(instructions, settings, "Settings", 
				0, -G.height/2/dpi + 40f, 192, 40f);
		b1.addAction(Actions.alpha(.5f));
		b2.addAction(Actions.alpha(.25f));
		b1.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));
		b2.addAction(Actions.delay(d, Actions.fadeIn(fadeDur)));
				*/
	}

	public void bounce (Actor a, float delay, float every, float dur, float h) {
		a.addAction(Actions.delay(delay,
					Actions.forever(Actions.delay(every,
							Actions.sequence(
								Actions.moveBy(0, h, dur),
								Actions.moveBy(0, -h, dur)
								)
							))
					));
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		if (current != game) {
			G.clear();
			Gdx.gl.glEnable(GL20.GL_BLEND);
			G.input(false);
			G.sb.begin();
			G.settings.drawStars();
			G.sb.end();
			Gdx.gl.glEnable(GL20.GL_BLEND);
			//G.glider.draw(new Color(0, 0, 0, .5f));
			if (current != settings && current != statistics
					&& current != colorSettings
					&& current != controlSettings
					&& current != modeSettings
					&& current != loginSettings
					&& current != bottomSettings) {
				G.tick(false, true);
				G.glider.draw();
			} else {
				G.tick(false, false);
			}
		}
		current.act(dt);
		current.draw();
		G.changeSeason();

		/*
		G.tick();
		G.clear();
		G.draw();
		G.clean();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		*/
	}

	public float getDpi () {
		return dpi;
	}
}
