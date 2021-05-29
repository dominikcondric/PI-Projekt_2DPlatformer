package entities;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import abilities.Ability;
import abilities.FireballAbility;
import scenes.Scene;

public class Player extends Entity {
	private int hp = 50; 
	private int maxHp = 50;
	private TextureAtlas atlas;
	
	private ArrayList<Ability> abilities;
	private ArrayList<Item> items;
	
	private Animation<TextureRegion> playerIdleAnim;
	private Animation<TextureRegion> playerRunAnim;
	private Animation<TextureRegion> playerCastAnim;
	private Animation<TextureRegion> playerAttackAnim1;
	private Animation<TextureRegion> playerAttackAnim2;
	private Animation<TextureRegion> playerAttackAnim3;
	private Animation<TextureRegion> currentAttackAnim;
	//private Animation<TextureRegion> playerCrouchAnim;
	private TextureRegion playerIdle;
	private TextureRegion playerFall;
	private TextureRegion playerJump;
	
	private Array<TextureRegion> framesAttack1 = new Array<TextureRegion>();
	private Array<TextureRegion> framesAttack2 = new Array<TextureRegion>();
	private Array<TextureRegion> framesAttack3 = new Array<TextureRegion>();
	private Array<TextureRegion> framesAttackCurrent = new Array<TextureRegion>();
	
	private final int MOVE_THRESHOLD_LEFT = -6;
	private final int MOVE_THRESHOLD_RIGHT = 6;
	private final int ON_GROUND = 0;
	
	private enum State { FALLING, JUMPING, STANDING, RUNNING, DEAD, CASTING, ATTACKING };
	private State currentState;
	private State previousState;
    
	public boolean inRange;
	//private boolean isCrouching = false;
	private float stateTimer;
	private TextureRegion currentRegion;
	private boolean hasAttacked = false;
	private float attackCooldown = 0.25f;
	
	FixtureDef fdef;
	Fixture melee;

