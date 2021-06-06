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
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import scenes.Scene;
import tools.CollisionListener;

public class Slime extends Enemy {
	private Fixture leftFixture;
	private Fixture rightFixture;
	private boolean drawLeftRight=true;
	private Sound slimeMove = Gdx.audio.newSound(Gdx.files.internal("sounds/slime_jump.wav"));
	private Array<TextureRegion> moveFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> moveAnim;
	private Array<TextureRegion> attackFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> attackAnim;
	private enum State {MOVING, ATTACKING, STANDING};
	private boolean hasAttacked = false;
	private State currentState;
	private State previousState;

	private float jumpTimer = 2;

	public Slime(Vector2 position) {
		super(position);		
		setAnimations();

		stopTime=-1f;
		facingRight=false;
		moveSpeed=0.1f;
		jumpHeight=15f;
	}
	
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(initialPosition);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(0.8f / 2.f, 0.4f / 2.f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT & ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		fdef.shape = polShape;
		fdef.friction = 0;

		body.createFixture(fdef).setUserData(this);
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength, visionHeight, new Vector2(0,visionHeight-0.2f), 0);
		
		fdef.shape = vision;
		fdef.isSensor = true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.ENEMY_VISION_SENSOR_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);
		
		polShape.dispose();
	}

	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		//System.out.println("lijevi: "+ contactsLeft);
		//System.out.println("desni: "+ contactsRight);
		currentRegion = getFrame(deltaTime);
		
		hasAttacked = false;
		if (activeAI) {
			move(getDirection(scene.getPlayer()));
		}
			
		jumpTimer -= deltaTime;
		
		if (activeAI) {
			move(getDirection(scene.getPlayer()));
		}
		
        if (body.getLinearVelocity().y < 0)  {
			body.setLinearDamping(0);
        } else {
            body.setLinearDamping(12);
        }
        //System.out.println(activeAI);
        
		if (activeAI && drawLeftRight) {
			contactsRight = 0;
			contactsLeft = 0;
			
			FixtureDef fdef = new FixtureDef();

			EdgeShape dropcheck = new EdgeShape();
			dropcheck.set(new Vector2(1f,-1.15f), new Vector2(1f,-2.5f));
			
			fdef.shape = dropcheck;
			fdef.isSensor = true;
			fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.RIGHT_BOTTOM_ENEMY_SENSOR_BIT;
			fdef.filter.maskBits = CollisionListener.SOLID_WALL_BIT;
			leftFixture = (Fixture) body.createFixture(fdef);
			leftFixture.setUserData(this);

			dropcheck.set(new Vector2(-1f,-1.15f), new Vector2(-1f,-2.5f));
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
		if (direction == - 1 || direction == 2 && body.getLinearVelocity().y >= 0 && contactsLeft != 0 ) {
			moveLeft();
			facingRight = false;
		} else if (direction == 1 || direction == 4 && body.getLinearVelocity().y >= 0 && contactsRight != 0){
			moveRight();
			facingRight = true;
		}
		
		if(direction >= 2 && body.getLinearVelocity().y == 0 && jumpTimer < 0) {
			slimeMove.play(0.5f);
			jump();
			jumpTimer = 2;
		}		
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
		} else if (!self.isSensor() && (other.getFilterData().categoryBits & CollisionListener.PLAYER_BIT) != 0 && ((Player)other.getUserData()).hasAttacked()) {
			hit.play(0.5f);
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
		else if (!self.isSensor() && (other.getFilterData().categoryBits & CollisionListener.PLAYER_BIT) != 0) {
			hasAttacked = true;
		}
	}
	
	private void setAnimations() {
		atlas = new TextureAtlas(Gdx.files.internal("slimesprites\\slime_sprites.atlas"));
		currentRegion = new TextureRegion(atlas.findRegion("slime_idle"), 0, 0, 28, 14);
		for(int i = 0; i < 5; i++) {
			switch (i) {
				case 0:
					idleFrames.add(new TextureRegion(atlas.findRegion("slime_idle"), i * 30 , 0, 28, 14 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("slime_move"), i * 28 , 0, 26, 12 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("slime_attack"), i * 31 , 0, 28, 11 ));
					break;
				case 1:
					idleFrames.add(new TextureRegion(atlas.findRegion("slime_idle"), i * 30 , 0, 28, 14 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("slime_move"), i * 28 , 0, 26, 12 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("slime_attack"), i * 31 , 0, 29, 11 ));
					break;
				case 2:
					idleFrames.add(new TextureRegion(atlas.findRegion("slime_idle"), i * 30 , 0, 28, 14 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("slime_move"), i * 28 , 0, 24, 12 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("slime_attack"), i * 31 , 0, 28, 20 ));
					break;
				case 3:
					idleFrames.add(new TextureRegion(atlas.findRegion("slime_idle"), i * 30 , 0, 28, 13 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("slime_move"), i * 28 , 0, 26, 11 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("slime_attack"), i * 31 , 0, 28, 15 ));
					break;
				case 4:
					attackFrames.add(new TextureRegion(atlas.findRegion("slime_attack"), i * 31 , 0, 28, 12 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("slime_attack"), i * 31 , 0, 28, 12 ));
					break;			
			}
			
		}
		idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);
		moveAnim = new Animation<TextureRegion>(0.1f, moveFrames);
		attackAnim = new Animation<TextureRegion>(0.1f, attackFrames);
	}
	

	public TextureRegion getFrame(float deltaTime) {
		previousState = currentState;
		TextureRegion region;		

		if(currentState == State.ATTACKING && attackAnim.getKeyFrameIndex(stateTimer) != 4) {
			region = (TextureRegion) attackAnim.getKeyFrame(stateTimer, true);
			needsFlip(region);
			stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
			
			return region;
		}
		
        currentState = getState();

        switch(currentState){
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

		if(hasAttacked)
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
	
			float drawScaleX = (facingRight ? -1 : 1) * 1/35f;
			float drawScaleY = 1/35f;
	
			float drawOriginX = 0;
			float drawOriginY = 0;
			
			float offsetX = -0.4f;
			float offsetY = 0.22f;
			
			batch.draw(atlasRegion ,body.getPosition().x + (facingRight ? -offsetX : offsetX) , body.getPosition().y - offsetY , drawOriginX, drawOriginY , atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight(), drawScaleX, drawScaleY, 0);
		}
		
	}

}
