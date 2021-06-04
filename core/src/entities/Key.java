package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import sceneAnimations.PickupAnimation;
import scenes.Scene;

public class Key extends Item {
	private String keyID;

	public Key(Vector2 position, String keyID) {
		super(position);
		this.keyID = keyID;
		sprite.setRegion(new TextureRegion(new Texture(Gdx.files.internal("items/key.png"))));
		sprite.setSize(0.5f, 0.5f);
	}

	@Override
	public void appear() {
		sprite.translate(0, 0.4f);
		triggerPickup = true;
	}
	
	public String getKeyID() {
		return keyID;
	}

	@Override
	public void update(Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if (triggerPickup) {
			String message = "You just found a key. Wondering what it unlocks...";
			scene.beginAnimation(new PickupAnimation(this, scene.getPlayer(), message));
		}
		
		triggerPickup = false;
	}

	@Override
	public void addToWorld(World world) {
		
	}
}
