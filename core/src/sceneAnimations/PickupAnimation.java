package sceneAnimations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import entities.Item;
import entities.Player;

public class PickupAnimation extends SceneAnimation {
	private Item item;
	private Player player;
	private String message;
	private boolean isPlaying = false;
	Sound pickup = Gdx.audio.newSound(Gdx.files.internal("sounds/item.wav"));
	
	public PickupAnimation(Item item, Player player, String message) {
		this.item = item;
		this.message = message;
		this.player = player;
		stopScene = true;
		minimumDuration = 1.f;
	}

	@Override
	public void animate(float deltaTime) {
		minimumDuration -= deltaTime;
		if (minimumDuration > 0.f) {
			if(Vector2.len2(item.getPosition().x - player.getPosition().x, item.getPosition().y - player.getPosition().y) > 1e-1f) {
				item.setPosition(item.getPosition().add(player.getPosition().sub(item.getPosition()).nor().scl(deltaTime * 2.f)));
			} else if (!isPlaying) {
				pickup.play();
				isPlaying = true;
				if (dialogueText.isEmpty()) {
					dialogueText.append(message + "\nPress Enter to continue...");
				}
			}
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			finished = true;
			item.disappear();
			player.addItem(item);
		}
	}
}
