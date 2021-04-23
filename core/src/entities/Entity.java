package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Entity {
	protected Body playerBody;
	protected Sprite sprite;


	public Sprite getSprite() {
		return sprite;
	}

	public abstract void addToWorld(World world);
	
	public void update(float deltaTime) {
		sprite.setPosition(playerBody.getPosition().x - sprite.getWidth() / 2.f, playerBody.getPosition().y - sprite.getHeight() / 2.f);
	}
		
		
	
	
	

}
