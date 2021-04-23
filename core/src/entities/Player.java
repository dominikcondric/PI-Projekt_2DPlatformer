package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Entity{
	
	
	public Player(World world) {
	
		Texture playerImg = new Texture("player.png");	
		sprite = new Sprite(playerImg);
		sprite.setPosition(0, 256);
		addToWorld(world);
		
	}
	
	public void addToWorld(World world){
		
		
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX()+ sprite.getWidth()/2, sprite.getY() + sprite.getHeight()/2);
		bodyDefinition.type = BodyDef.BodyType.DynamicBody;
		
		playerBody = world.createBody(bodyDefinition);
		
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth(), sprite.getHeight());
		
		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;

		playerBody.createFixture(fdef);
	}
	
	@Override
	public void update() {
		super.update();
		
	}

}
