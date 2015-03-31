package org.anism.lotw;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.anism.lotw.Glider;
import org.anism.lotw.Glob;
import org.anism.lotw.goodies.Goody;
import org.anism.lotw.goodies.Baddy;

public class Stats {
	class Field {
		int total;
		int best;
		String name;

		public Field (String n) {
			name = n;
		}

		public void update (int n) {
			total += n;
			if (n > best) best = n;
		}

		public void write (Preferences prefs) {
			prefs.putInteger("total " + name, total);
			prefs.putInteger("best " + name, best);
			prefs.flush();
		}

		public void read (Preferences prefs) {
			total = prefs.getInteger("total " + name, 0);
			best = prefs.getInteger("best " + name, 0);
		}

		public void display (Table table, Label.LabelStyle ls) {
			Color black = new Color(0, 0, 0, 1);
			table.add(new Label(Integer.toString(total), ls)).pad(4);
			table.add(new Label(Integer.toString(best), ls)).pad(4);
		}

		public int getTotal () {
			return total;
		}
	}

	Field score = new Field("Score");
	Field damages = new Field("Damages");
	Field snitches = new Field("Snitches");
	Field combo = new Field("Combo");

	Field[] fields = {score, damages, snitches, combo};

	Glob G;

	public Stats (Glob g) {
		G = g;
	}

	public int totalScore () {
		return score.getTotal();
	}

	public void update (Glider g) {
		score.update(g.score);
		damages.update(g.damages);
		snitches.update(g.snitches);
		combo.update(g.maxCombo);
	}

	public void set (Stats s) {
		score = s.score;
		damages = s.damages;
		snitches = s.snitches;
		combo = s.combo;
	}

	public void write () {
		Preferences prefs = Gdx.app.getPreferences("Lifetime Stats");
		for (Field f : fields) {
			f.write(prefs);
		}
	}

	public void read () {
		Preferences prefs = Gdx.app.getPreferences("Lifetime Stats");
		for (Field f : fields) {
			f.read(prefs);
		}
	}

	public Table display (Label.LabelStyle ls) {
		Table table = new Table();
		table.setFillParent(true);
		Color black = new Color(0, 0, 0, 1);
		table.add(new Label("", ls)).pad(4);
		table.add(new Label("Total", ls)).pad(4);
		table.add(new Label("Best", ls)).pad(4);

		table.defaults().pad(4 * G.dpi);

		table.row();
		Goody gimg = new Goody(G, 0, 0, G.colors.goodyGreen);
		Goody simg = new Goody(G, 0, 0, G.colors.goodyGold);
		Goody bimg = new Baddy(G, G.colors.goodyRed);
		table.add(gimg);
		score.display(table, ls);

		table.row();
		table.add(simg);
		snitches.display(table, ls);

		table.row();
		table.add(bimg);
		damages.display(table, ls);

		table.row();
		table.add(new Label("", ls));
		table.row();

		table.add(new Label("Combo", ls)).colspan(2);
		table.add(new Label(Integer.toString(combo.best), ls));

		return table;
	}
}
