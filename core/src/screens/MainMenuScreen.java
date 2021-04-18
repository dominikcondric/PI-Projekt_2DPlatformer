package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.platformer.Platformer;

public class MainMenuScreen implements Screen {
	private final Platformer game;
	private BitmapFont font;
	private Stage userInterface;
	TextButton newGameButton;
	TextButton resumeGameButton;
	TextButton optionsButton;
	Table table;

	public MainMenuScreen(final Platformer game) {
		// Initialization
		this.game = game;
		font = new BitmapFont();
		userInterface = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		// Setup input
		Gdx.input.setInputProcessor(userInterface);
		font.getData().setScale(20.f);

		// Setup
		table = new Table();
		userInterface.addActor(table);
		table.setFillParent(true);

		table.align(Align.center);
		float buttonHeight = userInterface.getHeight() / 8;
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB888);
		pixmap.setColor(Color.BROWN);
		pixmap.fill();
		TextureRegionDrawable texture = new TextureRegionDrawable(new Texture(pixmap));
		
		TextButtonStyle style = new TextButtonStyle();
		style.font = font;
		style.fontColor = Color.WHITE;
		style.overFontColor = Color.SKY;
		style.up = texture;
		
		newGameButton = new TextButton("NEW GAME", style);
		resumeGameButton = new TextButton("RESUME GAME", style);
		optionsButton = new TextButton("OPTIONS", style);
		
		Cell<TextButton> cell;
		table.row();
		cell = table.add(newGameButton);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);

		table.row();
		cell = table.add(resumeGameButton);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);
		cell.fill();
		
		table.row();
		cell = table.add(optionsButton);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);

		newGameButton.getLabel().setFontScale(buttonHeight / 20.f);
		resumeGameButton.getLabel().setFontScale(buttonHeight / 20.f);
		optionsButton.getLabel().setFontScale(buttonHeight / 20.f);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.SKY);
		userInterface.act();
		userInterface.draw();
		
		if (newGameButton.getClickListener().isPressed()) {
			game.setScreen(new GameScreen(game));
			dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		userInterface.getViewport().update(width, height, false);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		font.dispose();
		userInterface.dispose();
	}

}
