package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;
import tools.CollisionListener;


public class Arrow extends Entity{
	private boolean firedRight;
	private Fixture arrowBody;
	private FixtureDef fdef;
	private float hitDmg;
	
	protected Arrow(Vector2 entityPosition, boolean firedRight, float hitDmg) {
		super(entityPosition);
		this.hitDmg = hitDmg;
		this.firedRight = firedRight;
		Texture texture = new Texture(Gdx.files.internal("projectiles\\arrow.png"));
		sprite.setTexture(texture);
		
		if (firedRight) {
			sprite.setX(entityPosition.x + 1f);
		} else {
			sprite.flip(true, false);
			sprite.setX(entityPosition.x - 1f);
		}
		
		sprite.setY(entityPosition.y + 0.4f);
		sprite.setSize(0.8f, 0.1f);
	}

	@Override
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX(), sprite.getY());
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f , sprite.getHeight() / 2.f );
		body.setGravityScale(0);
		fdef = new FixtureDef();
		fdef.shape = polShape;
		fdef.filter.categoryBits = CollisionListener.PROJECTILE_BIT | CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT | CollisionListener.SOLID_WALL_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
	
		arrowBody = this.body.createFixture(fdef);
		arrowBody.setUserData(this);
		
		if (firedRight)
			body.setLinearVelocity(new Vector2(10f, 0f));
		else
			body.setLinearVelocity(new Vector2(-10f, 0f));
		
		polShape.dispose();
		
	}
	
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,0));
		}
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if (!other.isSensor()) {
			onHit();
		}
	}
	
	private void onHit() {	
    	setToDestroy = true;
    }

	public float getHitDmg() {
		return hitDmg;
	}
	
	public void reset(World world) {
		setToDestroy = true;
	}
}
