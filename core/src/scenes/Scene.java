package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.platformer.Platformer;

import box2dLight.RayHandler;
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
	protected RayHandler rayHandler;
	private ArrayList<Integer> toDestroy = new ArrayList<Integer>();
	protected SceneAnimation runningAnimation = null;
	protected Player player = null;
	protected float visibleMapScale = 4.f;
	protected Color ambientLight = Color.BLACK;
	protected Music music;
	protected Color batchTintColor;
	
	public Scene(final TmxMapLoader mapLoader, String mapFilePath, final SpriteBatch batch) {
		this.map = mapLoader.load(mapFilePath);
		float mapTileSize = map.getProperties().get("tilewidth", Integer.class);
		mapRenderer = new OrthogonalTiledMapRenderer(this.map, 1 / mapTileSize, batch);
		box2DWorld = new World(new Vector2(0.f, -18.81f), true);
		box2DWorld.setContactListener(new CollisionListener());
		rayHandler = new RayHandler(box2DWorld);
		batchTintColor = new Color(0.5f, 0.5f, 0.5f, 1.f);
		
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
	
	public SceneAnimation getSceneAnimation() {
		return runningAnimation;
	}
	
	public ArrayList<SceneTrigger> getTriggers() {
		return triggers;
	}
	
	public RayHandler getRayHandler() {
		return rayHandler;
	}
	
	public void resetPlayer() {
		player = null;
	}
	
	public void resetEntities() {
		toDestroy.clear();
		
		for (int i = entities.size() - 1; i >= 0; --i) {
			Entity e = entities.get(i);
			e.reset(box2DWorld);
			if (e.isSetToDestroy())
				toDestroy.add(i);
		}
		
		for (Integer i : toDestroy) {
			entities.get(i).destroyBody(box2DWorld);
			entities.remove(i.intValue());
		}
		
		toDestroy.clear();
		placePlayerOnScene(player);
	}
	
	public Vector3 getTiledMapSize() {
		MapProperties props = map.getProperties();
		int width = props.get("width", Integer.class);
		int height = props.get("height", Integer.class);
		
		return new Vector3(width, height, visibleMapScale);
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
			player.controllable = true;
		}
	}
	
	public void render(SpriteBatch batch, OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.getBatch().setColor(batchTintColor);
		mapRenderer.render();
		batch.setColor(batchTintColor);
		rayHandler.setCombinedMatrix(camera);
		rayHandler.setAmbientLight(ambientLight);
		batch.begin();
		for (final Entity e : entities) {
			e.render(batch);
		}
		batch.end();
		rayHandler.updateAndRender();
	}
	
	public void update(float deltaTime) {
		box2DWorld.step(deltaTime, 10, 10);
		if (!music.isPlaying()) {
			music.play();
		}
		
		if (runningAnimation != null) {
			player.controllable = false;
			runningAnimation.animate(deltaTime);
			if (runningAnimation.isFinished()) {
				runningAnimation = null;
				player.controllable = true;
			}
		} 
		
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
		
		music.setVolume(Platformer.musicVolume);
	}
	
	public void dispose() {
		box2DWorld.dispose();
		map.dispose();
		music.stop();
		rayHandler.dispose();
	}
	
	public void playMusic() {
		music.setVolume(Platformer.musicVolume);
		music.play();
		music.setLooping(true);
	}
	
	public void stopMusic(boolean pause) {
		if (pause) {
			music.pause();
		} else {
			music.stop();
		}
	}
	
	protected abstract void placePlayerOnScene(Player player);
}