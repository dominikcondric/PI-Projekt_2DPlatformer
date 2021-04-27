package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import entities.Entity;
import entities.Player;
import entities.Projectile;

public class TestScene extends Scene {
	
	public TestScene(TiledMap map, final SpriteBatch batch, float mapTileSize) {
		super(map, batch, mapTileSize);
		
		camera.setToOrtho(false, Gdx.graphics.getWidth() / 20.f, Gdx.graphics.getHeight() / 20.f);
		camera.update();
		mapRenderer.setView(camera);
		
		Player player = new Player(box2DWorld);
		addEntity(player, true);
		
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
	public void render(SpriteBatch batch) {
		super.render(batch);
	}
	
	@Override
	public void update(SpriteBatch batch, float deltaTime) {
		super.update(batch, deltaTime);
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
		if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			Projectile projectile = new Projectile(box2DWorld, playable.getSprite().getX(), playable.getSprite().getY(), ((Player)playable).runningRight);
			addEntity(projectile, false);
		}
		
		camera.update();
		for (Entity entity : entities) 
			entity.update(deltaTime);
	}
}