	public Player(Vector2 position) {
		super(position);
		atlas = new TextureAtlas(Gdx.files.internal("aerosprites\\movement_casting_v2.atlas"));
		abilities = new ArrayList<Ability>(2);
		abilities.add(new FireballAbility());
		items = new ArrayList<Item>(1);
		
		Array<TextureRegion> framesIdle = new Array<TextureRegion>();
		Array<TextureRegion> framesRun = new Array<TextureRegion>();
		Array<TextureRegion> framesCast = new Array<TextureRegion>();
		//Array<TextureRegion> framesCrouch = new Array<TextureRegion>();
		
		
		playerJump = new TextureRegion(atlas.findRegion("jump"), 46, 0, 20, 24);
		playerFall = new TextureRegion(atlas.findRegion("jump"), 69, 0, 20, 24);

		for(int i = 0; i < 4; i++) {
			switch(i) {
				case 0:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 23, 27 ));
					break;
				case 1:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 23, 27 ));
					break;
				case 2:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 25, 28 ));
					
					break;
				case 3:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 25, 28 ));
					break;
			}
			
		}
		
		/*for(int i = 0; i < 4; i++) {
			switch(i) {
				case 0:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), i * 22, 0, 19, 29 ));
					break;
				case 1:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), i * 22, 0, 17, 30 ));
					break;
				case 2:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), i * 22, 0, 19, 30 ));
					
					break;
				case 3:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle"), i * 22, 0, 20, 29 ));
					break;
			}
			
		}
		*/
		playerIdleAnim = new Animation<TextureRegion>(0.1f, framesIdle);
		
		for(int i = 0; i < 6; i++) {
			switch(i) {
			case 0:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 28 ));
				break;
			case 1:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 27 ));
				break;
			case 2:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 25 ));
				break;
			case 3:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 23, 28 ));
				playerIdle = new TextureRegion(atlas.findRegion("run"), i * 25, 0, 23, 28 );
				break;
			case 4:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 27 ));
				break;
			case 5:
				framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 25 ));
				break;
		}
		
		}
		playerRunAnim = new Animation<TextureRegion>(0.1f, framesRun);
	
		for(int i = 0; i < 5; i++) {
			switch(i) {
				case 0:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 21, 26 ));
					break;
				case 1:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 18, 25 ));
					break;
				case 2:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 17, 25 ));
					break;
				case 3:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 27, 24 ));
					break;
				case 4:
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29 - 29, 0, 27, 24 ));
					break;
			}
		}
		playerCastAnim = new Animation<TextureRegion>(0.1f, framesCast);
		
		/*for(int i = 0; i < 4; i++) {
			switch(i) {
				case 0:
					framesCrouch.add(new TextureRegion(atlas.findRegion("crouch"), i * 22, 0, 19, 21 ));
					break;
				case 1:
					framesCrouch.add(new TextureRegion(atlas.findRegion("crouch"), i * 22, 0, 20, 22 ));
					break;
				case 2:
					framesCrouch.add(new TextureRegion(atlas.findRegion("crouch"), i * 22, 0, 19, 22 ));
					break;
				case 3:
					framesCrouch.add(new TextureRegion(atlas.findRegion("crouch"), i * 22, 0, 17, 21 ));
					break;
			}
			
		}
		playerCrouchAnim = new Animation<TextureRegion>(0.1f, framesCrouch); */
		
		atlas = new TextureAtlas(Gdx.files.internal("aerosprites\\aero_attacks.atlas"));
		
		for(int i = 0; i < 5; i++) {
			switch(i) {
				case 0:
					framesAttack1.add(new TextureRegion(atlas.findRegion("groundattack1"), i * 36, 0, 27, 22 ));
					break;
				case 1:
					framesAttack1.add(new TextureRegion(atlas.findRegion("groundattack1"), i * 36, 0, 25, 20 ));
					break;
				case 2:
					framesAttack1.add(new TextureRegion(atlas.findRegion("groundattack1"), i * 36, 0, 34, 36 ));
					break;
				case 3:
					framesAttack1.add(new TextureRegion(atlas.findRegion("groundattack1"), i * 36, 0, 27, 36 ));
					break;
				case 4:
					framesAttack1.add(new TextureRegion(atlas.findRegion("groundattack1"), i * 36, 0, 19, 32 ));
					framesAttack1.add(new TextureRegion(atlas.findRegion("groundattack1"), i * 36, 0, 19, 32 ));
					break;
			}
		}
		playerAttackAnim1 = new Animation<TextureRegion>(0.05f,framesAttack1);
		currentAttackAnim = playerAttackAnim1;
		
		for(int i = 0; i < 6; i++) {
			switch(i) {

				case 1:
					framesAttack2.add(new TextureRegion(atlas.findRegion("groundattack2"), i * 39, 0, 18, 27 ));
					break;
				case 2:
					framesAttack2.add(new TextureRegion(atlas.findRegion("groundattack2"), i * 39, 0, 20, 27 ));
					break;
				case 3:
					framesAttack2.add(new TextureRegion(atlas.findRegion("groundattack2"), i * 39, 0, 37, 29 ));
					break;
				case 4:
					framesAttack2.add(new TextureRegion(atlas.findRegion("groundattack2"), i * 39, 0, 32, 21 ));
					break;
				case 5:
					framesAttack2.add(new TextureRegion(atlas.findRegion("groundattack2"), i * 39, 0, 31, 22 ));
					framesAttack2.add(new TextureRegion(atlas.findRegion("groundattack2"), i * 39, 0, 31, 22 ));
					break;
			}
		}
		playerAttackAnim2 = new Animation<TextureRegion>(0.05f,framesAttack2);
		
		for(int i = 0; i < 6; i++) {
			switch(i) {
				case 1:
					framesAttack3.add(new TextureRegion(atlas.findRegion("groundattack3"), i * 50, 0, 20, 26 ));
					break;
				case 2:
					framesAttack3.add(new TextureRegion(atlas.findRegion("groundattack3"), i * 50, 0, 48, 23 ));
					break;
				case 3:
					framesAttack3.add(new TextureRegion(atlas.findRegion("groundattack3"), i * 50, 0, 31, 19 ));
					break;
				case 4:
					framesAttack3.add(new TextureRegion(atlas.findRegion("groundattack3"), i * 50, 0, 34, 20 ));
					break;
				case 5:
					framesAttack3.add(new TextureRegion(atlas.findRegion("groundattack3"), i * 50, 0, 34, 20 ));
					framesAttack3.add(new TextureRegion(atlas.findRegion("groundattack3"), i * 50, 0, 34, 20 ));
					break;
			}
		}
		playerAttackAnim3 = new Animation<TextureRegion>(0.05f,framesAttack3);
		
	}
	
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(0.78f / 2.f, 1.25f / 2.f);

		fdef = new FixtureDef();
		fdef.shape = polShape;
		//fdef.filter.categoryBits = 1;
		this.body.createFixture(fdef).setUserData(this);
		
		/*PolygonShape meleeRange = new PolygonShape();
		meleeRange.setAsBox(0.78f * 2, 1.25f / 2.f);
		fdef.shape = meleeRange;
		fdef.filter.categoryBits = 2;
		this.body.createFixture(fdef).setUserData(this);
		*/
		polShape.dispose();
		
	}
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if (hp == 0) {
			hp = maxHp;
			return;
		}
		
		Vector2 playerVelocity = body.getLinearVelocity();
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			abilities.get(0).cast(scene, this);
		}
		
		currentRegion = getFrame(deltaTime);
		sprite.setRegion(currentRegion);
		
		for (Ability ability : abilities)
			ability.update(deltaTime);
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && playerVelocity.y == ON_GROUND) {
			jump();
		}	

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerVelocity.x <= MOVE_THRESHOLD_RIGHT) {
        	moveRight();
        }	
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && playerVelocity.x >= MOVE_THRESHOLD_LEFT) {
        	moveLeft();
        }
        
        if(hasAttacked) {
        	body.destroyFixture(melee);
        	hasAttacked = false;
        }
        
        if(attackCooldown == 0) {
        	if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
        		meleeAttack();
        		attackCooldown = 0.25f;
        	}
        }
        attackCooldown -= deltaTime;
        if(attackCooldown < 0) attackCooldown = 0;
        
        
        /*if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
        	crouch();
        }*/
        
        if(playerVelocity.y < ON_GROUND - 0.1 || playerVelocity.y > ON_GROUND + 0.1) {
        	body.setLinearDamping(0);
        }else {
        	body.setLinearDamping(12);
        }


        if (body.getPosition().y < 0.f) {
        	setToDestroy = true;
        	body.setTransform(2.f, 8.f, 0.f);
        }
	}

	private void meleeAttack() {
		PolygonShape attackRange = new PolygonShape();
		attackRange.setAsBox(0.78f * 0.7f , 1.25f / 2.f, new Vector2(facingRight ? 1f : -1f,0), 0 );
		fdef.shape = attackRange;
		fdef.isSensor = true;
		//fdef.filter.categoryBits = 2;
		melee = this.body.createFixture(fdef);
		melee.setUserData(this);
		hasAttacked = true;
	}



	/*private void crouch() {
		((PolygonShape)(body.getFixtureList().get(0).getShape())).setAsBox(0.78f / 2.f, isCrouching ? 1.25f / 2.f : 1.25f / 2.f / 2f);
    	body.setTransform(sprite.getX() + sprite.getWidth() / 2.f, isCrouching ? sprite.getY() + sprite.getHeight() / 2.f + 0.3f : sprite.getY() + sprite.getHeight() / 2.f - 0.3f, 0);
    	isCrouching = !isCrouching;
	} */

	public TextureRegion getFrame(float deltaTime){
		
		TextureRegion region;
		Random randomAnimation = new Random();
		
		if(currentState == State.CASTING && playerCastAnim.getKeyFrameIndex(stateTimer) != 4) {
			region = (TextureRegion) playerCastAnim.getKeyFrame(stateTimer, true);
			needsFlip(region);
			stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
			
			return region;
		}else if(currentState == State.ATTACKING && currentAttackAnim.getKeyFrameIndex(stateTimer) != framesAttackCurrent.size - 1) {
			region = (TextureRegion) currentAttackAnim.getKeyFrame(stateTimer, true);
			needsFlip(region);
			stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
			
			return region;
		}
        currentState = getState();
        
        

        switch(currentState){

        	case CASTING:
        		region = playerCastAnim.getKeyFrame(stateTimer, true);
        		break;
        	case ATTACKING:
        		switch(randomAnimation.nextInt(3)) {
        			case 0 : 
        				region = playerAttackAnim1.getKeyFrame(stateTimer, true);
        				currentAttackAnim = playerAttackAnim1;
        				framesAttackCurrent = framesAttack1;
        				break;
        			case 1 : 
        				region = playerAttackAnim2.getKeyFrame(stateTimer, true);
        				currentAttackAnim = playerAttackAnim2;
        				framesAttackCurrent = framesAttack2;
        				break;
        			case 2 : 
        				region = playerAttackAnim3.getKeyFrame(stateTimer, true);
        				currentAttackAnim = playerAttackAnim3;
        				framesAttackCurrent = framesAttack3;
        				break;
        		}
        		
         	case JUMPING:
        		region = playerJump;
        		break;
        	case FALLING:
        		region = playerFall;
        		break;
            case RUNNING:
                region = playerRunAnim.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            	region = playerIdleAnim.getKeyFrame(stateTimer, true);
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
	
	public State getState() {
		if(abilities.get(0).isTriggered())
			return State.CASTING;
		else if(hasAttacked)
			return State.ATTACKING;
		else if((body.getLinearVelocity().y > 0 ))//&& currentState == State.JUMPING) || (body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
        	return State.JUMPING;   
        else if(body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(body.getLinearVelocity().x < -0.3f || body.getLinearVelocity().x > 0.3f)
            return State.RUNNING;
        else
            return State.STANDING;
    }

	private void jump() {
		body.applyLinearImpulse(new Vector2(0, 11f), body.getWorldCenter(), true);
	}
	
	private void moveRight() {
		body.applyLinearImpulse(new Vector2(3.5f, 0), body.getWorldCenter(), true);
    	facingRight = true;
	}
	private void moveLeft() {
    	body.applyLinearImpulse(new Vector2(-3.5f, 0), body.getWorldCenter(), true);
    	facingRight = false;
	}
	
	public float getStateTimer(){
        return stateTimer;
    }
	
	public boolean hasAttacked() {
		return hasAttacked;
	}
	
	public ArrayList<Ability> getAbilityList() {
		return abilities;
	}
	
	public void addItem(Item item) {
		items.add(item);
	}
	
	public void onHit(float x) {
		this.hp--;
		if(this.hp<=0)
			setToDestroy = true;
		if(x==0) return;
		if(this.body.getPosition().x < x) {
	    	body.applyLinearImpulse(new Vector2(-10f, 1f), body.getWorldCenter(), true);
		}
		else {
			body.applyLinearImpulse(new Vector2(10f, 1f), body.getWorldCenter(), true);
		}		
	}
	
	public int getHp() {
		return this.hp;
	}
	
	public int getMaxHp() {
		return this.maxHp;
	}
	
	private void needsFlip(TextureRegion region) {
		 if((!facingRight) && !region.isFlipX()){
	            region.flip(true, false);
	            facingRight = false;
	        }

	        else if((facingRight) && region.isFlipX()){
	            region.flip(true, false);
	            facingRight = true;
	        }	
	}
	
	@Override
	public void render(SpriteBatch batch) {
		AtlasRegion atlasRegion = new AtlasRegion(currentRegion);
		float width = 34; 
		float height = 36;

		float scaleWidth = (float) atlasRegion.packedWidth / atlasRegion.originalWidth;
		float scaleHeight = (float) atlasRegion.packedHeight / atlasRegion.originalHeight;

		float drawWidth = width * scaleWidth /16;
		float drawHeight = height * scaleHeight /16;

		float drawScaleX = 0.5f;
		float drawScaleY = 0.5f;


		float drawOriginX = 0;
		float drawOriginY = 0;

		batch.draw(atlasRegion, sprite.getX()-0.5f, sprite.getY()-0.65f, drawOriginX, drawOriginY, drawWidth, drawHeight, drawScaleX, drawScaleY, 0);
		
	}

	@Override
	public void resolveCollision(Fixture self, Fixture other) {
		if(other.getUserData() instanceof Enemy && !other.isSensor() && !hasAttacked) {
			onHit(((Enemy) other.getUserData()).getPosition().x);
		}
	}
}