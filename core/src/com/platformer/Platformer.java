package com.platformer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import screens.MainMenuScreen;
import utility.OptionsUI;

public class Platformer extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	public static float musicVolume = 0.1f;
	public static float effectsVolume = 0.5f;
	public OptionsUI options;

	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont(false);
		options = new OptionsUI(font, batch);
		this.setScreen(new MainMenuScreen(this));
	}
	
	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
	}
}