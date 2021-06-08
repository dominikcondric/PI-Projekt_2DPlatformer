package entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import scenes.Scene;


public abstract class Enemy extends Entity {
	protected float stateTimer;
	protected float stopTime;
	protected TextureRegion idle;
	protected Animation<TextureRegion> idleAnim;
	protected int hp = 5;
	protected int maxHp = 5;
	protected float visionHeight = 3f;
	protected float visionLength = 4f;
	protected float jumpHeight = 11f;
	protected float moveSpeed = .6f;
	protected int direction;
	protected boolean activeAI = false;
	protected boolean shouldBeStopped = false;
	//
	protected float contactsLeft = 0;
	protected float contactsRight = 0;
	protected boolean playerInVision = false;
	//
	protected Array<TextureRegion> idleFrames = new Array<TextureRegion>();
	protected TextureAtlas atlas;
	protected TextureRegion currentRegion;
	
	protected Sound hit = Gdx.audio.newSound(Gdx.files.internal("sounds/sword_hit3.wav"));

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
	}
	


	public int getDirection(Player player) {
		//enemy manji x dakle true onda se mice enemy u desno
		//inace se mice u lijevo
		int dir = 0;
		if (body.getPosition().x < player.body.getPosition().x) {
			dir++;
		} else if (body.getPosition().x > player.body.getPosition().x){
			dir--;
		}
		
		if (body.getPosition().y < player.body.getPosition().y)
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
		body.setLinearDamping(0);
		body.setLinearVelocity(new Vector2(0, jumpHeight));
		
	}

	public void moveLeft() {
		body.setLinearDamping(10);
		body.applyLinearImpulse(new Vector2(-moveSpeed, 0), body.getWorldCenter(), true);
    	
	}

	public void moveRight() {
		body.setLinearDamping(10);
		body.applyLinearImpulse(new Vector2(moveSpeed, 0), body.getWorldCenter(), true);
    	
	}
	
	public void moveDown() {
		body.setLinearDamping(10);
		body.applyLinearImpulse(new Vector2(0, -jumpHeight), body.getWorldCenter(), true);

	}
	
	public void moveUp() {
		body.setLinearDamping(10);
		body.applyLinearImpulse(new Vector2(0, jumpHeight), body.getWorldCenter(), true);
		
	}

	protected void onHit(boolean pushRight, float dmg) {
		float xPush = 15f;
		if (!pushRight) 
			xPush *= -1.f;
		
		body.applyLinearImpulse(new Vector2(xPush, 0.f), body.getWorldCenter(), true);
		hp -= dmg;
		if (this.hp <= 0)
			active = false;
	}


	public void activateAI() {
		this.activeAI = true;
	}

	public void stopAI() {
		this.activeAI = false;
	}
	
	
	
	
	@Override
	public void reset(World world) {
		super.reset(world);
		hp = maxHp;
		stopAI();
	}
}