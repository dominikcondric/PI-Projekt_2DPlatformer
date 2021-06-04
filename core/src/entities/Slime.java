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

import scenes.Scene;

public class Slime extends Enemy {
	protected Fixture left;
	protected Fixture right;
	protected boolean drawleftright=true;
	private Sound slimeMove = Gdx.audio.newSound(Gdx.files.internal("sounds/slime_jump.wav"));

	private final int ON_GROUND = 0;
	private float jumpTimer = 2;

	public Slime(Vector2 position) {
		super(position);		
		setAnimations();
		sprite.setRegion(idle);
		sprite.setSize(0.9f, 0.9f);
		sprite.setScale(2f, 2f);
		

		stopTime=-1f;
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
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;

		this.body.createFixture(fdef).setUserData(this);
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength, visionHeight, new Vector2(0,visionHeight-(sprite.getHeight()/2)), 0);
		
		fdef.shape = vision;
		fdef.isSensor=true;
		this.body.createFixture(fdef).setUserData(this);
		
		polShape.dispose();
	}

	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		jumpTimer -= deltaTime;
		if (this.activeAI) {
			this.move(this.getDirection(scene.getPlayer()));
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
			//0x0002 left 0x0004 right
			
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
		
		if(!this.facingRight && this.contactsright<=0 && this.activeAI && !playerInVision) {
				this.activeAI=false;			

		}
		else if(this.facingRight && this.contactsleft<=0 && this.activeAI && !playerInVision) {
			this.activeAI=false;
			}
	}



	@Override
	public void move(int direction) {
		this.direction = direction;
		if(direction==-1 || direction==2 && this.body.getLinearVelocity().y>=0 && this.contactsright!=0) {
			moveLeft();
			this.facingRight=false;
		}
		else if (direction==1 || direction == 4 && this.body.getLinearVelocity().y>=0 && this.contactsleft!=0){
			moveRight();
			this.facingRight=true;
		}
		if(direction>=2 && this.body.getLinearVelocity().y==0 && jumpTimer < 0) {
			slimeMove.play(0.5f);
			jump();
			jumpTimer = 2;
		}		
	}

	@Override
	public void resolveCollisionEnd(Fixture self, Fixture other) {
		if(other.getUserData() instanceof Player && self.isSensor() && self.getFilterData().categoryBits==1) {
			((Enemy) self.getUserData()).playerInVision=false;
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
		}
		if (other.getUserData() instanceof Player && self.isSensor()) {
			activateAI();
			((Enemy) self.getUserData()).playerInVision=true;
			if (((Player)other.getUserData()).getHp() <= 0) {
				stopAI();
			}
		} else if (!self.isSensor() && other.getUserData() instanceof Player && ((Player)other.getUserData()).hasAttacked()) {
			hit.play(0.5f);
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
		atlas = new TextureAtlas(Gdx.files.internal("slimesprites\\idle_slime.atlas"));
		idle = new TextureRegion(atlas.findRegion("idle_slime01"), 0, 0, 19, 18);
		for(int i = 0; i < 6; i++) {
			idleFrames.add(new TextureRegion(atlas.findRegion("idle_slime01"), i * 23+5 , 0, 19, 18 ));
		}
		idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);
	}

}
