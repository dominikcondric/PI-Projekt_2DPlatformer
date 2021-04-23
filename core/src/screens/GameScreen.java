package screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.platformer.Platformer;
import entities.Player;

import scenes.TestScene;

public class GameScreen implements Screen {
	private final Platformer game;
	private final TmxMapLoader tiledMapLoader;
	private TestScene scene;
	OrthogonalTiledMapRenderer mapRenderer;
//	private Box2DDebugRenderer physicsDebugRenderer;

	public GameScreen(final Platformer game) {
		// Game handle
		this.game = game;
		tiledMapLoader = new TmxMapLoader();
		
		mapRenderer = new OrthogonalTiledMapRenderer(null);
		scene = new TestScene(tiledMapLoader.load("Cave/Maps/demo3.tmx"));
		scene.setSceneForRendering(mapRenderer);
		
		
	}

	@Override
	public void render (float delta) {
		ScreenUtils.clear(Color.SKY);
		scene.update(mapRenderer, game.batch, delta);
		mapRenderer.render();
		scene.renderEntities(game.batch);
	}

	@Override
	public void dispose () {
		mapRenderer.dispose();
		scene.dispose();
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
