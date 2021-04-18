package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;

public class GameScreen implements Screen {
	final Platformer game;
	private OrthographicCamera camera;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;

	public GameScreen(final Platformer game) {
		// Game handle
		this.game = game;

		// Camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
		
		// Map
		TmxMapLoader mapLoader = new TmxMapLoader();
		map = mapLoader.load("StartingMap.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map);
		mapRenderer.setView(camera);
	}

	private void update() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.translate(-1.0f, 0.f);
			camera.update();
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.translate(1.f, 0.f);
			camera.update();
		}
	}
	
	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.SKY);
		mapRenderer.getBatch().setProjectionMatrix(camera.combined);
		mapRenderer.render();
		update();
	}

	@Override
	public void dispose () {
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
		dispose();
		map.dispose();
		mapRenderer.dispose();
	}
}
