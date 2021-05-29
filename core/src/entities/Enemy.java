package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import scenes.Scene;

public class Enemy extends Entity {
	private float stateTimer;
	private TextureRegion slimeIdle;
	private Animation<TextureRegion> slimeIdleAnim;
	protected int hp = 3;
	protected float visionHeight=3f;
	protected float visionLength=4f;
	protected boolean active = false;
	private Array<TextureRegion> idleFrames = new Array<TextureRegion>();
	private TextureAtlas atlas;

	public Enemy(Vector2 position) {
		super(position);
		atlas = new TextureAtlas(Gdx.files.internal("slimesprites\\idle_slime.atlas"));
		slimeIdle = new TextureRegion(atlas.findRegion("idle_slime01"), 0, 0, 19, 18);
		for(int i = 0; i < 6; i++) {
			idleFrames.add(new TextureRegion(atlas.findRegion("idle_slime01"), i * 23+5 , 0, 19, 18 ));
		}
		slimeIdleAnim = new Animation<TextureRegion>(0.1f, idleFrames);
		sprite.setRegion(slimeIdle);
		sprite.setSize(0.9f, 0.9f);
		sprite.setScale(2f, 2f);
	}
	
	@Override
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		
		
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		//bodyDefinition.type = BodyDef.BodyType.StaticBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f, sprite.getHeight() / 2.f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;

		this.body.createFixture(fdef).setUserData(this);
		
		/*EdgeShape vision = new EdgeShape();
		vision.set(new Vector2(-2,-2), new Vector2(2,2));*/
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength, visionHeight, new Vector2(0,visionHeight-(sprite.getHeight()/2)), 0);
		
		fdef.shape = vision;
		fdef.isSensor=true;
		this.body.createFixture(fdef).setUserData(this);
		
		
		polShape.dispose();
	}
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		TextureRegion currentRegion = getFrame(deltaTime);
		sprite.setRegion(currentRegion);
		if (body.getPosition().y < 0.f) {
			setToDestroy = true;
		}
		
		if (this.active) {
			this.move(this.getDirection(scene.getPlayer()));
		}
		
        if (body.getLinearVelocity().y < 0)  {
			body.setLinearDamping(0);
        } else {
            body.setLinearDamping(12);
        }
	}
	


	public int getDirection(Player player) {
		//enemy manji x dakle true onda se mice enemy u desno
		//inace se mice u lijevo
		int dir = 0;
		if (this.sprite.getX()<player.sprite.getX()) {
			dir++;
		} else {
			dir--;
		}
		
		if (this.sprite.getY()<player.sprite.getY())
			dir	+= 3;
		
		return dir;
	}

	public void move(int direction) {
		if (direction == -1 || direction == 2) {
			moveLeft();
		} else if (direction == 1 || direction == 4){
			moveRight();
		}
		
		if(direction >= 2 && this.body.getLinearVelocity().y == 0) {
			jump();
		}
	}

	public void jump() {
		body.applyLinearImpulse(new Vector2(0, 11f), body.getWorldCenter(), true);
	}

	public void moveLeft() {
    	body.applyLinearImpulse(new Vector2(-0.3f, 0.f), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
	}

	public void moveRight() {
		body.applyLinearImpulse(new Vector2(0.3f, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
	}

	private void onHit(boolean pushRight) {
		float xPush = 10f;
		if (!pushRight) 
			xPush *= -1.f;
		
		body.applyLinearImpulse(new Vector2(xPush, 0.f), body.getWorldCenter(), true);
		this.hp--;
		if (this.hp <= 0)
			setToDestroy = true;
	}

	private void activate() {
		this.active = true;
	}

	private void stop() {
		this.active = false;
		
	}
	
	public TextureRegion getFrame(float deltaTime) {
		TextureRegion region = (TextureRegion) slimeIdleAnim.getKeyFrame(stateTimer, true);
        
        if (body.getLinearVelocity().x < 0 && region.isFlipX()) {
            region.flip(true, false);
        } else if (body.getLinearVelocity().x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }

        stateTimer = stateTimer + deltaTime;
        return region;
    }

	@Override
	public void resolveCollision(Fixture self, Fixture other) {
		//kontakt playera i enemy visiona
		//posto ne postoji senzor s player objektom u userdata to znaci da enemy u ovom slucaju mora biti senzor a player mora biti sam hitbox playera
		if (other.getUserData() instanceof Player && self.isSensor()) {
			activate();
			if (((Player)other.getUserData()).getHp() <= 0)
				stop();
		} else if (!self.isSensor() && other.getUserData() instanceof Player && ((Player)other.getUserData()).hasAttacked()) {
			onHit(((Player)other.getUserData()).facingRight);
		} else if (!self.isSensor() && other.getUserData() instanceof Fireball) {
			onHit(((Fireball)other.getUserData()).facingRight);
		}
	}
}