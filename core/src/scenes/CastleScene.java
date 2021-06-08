package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import box2dLight.PointLight;
import entities.Chest;
import entities.Coin;
import entities.FireballItem;
import entities.FirstBoss;
import entities.MeleeGuard;
import entities.Player;
import entities.RangedGuard;
import entities.Slime;
import entities.Sorcerer;
import tools.CollisionListener;

public class CastleScene extends Scene {
	private ArrayList<PointLight> lights;
	public CastleScene(TmxMapLoader mapLoader, SpriteBatch batch) {
		super(mapLoader, "Castle/castle_map.tmx", batch);
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/castle_music.mp3"));
	}

	@Override
	protected void constructTileMap() {
		BodyDef bodyDef = null;
		PolygonShape shape = null;
		FixtureDef fixtureDef = null;
		Body body = null;
		float scalingFactor = 1f / map.getProperties().get("tilewidth", Integer.class);
		for (MapObject object : map.getLayers().get("Wall Object").getObjects().getByType(RectangleMapObject.class)) {
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
			fixtureDef.filter.categoryBits = CollisionListener.SOLID_WALL_BIT;
			fixtureDef.filter.maskBits = 0xFF & ~CollisionListener.PROJECTILE_BIT;

			body.createFixture(fixtureDef);
			shape.dispose();
		}
		for (MapObject object : map.getLayers().get("Platform Object").getObjects().getByType(RectangleMapObject.class)) {
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
			fixtureDef.filter.categoryBits = CollisionListener.PLATFORM_BIT;
			fixtureDef.filter.maskBits = 0xFF & ~CollisionListener.PROJECTILE_BIT;

			body.createFixture(fixtureDef);
			shape.dispose();
		}
		
	}

	@Override
	public void constructEntities() {
		lights = new ArrayList<PointLight>(4);

		addEntity(new RangedGuard(new Vector2(82.f, 13.f)));
        addEntity(new RangedGuard(new Vector2(68.f, 13.f)));
        addEntity(new RangedGuard(new Vector2(110.f, 13.f)));
        addEntity(new MeleeGuard(new Vector2(110.f, 13.f)));
        addEntity(new RangedGuard(new Vector2(135.f, 13.f)));
        addEntity(new RangedGuard(new Vector2(160f, 13.f)));
        addEntity(new RangedGuard(new Vector2(192f, 13.f)));
        FireballItem fireballItem = new FireballItem(new Vector2(5.1f, 8.1f));
        addEntity(fireballItem);
        addEntity(new FirstBoss(new Vector2(80f, 15.f)));


       addEntity(new Chest(new Vector2(5f, 8f), fireballItem));
       addEntity(new MeleeGuard(new Vector2(68.f, 13.f)));
		
		float scalingFactor = 1f / map.getProperties().get("tilewidth", Integer.class);
		for(MapObject object : map.getLayers().get("Lights Torch").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.GOLD);
			c.a *= 0.50f;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			light.setStaticLight(true);
			light.setSoft(false);
			
			lights.add(light);
		}
		//WINDOWS
		for(MapObject object : map.getLayers().get("Lights Windows").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.PURPLE);
			c.a *= 0.3f;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			light.setStaticLight(true);
			light.setSoft(false);
			
			lights.add(light);
		}
		//BIG WINDOWS
		for(MapObject object : map.getLayers().get("Lights Windows Big").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.PURPLE);
			c.a *= 0.70f;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			light.setSoft(false);
			light.setStaticLight(true);
			
			lights.add(light);
		}
		//CHANDELIER
		for(MapObject object : map.getLayers().get("Lights Chandelier").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.GOLD);
			c.a *= 0.40f;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			light.setSoft(false);
			light.setStaticLight(true);
			
			lights.add(light);
		}
		//SKY WEAK
		for(MapObject object : map.getLayers().get("Lights Sky Weak").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.WHITE);
			c.a *= 0.3f;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			light.setSoft(false);
			light.setStaticLight(true);
			
			lights.add(light);
		}
		//SKY STRONG
		for(MapObject object : map.getLayers().get("Lights Sky Strong").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.WHITE);
			c.a *= 0.6f;
			PointLight light = new PointLight(rayHandler, 50, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			light.setSoft(false);
			light.setStaticLight(true);
			
			lights.add(light);
		}
		
		// COINS
		TiledMapTileLayer coinLayer = (TiledMapTileLayer)map.getLayers().get("Coin Layer");
		for (RectangleMapObject object : map.getLayers().get("Coin Object").getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = object.getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Coin coin = new Coin(rect.getPosition(new Vector2()), coinLayer.getCell((int)rect.getX(), (int)rect.getY()));
			addEntity(coin);
		}
	}

	@Override
	protected void placePlayerOnScene(Player player) {
		player.setPosition(new Vector2(2.f, 9.f));
	}
}
