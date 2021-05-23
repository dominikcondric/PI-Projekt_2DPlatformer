package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.platformer.Platformer;

public class GameOverScreen implements Screen {
	final Platformer game;
	private Stage userInterface;
	private Table table;
	private final GameScreen gameScreen;
	private TextButton continueGame;
	private TextButton exitGame;
	public enum ScreenType { GAME_OVER };
	
	public GameOverScreen(final Platformer game, final GameScreen gameScreen, ScreenType type) {
		this.game = game;
		this.gameScreen = gameScreen;
		
		userInterface = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), game.batch);
		table = new Table();
		
		Gdx.input.setInputProcessor(userInterface);
		
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
		style.font = game.font;
		style.fontColor = Color.WHITE;
		style.overFontColor = new Color(0.415f, 0.14f, 0.087f, 1f);
		style.up = texture;
		
		String s = new String();
		if (type == ScreenType.GAME_OVER) {
			s = "Game over";
		}
		
		Label gameStateLabel = new Label(s, new Label.LabelStyle(game.font, Color.BROWN));
		gameStateLabel.setFontScale(4.f);
		Cell<Label> labelCell = table.add(gameStateLabel);
		labelCell.align(Align.center);
		gameStateLabel.setAlignment(Align.center);
		labelCell.width(userInterface.getWidth() / 2);
		labelCell.height(buttonHeight);
		table.row();
		
		continueGame = new TextButton("Continue game", style);
		exitGame = new TextButton("Exit game", style);
		
		Cell<TextButton> cell;
		cell = table.add(continueGame);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);

		table.row();
		cell = table.add(exitGame);
		cell.spaceBottom(buttonHeight / 3.f);
		cell.width(userInterface.getWidth() / 2);
		cell.height(buttonHeight);
		cell.fill();
		
		continueGame.getLabel().setFontScale(buttonHeight / 25.f);
		exitGame.getLabel().setFontScale(buttonHeight / 25.f);
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
		if (continueGame.getClickListener().isPressed()) {
			game.setScreen(gameScreen);
		} else if (exitGame.getClickListener().isPressed()) {
			game.setScreen(new MainMenuScreen(game));
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
