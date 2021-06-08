package entities;

import java.util.List;

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
import utility.Pair;


public class FirstBoss extends Enemy {
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
	private int currentPos = 0;
	private int[] positionsx = {80, 75, 80};
	private int[] positionsy = {15, 16, 15};
	private float attackAnimDelay;
	private float bodyHeight=2f;
	private float bodyWidth=2f;

	
	public FirstBoss(Vector2 position) {
		super(position);
		setAnimations();

		facingRight=false;
		moveSpeed=0.1f;
		jumpHeight=0.1f;
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
		vision.setAsBox(visionLength, visionHeight, new Vector2(0,visionHeight-0.2f), 0);
		
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
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		currentRegion = getFrame(deltaTime);
		
//		System.out.println(body.getPosition().x + " " + body.getPosition().y);
		//System.out.println(currentPos);
		if(hasAttacked) {
			attackAnimDelay -= deltaTime;
		}
		if(attackAnimDelay <= 0) {
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
			shoot();
			//System.out.println(body.getPosition().x + " " + body.getPosition().y);
			//System.out.println((positionsx[currentPos] + 1f) + " " + (positionsx[currentPos] + 1f));
			System.out.println(currentPos);
			move(getHeading(positionsx[currentPos], positionsy[currentPos]));
			if((body.getPosition().x<=positionsx[currentPos] + 0.1f && body.getPosition().x>=positionsx[currentPos] - 0.1f) &&
			   (body.getPosition().y<=positionsy[currentPos] + 0.1f && body.getPosition().y>=positionsy[currentPos] - 0.1f)) currentPos++;
			}
		if(currentPos>=2) currentPos=0;

	
	}
	private int getHeading(int x, int y) {
		//enemy manji x dakle true onda se mice enemy u desno
		//inace se mice u lijevo
		int dir = 0;
		if (body.getPosition().x < x) {
			dir++;
		} else if (body.getPosition().x > x){
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

}
