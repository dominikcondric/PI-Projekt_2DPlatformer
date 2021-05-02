package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import entities.Enemy;
import entities.Entity;
import entities.Player;
import tools.CollisionListener;

public class Scene {
	protected TiledMap map;
	protected World box2DWorld;
	protected OrthogonalTiledMapRenderer mapRenderer;
	protected ArrayList<Entity> entities;
	protected ArrayList<SceneTrigger> triggers;
	ArrayList<Integer> toDestroy = new ArrayList<Integer>();
	
	public Scene(final TiledMap map, final SpriteBatch batch, float mapTileSize) {
		this.map = map;
		mapRenderer = new OrthogonalTiledMapRenderer(this.map, 1 / mapTileSize, batch);
		box2DWorld = new World(new Vector2(0.f, -18.81f), true);
		box2DWorld.setContactListener(new CollisionListener());
		
		BodyDef bodyDef = null;
		PolygonShape shape = null;
		Body body = null;
		float scalingFactor = 1 / mapTileSize;
		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			bodyDef.position.set(rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			body = box2DWorld.createBody(bodyDef);
			shape = new PolygonShape();
			shape.setAsBox(rect.getWidth() / 2f, rect.getHeight() / 2f);
			body.createFixture(shape, 0.f);
			shape.dispose();
		}
		
		entities = new ArrayList<Entity>(5);
		triggers = new ArrayList<SceneTrigger>(2);
	}
	
	public World getWorld() {
		return box2DWorld;
	}
	
	public ArrayList<SceneTrigger> getTriggers() {
		return triggers;
	}
	
	public void addTrigger(SceneTrigger trigger) {
		triggers.add(trigger);
	}
	
	public void addEntity(Entity entity) {
		entities.add(entity);
		entity.addToWorld(box2DWorld);
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
	
	//dodano
	
	public void ClosestEnemy(Player player) {
		Enemy closestEnemy=null;
		float closestdist=10000000f;
		
		for(Entity enemy:entities) {
			if(!(enemy instanceof Enemy)) continue;
			/*float disttoplayer=(float) Math.sqrt(
				Math.pow(enemy.getSprite().getX()-player.getSprite().getX(),2) 
				+ Math.pow(enemy.getSprite().getY()-player.getSprite().getY(),2)
				);
			*/
			float disttoplayer=getDistance(enemy.getSprite().getX(), player.getSprite().getX(), enemy.getSprite().getY(), player.getSprite().getY());
			if(disttoplayer<closestdist) {
				closestdist=disttoplayer;
				closestEnemy=(Enemy) enemy;
			}
		}
		closestEnemy.activate();
	}
	
	public float getDistance(float x1, float y1, float x2, float y2) {
		float distance=(float) Math.sqrt(
				Math.pow(x1-x2,2) + Math.pow(y1-y2,2)
				);
		return distance;
	}
	
	public Player getPlayer() {
		Player player;
		for(Entity entity : entities) {
			if(entity instanceof Player) return (Player) entity;
		}
		return null;
	}
	
	public void dispose() {
		box2DWorld.dispose();
		map.dispose();
	}




}