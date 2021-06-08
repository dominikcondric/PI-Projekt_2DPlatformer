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
	private Table mainMenuTable;
	private Table optionsTable;

	public MainMenuScreen(final Platformer game) {
		// Initialization
		this.game = game;
		optionsTable = game.options.getTable();
		userInterface = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), game.batch);
		
		// Setup input
		Gdx.input.setInputProcessor(userInterface);

		// Setup
		mainMenuTable = new Table();
		TextureRegionDrawable backgroundImage = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Forest/forest24000.jpeg"))));
		mainMenuTable.setBackground(backgroundImage);
		game.options.setBackground(backgroundImage.tint(new Color(0.3f, 0.3f, 0.3f, 1.f)));
		userInterface.addActor(mainMenuTable);
		mainMenuTable.setFillParent(true);

		mainMenuTable.align(Align.center);
		float buttonHeight = userInterface.getHeight() / 8;
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB888);
		pixmap.setColor(Color.BROWN);
		pixmap.fill();
		TextureRegionDrawable buttonBackground = new TextureRegionDrawable(new Texture(pixmap));
		
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = game.font;
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.overFontColor = new Color(0.415f, 0.14f, 0.087f, 1f);
		textButtonStyle.up = buttonBackground;
		
		newGameButton = new TextButton("New game", textButtonStyle);
		resumeGameButton = new TextButton("Resume game", textButtonStyle);
		optionsButton = new TextButton("Options", textButtonStyle);
		
		Image title = new Image(new TextureRegion(new Texture(Gdx.files.internal("AerosAdventureLogo.png"))));
		
		mainMenuTable.row();
		Cell<Image> imageCell = mainMenuTable.add(title);
		imageCell.spaceBottom(buttonHeight / 3.f);
		imageCell.width(userInterface.getWidth() / 2);
		imageCell.height(buttonHeight * 2f);
		
		Cell<TextButton> cell;
		mainMenuTable.row();
		cell = mainMenuTable.add(newGameButton);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);

		mainMenuTable.row();
		cell = mainMenuTable.add(resumeGameButton);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);
		cell.fill();
		
		mainMenuTable.row();
		cell = mainMenuTable.add(optionsButton);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);

		newGameButton.getLabel().setFontScale(buttonHeight / 25.f);
		resumeGameButton.getLabel().setFontScale(buttonHeight / 25.f);
		optionsButton.getLabel().setFontScale(buttonHeight / 25.f);
		
		
		// Options table
		userInterface.addActor(optionsTable);
		pixmap.dispose();
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
			dispose();
			game.setScreen(new GameScreen(game));
		}
		
		if (optionsButton.getClickListener().isPressed()) {
			optionsTable.setVisible(true);
			mainMenuTable.setVisible(false);
		}
		
		if (game.options.backButton.getClickListener().isPressed()) {
			optionsTable.setVisible(false);
			mainMenuTable.setVisible(true);
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
