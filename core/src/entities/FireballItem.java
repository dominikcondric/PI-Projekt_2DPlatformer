package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

import sceneAnimations.PickupAnimation;
import scenes.Scene;

public class FireballItem extends Item {
	
	public FireballItem(Vector2 position) {
		super(position);
		sprite.setRegion(new Texture(Gdx.files.internal("projectiles/fireball.png")));
		sprite.setSize(0.5f, 0.3f);
		sprite.setRegionWidth(35);
		sprite.setRegionHeight(28);
	}

	@Override
	public void appear() {
		sprite.translate(0, 0.4f);
		triggerPickup = true;
	}

	@Override
	public void addToWorld(World world) {

	}
	
	@Override
	public void update(Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if (triggerPickup) {
			String message = "Wooow, You just found a fireball\n To use it tap F.";
			scene.beginAnimation(new PickupAnimation(this, scene.getPlayer(), message));
			scene.getPlayer().getAbilityList().get(0).active = true;
		}
		
		triggerPickup = false;
	}
}
