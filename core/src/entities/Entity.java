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
	protected boolean setToDestroy = false;
	protected boolean facingRight = true;
	
	protected Entity(Vector2 position) {
		sprite = new Sprite();
		sprite.setPosition(position.x, position.y);
	}
	
	public abstract void addToWorld(World world);
	
	public abstract void resolveCollision(Fixture self, Fixture other);

	public Vector2 getPosition() {
		return new Vector2(sprite.getX(), sprite.getY());
	}
	
	public void destroyBody(World world) {
		if (body != null) world.destroyBody(body);
	}
	
	public boolean getFacingDirection() {
		return facingRight;
	}
	
	public void render(SpriteBatch batch) {
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
	}
	
	public void setPosition(Vector2 position) {
		sprite.setPosition(position.x, position.y);
		if (body != null) body.setTransform(position, 0);
	}
	
	public void update(final Scene scene, float deltaTime) {
		if (body != null) sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2.f, body.getPosition().y - sprite.getHeight() / 2.f);
		setToDestroy = false;
	}
	
	public boolean isSetToDestroy() {
		return setToDestroy;
	}
	
	public void setToDestroy(boolean destroy) {
		setToDestroy = destroy;
	}
}