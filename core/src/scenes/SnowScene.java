package scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import entities.Chest;
import entities.Entity;
import entities.FireballItem;
import entities.Key;
import entities.Player;
import entities.RangedGuard;
import entities.Slime;

public class SnowScene extends Scene {

	public SnowScene(TmxMapLoader mapLoader, SpriteBatch batch) {
		super(mapLoader, "Snow/snow.tmx", batch);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void constructTileMap() {
		// TODO Auto-generated method stub
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
			fixtureDef.friction = 0.f;
			
			body.createFixture(fixtureDef);
			shape.dispose();
		}
		
		
		for (MapObject object : map.getLayers().get(3).getObjects().getByType(PolygonMapObject.class)) {
			Polygon poly = ((PolygonMapObject)object).getPolygon();
			Rectangle rect = poly.getBoundingRectangle();
			rect.set(rect.getX() * scalingFactor, rect.getY() * scalingFactor, rect.getWidth() * scalingFactor, rect.getHeight() * scalingFactor);
			bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			bodyDef.position.set(rect.getX(), rect.getY());
			
			for(int i = 0; i < poly.getVertices().length; i++) {
				poly.getVertices()[i] *= scalingFactor;
			}
			
			body = box2DWorld.createBody(bodyDef);
			shape = new PolygonShape();
			shape.set(poly.getVertices());
			fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.friction = 0.f;
			
			body.createFixture(fixtureDef);
			shape.dispose();
		}

	}

	@Override
	public void constructEntities() {
		addEntity(new Slime(new Vector2(13.f, 8.f)));
		addEntity(new Slime(new Vector2(68.f, 12.f)));
		addEntity(new Slime(new Vector2(112.f, 21.f)));
		addEntity(new RangedGuard(new Vector2(89.f, 33.f)));
		Key key = new Key(new Vector2(115.1f, 21.f), "first");
		FireballItem fireballItem = new FireballItem(new Vector2(75.1f, 32.1f));
		addEntity(fireballItem);
		addEntity(key);
		addEntity(new Chest(new Vector2(115.f, 21.f), key));
		addEntity(new Chest(new Vector2(75f, 32f), fireballItem));

	}

	@Override
	protected void placePlayerOnScene(Player player) {
		// TODO Auto-generated method stub
		player.setPosition(new Vector2(2, 40));
		
	}

}
