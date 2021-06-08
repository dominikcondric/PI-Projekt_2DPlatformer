package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import scenes.Scene;
import tools.CollisionListener;


public class FirstBoss extends Enemy {

	private Array<TextureRegion> moveFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> moveAnim;
	private Array<TextureRegion> attackFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> attackAnim;
	private Array<TextureRegion> dieFrames = new Array<TextureRegion>();
	private Animation<TextureRegion> dieAnim;
	private enum State {MOVING, ATTACKING, STANDING, DYING};
	private boolean hasAttacked = false;
	private State currentState;
	private State previousState;
	private int currentPos = 0;
	private int[] positionsx = {174,180,190,203,203,204,194,194,190,187,183,177};
	private int[] positionsy = {20,23,24,22,17,14,14,16,16,16,18,16};
	private float attackAnimDelay;
	private float bodyHeight=2f;
	private float bodyWidth=1f;
	private boolean deathAnimStart;
	private float deathAnimDelay = 2f;
	private int hp;

	
	public FirstBoss(Vector2 position) {
		super(position);
		setAnimations();
		hp = 1;
		facingRight=false;
		moveSpeed=0.3f;
		jumpHeight=0.3f;
		attackAnimDelay=1f;
		activeAI=false;
		
		}

	@Override
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(initialPosition);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(bodyWidth / 2.f, bodyHeight / 2.f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT & ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		fdef.shape = polShape;
		fdef.friction = 0;

		body.createFixture(fdef).setUserData(this);
		body.setGravityScale(0);
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength * 4, visionHeight * 4, new Vector2(0,visionHeight-4f), 0);
		
