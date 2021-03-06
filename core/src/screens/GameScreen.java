package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;

import box2dLight.Light;
import box2dLight.PointLight;
import entities.Player;
import scenes.CastleInDistanceScene;
import scenes.CastleScene;
import scenes.ForestScene;
import scenes.OutOfMapTrigger;
import scenes.Scene;
import screens.GameOverScreen.ScreenType;
import tools.CollisionListener;
import utility.Hud;
import utility.SceneManager;

public class GameScreen implements Screen {
	private final Platformer game;
	private final TmxMapLoader tiledMapLoader;
	private Box2DDebugRenderer physicsDebugRenderer;
	private OrthographicCamera camera;
	private SceneManager sceneManager;
	private Player player;
	private Hud inGameHud;
	private boolean debug = false;
	private boolean paused = false;

	public GameScreen(final Platformer game) {
		// Game handle
		this.game = game;
		camera = new OrthographicCamera();
		float aspectRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
		camera.setToOrtho(false, 20.f * aspectRatio, 20.f);
		physicsDebugRenderer = new Box2DDebugRenderer();
		tiledMapLoader = new TmxMapLoader();
		sceneManager = new SceneManager();
		player = new Player(new Vector2(2.f, 39.f));
		inGameHud = new Hud(player, game.batch, game.font, game.options);
		Light.setGlobalContactFilter((short)CollisionListener.LIGHT_BIT, (short)0, (short)(CollisionListener.PLATFORM_BIT | CollisionListener.SOLID_WALL_BIT | CollisionListener.OTHERS_BIT));
		
		Scene castleScene = new CastleScene(tiledMapLoader, game.batch);
		sceneManager.addScene(castleScene, "Castle", true);
		Scene introScene = new CastleInDistanceScene(tiledMapLoader, game.batch);
		sceneManager.addScene(introScene, "Intro", true);
		Scene forestScene = new ForestScene(tiledMapLoader, game.batch);
		sceneManager.addScene(forestScene, "Forest", false);
		introScene.addTrigger(new OutOfMapTrigger(castleScene, player, new Vector2(19.f, 19.f), true));
		castleScene.addTrigger(new OutOfMapTrigger(forestScene, player, new Vector2(220.f, 200.f), true));
		introScene.addEntity(player);
	}

	private void update(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P) && !paused) {
			debug = !debug;
		}
		
		Vector2 playerPosition = player.getPosition();
		Vector3 activeMapSize = sceneManager.getActiveScene().getTiledMapSize();
		
		float cameraZoom = activeMapSize.y / activeMapSize.z;
		float aspectRatio = (float)Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
		
		camera.setToOrtho(false, cameraZoom * aspectRatio, cameraZoom);
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
		sceneManager.update(deltaTime);
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			paused = !paused;
			if (paused)
				sceneManager.getActiveScene().stopMusic(true);
			else 
				sceneManager.getActiveScene().playMusic();
		} 
		
		if (paused == true) {
			if (inGameHud.resumeButton.getClickListener().isPressed()) {
				paused = false;
			}
			
			if (inGameHud.quitButton.getClickListener().isPressed()) {
				dispose();
				game.setScreen(new MainMenuScreen(game));
			}
		} else if (!player.isActive()) {
			sceneManager.getActiveScene().resetEntities();
			sceneManager.getActiveScene().stopMusic(false);
			game.setScreen(new GameOverScreen(game, this, ScreenType.GAME_OVER));
		}
	}
	
	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.BLACK);
		Scene activeScene = sceneManager.getActiveScene();
		game.batch.setProjectionMatrix(camera.combined);
		
		if (!paused && !sceneManager.isTransitionTriggered()) {
			activeScene.update(delta);
		}
		
		activeScene.render(game.batch, camera);
		if (debug) {
			physicsDebugRenderer.render(activeScene.getWorld(), camera.combined);
		}
			
		inGameHud.render(activeScene, paused, sceneManager.isTransitionTriggered());
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
		inGameHud.onResize(width, height);
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