package scenes;

import entities.Player;

public class PlayerLocationTrigger extends SceneTrigger {
	private Player player;
	
	public PlayerLocationTrigger(final Scene sceneToFollow, final Player p) {
		super(sceneToFollow);
		this.player = p;
	}
	
	@Override
	public boolean isTriggered() {
		if (player.getBody().getPosition().x > 20.f && !player.isSetToDestroy()) {
			player.getSprite().setPosition(2.f, 8.f);
			sceneToFollow.addEntity(player);
			return true;
		}
		
		return false;
	}

}
