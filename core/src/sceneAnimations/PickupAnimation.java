package sceneAnimations;

import com.badlogic.gdx.math.Vector2;

import entities.Item;
import entities.Player;

public class PickupAnimation extends SceneAnimation {
	private Item item;
	private Player player;
	
	public PickupAnimation(Item item, Player player) {
		this.item = item;
		this.player = player;
		stopScene = true;
	}

	@Override
	public void animate(float deltaTime) {
		if (Vector2.len2(item.getPosition().x - player.getPosition().x, item.getPosition().y - player.getPosition().y) > 1e-1f) {
			item.setPosition(item.getPosition().add(player.getPosition().sub(item.getPosition()).scl(deltaTime)));
		} else {
			finished = true;
			item.setToDestroy(true);
			player.addItem(item);
		}
	}

}
