package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;

public class Enemy extends Entity {
	
	protected int hp=5;
	protected float visionHeight=3f;
	protected float visionLength=4f;
	protected boolean active=false;

	public Enemy() {
		Texture playerImg = new Texture("player.png");	
		sprite = new Sprite(playerImg);
		/*sprite.setPosition(5.f, 8.f);
		sprite.setSize(1.f, 1.5f);*/
		sprite.setBounds(5.f, 8.f, 1f, 1.5f);

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
		this.body.createFixture(fdef).setUserData("sensor");
		
		
		polShape.dispose();
	}
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if(this.active) {
			this.move(this.getDirection(scene.getPlayer()));
		}
	}
	


	public boolean getDirection(Player player) {
		//enemy manji x dakle true onda se mice enemy u desno
		//inace se mice u ljevo
		if(this.getSprite().getX()<player.getSprite().getX()) return true;
		return false;
	}

	public void move(boolean direction) {
		if(direction) {
			moveRight();
		}
		else {
			moveLeft();
		}
		
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
		System.out.print(this.hp);
	}

	public void activate() {
		this.active=true;
		System.out.println("RADI");
	}

	public void stop() {
		this.active=false;
		
	}
}