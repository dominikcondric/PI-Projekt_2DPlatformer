package scenes;

import entities.Enemy;

public class EnemyTrigger extends SceneTrigger {
	private Enemy enemy;	
	
	public EnemyTrigger(final Scene sceneToFollow, Enemy enemy) {
		super(sceneToFollow);
		this.enemy = enemy;
	}
	
	@Override
	public boolean isTriggered() {
		return enemy.isSetToDestroy();
	}

}
