package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;

public abstract class Entity {
	protected Body body;
	protected Sprite sprite;
	protected boolean active = true;
	protected boolean setToDestroy = false;
	protected boolean facingRight = true;
	protected Vector2 initialPosition;
	
	protected Entity(Vector2 position) {
		sprite = new Sprite();
		sprite.setPosition(position.x, position.y);
		this.initialPosition = position;
	}
	
	public abstract void addToWorld(World world);
	
	public void reset(World world) {
		active = true;
		sprite.setPosition(initialPosition.x, initialPosition.y);
		if (body != null) {
			destroyBody(world);
			addToWorld(world);
		}
	}

	public Vector2 getPosition() {
		return new Vector2(sprite.getX(), sprite.getY());
	}
	
	public void destroyBody(World world) {
		if (body != null) 
			world.destroyBody(body);
	}
	
	public boolean getFacingDirection() {
		return facingRight;
	}
	
	public void render(SpriteBatch batch) {
		if (active)
			batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
	}
	
	public void setPosition(Vector2 position) {
		sprite.setPosition(position.x, position.y);
		if (body != null) 
			body.setTransform(position, 0);
	}
	
	public void update(final Scene scene, float deltaTime) {
		if (body != null) {
			sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2.f, body.getPosition().y - sprite.getHeight() / 2.f);
			if (body.getPosition().y < 0.f) {
				active = false;
			}
			body.setActive(active);
		}
	}
	
	public boolean isSetToDestroy() {
		return setToDestroy;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void resolveCollisionEnd(Fixture A, Fixture B) {}
	public void resolveCollisionBegin(Fixture self, Fixture other) {}
	public void resolvePreSolve(Fixture A, Fixture B) {}
}