package abilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import entities.Entity;
import entities.Player;
import entities.Shield;
import scenes.Scene;

public class ShieldAbility extends Ability {
	private Shield shield = null;
	
	public ShieldAbility() {
		super(20.f);
		hudTexture = new TextureRegion(new Texture(Gdx.files.internal("bubble_3.png")));
	}
	
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (shield != null && shield.isSetToDestroy()) {
			shield = null;
		}
	}
	
	@Override
	public void cast(Scene scene, Entity caster) {
		if (currentCooldownTime == cooldownTime && active) {
			if (shield == null) {
				shield = new Shield(caster.getPosition(), (Player)caster);
				scene.addEntity(shield);
			} else {
				shield.reset(null);
			}
			
			triggered = true;
		}
	}
}
