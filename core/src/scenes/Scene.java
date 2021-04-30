package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import entities.Entity;
import entities.Player;

public abstract class Scene {
	protected TiledMap map;
	protected World box2DWorld;
	protected OrthogonalTiledMapRenderer mapRenderer;
	protected Player player;
	protected ArrayList<Integer> toDestroy;
	protected final String name;
	
	Scene(final TiledMap map, final SpriteBatch batch, float mapTileSize, final String sceneName) {
		this.map = map;
		this.name = sceneName;
		mapRenderer = new OrthogonalTiledMapRenderer(this.map, 1 / mapTileSize, batch);
		box2DWorld = new World(new Vector2(0.f, -18.81f), true);
		toDestroy = new ArrayList<Integer>();
	}
	
	public void addEntity(Entity entity) {
		entity.addToWorld(box2DWorld);
		if (entity instanceof Player) {
			player = (Player)entity;
		}
	}
	
	public World getWorld() {
		return box2DWorld;
	}
	
	public String getName() {
		return name;
	}
	
	public void render(SpriteBatch batch, OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render();
		batch.begin();
		player.render(batch);
		batch.end();
	}
	
	public void update(SpriteBatch batch, float deltaTime) {
		box2DWorld.step(deltaTime, 10, 10);
	}
	
	public abstract boolean shouldTransit(String nextSceneName);
	
	public abstract void transitFromScene(Scene previousScene);
	
	public void dispose() {
		box2DWorld.dispose();
		map.dispose();
	}
	
}