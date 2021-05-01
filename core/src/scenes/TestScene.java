package scenes;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import entities.Projectile;
import entities.Enemies;
import entities.Entity;


public class TestScene extends Scene {
	private ArrayList<Projectile> projectiles;
	private ArrayList<Enemies> enemies;
	
	public TestScene(TiledMap map, final SpriteBatch batch, float mapTileSize, final String sceneName) {
		super(map, batch, mapTileSize, sceneName);
		
		projectiles = new ArrayList<Projectile>();
		enemies = new ArrayList<Enemies>(2);
		
		Enemies enemy= new Enemies();
		addEntity(enemy);
		
		BodyDef bodyDefinition = new BodyDef();
		Body body = null;
		PolygonShape polyShape = new PolygonShape();
		float scalingFactor = 1 / mapTileSize;
		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
			bodyDefinition.type = BodyDef.BodyType.StaticBody;
			bodyDefinition.position.set((rectangle.getX() + rectangle.getWidth() / 2.f) * scalingFactor, (rectangle.getY() + rectangle.getHeight() / 2.f)  * scalingFactor);
			body = box2DWorld.createBody(bodyDefinition);
			polyShape.setAsBox(rectangle.getWidth() / 2.f  * scalingFactor, rectangle.getHeight() / 2.f * scalingFactor);
			
			FixtureDef fdef = new FixtureDef();
			fdef.shape = polyShape;
			fdef.friction = 0;
			
			body.createFixture(fdef);
		}
		
		polyShape.dispose();
	}
	
	@Override 
	public void addEntity(Entity entity) {
		super.addEntity(entity);
		if (entity instanceof Enemies) {
			enemies.add((Enemies)entity);
		}
	}
	
	@Override
	public void render(SpriteBatch batch, OrthographicCamera camera) {
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		batch.begin();
		player.render(batch);
		for (Projectile p : projectiles) {
			p.render(batch);
		}
		
		for (Enemies enemy : enemies) {
			enemy.render(batch);
		}
		
		batch.end();
	}
	
	@Override
	public boolean shouldTransit(String sceneName) {
		switch (sceneName) {
			case "Desert":
				if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
					return true;
				}
				break;
		}
		
		return false;
	}
	
	@Override
	public void transitFromScene(Scene previousScene) {
		// TODO Auto-generated method stub
		switch (previousScene.getName()) {
			case "Cave":
				this.player = previousScene.player;
				player.getSprite().setPosition(2.f, 30.f);
				previousScene.box2DWorld.destroyBody(player.getBody());
				player.addToWorld(box2DWorld);
				previousScene.player = null;
				break;
		}
	}
		
	
	@Override
	public void update(SpriteBatch batch, float deltaTime) {
		super.update(batch, deltaTime);
		
		if (player.isSetToDestroy()) {
			player = null;
		} else {
			player.update(deltaTime);
		}
		
		for (int i = projectiles.size() - 1; i >= 0; --i) {
			if (projectiles.get(i).isSetToDestroy()) {
				toDestroy.add(i);
			} else { 
				projectiles.get(i).update(deltaTime);
			}
		}
		
		for (Integer i : toDestroy) {
			box2DWorld.destroyBody(projectiles.get(i.intValue()).getBody());
			projectiles.remove(i.intValue());
		}
		
		toDestroy.clear();
		
		for (int i = enemies.size() - 1; i >= 0; --i) {
			if (enemies.get(i).isSetToDestroy()) {
				toDestroy.add(i);
			} else { 
				enemies.get(i).update(deltaTime);
			}
		}
		
		for (Integer i : toDestroy) {
			box2DWorld.destroyBody(enemies.get(i.intValue()).getBody());
			enemies.remove(i.intValue());
		}
		
		toDestroy.clear();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			Projectile p = new Projectile(player.getSprite().getX(), player.getSprite().getY(), player.runningRight);
			p.addToWorld(box2DWorld);
			projectiles.add(p);
		}
	}
}