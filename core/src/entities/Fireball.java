package entities;

import com.badlogic.gdx.Gdx;
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

public class Fireball extends Entity {
	private boolean firedRight;
	private Animation<TextureRegion> flying;
	private float stateTimer;
	private TextureAtlas atlas;
	
	public Fireball(Vector2 entityPosition, boolean firedRight) {
		super(entityPosition);
		this.firedRight = firedRight;
		
		atlas = new TextureAtlas(Gdx.files.internal("projectiles\\fireball.atlas"));
		TextureRegion projectileImage = new TextureRegion(atlas.findRegion("FB001"),0 , 0, 35, 17);
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for(int i = 0; i < 5; i++) {
			frames.add(new TextureRegion(atlas.findRegion("FB001"),i * 37 , 0, 35, 17));
		}
		
		flying = new Animation<TextureRegion>(0.1f, frames);
		
		sprite.setRegion(projectileImage);
		
		if (firedRight) {
			sprite.setX(entityPosition.x + 1f);
		} else {
			sprite.flip(true, false);
			sprite.setX(entityPosition.x - 1f);
		}
		
		sprite.setY(entityPosition.y + 0.07f);
		sprite.setSize(0.8f, 0.4f);
	}

	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX(), sprite.getY());
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() / 2.f , sprite.getHeight() / 2.f );
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;

				
		this.body.createFixture(fdef).setUserData(this);
		
		fdef.friction = 0;
		fdef.restitution = 1;
		
		body.setGravityScale(0);
		
		if (firedRight)
			body.setLinearVelocity(new Vector2(10f, 0f));
		else
			body.setLinearVelocity(new Vector2(-10f, 0f));
		
		polShape.dispose();
	}
	
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		sprite.setRegion(getFrame(deltaTime));
		if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,0));
		}
		
		if(body.getLinearVelocity().x == 0.f) {
            setToDestroy = true;
        }
	}
    
    private void onHit() {
    	setToDestroy = true;
    }
    
    public TextureRegion getFrame(float deltaTime){

        TextureRegion region = (TextureRegion) flying.getKeyFrame(stateTimer, true);
        if((body.getLinearVelocity().x < 0 || !firedRight) && !region.isFlipX()){
            region.flip(true, false);
            firedRight = false;
        }

        else if((body.getLinearVelocity().x > 0 || firedRight) && region.isFlipX()){
            region.flip(true, false);
            firedRight = true;
        }
        
        stateTimer = stateTimer + deltaTime;
        return region;
    }

	@Override
	public void resolveCollision(Fixture self, Fixture other) {
		// TODO Auto-generated method stub
		if (!other.isSensor()) {
			onHit();
		}
	}
	
	@Override
	public void resolveCollisionEnd(Fixture A, Fixture B) {
	}
	
	@Override
	public void resolvePreSolve(Fixture A, Fixture B) {
		// TODO Auto-generated method stub
		
	}
}
