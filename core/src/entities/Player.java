package entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Entity {
	
	private final int MOVE_THRESHOLD_LEFT = -6;
	private final int MOVE_THRESHOLD_RIGHT = 6;
	private final int ON_GROUND = 0;
	
	public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD };
	public boolean runningRight = true;
	public ArrayList<Projectile> projectiles = new ArrayList<Projectile>(); 
	
	public Player(World world) {
		Texture playerImg = new Texture("player.png");	
		sprite = new Sprite(playerImg);
		sprite.setPosition(2.f, 8.f);
		sprite.setSize(1.f, 1.5f);
		addToWorld(world);
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

		this.body.createFixture(fdef).setUserData(this);;
		polShape.dispose();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		Vector2 playerVelocity = body.getLinearVelocity();
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && playerVelocity.y == ON_GROUND) {
			System.out.println(sprite.getX());
			jump();
		}	

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && playerVelocity.x <= MOVE_THRESHOLD_RIGHT) {
        	moveRight();
        }	
        
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && playerVelocity.x >= MOVE_THRESHOLD_LEFT) {
        	moveLeft();
        }
        
        if(playerVelocity.y != ON_GROUND) {
        	body.setLinearDamping(0);
        }else {
        	body.setLinearDamping(12);
        }
        
	}
	
	
	public void jump() {
		body.applyLinearImpulse(new Vector2(0, 11f), body.getWorldCenter(), true);
	}
	
	public void moveRight() {
		body.applyLinearImpulse(new Vector2(3.5f, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
    	runningRight = true;
	}
	public void moveLeft() {
    	body.applyLinearImpulse(new Vector2(-3.5f, 0), body.getWorldCenter(), true);
    	body.setLinearDamping(12);
    	runningRight = false;
	}
	
	public void basicattack() {
		
	}

}