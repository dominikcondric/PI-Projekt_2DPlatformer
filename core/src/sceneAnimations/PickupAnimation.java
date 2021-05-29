package sceneAnimations;

import com.badlogic.gdx.math.Vector2;

import entities.Item;
import entities.Player;

public class PickupAnimation extends SceneAnimation {
	private Item item;
	private Player player;
	private String message;
	
	public PickupAnimation(Item item, Player player, String message) {
		this.item = item;
		this.message = message;
		this.player = player;
		stopScene = true;
		minimumDuration = 3.f;
	}

	@Override
	public void animate(float deltaTime) {
		minimumDuration -= deltaTime;
		if (minimumDuration > 0.f || Vector2.len2(item.getPosition().x - player.getPosition().x, item.getPosition().y - player.getPosition().y) > 1e-1f) {
			item.setPosition(item.getPosition().add(player.getPosition().sub(item.getPosition()).scl(deltaTime)));
			if (dialogueText.isEmpty()) {
				dialogueText.append(message);
			}
		} else {
			finished = true;
			item.setToDestroy(true);
			player.addItem(item);
		}
	}

}
