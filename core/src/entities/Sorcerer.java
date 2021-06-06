package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import tools.CollisionListener;

public class Sorcerer extends Enemy {

	public Sorcerer(Vector2 position) {
		super(position);
		sprite.setRegion(new TextureRegion(new Texture(Gdx.files.internal("sorcerersprites/wizard.png"))));
		sprite.setSize(0.8f, 1f);
		activeAI=true;
		facingRight=false;
		moveSpeed=0.3f;
		jumpHeight=0f;
	}

	@Override
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();

		polShape.setAsBox(0.8f/2, 1f/2);
		
		FixtureDef fdef = new FixtureDef();
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT & ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		fdef.shape = polShape;
		

		body.createFixture(fdef).setUserData(this);
		body.setGravityScale(0);
		
		PolygonShape vision = new PolygonShape();
		vision.setAsBox(visionLength, visionHeight, new Vector2(0,visionHeight-(sprite.getHeight()/2)), 0);
		
		fdef.shape = vision;
		fdef.isSensor=true;
		fdef.filter.categoryBits = CollisionListener.ENEMY_BIT;
		fdef.filter.maskBits = 0xFF & ~CollisionListener.LIGHT_BIT | ~CollisionListener.INTERACTABLE_BIT;
		fdef.filter.groupIndex = -CollisionListener.ENEMY_BIT;
		body.createFixture(fdef).setUserData(this);

	}

	@Override
	public void move(int direction) {
		

	}

}
