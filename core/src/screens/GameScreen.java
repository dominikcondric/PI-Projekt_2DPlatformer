package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;

import entities.Player;
import scenes.Scene;
import scenes.TestScene;
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
		tiledMapLoader = new TmxMapLoader();
		sceneManager = new SceneManager();
		
		TestScene caveScene = new TestScene(tiledMapLoader.load("Cave/Maps/demo3.tmx"), game.batch, 32.f, "Cave");
		sceneManager.addScene(caveScene, true);
		physicsDebugRenderer = new Box2DDebugRenderer();
		
		TestScene desertScene = new TestScene(tiledMapLoader.load("Desert/desert_map.tmx"), game.batch, 32.f, "Desert");
		sceneManager.addScene(desertScene, false);
		sceneManager.addSceneToFollow("Cave", "Desert");
		
		player = new Player();
		player.getSprite().setPosition(2.f, 8.f);
		caveScene.addEntity(player);
	}

	private void update(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) 
			debug = !debug;
		
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.position.add(-100.f * deltaTime, 0, 0);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.position.add(100.f * deltaTime, 0, 0);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.position.add(0.f, 100.f * deltaTime, 0.f);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camera.position.add(0.f, -100.f * deltaTime, 0);
		}
		
		camera.update();
		sceneManager.update();
	}
	
	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.SKY);
		game.batch.setProjectionMatrix(camera.combined);
		Scene activeScene = sceneManager.getActiveScene();
		activeScene.update(game.batch, delta);
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