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
import scenes.Scene;

public class Hud implements Disposable {
	private Stage hud;
	private ArrayList<Stack> abilityIcons;
	private int maxHp;
	private ProgressBar progressBar;
	private ShapeRenderer shapeRenderer;
	private Table pauseTable;
	public TextButton quitButton;
	public TextButton resumeButton;
	public TextButton optionsButton;
	private Label dialogueBox;
	private Label coinCount;
	
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
		
		abilityIcons = new ArrayList<Stack>(4);
		float offset = 60.f;
		for (Ability ability : player.getAbilityList()) {
			Image abilityImage = new Image(new TextureRegion(ability.getHudTextureRegion()));
			Label cooldownTimer = new Label(Integer.toString((int)ability.getCooldownTime()), new LabelStyle(font, Color.BLACK));
			cooldownTimer.setAlignment(Align.center);
			cooldownTimer.setFontScale(2f);
			abilityImage.setSize(40f, 40f);
			Stack abilityStack = new Stack();
			abilityStack.addActor(abilityImage);
			abilityStack.addActor(cooldownTimer);
			abilityStack.setPosition(hud.getWidth() - offset, hud.getHeight() - 60.f);
			offset *= 2.f;
			abilityStack.setSize(40.f, 40.f);
			abilityStack.setVisible(ability.active);
			abilityIcons.add(abilityStack);
			hud.addActor(abilityStack);
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

		Pixmap p = new Pixmap(1, 1, Format.RGBA8888);
		p.setColor(0.f, 0.f, 0.f, 0.7f);
		p.fill();
		LabelStyle dialogueBoxStyle = new LabelStyle(font, Color.WHITE);
		dialogueBoxStyle.background =  new TextureRegionDrawable(new TextureRegion(new Texture(p)));
		p.dispose();
		dialogueBox = new Label("", dialogueBoxStyle);
		dialogueBox.setPosition(10.f, 10.f);
		dialogueBox.setSize(hud.getWidth() - 20.f, hud.getHeight() / 4.f);
		dialogueBox.setAlignment(Align.center);
		dialogueBox.setWrap(true);
		dialogueBox.setVisible(false);
		dialogueBox.setFontScale(2.f);
		
		hud.addActor(dialogueBox);
		
		coinCount = new Label("", new LabelStyle(font, Color.BROWN));
		coinCount.setFontScale(2.5f);
		coinCount.setSize(40f, 40f);
		coinCount.setPosition(hud.getWidth() / 2.f, hud.getHeight() - 50.f);
		coinCount.setAlignment(Align.center);
		hud.addActor(coinCount);
		
		Image coinImage = new Image(new TextureRegion(new Texture(Gdx.files.internal("Castle/Castle_Tilesets/Coin/Coin.png")), 0, 0, 32, 32));
		coinImage.setPosition(hud.getWidth() / 2.f - 40f, hud.getHeight() - 50.f);
		coinImage.setSize(40f, 40f);
		hud.addActor(coinImage);
	}
	
	public void onResize(int width, int height) {
		hud.getViewport().setScreenSize(width, height);
	}
	
	private void update(final Scene scene, boolean gamePaused) {
		Player player = scene.getPlayer();
		progressBar.setValue(player.getHp());
		coinCount.setText(Integer.toString(player.getCoinCount()));
		
		ArrayList<Ability> abilities = player.getAbilityList();
		for (int i = 0; i < abilities.size(); ++i) {
			Ability ability = abilities.get(i);
			if (ability.active) {
				Label cooldownTimer = null;
				abilityIcons.get(i).setVisible(true);
				
				if (abilityIcons.get(i).getChild(1) instanceof Label) {
					cooldownTimer = (Label)abilityIcons.get(i).getChild(1);
				} else {
					cooldownTimer = (Label)abilityIcons.get(i).getChild(0);
				}
				
				cooldownTimer.setText(Integer.toString((int)ability.getCurrentCooldownTime() + 1));
				if (ability.getCurrentCooldownTime() < ability.getCooldownTime()) {
					cooldownTimer.setVisible(true);
				} else {
					cooldownTimer.setVisible(false);
				}
			} else {
				abilityIcons.get(i).setVisible(false);
			}
		}
		
		pauseTable.setVisible(gamePaused);
		if (scene.getSceneAnimation() != null) {
			dialogueBox.setText(scene.getSceneAnimation().getDialogueText());
			dialogueBox.setVisible(true);
		} else {
			dialogueBox.setText("");
			dialogueBox.setVisible(false);
		}
	}
	
	private void dimBackground() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.updateMatrices();
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(0.f, 0.f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shapeRenderer.end();
	}
	
	public void render(final Scene scene, boolean gamePaused, boolean sceneInTransition) {
		update(scene, gamePaused);
		
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
