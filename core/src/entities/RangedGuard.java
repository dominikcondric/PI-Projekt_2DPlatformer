package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public class RangedGuard extends Enemy {
	protected Fixture leftFixture;
	protected Fixture rightFixture;
	private boolean drawLeftRight = true;
//	private boolean previousRight = false;
	private boolean playerWasInVision;
	private Animation<TextureRegion> runAnim;
	private Animation<TextureRegion> shootAnim;
	private enum State {RUNNING, SHOOTING, STANDING};
	private State currentState;
	private State previousState;
	private boolean hasAttacked = false;
	TextureAtlas atlasRun;
	TextureAtlas atlasShoot;
	Array<TextureRegion> runFrames = new Array<TextureRegion>();
	Array<TextureRegion> shootFrames = new Array<TextureRegion>();
	private Sound arrowShoot = Gdx.audio.newSound(Gdx.files.internal("sounds/arrow.wav"));

	public RangedGuard(Vector2 position) {
		super(position);		
		setAnimations();
		sprite.setRegion(idle);
		sprite.setSize(0.9f, 0.9f);
		sprite.setScale(2f, 2f);

		activeAI = true;
		facingRight = false;
		moveSpeed = 0.3f;
		jumpHeight = 11f;
	}

	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f, sprite.getHeight() / 2.f);

		FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT & ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		fdef.shape = polShape;

		body.createFixture(fdef).setUserData(this);
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength, visionHeight, new Vector2(0,visionHeight-(sprite.getHeight()/2)), 0);
		
		fdef.shape = vision;
		fdef.isSensor=true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT | ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);
		
		EdgeShape wallcheck = new EdgeShape();
		
		wallcheck.set(new Vector2(1f,0f), new Vector2(1f,1.5f));
		fdef.shape = wallcheck;
		fdef.isSensor=true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.RIGHT_ENEMY_SENSOR_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT | ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);

		wallcheck.set(new Vector2(-1f,0f), new Vector2(-1f,1.5f));
		fdef.shape = wallcheck;
		fdef.isSensor = true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.LEFT_ENEMY_SENSOR_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT | ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);
		
		polShape.dispose();
	}

	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		
		if(!playerInVision) { 
			activateAI();
		}
		hasAttacked = false;
		if (activeAI) {
			if(facingRight)move(1);
			else move(0);
			stopTime=stateTimer+0.5f;
		}
		else if(stopTime<=stateTimer && active) {
			shoot(scene);
			stopTime=stateTimer+0.5f;
		}
		
        if (body.getLinearVelocity().y < 0)  {
			body.setLinearDamping(0);
        } else {
            body.setLinearDamping(12);
        }
        
		if(activeAI && drawLeftRight) {
			contactsRight = 0;
			contactsLeft = 0;
			
			FixtureDef fdef=new FixtureDef();;

			EdgeShape dropcheck = new EdgeShape();
			dropcheck.set(new Vector2(1f,-1.15f), new Vector2(1f,-2.5f));
			
			fdef.shape = dropcheck;
			fdef.isSensor = true;
			fdef.filter.categoryBits = 0x0002;
			leftFixture = (Fixture) body.createFixture(fdef);
			leftFixture.setUserData(this);

			dropcheck.set(new Vector2(-1f,-1.15f), new Vector2(-1f,-2.5f));
			fdef.shape = dropcheck;
			fdef.isSensor = true;
			fdef.filter.categoryBits = 0x0004;
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
	}
	
	@Override
	public TextureRegion getFrame(float deltaTime) {
		previousState = currentState;
		TextureRegion region = null;
		
		if (currentState == State.SHOOTING && shootAnim.getKeyFrameIndex(stateTimer) != 9) {
			region = (TextureRegion) shootAnim.getKeyFrame(stateTimer, true);
			needsFlip(region);
			stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
			
			return region;
		}
		
        currentState = getState();

        switch(currentState){
        	case SHOOTING:
        		region = shootAnim.getKeyFrame(stateTimer, false);
        		break;
            case RUNNING:
            	region = runAnim.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            	region = idleAnim.getKeyFrame(stateTimer, true);
            	break;
        }
        	
        needsFlip(region);
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        return region;
    }
	
	public State getState() {

		if(hasAttacked)
			return State.SHOOTING;
        else if(body.getLinearVelocity().x < -0.3f || body.getLinearVelocity().x > 0.3f)
            return State.RUNNING;
        else
            return State.STANDING;
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

	private void shoot(Scene scene) {
		scene.addEntity(new Arrow(getPosition(), getFacingDirection(), 0));
		arrowShoot.play();
		hasAttacked = true;
}

	@Override
	public void move(int direction) {
		this.direction = direction;
		if(contactsRight == 0 && !playerWasInVision) facingRight=true;
		else if(contactsLeft == 0  && !playerWasInVision) facingRight=false;
		
		playerWasInVision=false;
		
		if(direction==0 && body.getLinearVelocity().y>=0 ) {
			moveLeft();
		}
		else if (direction==1 && body.getLinearVelocity().y>=0 ){
			moveRight();
		}
	
	}

	@Override
	public void resolveCollisionEnd(Fixture self, Fixture other) {
		if(other.getUserData() instanceof Player && self.isSensor() && self.getFilterData().categoryBits==1) {
			playerInVision = false;
			playerWasInVision = true;
		}
		
		if((other.getFilterData().categoryBits & (CollisionListener.SOLID_WALL_BIT | CollisionListener.PLATFORM_BIT)) != 0 || !activeAI) 
			return;
		
		if ((self.getFilterData().categoryBits & CollisionListener.LEFT_ENEMY_SENSOR_BIT) != 0) 
			contactsLeft--;
		else if ((self.getFilterData().categoryBits & CollisionListener.RIGHT_ENEMY_SENSOR_BIT) != 0) 
			contactsRight--;
	}

	@Override
	public void resolvePreSolve(Fixture self, Fixture other) {	
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if((other.getFilterData().categoryBits & CollisionListener.ENEMY_BIT) != 0 && ((Enemy) self.getUserData()).activeAI) {
			if ((self.getFilterData().categoryBits & CollisionListener.LEFT_ENEMY_SENSOR_BIT) != 0) 
				contactsLeft--;
			else if ((self.getFilterData().categoryBits & CollisionListener.RIGHT_ENEMY_SENSOR_BIT) != 0) 
				contactsRight--;
		}
		
		if ((other.getFilterData().categoryBits & CollisionListener.PLAYER_BIT) != 0 && self.isSensor()) {
			stopAI();
			playerInVision = true;
			if (other.getBody().getPosition().x < body.getPosition().x) {
				move(0);
				facingRight = false;
			} else {
				move(1);
				facingRight = true;
			}
			
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
	}		
	
	private void setAnimations() {
		atlas = new TextureAtlas(Gdx.files.internal("archersprites\\archer_idle.atlas"));
		atlasRun = new TextureAtlas(Gdx.files.internal("archersprites\\archer_run.atlas"));
		atlasShoot = new TextureAtlas(Gdx.files.internal("archersprites\\archer_shoot.atlas"));
		idle = new TextureRegion(atlas.findRegion("tile000"), 0, 0, 25, 39);
		for(int i = 0; i < 8; i++) {
			switch(i) {
				case 0:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 25, 39 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 42, 31 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 25, 39 ));
					break;
				case 1:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 25, 39 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 42, 32 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 25, 39 ));
					break;
				case 2:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 27, 39 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 43, 33 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 30, 39 ));
					break;
				case 3:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 26, 38 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 42, 33 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 26, 39 ));
					break;
				case 4:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 25, 38 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 40, 33 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 40, 38 ));
					break;
				case 5:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 25, 37 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 38, 33 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 37, 38 ));
					break;
				case 6:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 25, 38 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 33, 34 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 28, 36 ));
					break;
				case 7:
					idleFrames.add(new TextureRegion(atlas.findRegion("tile000"), i * 29 , 0, 25, 40 ));
					runFrames.add(new TextureRegion(atlasRun.findRegion("tile000"), i * 45 , 0, 37, 32 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 28, 36 ));
					break;
				case 8:
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 28, 36 ));
					shootFrames.add(new TextureRegion(atlasShoot.findRegion("tile000"), i * 42 , 0, 28, 36 ));
					break;
			}
		}
		idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);		
		runAnim = new Animation<TextureRegion>(0.1f, runFrames);
		shootAnim = new Animation<TextureRegion>(0.1f, idleFrames);
		
	}

}
