package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import tools.CollisionListener;

public class EnemyFireball extends Entity {
	private boolean firedRight;
	private Animation<TextureRegion> flying;
	private Animation<TextureRegion> explosionAnim;
	private float stateTimer;
	private TextureAtlas atlas;
	private Fixture explosionSensor;
	private Fixture fireballBody;
	private FixtureDef fdef;
	private float hitDmg;
	private float explosionDmg;
	private boolean setToExplode = false;
	private float animLenght = 1.1f;
	private boolean exploded = false;
	private enum State { FLYING, EXPLODING};
	private State currentState;
	private State previousState;
	Sound explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/fireball.wav"));
	private float xSpeed;
	private float ySpeed;
	private float imageDirection=0;
	
	public EnemyFireball(Vector2 entityPosition, boolean firedRight, float hitDmg, float explosionDmg, float xSpeed, float ySpeed ) {
		super(entityPosition);
		
		setAnimations();
		this.firedRight = firedRight;
		this.hitDmg = hitDmg;
		this.explosionDmg = explosionDmg;
		this.xSpeed=xSpeed;
		this.ySpeed=ySpeed;
		
		//image direction N=1, NE=2, E=3, SE=4, S=5, SW=6, W=7, NW= 8
		
		if (firedRight) {
			sprite.setX(entityPosition.x + 1f);
		} else {
			sprite.flip(true, false);
			sprite.setX(entityPosition.x - 1f);
		}
		
		sprite.setY(entityPosition.y + 0.07f);
		sprite.setSize(1f, 1f);
		sprite.setOriginCenter();
		
		if (firedRight) {
			if(imageDirection == 2)sprite.setRotation(45);
			else if(imageDirection == 4)sprite.setRotation(-45);
		} else {
			if(imageDirection == 1)sprite.setRotation(90);
			else if(imageDirection == 5)sprite.setRotation(-90);
			else if(imageDirection == 6)sprite.setRotation(-45);
			else if(imageDirection == 8)sprite.setRotation(45);
		}
	}

	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX(), sprite.getY());
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f , sprite.getHeight() / 2.f );
		
		fdef = new FixtureDef();
		fdef.shape = polShape;
		fdef.filter.categoryBits = CollisionListener.PROJECTILE_BIT | CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.PLATFORM_BIT;
		fdef.isSensor=true;
	
		fireballBody = this.body.createFixture(fdef);
		fireballBody.setUserData(this);
		
		body.setGravityScale(0);
		body.setBullet(true);
	
		/*if (firedRight)
			body.setLinearVelocity(new Vector2(10f, 0f));
		else
			body.setLinearVelocity(new Vector2(-10f, 0f));*/
		
		body.setLinearVelocity(new Vector2(xSpeed,ySpeed));
		
		
		polShape.dispose();
	}
	
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		sprite.setRegion(getFrame(deltaTime));
		
		/*if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,0));
		}*/
		
		if(setToExplode) {
			body.setLinearVelocity(0, 0);
			body.destroyFixture(fireballBody);
			PolygonShape polShape = new PolygonShape();
			polShape.setAsBox(5, 2.5f);
			fdef.shape = polShape;
			fdef.isSensor = true;
			explosionSensor = this.body.createFixture(fdef);
			explosionSensor.setUserData(this);
			exploded = true;
			setToExplode = false;
			explosionSound.play(0.5f);
			body.destroyFixture(explosionSensor);
		}
		if(exploded) {
			animLenght -= deltaTime;
			if(animLenght <= 0) {
				setToDestroy = true;
			}
		}
	}
    
    private void onHit() {	
    	setToExplode = true;
    }
    
    public TextureRegion getFrame(float deltaTime){
    	previousState = currentState;
    	TextureRegion region = null;
    	currentState = getState();
    	switch(currentState) {
    		case FLYING:
    			sprite.setSize(1, 1);
    	        region = (TextureRegion) flying.getKeyFrame(stateTimer, true);
    	        if((body.getLinearVelocity().x < 0 || !firedRight) && !region.isFlipX()){
    	            region.flip(true, false);
    	            firedRight = false;
    	        }
    	
    	        else if((body.getLinearVelocity().x > 0 || firedRight) && region.isFlipX()){
    	            region.flip(true, false);
    	            firedRight = true;
    	        }
    	        break;
    		case EXPLODING:
    			sprite.setSize(10f, 5f);
        		region = (TextureRegion) explosionAnim.getKeyFrame(stateTimer, false);
        		break;
    	}
    	stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        return region;
    }
    
    public State getState() {
    	if(exploded) {
    		return State.EXPLODING;
    	}else {
    		return State.FLYING;
    	}
    	
    }

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if (!other.isSensor() && (other.getFilterData().categoryBits & CollisionListener.ENEMY_BIT) == 0 ) {
			onHit();
		}
	}
	public boolean isSetToExplode() {
		return setToExplode;
	}
	
	public float getHitDmg() {
		return hitDmg;
	}

	public float getExplosionDmg() {
		return explosionDmg;
	}
	
	private void setAnimations() {
		atlas = new TextureAtlas(Gdx.files.internal("projectiles\\enemy_fireball.atlas"));
		TextureRegion projectileImage = new TextureRegion(atlas.findRegion("FB500-1"),0 , 0, 304, 304);
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for(int i = 0; i < 5; i++) {
			frames.add(new TextureRegion(atlas.findRegion("FB500-1"),i * 306 , 0, 304, 304));
		}
		
		flying = new Animation<TextureRegion>(0.1f, frames);
		
		frames.clear();
		
		atlas = new TextureAtlas(Gdx.files.internal("projectiles\\fireball_explosion.atlas"));
		for(int i = 0; i < 11; i++) {
			frames.add(new TextureRegion(atlas.findRegion("tile003"), i * 66, 0, 64, 64));
		}
		explosionAnim = new Animation<TextureRegion>(0.1f, frames);
		
		sprite.setRegion(projectileImage);
	}
}
