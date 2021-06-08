package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;
import tools.CollisionListener;

public class Shield extends Entity {
	private Entity owner;
	private int hp = 3;

	public Shield(Vector2 position, Player owner) {
		super(position);
		this.owner = owner;
		sprite.setRegion(new Texture(Gdx.files.internal("bubble_3.png")));
		sprite.setSize(2.f, 2.f);
		sprite.setPosition(position.x, position.y);
	}

	@Override
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(initialPosition);
		bodyDefinition.type = BodyDef.BodyType.KinematicBody;
		body = world.createBody(bodyDefinition);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.filter.categoryBits = (short)CollisionListener.OTHERS_BIT;
		fixtureDef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT & ~CollisionListener.SOLID_WALL_BIT & ~CollisionListener.PLATFORM_BIT;
		CircleShape shape = new CircleShape();
		shape.setRadius(1.f);
		fixtureDef.shape = shape;
		body.createFixture(fixtureDef).setUserData(this);
	}

	@Override
	public void update(Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		body.setTransform(owner.getPosition(), 0.f);
	}
	
	private void onHit(float enemyX) {
		hp--;
		System.out.println(hp);
		if(hp <= 0)
			setToDestroy = true;
		if (enemyX == 0)
			return;
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if ((other.getFilterData().categoryBits & CollisionListener.ENEMY_BIT) != 0 && !other.isSensor()) {
			onHit(((Enemy) other.getUserData()).getPosition().x);
		}
	}
	
	@Override
	public void reset(World world) {
		hp = 3;
	}
}
