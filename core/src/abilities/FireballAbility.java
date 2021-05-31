package abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import entities.Entity;
import entities.Fireball;
import scenes.Scene;

public class FireballAbility extends Ability {

	public FireballAbility() {
		super(3f);
		sound = Gdx.audio.newSound(Gdx.files.internal("sounds/fireball.wav"));
		hudTexture = new TextureRegion(new Texture(Gdx.files.internal("projectiles/fireball.png")));
		hudTexture.setRegionWidth(35);
		hudTexture.setRegionHeight(28);
	}

	@Override
	public void cast(Scene scene, Entity caster) {
		if (currentCooldownTime == cooldownTime && active) {
			scene.addEntity(new Fireball(caster.getPosition(), caster.getFacingDirection(), 2f, 1f));
			sound.play();
			triggered = true;
		}
	}

}
