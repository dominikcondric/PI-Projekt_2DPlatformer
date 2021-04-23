package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import entities.Entity;
import entities.Player;

public class TestScene extends Scene {
	
	public TestScene(TiledMap map) {
		super(map);
//		camera.zoom = 0.4f;
		Player player = new Player(box2DWorld);
		addEntity(player);
		camera.update();
		
		BodyDef bodyDefinition = new BodyDef();
		Body body = null;
		PolygonShape polyShape = new PolygonShape();
		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
			bodyDefinition.type = BodyDef.BodyType.StaticBody;
			bodyDefinition.position.set(rectangle.getX() + rectangle.getWidth() / 2.f, rectangle.getY() + rectangle.getHeight() / 2.f);
			body = box2DWorld.createBody(bodyDefinition);
			polyShape.setAsBox(rectangle.getWidth() / 2.f, rectangle.getHeight() / 2.f);
			body.createFixture(polyShape, 0.f);
		}
	}
	
	@Override
	public void update(OrthogonalTiledMapRenderer mapRenderer, SpriteBatch batch, float deltaTime) {
		super.update(mapRenderer, batch, deltaTime);
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.position.add(-100.f * deltaTime, 0, 0);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.position.add(100.f * deltaTime, 0, 0);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.position.add(0.f, 100.f * deltaTime, 0.f);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camera.position.add(0.f, -100.f * deltaTime, 0);
		}
		
		camera.update();
		for (Entity entity : entities) 
			entity.update(deltaTime);
	}
}
