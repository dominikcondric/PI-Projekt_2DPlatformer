package utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import entities.Player;

public class Hud implements Disposable {
	private Stage hud;
	private Label cooldownTimer;
	private int maxHp;
	private ProgressBar progressBar;
	
	public Hud(Player player, SpriteBatch batch, BitmapFont font) {
		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		Table table = new Table();
		table.setFillParent(true);
		table.top().left();
		table.pad(20.f);
		
		maxHp = player.getHp();
		table.row().height(50f).center().width(50f);
		
		table.add(new Label("HP:", new LabelStyle(font, Color.BLACK))).width(30f).height(20.f);
		
		Pixmap p = new Pixmap(1, 1, Format.RGB888);
		p.setColor(Color.RED);
		p.fill();
		
		TextureRegion barRegion = new TextureRegion(new Texture(p));
		barRegion.setRegionHeight(20);
		ProgressBarStyle barStyle = new ProgressBarStyle(null, new TextureRegionDrawable(barRegion));
		barStyle.knobBefore = barStyle.knob;
		progressBar = new ProgressBar(0f, maxHp, 1.0f, false, barStyle);
		progressBar.setHeight(20.f);
		progressBar.setWidth(200.f);
		progressBar.setAnimateDuration(0.4f);
		table.add(progressBar).width(200f).height(40f);
		
		p.dispose();
		
		LabelStyle style = new LabelStyle(font, Color.BLACK);
		cooldownTimer = new Label("10", style);
		cooldownTimer.setFontScale(2f);
		cooldownTimer.setAlignment(Align.center);
		
		TextureRegion fireballRegion = new TextureRegion(new Texture(Gdx.files.internal("projectiles/fireball.png")));
		fireballRegion.setRegionHeight(28);
		fireballRegion.setRegionWidth(35);
		
		Image fireballImage = new Image(new TextureRegion(fireballRegion));
		Stack abilityFireball = new Stack();
		abilityFireball.addActor(fireballImage);
		abilityFireball.addActor(cooldownTimer);
		
		table.add(abilityFireball).width(50f).spaceLeft(960f);
		
		hud.addActor(table);
	}
	
	private void update(final Player player) {
		progressBar.setValue(player.hp);
		
		if (player.currentProjectileCooldown < player.projectileCooldown) {
			cooldownTimer.setVisible(true);
			cooldownTimer.setText(Integer.toString((int)player.currentProjectileCooldown + 1));
		} else {
			cooldownTimer.setVisible(false);
		}
	}
	
	public void render(final Player player) {
		update(player);
		hud.act();
		hud.draw();
	}

	@Override
	public void dispose() {
		hud.dispose();
	}
}
