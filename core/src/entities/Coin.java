package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Coin {
	private Cell coinCell;
	private boolean setToDestroy = false;
	private TiledMapTile tile;
	private Body body;
	private Vector2 position;
	private Sound sound;

	public Coin(Vector2 position, Cell coinCell) {
		this.coinCell = coinCell;
		this.tile = coinCell.getTile();
		this.position = position;
		sound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin.wav"));
	}
	
	public void addToWorld(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(position.x + 0.5f, position.y + 0.5f);
		body = world.createBody(bodyDef);
		
		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(0.5f, 0.5f);
		
		Fixture fixture = body.createFixture(polyShape, 0.f);
		fixture.setSensor(true);
		fixture.setUserData(this);
		
		polyShape.dispose();
	}

	public void setToDestroy(boolean destroy) {
		setToDestroy = destroy;
	}
	
	public boolean isSetToDestroy() {
		return setToDestroy;
	}
	
	public void update() {
		if (setToDestroy && body != null) {
			coinCell.setTile(null);
			body.getWorld().destroyBody(body);
			body = null;
			sound.play();
		}
	}
	
}
