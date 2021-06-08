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

public class Orb extends Entity {
	private String orbType;
	public boolean pickedUp = false;
	private TiledMapTile tile;
	private Cell tileCell;
	private boolean pickupable = false;

	public Orb(Vector2 position, String orbType, Cell tileCell) {
		super(position);
		this.orbType = orbType;
		this.tileCell = tileCell;
		tile = tileCell.getTile();
	}

	@Override
	public void addToWorld(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(initialPosition.x + 0.5f, initialPosition.y + 0.5f);
		body = world.createBody(bodyDef);
		
		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(0.5f, 0.5f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polyShape;
		fdef.filter.categoryBits = CollisionListener.INTERACTABLE_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT;
		Fixture fixture = body.createFixture(fdef);
		
		fixture.setSensor(true);
		fixture.setUserData(this);
		
		polyShape.dispose();
	}
	
	

	@Override
	public void update(Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if (pickupable && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			active = false;
			tileCell.setTile(null);
		}
	}

	@Override
	public void reset(World world) {
		super.reset(world);
		tileCell.setTile(tile);
	}
	
	@Override
	public void render(SpriteBatch batch) {
	}

	@Override
	public void resolveCollisionEnd(Fixture A, Fixture B) {
		pickupable = false;
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		pickupable = true;
	}

	
}
