package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
//import entities.Entity;

public abstract class Scene {
	protected TiledMap map;
//	protected ArrayList<Entity> entities; 
	protected World box2DWorld;
	protected OrthographicCamera camera;
	
	Scene(final TiledMap map) {
		this.map = map;
		box2DWorld = new World(new Vector2(0.f, -9.81f), true);
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
	}
	
	public void addEntity(Entity entity) {
		entity.addToWorld(box2DWorld);
		entities.add(entity);
	}
	
	public void setSceneForRendering(OrthogonalTiledMapRenderer mapRenderer) {
		mapRenderer.setMap(map);
		mapRenderer.setView(camera);
	}
	
	public void renderEntities(SpriteBatch batch) {
		batch.begin();
		for (final Entity entity : entities) {
			final Sprite sprite = entity.getSprite();
			batch.draw(sprite, sprite.getX(), sprite.getY());
		}
		batch.end();
	}
	
	public void update(OrthogonalTiledMapRenderer mapRenderer, SpriteBatch batch, float deltaTime) {
		mapRenderer.setView(camera);
		batch.setProjectionMatrix(camera.combined);
		box2DWorld.step(Gdx.graphics.getDeltaTime(), 10, 10);
	}
	
	public void dispose() {
		box2DWorld.dispose();
		map.dispose();
	}
	
}
