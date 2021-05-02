package scenes;

import entities.Player;

public class OutOfMapTrigger extends SceneTrigger {
	private Player player;
	
	public OutOfMapTrigger(final Scene sceneToFollow, final Player p) {
		super(sceneToFollow);
		this.player = p;
	}
	
	@Override
	public boolean isTriggered() {
		if (player.getBody().getPosition().y < 0.f) {
			return true;
		}
		
		return false;
	}

}
