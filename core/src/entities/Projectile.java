package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pools;
import com.platformer.Platformer;

public class Projectile extends Entity {
	
	/*boolean destroy;
	boolean destroyed;*/
	boolean firedRight;
	//float stateTime;
	World world;
	
	public Projectile(World world, float playerX, float playerY, boolean firedRight) {
		this.world = world;
		this.firedRight = firedRight;
		
		Texture projectileImg = new Texture("projectile.png");	
		sprite = new Sprite(projectileImg);
		sprite.setPosition(playerX, playerY);
		sprite.setSize((float)0.4, (float)0.4);
		
		addToWorld(world);
		
	}

	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set((float) ((float) (firedRight ? (float)(sprite.getX() + sprite.getWidth() / 2.f) + 1.6 :(float) (sprite.getX() + sprite.getWidth() / 2.f)) - 0.5), (float) (sprite.getY() + sprite.getHeight() + 0.5) );
		//bodyDefinition.position.set((float) (firedRight ? sprite.getX() : sprite.getX() ), (float) (sprite.getY()) );
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
		body.setLinearVelocity(new Vector2(firedRight ? 20f : -20f,0));
		
		
		polShape.dispose();
		
	}
	
	public void update(float deltaTime) {
		super.update(deltaTime);
		//stateTime += deltaTime;
		if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,0));
		}
		
		/*if((stateTime > 2 || destroy) && !destroyed ) {
            world.destroyBody(body);
            destroyed = true;
        }*/
		
		/*if((firedRight && body.getLinearVelocity().x < 0) || (!firedRight && body.getLinearVelocity().x > 1))
            setToDestroy();*/
		
	}
	
	/*public void setToDestroy(){
        destroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }*/
    
    public void onHit() {
    	System.out.println("onhitprojectile");
    	this.remove();
    }

}
