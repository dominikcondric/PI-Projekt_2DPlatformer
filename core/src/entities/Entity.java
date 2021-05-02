package entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;

public abstract class Entity {
	protected Body body;
	protected Sprite sprite;
	protected boolean setToDestroy = false;
	protected TextureAtlas atlas;
	
	protected Entity(Vector2 position) {
		sprite = new Sprite();
		sprite.setPosition(position.x, position.y);
	}
	
	public abstract void addToWorld(World world);

	public void setPosition(float x, float y) {
		sprite.setPosition(x, y);
	}
	
	public Body getBody() {
		return body;
	}
	
	public void render(SpriteBatch batch) {
		batch.draw(sprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
	}
	
	public void update(final Scene scene, float deltaTime) {
		sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2.f, body.getPosition().y - sprite.getHeight() / 2.f);
		setToDestroy = false;
	}
	
	public boolean isSetToDestroy() {
		return setToDestroy;
	}
	
	public void setToDestroy(boolean destroy) {
		setToDestroy = destroy;
	}
}