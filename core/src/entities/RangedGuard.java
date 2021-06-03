package entities;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

public class RangedGuard extends Enemy {
	
	protected Fixture left;
	protected Fixture right;
	protected boolean drawleftright=true;
	protected boolean previousRight=false;
	protected boolean playerWasInVision;
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

	public RangedGuard(Vector2 position) {
		super(position);		
		setAnimations();
		sprite.setRegion(idle);
		sprite.setSize(0.9f, 0.9f);
		sprite.setScale(2f, 2f);

		activeAI=true;
		facingRight=false;
		moveSpeed=0.3f;
		jumpHeight=11f;
	}
	
	

	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		
		
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f, sprite.getHeight() / 2.f);
		//polShape.setAsBox(sprite.getWidth() / 1.5f, sprite.getHeight() / 1.5f);

		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;

		this.body.createFixture(fdef).setUserData(this);
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength, visionHeight, new Vector2(0,visionHeight-(sprite.getHeight()/2)), 0);
		
		fdef.shape = vision;
		fdef.isSensor=true;
		this.body.createFixture(fdef).setUserData(this);
		
		EdgeShape wallcheck = new EdgeShape();
		wallcheck.set(new Vector2(1f,0f), new Vector2(1f,1.5f));

		//0x0005 left 0x0006 right
		fdef.shape = wallcheck;
		fdef.isSensor=true;
		fdef.filter.categoryBits = 0x0005;
		this.body.createFixture(fdef).setUserData(this);

		wallcheck.set(new Vector2(-1f,0f), new Vector2(-1f,1.5f));
		fdef.shape = wallcheck;
		fdef.isSensor = true;
		fdef.filter.categoryBits = 0x0006;
		this.body.createFixture(fdef).setUserData(this);
		
		polShape.dispose();
	}

	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		
		if(!this.playerInVision) { 
			activateAI();
		}
		hasAttacked = false;
		if (this.activeAI) {
			if(this.facingRight)this.move(1);
			else this.move(0);
			stopTime=stateTimer+0.5f;
		}
		else if(stopTime<=stateTimer) {
			shoot(scene);
			stopTime=stateTimer+0.5f;
		}
		
		
        if (body.getLinearVelocity().y < 0)  {
			body.setLinearDamping(0);
        } else {
            body.setLinearDamping(12);
        }
        
		if(this.activeAI && drawleftright){
			this.contactsright=0;
			this.contactsleft=0;
			
			FixtureDef fdef=new FixtureDef();;

			EdgeShape dropcheck = new EdgeShape();
			dropcheck.set(new Vector2(1f,-1.15f), new Vector2(1f,-2.5f));
			
			//0x0002 left 0x0004 right/*
			
			fdef.shape = dropcheck;
			fdef.isSensor=true;
			fdef.filter.categoryBits = 0x0002;
			this.left=(Fixture) this.body.createFixture(fdef);
			this.left.setUserData(this);

			dropcheck.set(new Vector2(-1f,-1.15f), new Vector2(-1f,-2.5f));
			fdef.shape = dropcheck;
			fdef.isSensor = true;
			fdef.filter.categoryBits = 0x0004;
			this.right=(Fixture) this.body.createFixture(fdef);
			this.right.setUserData(this);		
			
			drawleftright=false;
			return;
			}
		
		if(!this.activeAI && drawleftright==false) {
			body.destroyFixture(left);
			body.destroyFixture(right);
			this.contactsright=0;
			this.contactsleft=0;
			drawleftright=true;
		}

	}
	
	@Override
	public TextureRegion getFrame(float deltaTime) {
		previousState = currentState;
		TextureRegion region = null;
		
		if(currentState == State.SHOOTING && shootAnim.getKeyFrameIndex(stateTimer) != 9) {
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
		scene.addEntity(new Arrow(this.getPosition(), this.getFacingDirection(), 0));
		hasAttacked = true;

	}

	@Override
	public void move(int direction) {
		this.direction = direction;
		if(this.contactsright==0 && !this.playerWasInVision) this.facingRight=true;
		else if(this.contactsleft==0  && !this.playerWasInVision) this.facingRight=false;
		
		playerWasInVision=false;
		
		if(direction==0 && this.body.getLinearVelocity().y>=0 ) {
			moveLeft();
		}
		else if (direction==1 && this.body.getLinearVelocity().y>=0 ){
			moveRight();
		}
	
	}

	@Override
	public void resolveCollisionEnd(Fixture self, Fixture other) {
		if(other.getUserData() instanceof Player && self.isSensor() && self.getFilterData().categoryBits==1) {
			((Enemy) self.getUserData()).playerInVision=false;
			((RangedGuard) self.getUserData()).playerWasInVision=true;
		}
		if(other.getFilterData().categoryBits!=3 || !((Enemy) self.getUserData()).activeAI)return;
		if(self.getFilterData().categoryBits == 0x0002) this.contactsleft--;
		else if(self.getFilterData().categoryBits == 0x0004) this.contactsright--;

	}

	@Override
	public void resolvePreSolve(Fixture self, Fixture other) {	
	}

	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {
		if(other.getFilterData().categoryBits==3 && ((Enemy) self.getUserData()).activeAI) {
			if(self.getFilterData().categoryBits==4)this.contactsright++;
			else if(self.getFilterData().categoryBits==2) this.contactsleft++;
			if(self.getFilterData().categoryBits==5) {
				this.facingRight=false;
			}
			else if(self.getFilterData().categoryBits==6) {
				this.facingRight=true;
			}
		}
		if (other.getUserData() instanceof Player && self.isSensor()) {
			stopAI();
			((Enemy) self.getUserData()).playerInVision=true;
			if(other.getBody().getPosition().x < body.getPosition().x) {
				this.move(0);
				this.facingRight=false;
			}
			else {
				this.move(1);
				this.facingRight=true;
			}
			((RangedGuard) self.getUserData()).previousRight=((RangedGuard) self.getUserData()).facingRight;
			if (((Player)other.getUserData()).getHp() <= 0) {
				stopAI();
			}
		} else if (!self.isSensor() && other.getUserData() instanceof Player && ((Player)other.getUserData()).hasAttacked()) {
			Player player = (Player)other.getUserData();
			onHit(player.facingRight, player.getSwordDmg());
		} else if (!self.isSensor() && other.getUserData() instanceof Fireball) {
			Fireball fireball = (Fireball)other.getUserData();
			if(fireball.isSetToExplode()) {
				onHit(fireball.facingRight, fireball.getExplosionDmg());
			}else {
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
