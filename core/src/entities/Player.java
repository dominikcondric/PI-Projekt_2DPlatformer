package entities;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
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
	private int jumpCount = 0;
	private TextureAtlas atlas;
	public boolean controllable = true;
	private int coinCount = 0;
	
	private float swordDmg;
		
	private Sound swordSlash = Gdx.audio.newSound(Gdx.files.internal("sounds/sword.wav"));
	private Sound footstep = Gdx.audio.newSound(Gdx.files.internal("sounds/footstep.wav"));
	private Sound jump = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));
	private Sound land = Gdx.audio.newSound(Gdx.files.internal("sounds/landing.wav"));
	
	private ArrayList<Ability> abilities;
	private ArrayList<Item> items;
	
	private Animation<TextureRegion> playerIdleAnim;
	private Animation<TextureRegion> playerRunAnim;
	private Animation<TextureRegion> playerCastAnim;
	private Animation<TextureRegion> playerAttackAnim1;
	private Animation<TextureRegion> playerAttackAnim2;
	private Animation<TextureRegion> playerAttackAnim3;
	private Animation<TextureRegion> currentAttackAnim;
	private TextureRegion playerFall;
	private TextureRegion playerJump;
	
	private Array<TextureRegion> framesAttack1 = new Array<TextureRegion>();
	private Array<TextureRegion> framesAttack2 = new Array<TextureRegion>();
	private Array<TextureRegion> framesAttack3 = new Array<TextureRegion>();
	private Array<TextureRegion> framesAttackCurrent = new Array<TextureRegion>();
	
	private float moveThresholdLeft = -6;
	private float moveThresholdRight = 6;
	private final int ON_GROUND = 0;
	
	private enum State { FALLING, JUMPING, STANDING, RUNNING, DEAD, CASTING, ATTACKING };
	private State currentState;
	private State previousState;
    
	public boolean inRange;
	private float stateTimer;
	private TextureRegion currentRegion;
	private boolean hasAttacked = false;
	private float attackCooldown = 0.25f;
	private boolean isFootstepPlaying = false;
	TextureAtlas atlasAttacks;
	
	FixtureDef fdef;
	Fixture melee;

	public Player(Vector2 position) {
		super(position);
		abilities = new ArrayList<Ability>(2);
		abilities.add(new FireballAbility());
		items = new ArrayList<Item>(1);
		swordDmg = 1;
		setAnimations();
	}

	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(initialPosition);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		this.body = world.createBody(bodyDefinition);
		fdef = new FixtureDef();
		fdef.filter.categoryBits = 0x01; // Player bit set to 1
		
		EdgeShape bottomShape = new EdgeShape();
		bottomShape.set(body.getLocalCenter().x - 0.76f/2f, body.getLocalCenter().y - 1.25f / 2.f, body.getLocalCenter().x + 0.76f/2f, body.getLocalCenter().y - 1.25f / 2.f);
		fdef.shape = bottomShape;
		fdef.friction = 2f;
		this.body.createFixture(fdef).setUserData(this);
		
		EdgeShape leftShape = new EdgeShape();
		leftShape.set(body.getLocalCenter().x - 0.78f/2f, body.getLocalCenter().y - 1.23f / 2.f, body.getLocalCenter().x - 0.78f/2f, body.getLocalCenter().y + 1.25f / 2.f);
		fdef.shape = leftShape;
		fdef.friction = 0;
		this.body.createFixture(fdef).setUserData(this);
		
		EdgeShape rightShape = new EdgeShape();
		rightShape.set(body.getLocalCenter().x + 0.78f/2f, body.getLocalCenter().y - 1.23f / 2.f, body.getLocalCenter().x + 0.78f/2f, body.getLocalCenter().y + 1.25f / 2.f);
		fdef.shape = rightShape;
		fdef.friction = 0;
		this.body.createFixture(fdef).setUserData(this);
		
		EdgeShape topShape = new EdgeShape();
		topShape.set(body.getLocalCenter().x - 0.78f/2f, body.getLocalCenter().y + 1.25f / 2.f, body.getLocalCenter().x + 0.78f/2f, body.getLocalCenter().y + 1.25f / 2.f);
		fdef.shape = topShape;
		fdef.friction = 0;
		this.body.createFixture(fdef).setUserData(this);
		
		bottomShape.dispose();
		topShape.dispose();
		leftShape.dispose();
		rightShape.dispose();
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
		
	     if(previousState == State.FALLING && currentState == State.STANDING) 
				land.play();
			
		if(currentState == State.RUNNING && !isFootstepPlaying) {
			footstep.loop(0.4f);
			isFootstepPlaying = true;
		}
		
		for (Ability ability : abilities)
			ability.update(deltaTime);
		
		if(playerVelocity.y == ON_GROUND && (currentState != State.JUMPING && currentState != State.FALLING)){
        	jumpCount = 0;
        	moveThresholdLeft = -6;
        	moveThresholdRight = 6;
        }else {
        	moveThresholdLeft = -4.5f;
        	moveThresholdRight = 4.5f;
        }
		
		if(hasAttacked) {
        	body.destroyFixture(melee);
        	hasAttacked = false;
        }
		
		if (controllable) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && (playerVelocity.y == ON_GROUND || jumpCount < 2)) {
				jumpCount++;
				jump.play();
				jump();
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
				dash();
			}
	
	        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) ) {
	        	moveRight();	
	        }	
	        
	        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
	        	moveLeft();
	        }
	        
	        if(attackCooldown == 0) {
	        	if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
	        		swordSlash.play();
	        		meleeAttack();
	        		attackCooldown = 0.25f;
	        	}
	        }
	        attackCooldown -= deltaTime;
	        if(attackCooldown < 0) attackCooldown = 0;
		}

        if(playerVelocity.x == 0 || playerVelocity.y != ON_GROUND) {
			isFootstepPlaying = false;
			footstep.stop();
		}
        
	}

	private void dash() {
		body.applyLinearImpulse(new Vector2(facingRight ? 10f : -10f, 0), body.getWorldCenter(), true);
		
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
		attackRange.dispose();
	}

	public TextureRegion getFrame(float deltaTime){
		previousState = currentState;
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
            	region = playerIdleAnim.getKeyFrame(stateTimer, true);
                break;
        }
        	
        needsFlip(region);
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
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
		//body.applyLinearImpulse(new Vector2(0, 9.5f), body.getWorldCenter(), true);
		body.setLinearVelocity(new Vector2(0, 8f));
	}
	
	public void moveRight() {
		if(body.getLinearVelocity().x <= moveThresholdRight) {
			body.applyLinearImpulse(new Vector2(1.5f, 0), body.getWorldCenter(), true);
			facingRight = true;
		}
	}
	
	public void moveLeft() {
		if(body.getLinearVelocity().x >= moveThresholdLeft) {
			body.applyLinearImpulse(new Vector2(-1.5f, 0), body.getWorldCenter(), true);
			facingRight = false;
		}
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
			active = false;
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
		 if((!facingRight)){
            facingRight = false;
        }

        else if((facingRight)){
            facingRight = true;
        }	
	}
	
	@Override
	public void render(SpriteBatch batch) {
		AtlasRegion atlasRegion = new AtlasRegion(currentRegion);

		float drawScaleX = (facingRight ? 1 : -1) * 1/22f;
		float drawScaleY = 1/22f;

		float drawOriginX = 0;
		float drawOriginY = 0;
		
		float offsetX = 0.55f;
		float offsetY = 0.65f;

		batch.draw(atlasRegion ,sprite.getX() + (facingRight ? -offsetX : offsetX) , sprite.getY() - offsetY , drawOriginX, drawOriginY , atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight(), drawScaleX, drawScaleY, 0);
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if(other.getUserData() instanceof Enemy && !other.isSensor() && !hasAttacked) {
			onHit(((Enemy) other.getUserData()).getPosition().x);
		} else if (other.getUserData() instanceof Coin && ((Coin)other.getUserData()).isActive()) {
			++coinCount;
		}
	}

	public float getSwordDmg() {
		return swordDmg;
	}
	
	public int getCoinCount() {
		return coinCount;
	}
	
	@Override
	public void reset(World world) {
		super.reset(world);
		hp = maxHp;
		items.clear();
		abilities.clear();
		abilities.add(new FireballAbility());
		coinCount = 0;
	}
	
	private void setAnimations() {
		atlas = new TextureAtlas(Gdx.files.internal("aerosprites\\movement_casting_v2.atlas"));
		atlasAttacks = new TextureAtlas(Gdx.files.internal("aerosprites\\aero_attacks.atlas"));
		Array<TextureRegion> framesIdle = new Array<TextureRegion>();
		Array<TextureRegion> framesRun = new Array<TextureRegion>();
		Array<TextureRegion> framesCast = new Array<TextureRegion>();
		
		playerJump = new TextureRegion(atlas.findRegion("jump"), 46, 0, 20, 24);
		playerFall = new TextureRegion(atlas.findRegion("jump"), 69, 0, 20, 24);

		for(int i = 0; i < 6; i++) {
			switch(i) {
				case 0:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 23, 27 ));
					framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 28 ));
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 21, 26 ));
					framesAttack1.add(new TextureRegion(atlasAttacks.findRegion("groundattack1"), i * 36, 0, 27, 22 ));
					break;
				case 1:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 23, 27 ));
					framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 27 ));
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 18, 25 ));
					framesAttack1.add(new TextureRegion(atlasAttacks.findRegion("groundattack1"), i * 36, 0, 25, 20 ));
					framesAttack2.add(new TextureRegion(atlasAttacks.findRegion("groundattack2"), i * 39, 0, 18, 27 ));
					framesAttack3.add(new TextureRegion(atlasAttacks.findRegion("groundattack3"), i * 50, 0, 20, 26 ));
					break;
				case 2:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 25, 28 ));
					framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 25 ));
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 17, 25 ));
					framesAttack1.add(new TextureRegion(atlasAttacks.findRegion("groundattack1"), i * 36, 0, 34, 36 ));	
					framesAttack2.add(new TextureRegion(atlasAttacks.findRegion("groundattack2"), i * 39, 0, 20, 27 ));
					framesAttack3.add(new TextureRegion(atlasAttacks.findRegion("groundattack3"), i * 50, 0, 48, 23 ));
					break;
				case 3:
					framesIdle.add(new TextureRegion(atlas.findRegion("idle_sword"), i * 27, 0, 25, 28 ));
					framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 23, 28 ));
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29, 0, 27, 24 ));
					framesAttack1.add(new TextureRegion(atlasAttacks.findRegion("groundattack1"), i * 36, 0, 27, 36 ));
					framesAttack2.add(new TextureRegion(atlasAttacks.findRegion("groundattack2"), i * 39, 0, 37, 29 ));
					framesAttack3.add(new TextureRegion(atlasAttacks.findRegion("groundattack3"), i * 50, 0, 31, 19 ));
					break;
				case 4:
					framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 27 ));
					framesCast.add(new TextureRegion(atlas.findRegion("cast"), i * 29 - 29, 0, 27, 24 ));
					framesAttack1.add(new TextureRegion(atlasAttacks.findRegion("groundattack1"), i * 36, 0, 19, 32 ));
					framesAttack1.add(new TextureRegion(atlasAttacks.findRegion("groundattack1"), i * 36, 0, 19, 32 ));
					framesAttack2.add(new TextureRegion(atlasAttacks.findRegion("groundattack2"), i * 39, 0, 32, 21 ));	
					framesAttack3.add(new TextureRegion(atlasAttacks.findRegion("groundattack3"), i * 50, 0, 34, 20 ));
					break;
				case 5:
					framesRun.add(new TextureRegion(atlas.findRegion("run"), i * 25, 0, 20, 25 ));
					framesAttack2.add(new TextureRegion(atlasAttacks.findRegion("groundattack2"), i * 39, 0, 31, 22 ));
					framesAttack2.add(new TextureRegion(atlasAttacks.findRegion("groundattack2"), i * 39, 0, 31, 22 ));
					framesAttack3.add(new TextureRegion(atlasAttacks.findRegion("groundattack3"), i * 50, 0, 34, 20 ));
					framesAttack3.add(new TextureRegion(atlasAttacks.findRegion("groundattack3"), i * 50, 0, 34, 20 ));
					break;
			}
			
		}
		
		playerIdleAnim = new Animation<TextureRegion>(0.1f, framesIdle);
		framesIdle.clear();
		playerRunAnim = new Animation<TextureRegion>(0.1f, framesRun);
		framesRun.clear();
		playerCastAnim = new Animation<TextureRegion>(0.1f, framesCast);	
		framesCast.clear();
		playerAttackAnim1 = new Animation<TextureRegion>(0.05f,framesAttack1);
		currentAttackAnim = playerAttackAnim1;
		playerAttackAnim2 = new Animation<TextureRegion>(0.05f,framesAttack2);
		playerAttackAnim3 = new Animation<TextureRegion>(0.05f,framesAttack3);
		
		
	}
}