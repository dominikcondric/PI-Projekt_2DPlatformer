package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class TestScene extends Scene {
	
	public TestScene(TiledMap map) {
		super(map);
//		camera.zoom = 0.4f;
		camera.update();
	}
	
	@Override
	public void update(OrthogonalTiledMapRenderer mapRenderer, SpriteBatch batch, float deltaTime) {
		super.update(mapRenderer, batch, deltaTime);
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
	}
}
