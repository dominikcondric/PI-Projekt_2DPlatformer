package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import scenes.Scene;
import tools.CollisionListener;

public class MeleeGuard extends Enemy {
	
	private Fixture leftFixture;
	private Fixture rightFixture;
	private boolean drawLeftRight=true;
	private Array<TextureRegion> moveFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> moveAnim;
	private Array<TextureRegion> attackFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> attackAnim;
	private Array<TextureRegion> blockFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> blockAnim;
	private enum State {MOVING, ATTACKING, STANDING, BLOCKING, HURTING};
	private boolean hasAttacked = false;
	private State currentState;
	private State previousState;
	private boolean destroyAttackFix = false;
	public float damage = 2;
	FixtureDef fdef;
	Fixture melee;
	private Sound shieldBlock = Gdx.audio.newSound(Gdx.files.internal("sounds/shield_block.wav"));
	private float moveDelay = 1f;
	private boolean usingShield = false;
	private float jumpTimer = 0;
	private int blockFramesSize;
	private int attackFramesSize;
	private boolean startAttackAnim = false;
	private float attackDelay = 1f;

	public MeleeGuard(Vector2 position) {
		super(position);
		setAnimations();

		stopTime=-1f;
		facingRight=false;
		moveSpeed=0.4f;
		jumpHeight=15f;
	}

	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(initialPosition);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(0.5f, 0.6f);
		
		fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT & ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		fdef.shape = polShape;
		fdef.friction = 0;

