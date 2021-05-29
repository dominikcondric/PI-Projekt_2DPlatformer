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

import entities.Entity;
import entities.Player;
import sceneAnimations.SceneAnimation;
import tools.CollisionListener;

public abstract class Scene {
	protected TiledMap map;
	protected World box2DWorld;
	protected OrthogonalTiledMapRenderer mapRenderer;
	protected ArrayList<Entity> entities;
	protected ArrayList<SceneTrigger> triggers;
	private ArrayList<Integer> toDestroy = new ArrayList<Integer>();
	protected SceneAnimation runningAnimation = null;
	protected Player player = null;
	
	public Scene(final TmxMapLoader mapLoader, String mapFilePath, final SpriteBatch batch) {
		this.map = mapLoader.load(mapFilePath);
		float mapTileSize = map.getProperties().get("tilewidth", Integer.class);
		mapRenderer = new OrthogonalTiledMapRenderer(this.map, 1 / mapTileSize, batch);
		box2DWorld = new World(new Vector2(0.f, -18.81f), true);
		box2DWorld.setContactListener(new CollisionListener());
		
		entities = new ArrayList<Entity>(5);
		triggers = new ArrayList<SceneTrigger>(2);
		constructTileMap();
		constructEntities();
	}
	
	protected abstract void constructTileMap();
	public abstract void constructEntities();
	
	public void beginAnimation(SceneAnimation animation) {
		runningAnimation = animation;
	}
	
	public World getWorld() {
		return box2DWorld;
	}
	
	public ArrayList<SceneTrigger> getTriggers() {
		return triggers;
	}
	
	public void resetPlayer() {
		player = null;
	}
	
	public void resetEntities() {
		for (Entity e : entities)
			e.destroyBody(box2DWorld);
		
		entities.clear();
		constructEntities();
	}
	
	public Vector2 getTiledMapSize() {
		MapProperties props = map.getProperties();
		int width = props.get("width", Integer.class);
		int height = props.get("height", Integer.class);
		
		return new Vector2(width, height);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void addTrigger(SceneTrigger trigger) {
		triggers.add(trigger);
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
		entity.addToWorld(box2DWorld);
		if (entity instanceof Player) {
			player = (Player)entity;
			placePlayerOnScene(player);
		}
	}
	
	public void render(SpriteBatch batch, OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render();
		batch.begin();
		for (final Entity e : entities) {
			e.render(batch);
		}
		batch.end();
	}
	
	public void update(float deltaTime) {
		if (runningAnimation != null) {
			runningAnimation.animate(deltaTime);
			if (runningAnimation.isFinished())
				runningAnimation = null;
		} 
		
		if (runningAnimation == null || (runningAnimation != null && !runningAnimation.shouldSceneStop())) {
			box2DWorld.step(deltaTime, 10, 10);
			for (int i = entities.size() - 1; i >= 0; --i) {
				if (entities.get(i).isSetToDestroy()) {
					toDestroy.add(i);
				} else {
					entities.get(i).update(this, deltaTime);
				}
			}
			
			for (Integer i : toDestroy) {
				entities.get(i.intValue()).destroyBody(box2DWorld);
				entities.remove(i.intValue());
			}
			
			toDestroy.clear();
		}
		
	}
	
	public void dispose() {
		box2DWorld.dispose();
		map.dispose();
	}

	protected abstract void placePlayerOnScene(Player player);
}