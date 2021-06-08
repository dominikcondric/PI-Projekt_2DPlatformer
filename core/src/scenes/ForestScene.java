package scenes;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

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
import entities.Chest;
import entities.FireballItem;
import entities.Key;
import entities.Player;
import entities.RangedGuard;
import entities.Slime;
import tools.CollisionListener;

public class ForestScene extends Scene {
	private ArrayList<PointLight> lights;
	
	public ForestScene(TmxMapLoader mapLoader, SpriteBatch batch) {	
		super(mapLoader, "Forest/forest.tmx", batch);
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/forest_music.mp3"));
	}

	@Override
	protected void constructTileMap() {
		BodyDef bodyDef = null;
		PolygonShape shape = null;
		FixtureDef fixtureDef = null;
		Body body = null;
		float scalingFactor = 1f / map.getProperties().get("tilewidth", Integer.class);
		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
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
			fixtureDef.friction = 1f;
			fixtureDef.filter.categoryBits = CollisionListener.SOLID_WALL_BIT;
			fixtureDef.filter.maskBits = 0xFF;

			body.createFixture(fixtureDef);
			shape.dispose();
		}
	}

	@Override
	public void constructEntities() {
		
		addEntity(new Slime(new Vector2(25.f, 31.f)));
		addEntity(new Slime(new Vector2(17.f, 37.f)));
		addEntity(new Slime(new Vector2(37.f, 31.f)));
		addEntity(new RangedGuard(new Vector2(53.f, 45.f)));
		Key key = new Key(new Vector2(85.1f, 32.f), "first");
		FireballItem fireballItem = new FireballItem(new Vector2(5.1f, 39.1f));
		addEntity(fireballItem);
		addEntity(key);
		addEntity(new Chest(new Vector2(85.f, 32.f), key));
		addEntity(new Chest(new Vector2(5f, 39f), fireballItem));
		

		lights = new ArrayList<PointLight>(4);
		addEntity(new Slime(new Vector2(34.f, 23.f)));
		//addEntity(new Enemy(new Vector2(39.f, 50.f)));
		float scalingFactor = 1f / map.getProperties().get("tilewidth", Integer.class);
		for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject)object).getRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			Color c = new Color(Color.GOLD);
			c.a *= 0.78;
			PointLight light = new PointLight(rayHandler, 150, c, 20, rect.getX() + rect.getWidth() / 2f, rect.getY() + rect.getHeight() / 2f);
			
			lights.add(light);
		}
	}

	@Override
	protected void placePlayerOnScene(Player player) {
		player.setPosition(new Vector2(3.f, 40.f));
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
}
