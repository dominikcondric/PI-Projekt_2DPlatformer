package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	private Stage userInterface;
	private TextButton newGameButton;
	private TextButton resumeGameButton;
	private TextButton optionsButton;
	private Table table;

	public MainMenuScreen(final Platformer game) {
		// Initialization
		this.game = game;
		userInterface = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), game.batch);
		
		// Setup input
		Gdx.input.setInputProcessor(userInterface);

		// Setup
		table = new Table();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Forest/forest24000.jpeg")))));
		userInterface.addActor(table);
		table.setFillParent(true);

		table.align(Align.center);
		float buttonHeight = userInterface.getHeight() / 8;
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB888);
		pixmap.setColor(Color.BROWN);
		pixmap.fill();
		TextureRegionDrawable texture = new TextureRegionDrawable(new Texture(pixmap));
		pixmap.dispose();
		
		TextButtonStyle style = new TextButtonStyle();
		style.font = game.font;
		style.fontColor = Color.WHITE;
		style.overFontColor = new Color(0.415f, 0.14f, 0.087f, 1f);
		style.up = texture;
		
		newGameButton = new TextButton("New game", style);
		resumeGameButton = new TextButton("Resume game", style);
		optionsButton = new TextButton("Options", style);
		
		Image title = new Image(new TextureRegion(new Texture(Gdx.files.internal("AerosAdventureLogo.png"))));
		
		table.row();
		Cell<Image> imageCell = table.add(title);
		imageCell.spaceBottom(buttonHeight / 3.f);
		imageCell.width(userInterface.getWidth() / 2);
		imageCell.height(buttonHeight * 2f);
		
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

		newGameButton.getLabel().setFontScale(buttonHeight / 25.f);
		resumeGameButton.getLabel().setFontScale(buttonHeight / 25.f);
		optionsButton.getLabel().setFontScale(buttonHeight / 25.f);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(Color.BLACK);
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
		userInterface.getViewport().setScreenSize(width, height);
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
		userInterface.dispose();
	}
}
