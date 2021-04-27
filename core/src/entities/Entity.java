package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class Entity {
	protected Body body;
	protected Sprite sprite;

	public Sprite getSprite() {
		return sprite;
	}

	public abstract void addToWorld(World world);
	
	public void update(float deltaTime) {
		sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2.f, body.getPosition().y - sprite.getHeight() / 2.f);
	}
}