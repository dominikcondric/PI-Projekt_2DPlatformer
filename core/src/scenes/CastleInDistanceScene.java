package scenes;

import com.badlogic.gdx.Gdx;
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

import entities.Player;
import sceneAnimations.IntroAnimation;
import tools.CollisionListener;

public class CastleInDistanceScene extends Scene {

	public CastleInDistanceScene(TmxMapLoader mapLoader, SpriteBatch batch) {
		super(mapLoader, "CastleInDistance/CastleInDistance.tmx", batch);
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/intro_music.mp3"));
		visibleMapScale = 1.8f;
		batchTintColor.set(0.5f, 0.5f, 0.5f, 1.f);
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
	}

	@Override
	protected void placePlayerOnScene(Player player) {
		player.setPosition(new Vector2(2.f, 1.7f));
		runningAnimation = new IntroAnimation(player);
	}

}
