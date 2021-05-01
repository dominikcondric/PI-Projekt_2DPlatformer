package scenes;

import entities.Enemys;

public class EnemyTrigger extends SceneTrigger {
	private Enemys enemy;	
	
	public EnemyTrigger(final Scene sceneToFollow, Enemys enemy) {
		super(sceneToFollow);
		this.enemy = enemy;
	}
	
	@Override
	public boolean isTriggered() {
		return enemy.isSetToDestroy();
	}

}
