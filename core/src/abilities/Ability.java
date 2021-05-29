package abilities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import entities.Entity;
import scenes.Scene;

public abstract class Ability {
	protected float cooldownTime;
	protected float currentCooldownTime;
	protected boolean triggered = false;
	protected TextureRegion hudTexture;
	public boolean active = false;
	
	protected Ability(float cooldownTime) {
		this.cooldownTime = cooldownTime;
		currentCooldownTime = cooldownTime;
	}

	public void update(float deltaTime) {
		if (triggered || currentCooldownTime != cooldownTime) {
			triggered = false;
			currentCooldownTime -= deltaTime;
			if (currentCooldownTime < 0.f) {
				currentCooldownTime = cooldownTime;
			}
		}
	}
	
	public abstract void cast(final Scene scene, Entity caster);
	
	public TextureRegion getHudTextureRegion() {
		return hudTexture;
	}
	
	public float getCooldownTime() {
		return cooldownTime;
	}
	
	public float getCurrentCooldownTime() {
		return currentCooldownTime;
	}
	
	public boolean isTriggered() {
		return triggered && active;
	}
}
