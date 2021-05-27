package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;
import entities.Entity;
import entities.Player;
import tools.CollisionListener;

public abstract class Scene {
	protected TiledMap map;
	protected World box2DWorld;
	protected OrthogonalTiledMapRenderer mapRenderer;
	protected ArrayList<Entity> entities;
	protected ArrayList<SceneTrigger> triggers;
	protected RayHandler rayHandler;
	private ArrayList<Integer> toDestroy = new ArrayList<Integer>();
	
	public Scene(final TmxMapLoader mapLoader, String mapFilePath, final SpriteBatch batch) {
		this.map = mapLoader.load(mapFilePath);
		float mapTileSize = map.getProperties().get("tilewidth", Integer.class);
		mapRenderer = new OrthogonalTiledMapRenderer(this.map, 1 / mapTileSize, batch);
		box2DWorld = new World(new Vector2(0.f, -18.81f), true);
		box2DWorld.setContactListener(new CollisionListener());
		rayHandler = new RayHandler(box2DWorld);
		
		entities = new ArrayList<Entity>(5);
		triggers = new ArrayList<SceneTrigger>(2);
		constructTileMap();
		constructEntities();
	}
	
	protected abstract void constructTileMap();
	public abstract void constructEntities();
	
	public World getWorld() {
		return box2DWorld;
	}
	
	public ArrayList<SceneTrigger> getTriggers() {
		return triggers;
	}
	
	public void resetEntities() {
		for (Entity e : entities)
			box2DWorld.destroyBody(e.getBody());
		
		entities.clear();
		constructEntities();
	}
	
	public Vector2 getTiledMapSize() {
		MapProperties props = map.getProperties();
		int width = props.get("width", Integer.class);
		int height = props.get("height", Integer.class);
		
		return new Vector2(width, height);
	}
	
	public void addTrigger(SceneTrigger trigger) {
		triggers.add(trigger);
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
		entity.addToWorld(box2DWorld);
		if (entity instanceof Player) {
			placePlayerOnScene(entity);
		}
	}
	
	public Player getPlayer() {
		for (Entity e : entities) {
			if (e instanceof Player) {
				return (Player)e;
			}
		}
		
		return null;
	}
	
	public void render(SpriteBatch batch, OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render();
		rayHandler.setCombinedMatrix(camera);
		rayHandler.setAmbientLight(1.f, 1.f, 1.f, 0.1f);
		batch.begin();
		for (final Entity e : entities) {
			e.render(batch);
		}
		batch.end();
		rayHandler.updateAndRender();
	}
	
	public void update(float deltaTime) {
		box2DWorld.step(deltaTime, 10, 10);
		for (int i = entities.size() - 1; i >= 0; --i) {
			if (entities.get(i).isSetToDestroy()) {
				toDestroy.add(i);
			} else {
				entities.get(i).update(this, deltaTime);
			}
		}
		
		for (Integer i : toDestroy) {
			box2DWorld.destroyBody(entities.get(i.intValue()).getBody());
			entities.remove(i.intValue());
		}
		
		toDestroy.clear();
	}
	
	public void dispose() {
		box2DWorld.dispose();
		map.dispose();
	}

	protected abstract void placePlayerOnScene(Entity player);
}