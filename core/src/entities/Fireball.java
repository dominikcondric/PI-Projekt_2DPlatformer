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

public class Fireball extends Entity {
	private boolean firedRight;
	private Animation<TextureRegion> flying;
	private Animation<TextureRegion> explosionAnim;
	private float stateTimer;
	private TextureAtlas atlas;
	private Fixture explosion;
	private Fixture fireballBody;
	private FixtureDef fdef;
	private float hitDmg;
	private float explosionDmg;
	private boolean didExplode = false;
	private float animLenght;
	private boolean exploded = false;
	private enum State { FLYING, EXPLODING};
	private State currentState;
	private State previousState;
	
	public Fireball(Vector2 entityPosition, boolean firedRight, float hitDmg, float explosionDmg) {
		super(entityPosition);
		animLenght = 1.1f;
		this.firedRight = firedRight;
		this.hitDmg = hitDmg;
		this.explosionDmg = explosionDmg;
		
		atlas = new TextureAtlas(Gdx.files.internal("projectiles\\fireball.atlas"));
		TextureRegion projectileImage = new TextureRegion(atlas.findRegion("FB001"),0 , 0, 35, 17);
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for(int i = 0; i < 5; i++) {
			frames.add(new TextureRegion(atlas.findRegion("FB001"),i * 37 , 0, 35, 17));
		}
		
		flying = new Animation<TextureRegion>(0.1f, frames);
		
		frames.clear();
		
		atlas = new TextureAtlas(Gdx.files.internal("projectiles\\fireball_explosion.atlas"));
		for(int i = 0; i < 11; i++) {
			frames.add(new TextureRegion(atlas.findRegion("tile003"), i * 66, 0, 64, 64));
		}
		explosionAnim = new Animation<TextureRegion>(0.1f, frames);
		
		sprite.setRegion(projectileImage);
		
		if (firedRight) {
			sprite.setX(entityPosition.x + 1f);
		} else {
			sprite.flip(true, false);
			sprite.setX(entityPosition.x - 1f);
		}
		
		sprite.setY(entityPosition.y + 0.07f);
		sprite.setSize(0.8f, 0.4f);
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
	
		fireballBody = this.body.createFixture(fdef);
		fireballBody.setUserData(this);
		
		body.setGravityScale(0);
		
		if (firedRight)
			body.setLinearVelocity(new Vector2(10f, 0f));
		else
			body.setLinearVelocity(new Vector2(-10f, 0f));
		
		polShape.dispose();
	}
	
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		sprite.setRegion(getFrame(deltaTime));
		
		if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,0));
		}
		
		if(didExplode) {
			body.destroyFixture(fireballBody);
			PolygonShape polShape = new PolygonShape();
			polShape.setAsBox(sprite.getWidth() /2.5f , sprite.getHeight() /2.5f);
			fdef.shape = polShape;
			fdef.isSensor = true;
			explosion = this.body.createFixture(fdef);
			explosion.setUserData(this);
			exploded = true;
			didExplode = false;
		}
		if(exploded) {
			
			animLenght -= deltaTime;
			if(animLenght <= 0) {
				setToDestroy = true;
			}
		}
	}
    
    private void onHit() {	
    	didExplode = true;
    }
    
    public TextureRegion getFrame(float deltaTime){
    	previousState = currentState;
    	TextureRegion region = null;
    	currentState = getState();
    	switch(currentState) {
    		case FLYING:
    			sprite.setSize(0.8f, 0.4f);
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
	public void resolveCollision(Fixture self, Fixture other) {
		if (!other.isSensor()) {
			
			onHit();
		}
	}
	public boolean didExplode() {
		return didExplode;
	}
	
	public float getHitDmg() {
		return hitDmg;
	}

	public float getExplosionDmg() {
		return explosionDmg;
	}
	
}
