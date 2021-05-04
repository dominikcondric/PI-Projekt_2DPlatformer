package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import scenes.Scene;

public class Player extends Entity {
	public int hp = 4; 
	
	@SuppressWarnings({ "unused", "rawtypes" })
	private Animation playerStandAnim;
	@SuppressWarnings("rawtypes")
	private Animation playerRunAnim;
	private TextureRegion playerStand;
	private TextureRegion playerFall;
	private TextureRegion playerJump;
	
	private final int MOVE_THRESHOLD_LEFT = -6;
	private final int MOVE_THRESHOLD_RIGHT = 6;
	private final int ON_GROUND = 0;
	
	public enum State { FALLING, JUMPING, STANDING, RUNNING, DEAD };
	public State currentState;
    public State previousState;
    
	public boolean runningRight = true;
	private float stateTimer;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Player(Vector2 position) {
		super(position);
		atlas = new TextureAtlas(Gdx.files.internal("aerosprites\\aero_v3.atlas"));
		playerStand = new TextureRegion(atlas.findRegion("aero3"), 0, 0, 16, 20);
		playerJump = new TextureRegion(atlas.findRegion("jumping"), 42, 0, 18, 20);
		playerFall = new TextureRegion(atlas.findRegion("jumping"), 84, 0, 18, 20);
		
		Array<TextureRegion> framesStand = new Array<TextureRegion>();
		Array<TextureRegion> framesRun = new Array<TextureRegion>();
		
		int xCoordinate = 0;
		framesStand.add(new TextureRegion(atlas.findRegion("aero3"), xCoordinate, 0, 19, 20 ));
		xCoordinate += 21;
		for(int i = 0; i < 3; i++) {
	        framesStand.add(new TextureRegion(atlas.findRegion("aero3"), xCoordinate, 0, 19, 21 ));
	        xCoordinate += 21;
		}
		for(int i = 0; i < 9; i++) {
	        framesStand.add(new TextureRegion(atlas.findRegion("aero3"), xCoordinate, 0, 19, 20 ));
	        xCoordinate += 21;
		}

	    playerStandAnim = new Animation(0.1f, framesStand);
	    
		for(int i = 0; i < 8; i++) {
	        framesRun.add(new TextureRegion(atlas.findRegion("aero3"), xCoordinate, 0, 19, 20));
	        xCoordinate += 21;
		}
	    playerRunAnim = new Animation(0.1f, framesRun);
		
	    sprite.setRegion(playerStand);
		sprite.setSize(0.9f, 1.125f);
	}
	
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f, sprite.getHeight() / 2.f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;

		this.body.createFixture(fdef).setUserData(this);
		polShape.dispose();
	}
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		Vector2 playerVelocity = body.getLinearVelocity();
		
		TextureRegion currentRegion = getFrame(deltaTime);
		
		sprite.setRegion(currentRegion);
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && playerVelocity.y == ON_GROUND) {
			jump();
		}	

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerVelocity.x <= MOVE_THRESHOLD_RIGHT) {
        	moveRight();
        }	
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && playerVelocity.x >= MOVE_THRESHOLD_LEFT) {
        	moveLeft();
        }
        
        if(playerVelocity.y != ON_GROUND) {
        	body.setLinearDamping(0);
        }else {
        	body.setLinearDamping(12);
        }

        
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
        	scene.addEntity(new Projectile(sprite.getX(), sprite.getY(), runningRight));
        }
        
        if (body.getPosition().y < 0.f) {
        	setToDestroy = true;
        	body.setTransform(2.f, 8.f, 0.f);
        }
	}
	
	public TextureRegion getFrame(float deltaTime){

        currentState = getState();
        TextureRegion region;

        switch(currentState){
        	case JUMPING:
        		region = playerJump;
        		break;
        	case FALLING:
        		region = playerFall;
        		break;
            case RUNNING:
                region = (TextureRegion) playerRunAnim.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            	region = (TextureRegion) playerStandAnim.getKeyFrame(stateTimer, true);
            	break;
            default:
                region = playerStand;
                break;
        }
        
        if((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        else if((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;

    }
	
	public State getState(){
		
        if((body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(body.getLinearVelocity().x < -0.3f || body.getLinearVelocity().x > 0.3f)
            return State.RUNNING;
        else
            return State.STANDING;
    }

	public void jump() {
		body.applyLinearImpulse(new Vector2(0, 11f), body.getWorldCenter(), true);
	}
	
	public void moveRight() {
		body.applyLinearImpulse(new Vector2(3.5f, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
    	runningRight = true;
	}
	public void moveLeft() {
    	body.applyLinearImpulse(new Vector2(-3.5f, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
    	runningRight = false;
	}
	
	public float getStateTimer(){
        return stateTimer;
    }

	
	public void onHit(float x) {
		this.hp--;
		if(this.hp<=0)
			setToDestroy = true;
		if(x==0) return;
		if(this.getBody().getPosition().x < x) {
	    	body.applyLinearImpulse(new Vector2(-12f, 2f), body.getWorldCenter(), true);
		}
		else {
			body.applyLinearImpulse(new Vector2(12f, 2f), body.getWorldCenter(), true);
		}		
		System.out.print(this.hp);
	}
	
	public int getHp() {
		return this.hp;
	}

	
	
	
	


}