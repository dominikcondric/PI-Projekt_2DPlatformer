package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;
import tools.CollisionListener;

public class Gate extends Entity {
	private String keyCode;
	private Vector2 size;
	private boolean openable = false;

	public Gate(Vector2 position, Vector2 size, Cell tileCell, String keyCode) {
		super(position);
		this.size = size;
		this.keyCode = keyCode;
	}

	@Override
	public void addToWorld(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(initialPosition.x + size.x / 2.f, initialPosition.y + size.y / 2.f);
		body = world.createBody(bodyDef);
		
		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(size.x / 2.f, size.y / 2.f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polyShape;
		fdef.filter.categoryBits = CollisionListener.SOLID_WALL_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT | CollisionListener.PROJECTILE_BIT | CollisionListener.ENEMY_BIT;
		Fixture fixture = body.createFixture(fdef);
		fixture.setUserData(this);
		
		polyShape.setAsBox(size.x, size.y / 2.f);
		fdef.shape = polyShape;
		fdef.filter.categoryBits = CollisionListener.INTERACTABLE_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT;
		fdef.isSensor = true;
		fixture = body.createFixture(fdef);
		fixture.setUserData(this);
		
		polyShape.dispose();
	}
	
	public void open(String key) {
		if (key.equals(keyCode)) {
			active = false;
		}
	}
	
	@Override
	public void update(Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if (openable && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			for (Item item : scene.getPlayer().getItems()) {
				if (item instanceof Key) {
					open(((Key)item).getKeyID());
				}
			}
		}
	}

	@Override
	public void render(SpriteBatch batch) {
	}
	
	@Override
	public void resolveCollisionEnd(Fixture A, Fixture B) {
		openable = false;
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		openable = true;
	}
}
