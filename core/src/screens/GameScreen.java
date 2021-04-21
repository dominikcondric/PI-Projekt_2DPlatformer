package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;

public class GameScreen implements Screen {
	private final Platformer game;
	private OrthographicCamera camera;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
//	private Box2DDebugRenderer physicsDebugRenderer;
//	private World world;

	public GameScreen(final Platformer game) {
		// Game handle
		this.game = game;

		// Camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		
		// Map
		TmxMapLoader mapLoader = new TmxMapLoader();
		map = mapLoader.load("Cave/Maps/demo3.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		mapRenderer.setView(camera);
		
//		physicsDebugRenderer = new Box2DDebugRenderer();
	}

	private void update() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.translate(-200.0f * deltaTime, 0.f);
			camera.update();
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.translate(200.f * deltaTime, 0.f);
			camera.update();
		}
		
//		testWorld.step(deltaTime, 10, 10);
	}
	
	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.SKY);
		mapRenderer.getBatch().setProjectionMatrix(camera.combined);
		mapRenderer.render();
//		physicsDebugRenderer.render(testWorld, camera.combined);
		update();
	}

	@Override
	public void dispose () {
		mapRenderer.dispose();
		map.dispose();
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
