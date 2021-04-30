package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;

import scenes.TestScene;

public class GameScreen implements Screen {
	private final Platformer game;
	private final TmxMapLoader tiledMapLoader;
	private TestScene scene;
	private Box2DDebugRenderer physicsDebugRenderer;
	private boolean debug = false;

	public GameScreen(final Platformer game) {
		// Game handle
		this.game = game;
		tiledMapLoader = new TmxMapLoader();
		scene = new TestScene(tiledMapLoader.load("Cave/Maps/demo3.tmx"), game.batch, 32.f);
		physicsDebugRenderer = new Box2DDebugRenderer();
	}

	private void update(float deltaTime) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) 
			debug = !debug;
	}
	
	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.SKY);
		scene.update(game.batch, delta);
		scene.render(game.batch);
		if (debug) {
			scene.renderPhysicsBodies(physicsDebugRenderer);
		}
		//placeholder da se moze izac i uc bez da ponovno launch
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			game.setScreen(new MainMenuScreen(game));
			dispose();
		}
		update(delta);
	}

	@Override
	public void dispose () {
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