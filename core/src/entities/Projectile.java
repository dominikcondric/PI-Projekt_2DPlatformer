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

public class Projectile extends Entity {
	private float stateTime = 0.f;
	private boolean firedRight;
	
	public Projectile(float playerX, float playerY, boolean firedRight) {
		this.firedRight = firedRight;
		Texture projectileImg = new Texture("projectile.png");	
		sprite = new Sprite(projectileImg);
		
		if (firedRight) {
			sprite.setX(playerX + 1.8f);
		} else {
			sprite.setX(playerX - 0.3f);
		}
		
		sprite.setY(playerY + 0.75f);
		sprite.setSize(0.3f, 0.3f);
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
	
	public void update(float deltaTime) {
		super.update(deltaTime);
		//stateTime += deltaTime;
		if(body.getLinearVelocity().y < 0) {
			body.setLinearVelocity(new Vector2(body.getLinearVelocity().x,0));
		}
		
		if(stateTime > 2.f || body.getLinearVelocity().x == 0.f) {
            setToDestroy = true;
        }
	}
    
    public void onHit() {
    	System.out.println("onhitprojectile");
    	setToDestroy = true;
    }
}