		fdef.shape = vision;
		fdef.isSensor = true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT | CollisionListener.ENEMY_VISION_SENSOR_BIT;
		fdef.filter.maskBits = CollisionListener.PLAYER_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);
	
		polShape.dispose();

	}
	

	public void move(int direction) {
		this.direction = direction;
		if (direction == - 1 || direction == 2 || direction == -4) {
			moveLeft();
			facingRight = false;
		} else if (direction == 1 || direction == 4 || direction == -2){
			moveRight();
			facingRight = true;
		}
		
		if(direction >= 2 ) {
			moveUp();
		}	
		else if (direction <= -2) moveDown();
	}
	private void setAnimations() {

		atlas = new TextureAtlas(Gdx.files.internal("sorcerersprites\\first_boss_sprites.atlas"));
		currentRegion = new TextureRegion(atlas.findRegion("idle_wizard"), 0, 0, 28, 14);
		for(int i = 0; i < 8; i++) {
			switch (i) {
				case 0:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 32, 55 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 48, 65 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					dieFrames.add(new TextureRegion(atlas.findRegion("die_wizard"), i * 53 , 0, 36, 46 ));
					break;
				case 1:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 34, 54 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 48, 66 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					dieFrames.add(new TextureRegion(atlas.findRegion("die_wizard"), i * 53 , 0, 47, 56 ));
					break;
				case 2:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 35, 54 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 49, 67 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					dieFrames.add(new TextureRegion(atlas.findRegion("die_wizard"), i * 53 , 0, 51, 52 ));
					break;
				case 3:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 32, 53 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 48, 67 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					dieFrames.add(new TextureRegion(atlas.findRegion("die_wizard"), i * 53 , 0, 44, 25 ));
					break;
				case 4:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 32, 55));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 48, 66 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					dieFrames.add(new TextureRegion(atlas.findRegion("die_wizard"), i * 53 , 0, 47, 27));
					dieFrames.add(new TextureRegion(atlas.findRegion("die_wizard"), i * 53 , 0, 47, 27));
					break;		
				case 5:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 35, 53 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 52, 66 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					break;
				case 6:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 35, 56 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 52, 68 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					break;
				case 7:
					idleFrames.add(new TextureRegion(atlas.findRegion("idle_wizard"), i * 37 , 0, 34, 57 ));
					moveFrames.add(new TextureRegion(atlas.findRegion("move_wizard"), i * 54 , 0, 52, 67 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					attackFrames.add(new TextureRegion(atlas.findRegion("attack_wizard"), i * 83 , 0, 48, 65 ));
					break;
			}
			
		}
		idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);
		moveAnim = new Animation<TextureRegion>(0.1f, moveFrames);
		attackAnim = new Animation<TextureRegion>(0.1f, attackFrames);
		dieAnim = new Animation<TextureRegion>(0.4f, dieFrames);
	}
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		currentRegion = getFrame(deltaTime);
		
		if(deathAnimStart) {
			deathAnimDelay -= deltaTime;
		}
		
		if(deathAnimDelay <= 0) {
			active = false;
			activeAI = false;
		}
			

		if(hasAttacked) {
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x, body.getPosition().y), true, 1, 1, 10f, 0f));
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x, body.getPosition().y + bodyHeight/2), true, 1, 1, 10f, 10f));
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x, body.getPosition().y - bodyHeight/2), true, 1, 1, 10f, -10f));
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x + bodyWidth/2, body.getPosition().y + bodyHeight/2), false, 1, 1, 0f, 10f));
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x + bodyWidth/2, body.getPosition().y - bodyHeight/2), false, 1, 1, 0f, -10f));
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x , body.getPosition().y), false, 1, 1, -10f, 0f));
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x , body.getPosition().y + bodyHeight/2), false, 1, 1, -10f, 10f));
			scene.addEntity(new EnemyFireball(new Vector2(body.getPosition().x , body.getPosition().y - bodyHeight/2), false, 1, 1, -10f, -10f));

			hasAttacked = false;
			attackAnimDelay = 5f;
		}
		
		if(activeAI) { 

			
			attackAnimDelay -= deltaTime;
			
			if(attackAnimDelay <= 0) {
				shoot();
				attackAnimDelay = 3f;
			}
				
			move(getHeading(positionsx[currentPos], positionsy[currentPos]));
			if((body.getPosition().x<=positionsx[currentPos] + 0.1f && body.getPosition().x>=positionsx[currentPos] - 0.1f) &&
			   (body.getPosition().y<=positionsy[currentPos] + 0.1f && body.getPosition().y>=positionsy[currentPos] - 0.1f)) currentPos++;
			}
		if(currentPos>=11) currentPos=0;

	
	}
	private int getHeading(int x, int y) {
		//enemy manji x dakle true onda se mice enemy u desno
		//inace se mice u lijevo
		int dir = 0;
		if (body.getPosition().x < x - 0.1f) {
			dir++;
		} else if (body.getPosition().x > x + 0.1f){
			dir--;
		}
		
		if (body.getPosition().y < y - 0.1f )
			dir	+= 3;
		else if (body.getPosition().y > y + 0.1f )
			dir-=3;
		return dir;	
	}

	private void shoot() {
		hasAttacked = true;		
	}

	public TextureRegion getFrame(float deltaTime) {
		previousState = currentState;
		TextureRegion region;		

		if(currentState == State.ATTACKING && attackAnim.getKeyFrameIndex(stateTimer) != 8) {
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
            case DYING:
            	region = dieAnim.getKeyFrame(stateTimer, true);
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
		if(deathAnimStart)
			return State.DYING;
		else if(hasAttacked)
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
	
			float drawScaleX = (facingRight ? -1 : 1) * 1/32f;
			float drawScaleY = 1/32f;
	
			float drawOriginX = 0;
			float drawOriginY = 0;
			
			float offsetX = -1f;
			float offsetY = 0.5f;
			
			batch.draw(atlasRegion ,body.getPosition().x + (facingRight ? -offsetX : offsetX) , body.getPosition().y - offsetY , drawOriginX, drawOriginY , atlasRegion.getRegionWidth(), atlasRegion.getRegionHeight(), drawScaleX, drawScaleY, 0);
		}
		
	}
	
	@Override
	public void resolveCollisionBegin(Fixture self, Fixture other) {

		
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
	
	@Override
	protected void onHit(boolean pushRight, float dmg) {
		float xPush = 15f;
		if (!pushRight) 
			xPush *= -1.f;
		
		body.applyLinearImpulse(new Vector2(xPush, 0.f), body.getWorldCenter(), true);
		hp -= dmg;
		if (this.hp <= 0)
			deathAnimStart = true;
	}

}