		body.createFixture(fdef).setUserData(this);
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength * 2, visionHeight, new Vector2(0,visionHeight-0.7f), 0);
		
		fdef.shape = vision;
		fdef.isSensor = true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.ENEMY_VISION_SENSOR_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);
		
		CircleShape attackVision = new CircleShape();
		attackVision.setRadius(1.5f);
		fdef.shape = attackVision;
		fdef.isSensor = true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.LEFT_UPPER_ENEMY_SENSOR_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);
		
		
		polShape.dispose();
	}

	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if(direction == 0)
			direction = getDirection(scene.getPlayer());
		moveDelay -= deltaTime;
		attackDelay -= deltaTime;
		startAttackAnim = false;
		if(destroyAttackFix) {
			body.destroyFixture(melee);
			destroyAttackFix = false;
		}
		if(hasAttacked && attackDelay <= 0) {
			meleeAttack();
			destroyAttackFix = true;
			hasAttacked = false;       	  
			startAttackAnim = true;
			attackDelay = 1f;
        }
		currentRegion = getFrame(deltaTime);
			
		
		usingShield = false;
		if (activeAI) {
			//ako mu player dode s druge strane nego sto gleda, priceka 2 sekunde pa se okrene
			if(((direction == 2 || direction == -1) && (getDirection(scene.getPlayer()) == 4 || getDirection(scene.getPlayer()) == 1)) 
					|| ((direction == 4 || direction == 1) && (getDirection(scene.getPlayer()) == 2 || getDirection(scene.getPlayer()) == -1)))
				moveDelay = 2f;		
			direction = getDirection(scene.getPlayer());
			if(moveDelay <= 0)
				move(getDirection(scene.getPlayer()));
		}
		if(moveDelay < 0) {
			moveDelay = 0;
		}
		jumpTimer -= deltaTime;
        if (body.getLinearVelocity().y < 0)  {
			body.setLinearDamping(0);
        } else {
            body.setLinearDamping(12);
        }
        
		if (activeAI && drawLeftRight) {
			contactsRight = 0;
			contactsLeft = 0;
			
			FixtureDef fdef = new FixtureDef();

			EdgeShape dropcheck = new EdgeShape();
			dropcheck.set(new Vector2(0.8f,-1.15f), new Vector2(0.8f,-3f));
			
			fdef.shape = dropcheck;
			fdef.isSensor = true;
			fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.RIGHT_BOTTOM_ENEMY_SENSOR_BIT;
			fdef.filter.maskBits = CollisionListener.SOLID_WALL_BIT;
			leftFixture = (Fixture) body.createFixture(fdef);
			leftFixture.setUserData(this);

			dropcheck.set(new Vector2(-0.8f,-1.15f), new Vector2(-0.8f,-3f));
			fdef.shape = dropcheck;
			fdef.isSensor = true;
			fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.LEFT_BOTTOM_ENEMY_SENSOR_BIT;
			fdef.filter.maskBits = CollisionListener.SOLID_WALL_BIT;
			rightFixture = (Fixture) body.createFixture(fdef);
			rightFixture.setUserData(this);		
			
			drawLeftRight = false;
			return;
		}
		
		if(!activeAI && drawLeftRight == false) {
			body.destroyFixture(leftFixture);
			body.destroyFixture(rightFixture);
			contactsRight = 0;
			contactsLeft = 0;
			drawLeftRight=true;
		}
		
		if(facingRight && contactsRight <= 0 && activeAI && !playerInVision) {
			activeAI=false;			
		} else if (!facingRight && contactsLeft <= 0 && activeAI && !playerInVision) {
			activeAI=false;
		}
	}

	@Override
	public void move(int direction) {
		this.direction = direction;
		
		if ((direction == - 1 || direction == 2)  && contactsLeft != 0 ) {
			moveLeft();
			facingRight = false;
		} else if ((direction == 1 || direction == 4)  && contactsRight != 0){
			moveRight();
			facingRight = true;
		}
			
		if(direction >= 2 && body.getLinearVelocity().y == 0 && jumpTimer < 0) {
			jump();
			jumpTimer = 10;
		}	
	
	}
	
	private void meleeAttack() {	
		PolygonShape attackRange = new PolygonShape();
		attackRange.setAsBox(0.78f * 0.7f , 1.25f / 2.f, new Vector2(facingRight ? 1f : -1f,0), 0 );
		fdef.shape = attackRange;
		fdef.isSensor = true;
		//fdef.filter.categoryBits = 2;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.RIGHT_UPPER_ENEMY_SENSOR_BIT; 
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		melee = this.body.createFixture(fdef);
		melee.setUserData(this); 
		attackRange.dispose(); 	
	}

	@Override
	public void resolveCollisionEnd(Fixture self, Fixture other) {
		
		
	
		if((other.getFilterData().categoryBits & CollisionListener.PLAYER_BIT) != 0 && self.isSensor() && (self.getFilterData().categoryBits & CollisionListener.ENEMY_VISION_SENSOR_BIT) != 0) {
			playerInVision = false;
		}
		
//		if(other.getFilterData().categoryBits!=3 || !((Enemy) self.getUserData()).activeAI)return;
//		if(self.getFilterData().categoryBits == 0x0002) contactsLeft--;
//		else if(self.getFilterData().categoryBits == 0x0004) contactsRight--;
		//if ((other.getFilterData().categoryBits & CollisionListener.SOLID_WALL_BIT) != 0 && activeAI)
		//	return;
		
		if ((self.getFilterData().categoryBits & CollisionListener.LEFT_BOTTOM_ENEMY_SENSOR_BIT) != 0) 
			contactsLeft--;
		else if ((self.getFilterData().categoryBits & CollisionListener.RIGHT_BOTTOM_ENEMY_SENSOR_BIT) != 0) 
			contactsRight--;
	}

	@Override
	public void resolvePreSolve(Fixture self, Fixture other) {	
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if ((other.getFilterData().categoryBits & CollisionListener.SOLID_WALL_BIT) != 0 && activeAI) {
			if ((self.getFilterData().categoryBits & CollisionListener.LEFT_BOTTOM_ENEMY_SENSOR_BIT) != 0) 
				contactsLeft++;
			else if ((self.getFilterData().categoryBits & CollisionListener.RIGHT_BOTTOM_ENEMY_SENSOR_BIT) != 0) 
				contactsRight++;
		}
		
		
		
		if ((other.getFilterData().categoryBits & CollisionListener.PLAYER_BIT) != 0 && self.isSensor()) {
			activateAI();
			playerInVision=true;
			if (((Player)other.getUserData()).getHp() <= 0) {
				stopAI();
			}
			if(!other.isSensor() && (self.getFilterData().categoryBits & CollisionListener.LEFT_UPPER_ENEMY_SENSOR_BIT) != 0) {
				if(!usingShield)
					hasAttacked = true;
			}
			
			
		} else if (!self.isSensor() && (other.getFilterData().categoryBits & CollisionListener.PLAYER_BIT) != 0 && ((Player)other.getUserData()).hasAttacked()) {
			
			Player player = (Player)other.getUserData();
			onHit(player.facingRight, player.getSwordDmg());
		} else if (!self.isSensor() && (other.getFilterData().categoryBits & CollisionListener.FIREBALL_BIT) != 0) {
			Fireball fireball = (Fireball)other.getUserData();
			if(fireball.isSetToExplode()) {
				onHit(fireball.facingRight, fireball.getExplosionDmg());
			} else {
				onHit(fireball.facingRight, fireball.getHitDmg());
			}
		}
	}
	
	@Override
	protected void onHit(boolean pushRight, float dmg) {
		if((!pushRight && !facingRight) || (pushRight && facingRight)) {
			hit.play(0.5f);
			float xPush = 15f;
			if (!pushRight) 
				xPush *= -1.f;
			
			body.applyLinearImpulse(new Vector2(xPush, 0.f), body.getWorldCenter(), true);
			hp -= dmg;
			if (this.hp <= 0)
				active = false;
		}else {
			body.setLinearVelocity(0, body.getLinearVelocity().y);
			usingShield = true;
			shieldBlock.play();
		}
		
		
	}
	
	private void setAnimations() {
		atlas = new TextureAtlas(Gdx.files.internal("melee_guard_sprites\\melee_guard.atlas"));
		currentRegion = new TextureRegion(atlas.findRegion("melee_guard_idle"), 0, 0, 44, 40);
		for(int i = 0; i < 9; i++) {
			switch (i) {
				case 0:
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 44, 40 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 42, 39 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 43, 38 ));
					blockFrames.add(new TextureRegion(atlas.findRegion("melee_guard_block"), i * 57 , 0, 41, 36 ));
					break;
				case 1:
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 44, 40 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 44, 40 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 29, 45 ));
					blockFrames.add(new TextureRegion(atlas.findRegion("melee_guard_block"), i * 57 , 0, 49, 41 ));
					break;
				case 2:
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 45, 39 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 45, 40 ));;
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 66, 50 ));
					blockFrames.add(new TextureRegion(atlas.findRegion("melee_guard_block"), i * 57 , 0, 53, 46 ));
					break;
				case 3:
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 45, 39 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 47, 41 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 67, 46 ));
					blockFrames.add(new TextureRegion(atlas.findRegion("melee_guard_block"), i * 57 , 0, 55, 48 ));
					break;
				case 4:
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 45, 40 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 67, 34 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 45, 40 ));
					blockFrames.add(new TextureRegion(atlas.findRegion("melee_guard_block"), i * 57 , 0, 55, 49 ));
					blockFrames.add(new TextureRegion(atlas.findRegion("melee_guard_block"), i * 57 , 0, 55, 49 ));
					break;	
				case 5:
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 56, 34 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 44, 39 ));
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 44, 40 ));
					break;
				case 6:
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 36, 35 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 45, 40 ));
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 44, 39 ));
					break;
				case 7:
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 41, 39 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("melee_guard_attack"), i * 69 , 0, 41, 39 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 45, 40 ));
					idleFrames.add(new TextureRegion(atlas.findRegion("melee_guard_idle"), i * 47 , 0, 44, 39 ));
					break;
				case 8:
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 47, 41 ));
					break;
				case 9:
					moveFrames.add(new TextureRegion(atlas.findRegion("melee_guard_move"), i * 49 , 0, 43, 40 ));
					break;
			}
			
		}
		idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);
		moveAnim = new Animation<TextureRegion>(0.1f, moveFrames);
		attackAnim = new Animation<TextureRegion>(0.1f, attackFrames);
		blockAnim = new Animation<TextureRegion>(0.1f, blockFrames);
		attackFramesSize = attackFrames.size - 1;
		blockFramesSize = blockFrames.size - 1;
		idleFrames.clear();
		moveFrames.clear();
		attackFrames.clear();
		blockFrames.clear();
	}
	

	public TextureRegion getFrame(float deltaTime) {
		previousState = currentState;
		TextureRegion region;		
		if(currentState == State.BLOCKING && blockAnim.getKeyFrameIndex(stateTimer) != blockFramesSize) {
			region = blockAnim.getKeyFrame(stateTimer, true);
			needsFlip(region);
			stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
			
			return region;
		}
		
		if(currentState == State.ATTACKING && attackAnim.getKeyFrameIndex(stateTimer) != attackFramesSize) {
			region = attackAnim.getKeyFrame(stateTimer, true);
			needsFlip(region);
			stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
			
			return region;
		}
		
        currentState = getState();

        switch(currentState){
        	case BLOCKING:
        		region = blockAnim.getKeyFrame(stateTimer, true);
        		break;
        	case ATTACKING:
        		region = attackAnim.getKeyFrame(stateTimer, true);
        		break;
            case MOVING:
            	region = moveAnim.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            	region = idleAnim.getKeyFrame(stateTimer, true);
            	break;
            default:
            	region = idleAnim.getKeyFrame(stateTimer, true);
            	break;
            	
        }
        	
        needsFlip(region);
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        return region;
    }
	
	public State getState() {
		if(usingShield)
			return State.BLOCKING;
		else if(startAttackAnim)
			return State.ATTACKING;
        else if(body.getLinearVelocity().x < -0.3f || body.getLinearVelocity().x > 0.3f)
            return State.MOVING;
        else
            return State.STANDING;
    }
	
	@Override
	public void render(SpriteBatch batch) {
		if(active) {
			AtlasRegion atlasRegion = new AtlasRegion(currentRegion);
	
			float drawScaleX = (facingRight ? 1 : -1) * 1/35f;
			float drawScaleY = 1/35f;
	
			float drawOriginX = 0;
			float drawOriginY = 0;
			
			float offsetX = 0.7f;
			float offsetY = 0.6f;
			
			batch.draw(atlasRegion ,body.getPosition().x + (facingRight ? -offsetX : offsetX) , body.getPosition().y - offsetY , drawOriginX, drawOriginY , atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight(), drawScaleX, drawScaleY, 0);
		}
		
	}

	
	public boolean hasAttacked() {
		return hasAttacked;
	}



}
