package utility;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import abilities.Ability;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import entities.Player;

public class Hud implements Disposable {
	private Stage hud;
	private ArrayList<Label> cooldownTimers;
	private int maxHp;
	private ProgressBar progressBar;
	private ShapeRenderer shapeRenderer;
	private Table pauseTable;
	public TextButton quitButton;
	public TextButton resumeButton;
	public TextButton optionsButton;
	
	public Hud(Player player, SpriteBatch batch, BitmapFont font) {
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(0.f, 0.f, 0.f, 0.9f);
		hud = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		Gdx.input.setInputProcessor(hud);
		
		maxHp = player.getHp();
		
		Group hp = new Group();
		Label hpLabel = new Label("HP:", new LabelStyle(font, Color.BLACK));
		hpLabel.setSize(30.f, 20.f);
		hpLabel.setPosition(20.f, hud.getHeight() - 40f);
		hp.addActor(hpLabel);
		
		Pixmap progressBarColor = new Pixmap(1, 1, Format.RGB888);
		progressBarColor.setColor(Color.RED);
		progressBarColor.fill();
		
		TextureRegion barRegion = new TextureRegion(new Texture(progressBarColor));
		barRegion.setRegionHeight(20);
		ProgressBarStyle barStyle = new ProgressBarStyle(null, new TextureRegionDrawable(barRegion));
		barStyle.knobBefore = barStyle.knob;
		progressBar = new ProgressBar(0f, maxHp, 1.0f, false, barStyle);
		progressBar.setHeight(20.f);
		progressBar.setWidth(200.f);
		progressBar.setAnimateDuration(0.4f);
		progressBar.setPosition(50f, hud.getHeight() - 40f);
		
		hp.addActor(progressBar);
		hud.addActor(hp);
		
		progressBarColor.dispose();
		
		cooldownTimers = new ArrayList<Label>(4);
		for (Ability ability : player.getAbilityList()) {
			Image fireballImage = new Image(new TextureRegion(ability.getHudTextureRegion()));
			Label cooldownTimer = new Label(Integer.toString((int)ability.getCooldownTime()), new LabelStyle(font, Color.BLACK));
			cooldownTimer.setAlignment(Align.center);
			cooldownTimer.setFontScale(2f);
			fireballImage.setSize(40f, 40f);
			Stack abilityFireball = new Stack();
			abilityFireball.addActor(fireballImage);
			abilityFireball.addActor(cooldownTimer);
			abilityFireball.setPosition(hud.getWidth() - 60.f, hud.getHeight() - 60f);
			abilityFireball.setSize(40.f, 40.f);
			cooldownTimers.add(cooldownTimer);
			hud.addActor(abilityFireball);
		}
		
		// Pause game gui
		pauseTable = new Table();
		
		pauseTable.center();
		pauseTable.setPosition(hud.getWidth() / 4f, hud.getHeight() / 4);
		pauseTable.setSize(hud.getWidth() / 2f, hud.getHeight() / 2f);
		float buttonHeight = hud.getHeight() / 8f;
		
		Pixmap buttonColor = new Pixmap(1, 1, Format.RGB888);
		buttonColor.setColor(Color.BROWN);
		buttonColor.fill();
		TextureRegionDrawable texture = new TextureRegionDrawable(new Texture(buttonColor));
		buttonColor.dispose();
		
		TextButtonStyle style = new TextButtonStyle();
		style.font = font;
		style.fontColor = Color.WHITE;
		style.overFontColor = new Color(0.415f, 0.14f, 0.087f, 1f);
		style.up = texture;
		
		resumeButton = new TextButton("Resume game", style);
		optionsButton = new TextButton("Options", style);
		quitButton = new TextButton("Quit", style);
		
		pauseTable.row().center().expandX();
		pauseTable.add(resumeButton).spaceBottom(buttonHeight / 3.f).height(buttonHeight).width(pauseTable.getWidth());

		pauseTable.row().expandX().center();
		pauseTable.add(optionsButton).spaceBottom(buttonHeight / 3.f).height(buttonHeight).width(pauseTable.getWidth());
		
		pauseTable.row().expandX().center();
		pauseTable.add(quitButton).spaceBottom(buttonHeight / 3.f).height(buttonHeight).width(pauseTable.getWidth());

		resumeButton.getLabel().setFontScale(buttonHeight / 25.f);
		optionsButton.getLabel().setFontScale(buttonHeight / 25.f);
		quitButton.getLabel().setFontScale(buttonHeight / 25.f);
		
		hud.addActor(pauseTable);
	}
	
	public void onResize(int width, int height) {
		hud.getViewport().setScreenSize(width, height);
	}
	
	private void update(final Player player, boolean gamePaused) {
		progressBar.setValue(player.getHp());
		
		ArrayList<Ability> abilities = player.getAbilityList();
		for (int i = 0; i < abilities.size(); ++i) {
			Ability ability = abilities.get(i);
			cooldownTimers.get(i).setText(Integer.toString((int)ability.getCurrentCooldownTime() + 1));
			if (ability.getCurrentCooldownTime() < ability.getCooldownTime()) {
				cooldownTimers.get(i).setVisible(true);
			} else {
				cooldownTimers.get(i).setVisible(false);
			}
		}
		
		pauseTable.setVisible(gamePaused);
	}
	
	private void dimBackground() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.updateMatrices();
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(0.f, 0.f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shapeRenderer.end();
	}
	
	public void render(final Player player, boolean gamePaused) {
		update(player, gamePaused);
		
		if (gamePaused)
			dimBackground();
		
		hud.act();
		hud.draw();
	}

	@Override
	public void dispose() {
		hud.dispose();
		shapeRenderer.dispose();
	}
}
