package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;

public class Coin extends Entity {
	private Cell coinCell;
	private TiledMapTile tile;
	private Sound sound;
	private boolean justPickedUp = false;

	public Coin(Vector2 position, Cell coinCell) {
		super(position);
		this.coinCell = coinCell;
		this.tile = coinCell.getTile();
		sound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin.wav"));
	}
	
	public void addToWorld(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(initialPosition.x + 0.5f, initialPosition.y + 0.5f);
		body = world.createBody(bodyDef);
		
		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(0.5f, 0.5f);
		
		Fixture fixture = body.createFixture(polyShape, 0.f);
		fixture.setSensor(true);
		fixture.setUserData(this);
		
		polyShape.dispose();
	}
	
	@Override
	public void render(SpriteBatch batch) {
	}

	@Override
	public void update(Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if (justPickedUp == true) {
			coinCell.setTile(null);
			sound.play();
			active = false;
		}
		justPickedUp = false;
	}
	
	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if (other.getUserData() instanceof Player) {
			justPickedUp = true;
			active = false;
		}
	}
	
	@Override 
	public void reset(World world) {
		super.reset(world);
		coinCell.setTile(tile);
	}
}
