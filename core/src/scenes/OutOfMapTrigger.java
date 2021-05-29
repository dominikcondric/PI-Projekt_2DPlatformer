package scenes;

import com.badlogic.gdx.math.Vector2;

import entities.Player;

public class OutOfMapTrigger extends SceneTrigger {
	private Player player;
	private Vector2 mapLimit;
	private boolean after;
	
	public OutOfMapTrigger(final Scene sceneToFollow, final Player p, Vector2 mapLimit, boolean after) {
		super(sceneToFollow);
		this.mapLimit = mapLimit;
		this.player = p;
		this.after = after;
	}
	
	@Override
	public boolean isTriggered() {
		if (after) {
			if (player.getPosition().x > mapLimit.x || player.getPosition().y > mapLimit.y) {
				return true;
			}
		} else {
			if (player.getPosition().x < mapLimit.x || player.getPosition().y < mapLimit.y) {
				return true;
			}
		}
		
		return false;
	}

}
