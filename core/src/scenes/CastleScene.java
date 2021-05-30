package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import box2dLight.PointLight;
import entities.Enemy;
import entities.Entity;
import entities.Player;

public class CastleScene extends Scene {
	private ArrayList<PointLight> lights;
	Music music = Gdx.audio.newMusic(Gdx.files.internal("sounds/castle_music.mp3"));
	public CastleScene(TmxMapLoader mapLoader, SpriteBatch batch) {
		super(mapLoader, "Castle/castle_map.tmx", batch);
	}

	@Override
	protected void constructTileMap() {
		BodyDef bodyDef = null;
		PolygonShape shape = null;
		FixtureDef fixtureDef = null;
		Body body = null;
		float scalingFactor = 1f / map.getProperties().get("tilewidth", Integer.class);
		for (MapObject object : map.getLayers().get(0).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			bodyDef.position.set(rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			body = box2DWorld.createBody(bodyDef);
			shape = new PolygonShape();
			shape.setAsBox(rect.getWidth() / 2f, rect.getHeight() / 2f);
			fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.friction = 1.f;
			
			body.createFixture(fixtureDef);
			shape.dispose();
		}
	}

	@Override
	public void constructEntities() {
		lights = new ArrayList<PointLight>(4);
		//addEntity(new Enemy(new Vector2(97.f, 57.f)));
		//addEntity(new Enemy(new Vector2(39.f, 50.f)));
		//TORCH
		float scalingFactor = 1f / map.getProperties().get("tilewidth", Integer.class);
		for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.GOLD);
			c.a *= 0.60;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			lights.add(light);
		}
		//WINDOWS
		for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.PURPLE);
			c.a *= 0.3;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			lights.add(light);
		}
		//BIG WINDOWS
		for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.PURPLE);
			c.a *= 0.70;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			lights.add(light);
		}
		//CHANDELIER
		for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.GOLD);
			c.a *= 0.50;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			lights.add(light);
		}
		//SKY WEAK
		for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.WHITE);
			c.a *= 0.3;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			lights.add(light);
		}
		//SKY STRONG
		for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.WHITE);
			c.a *= 0.6;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			lights.add(light);
		}
	}

	@Override
	protected void placePlayerOnScene(Player player) {
		player.setPosition(new Vector2(2.f, 10.f));
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		music.setVolume(0.1f);
		music.play();
		music.setLooping(true);
		
	}


}
