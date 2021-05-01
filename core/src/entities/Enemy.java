package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;

public class Enemy extends Entity {
	
	protected int hp=5;

	public Enemy() {
		Texture playerImg = new Texture("player.png");	
		sprite = new Sprite(playerImg);
		sprite.setPosition(5.f, 8.f);
		sprite.setSize(1.f, 1.5f);
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
		polShape.dispose();
	}
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
	}
	
	public void onHit() {
		this.hp --;
		if(this.hp<=0)
			setToDestroy = true;
		System.out.print(this.hp);
	}
}