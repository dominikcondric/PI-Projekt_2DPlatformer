package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;

import entities.Player;
import scenes.CaveScene;
import scenes.ForestScene;
import scenes.Scene;
import screens.GameOverScreen.ScreenType;
import utility.SceneManager;

public class GameScreen implements Screen {
	private final Platformer game;
	private final TmxMapLoader tiledMapLoader;
	private Box2DDebugRenderer physicsDebugRenderer;
	private OrthographicCamera camera;
	private SceneManager sceneManager;
	private Player player;
	private boolean debug = false;

	public GameScreen(final Platformer game) {
		// Game handle
		this.game = game;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth() / 30.f, Gdx.graphics.getHeight() / 30.f);
		physicsDebugRenderer = new Box2DDebugRenderer();
		tiledMapLoader = new TmxMapLoader();
		sceneManager = new SceneManager();
		
		Scene caveScene = new CaveScene(tiledMapLoader, game.batch);
		sceneManager.addScene(caveScene, "Cave", true);
		
		Scene forestScene = new ForestScene(tiledMapLoader, game.batch);
		sceneManager.addScene(forestScene, "Forest", true);
		
		player = new Player(new Vector2(2.f, 39.f));
		forestScene.addEntity(player);
	}

	private void update(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
			debug = !debug;
		}
		
		Vector2 playerPosition = player.getBody().getPosition();
		Vector2 activeMapSize = sceneManager.getActiveScene().getTiledMapSize();
		
		if (playerPosition.x - camera.viewportWidth / 2f < 0f) {
			camera.position.x = camera.viewportWidth / 2f;
		} else if (playerPosition.x + camera.viewportWidth / 2.f > activeMapSize.x) {
			camera.position.x = activeMapSize.x - camera.viewportWidth / 2f;
		} else {
			camera.position.x = playerPosition.x;
		}
		
		if (playerPosition.y - camera.viewportHeight / 2f < 0f) {
			camera.position.y = camera.viewportHeight / 2f;
		} else if (playerPosition.y + camera.viewportHeight / 2.f > activeMapSize.y) {
			camera.position.y = activeMapSize.y - camera.viewportHeight / 2f;
		} else {
			camera.position.y = playerPosition.y;
		}
		
		camera.update();
		sceneManager.update();
		
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			dispose();
			game.setScreen(new MainMenuScreen(game));
		} else if (player.isSetToDestroy()) {
			player.setToDestroy(false);
			sceneManager.getActiveScene().resetEntities();
			player.setPosition(2.f, 8.f);
			sceneManager.getActiveScene().addEntity(player);
			game.setScreen(new GameOverScreen(game, this, ScreenType.GAME_OVER));
		}
	}
	
	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.SKY);
		Scene activeScene = sceneManager.getActiveScene();
		game.batch.setProjectionMatrix(camera.combined);
		activeScene.update(delta);
		activeScene.render(game.batch, camera);
		if (debug) {
			physicsDebugRenderer.render(activeScene.getWorld(), camera.combined);
		}
		update(delta);
	}
	
	@Override
	public void dispose () {
		sceneManager.dispose();
		physicsDebugRenderer.dispose();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
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
}