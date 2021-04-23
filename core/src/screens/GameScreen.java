package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;
import entities.Player;

import scenes.TestScene;

public class GameScreen implements Screen {
	private final Platformer game;
	private final TmxMapLoader tiledMapLoader;
	private TestScene scene;
	OrthogonalTiledMapRenderer mapRenderer;
	private Box2DDebugRenderer physicsDebugRenderer;
	private boolean debug = false;

	public GameScreen(final Platformer game) {
		// Game handle
		this.game = game;
		tiledMapLoader = new TmxMapLoader();
		
		physicsDebugRenderer = new Box2DDebugRenderer();
		mapRenderer = new OrthogonalTiledMapRenderer(null);
		scene = new TestScene(tiledMapLoader.load("Cave/Maps/demo3.tmx"));
		scene.setSceneForRendering(mapRenderer);
	}

	private void update(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) 
			debug = !debug;
	}
	
	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.SKY);
		scene.update(mapRenderer, game.batch, delta);
		mapRenderer.render();
		scene.renderEntities(game.batch);
		if (debug) {
			scene.renderPhysicsBodies(physicsDebugRenderer);
		}
		update(delta);
	}

	@Override
	public void dispose () {
		mapRenderer.dispose();
		scene.dispose();
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
