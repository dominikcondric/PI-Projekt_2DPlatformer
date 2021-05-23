package scenes;

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

import entities.Enemy;
import entities.Entity;

public class CaveScene extends Scene {

	public CaveScene(TmxMapLoader mapLoader, SpriteBatch batch) {
		super(mapLoader, "Cave/Maps/demo3.tmx", batch);
	}

	@Override
	public void constructTileMap() {
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
	}


	@Override
	public void constructEntities() {
		addEntity(new Enemy(new Vector2(82.f, 20.f)));
		addEntity(new Enemy(new Vector2(8.f, 15.f)));
	}

	@Override
	public void placePlayerOnScene(Entity player) {
		player.setPosition(new Vector2(2.f, 8.f));
	}
}
