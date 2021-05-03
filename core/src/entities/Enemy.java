package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import scenes.Scene;



public class Enemy extends Entity {
	
	float stateTimer;
	int direction;
	private TextureRegion slimeIdle;
	@SuppressWarnings("rawtypes")
	private Animation slimeIdleAnim;
	protected int hp=5;
	protected float visionHeight=3f;
	protected float visionLength=4f;
	protected boolean active=false;
	private Array<TextureRegion> idleFrames = new Array<TextureRegion>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enemy(Vector2 position) {
		super(position);
		atlas = new TextureAtlas(Gdx.files.internal("slimesprites\\idle_slime.atlas"));
		slimeIdle = new TextureRegion(atlas.findRegion("idle_slime01"), 0, 0, 19, 18);
		for(int i = 0; i < 6; i++) {
			idleFrames.add(new TextureRegion(atlas.findRegion("idle_slime01"), i * 23+5 , 0, 19, 18 ));
		}
		slimeIdleAnim = new Animation(0.1f, idleFrames);
		sprite.setRegion(slimeIdle);
		sprite.setSize(0.9f, 0.9f);
		sprite.setScale(2f, 2f);
	}
	
	@Override
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		
		
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		//bodyDefinition.type = BodyDef.BodyType.StaticBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f, sprite.getHeight() / 2.f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;

		this.body.createFixture(fdef).setUserData(this);
		
		/*EdgeShape vision = new EdgeShape();
		vision.set(new Vector2(-2,-2), new Vector2(2,2));*/
		
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
		TextureRegion currentRegion = getFrame(deltaTime);
		sprite.setRegion(currentRegion);
		if (body.getPosition().y < 0.f) {
			setToDestroy = true;
		}
		
		if(this.active) {
			this.move(this.getDirection(scene.getPlayer()));
		}
		
        if(body.getLinearVelocity().y < 0)  {
			body.setLinearDamping(0);
        }else {
            body.setLinearDamping(12);
        }
	}
	


	public int getDirection(Player player) {
		//enemy manji x dakle true onda se mice enemy u desno
		//inace se mice u ljevo
		int dir=0;
		if(this.sprite.getX()<player.sprite.getX()) dir++;
		else dir--;
		if(this.sprite.getY()<player.sprite.getY()) dir+=3;
		return dir;

	}

	public void move(int direction) {
		this.direction = direction;
		if(direction==-1 || direction==2) {
			moveLeft();
		}
		else if (direction==1 || direction == 4 ){
			moveRight();

		}
		if(direction>=2 && this.getBody().getLinearVelocity().y==0) {
			jump();
		}
		
	}

	public void jump() {
		body.applyLinearImpulse(new Vector2(0, 11f), body.getWorldCenter(), true);
		
	}

	public void moveLeft() {
    	body.applyLinearImpulse(new Vector2(-0.5f, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
	}

	public void moveRight() {
		body.applyLinearImpulse(new Vector2(0.5f, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
	}

	public void onHit() {
		this.hp --;
		if(this.hp<=0)
			setToDestroy = true;
	}

	public void activate() {
		this.active=true;
		System.out.println("RADI");
	}

	public void stop() {
		this.active=false;
		
	}
	
	public TextureRegion getFrame(float deltaTime){

		TextureRegion region = (TextureRegion) slimeIdleAnim.getKeyFrame(stateTimer, true);
        
        if(body.getLinearVelocity().x < 0 && region.isFlipX()){
            region.flip(true, false);
        }
        else if(body.getLinearVelocity().x > 0 && !region.isFlipX()){
            region.flip(true, false);
        }

        stateTimer = stateTimer + deltaTime;
        return region;

    }
}