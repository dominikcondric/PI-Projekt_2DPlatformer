package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
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

public class Player extends Entity {
	public int hp = 4; 
	
	@SuppressWarnings({ "unused", "rawtypes" })
	private Animation playerIdleAnim;
	@SuppressWarnings("rawtypes")
	private Animation playerRunAnim;
	private Animation playerFallAnim;
	private Animation playerJumpAnim;
	private Animation playerCastAnim;
	private TextureRegion playerIdle;
	private TextureRegion playerFall;
	private TextureRegion playerJump;
	
	PolygonShape polShape;
	FixtureDef fdef;
	Fixture fix;
	
	private final int MOVE_THRESHOLD_LEFT = -6;
	private final int MOVE_THRESHOLD_RIGHT = 6;
	private final int ON_GROUND = 0;
	
	public enum State { FALLING, JUMPING, STANDING, RUNNING, DEAD, CASTING };
	public State currentState;
    public State previousState;
    
	public boolean runningRight = true;
	boolean lastAnim = false;
	private float stateTimer;
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Player(Vector2 position) {
		super(position);
		atlas = new TextureAtlas(Gdx.files.internal("aerosprites\\movement_casting_aero.atlas"));
		
		Array<TextureRegion> framesFall = new Array<TextureRegion>();
		Array<TextureRegion> framesIdle = new Array<TextureRegion>();
		Array<TextureRegion> framesRun = new Array<TextureRegion>();
		Array<TextureRegion> framesCast = new Array<TextureRegion>();
		
		int xCoordinate = 0;
		playerJump = new TextureRegion(atlas.findRegion("jump"), xCoordinate, 0, 20, 24);
		for(int i = 0; i < 2; i++) {
			framesFall.add(new TextureRegion(atlas.findRegion("fall"), xCoordinate, 0, 17, 31 ));
			xCoordinate += 19;
		}
		playerFallAnim = new Animation(0.1f, framesFall);
		xCoordinate = 0;
		for(int i = 0; i < 4; i++) {
			switch(i) {
				case 0:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), xCoordinate, 0, 19, 29 ));
					break;
				case 1:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), xCoordinate, 0, 17, 30 ));
					break;
				case 2:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), xCoordinate, 0, 19, 30 ));
					playerIdle = new TextureRegion(atlas.findRegion("idle"), xCoordinate, 0, 23, 31);
					break;
				case 3:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), xCoordinate, 0, 20, 29 ));
					break;
			}
			
	        xCoordinate += 22;
		}
		playerIdleAnim = new Animation(0.1f, framesIdle);
		xCoordinate = 0;
		for(int i = 0; i < 6; i++) {
			switch(i) {
			case 0:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), xCoordinate, 0, 20, 28 ));
				break;
			case 1:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), xCoordinate, 0, 20, 27 ));
				break;
			case 2:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), xCoordinate, 0, 20, 25 ));
				break;
			case 3:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), xCoordinate, 0, 23, 28 ));
				break;
			case 4:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), xCoordinate, 0, 20, 27 ));
				break;
			case 5:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), xCoordinate, 0, 20, 25 ));
				break;
		}
		
        xCoordinate += 25;
		}
		playerRunAnim = new Animation(0.1f, framesRun);
		
		xCoordinate = 0;
		for(int i = 0; i < 5; i++) {
			switch(i) {
				case 0:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), xCoordinate, 0, 21, 26 ));
					break;
				case 1:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), xCoordinate, 0, 18, 25 ));
					break;
				case 2:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), xCoordinate, 0, 17, 25 ));
					break;
				case 3:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), xCoordinate, 0, 27, 24 ));
					break;
				case 4:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), xCoordinate - 29, 0, 27, 24 ));
					break;
			}
			
	        xCoordinate += 29;
		}
		playerCastAnim = new Animation(0.1f, framesCast);
		
	    sprite.setRegion(playerJump);
		sprite.setSize(0.78f, 1.25f);
	}
	
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDefinition);
		
		polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f, sprite.getHeight() / 2.f);
		
		fdef = new FixtureDef();
		fdef.shape = polShape;
		
		fix = this.body.createFixture(fdef);
		fix.setUserData(this);
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
        
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
        	crouch();
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

	private void crouch() {

	}

	public TextureRegion getFrame(float deltaTime){
		
		TextureRegion region;
		if(currentState == State.CASTING && playerCastAnim.getKeyFrameIndex(stateTimer) != 4) {
			region = (TextureRegion) playerCastAnim.getKeyFrame(stateTimer, true);
			needsFlip(region);
			stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
			
			return region;
		}
        currentState = getState();
        

        switch(currentState){
        	case CASTING:
        		region = (TextureRegion) playerCastAnim.getKeyFrame(stateTimer, true);
        		break;
         	case JUMPING:
        		region = playerJump;
        		break;
        	case FALLING:
        		region = (TextureRegion) playerFallAnim.getKeyFrame(stateTimer, true);
        		break;
            case RUNNING:
                region = (TextureRegion) playerRunAnim.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            	region = (TextureRegion) playerIdleAnim.getKeyFrame(stateTimer, true);
            	break;
            default:
                region = playerIdle;
                break;
        }
        	
        
        needsFlip(region);

        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;

    }
	
	public State getState(){
		
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.F))
			return State.CASTING;
		else if((body.getLinearVelocity().y > 0 ))//&& currentState == State.JUMPING) || (body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
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
	
	public void needsFlip(TextureRegion region) {
		 if((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
	            region.flip(true, false);
	            runningRight = false;
	        }

	        else if((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
	            region.flip(true, false);
	            runningRight = true;
	        }	
	}
	


}