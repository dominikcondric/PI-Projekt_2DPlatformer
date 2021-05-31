package entities;

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


public abstract class Enemy extends Entity {
	protected float stateTimer;
	protected float stopTime;
	protected TextureRegion idle;
	protected Animation<TextureRegion> idleAnim;
	protected int hp = 5;


	protected float visionHeight=3f;
	protected float visionLength=4f;
	protected float jumpheight=11f;
	protected float movespeed=0.6f;
	protected int direction;
	protected boolean active = false;
	protected boolean shouldBeStopped = false;
	//
	protected float contactsleft=0;
	protected float contactsright=0;
	protected boolean playerInVision=false;
	//
	protected Array<TextureRegion> idleFrames = new Array<TextureRegion>();
	protected TextureAtlas atlas;

	public Enemy(Vector2 position) {
		super(position);
		/*atlas = new TextureAtlas(Gdx.files.internal("slimesprites\\idle_slime.atlas"));
		idle = new TextureRegion(atlas.findRegion("idle_slime01"), 0, 0, 19, 18);
		for(int i = 0; i < 6; i++) {
			idleFrames.add(new TextureRegion(atlas.findRegion("idle_slime01"), i * 23+5 , 0, 19, 18 ));
		}
		idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);
		sprite.setRegion(idle);
		sprite.setSize(0.9f, 0.9f);
		sprite.setScale(2f, 2f);*/
	}
	
	@Override
	public abstract void addToWorld(World world);
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		TextureRegion currentRegion = getFrame(deltaTime);
		sprite.setRegion(currentRegion);
		if (body.getPosition().y < 0.f) {
			setToDestroy = true;
		}
		

	}
	


	public int getDirection(Player player) {
		//enemy manji x dakle true onda se mice enemy u desno
		//inace se mice u lijevo
		int dir = 0;
		if (this.sprite.getX()<player.sprite.getX()) {
			dir++;
		} else {
			dir--;
		}
		
		if (this.sprite.getY()<player.sprite.getY())
			dir	+= 3;
		
		return dir;
	}

	/*public void move(int direction) {
		if (direction == -1 || direction == 2) {
			moveLeft();
		} else if (direction == 1 || direction == 4){
			moveRight();
		}
		
		if(direction >= 2 && this.body.getLinearVelocity().y == 0) {
			jump();
		}
	}*/
	
	public abstract void move(int direction);

	public void jump() {
		body.applyLinearImpulse(new Vector2(0, this.jumpheight), body.getWorldCenter(), true);
	}

	public void moveLeft() {
    	body.applyLinearImpulse(new Vector2(-(this.movespeed), 0), body.getWorldCenter(), true);
    	body.setLinearDamping(20);
	}

	public void moveRight() {
		body.applyLinearImpulse(new Vector2(this.movespeed, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(20);
	}


	protected void onHit(boolean pushRight, float dmg) {

		float xPush = 15f;
		if (!pushRight) 
			xPush *= -1.f;
		
		body.applyLinearImpulse(new Vector2(xPush, 0.f), body.getWorldCenter(), true);
		hp -= dmg;
		if (this.hp <= 0)
			setToDestroy = true;
		System.out.println(hp);
	}


	public void activate() {
		this.active = true;
	}

	public void stop() {
		this.active = false;
	}

	
	public TextureRegion getFrame(float deltaTime) {
		TextureRegion region = (TextureRegion) idleAnim.getKeyFrame(stateTimer, true);
        
        if (body.getLinearVelocity().x < 0 && region.isFlipX()) {
            region.flip(true, false);
        } else if (body.getLinearVelocity().x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }

        stateTimer = stateTimer + deltaTime;
        
        return region;
    }

	@Override
	public abstract void resolveCollision(Fixture self, Fixture other); //{
		//kontakt playera i enemy visiona
		//posto ne postoji senzor s player objektom u userdata to znaci da enemy u ovom slucaju mora biti senzor a player mora biti sam hitbox playera
		/*if(this.shouldBeStopped && other.getFilterData().categoryBits==3) {
			if(this.facingRight && self.getFilterData().categoryBits==0x0004) this.shouldBeStopped=false;
			else if(!this.facingRight && self.getFilterData().categoryBits==0x0002) this.shouldBeStopped=false;
			System.out.println("should not be stopped");
		}*//*
		if(other.getFilterData().categoryBits==3 && ((Enemy) self.getUserData()).active) {
			if(self.getFilterData().categoryBits==4)this.contactsright++;
			else if(self.getFilterData().categoryBits==2) this.contactsleft++;
		}
		if (other.getUserData() instanceof Player && self.isSensor()) {
			activate();
			((Enemy) self.getUserData()).playerInVision=true;
			if (((Player)other.getUserData()).getHp() <= 0) {
				stop();
			}
		} else if (!self.isSensor() && other.getUserData() instanceof Player && ((Player)other.getUserData()).hasAttacked()) {
			Player player = (Player)other.getUserData();
			onHit(player.facingRight, player.getSwordDmg());
		} else if (!self.isSensor() && other.getUserData() instanceof Fireball) {
			Fireball fireball = (Fireball)other.getUserData();
			if(fireball.didExplode()) {
				onHit(fireball.facingRight, fireball.getExplosionDmg());
			}else {
				onHit(fireball.facingRight, fireball.getHitDmg());
			}
			
		}
	}*/
}