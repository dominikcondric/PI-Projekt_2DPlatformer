package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import entities.Entity;

public abstract class Scene {
	protected TiledMap map;
	protected ArrayList<Entity> entities; 
	protected World box2DWorld;
	protected OrthographicCamera camera;
	protected OrthogonalTiledMapRenderer mapRenderer;
	protected Entity playable;
	boolean isPlayable;
	
	Scene(final TiledMap map, final SpriteBatch batch, float mapTileSize) {
		this.map = map;
		mapRenderer = new OrthogonalTiledMapRenderer(this.map, 1 / mapTileSize, batch);
		box2DWorld = new World(new Vector2(0.f, -18.81f), true);
		camera = new OrthographicCamera();
		entities = new ArrayList<Entity>();
	}
	
	public void addEntity(Entity entity, boolean isPlayable) {
		entity.addToWorld(box2DWorld);
		if(isPlayable) {
			playable = entity;
		}
		entities.add(entity);
	}
	
	public void renderPhysicsBodies(Box2DDebugRenderer physicsDebugRenderer) {
		physicsDebugRenderer.render(box2DWorld, camera.combined);
	}
	
	public void render(SpriteBatch batch) {
		mapRenderer.getBatch().setProjectionMatrix(camera.combined);
		mapRenderer.render();
		batch.begin();
		for (final Entity entity : entities) {
			final Sprite sprite = entity.getSprite();
			batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
		}
		batch.end();
	}
	
	public void update(SpriteBatch batch, float deltaTime) {
		batch.setProjectionMatrix(camera.combined);
		box2DWorld.step(deltaTime, 10, 10);
	}
	
	public void dispose() {
		box2DWorld.dispose();
		map.dispose();
	}
	
}