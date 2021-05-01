package scenes;

public abstract class SceneTrigger {
	public final Scene sceneToFollow;
	
	public SceneTrigger(final Scene sceneToFollow) {
		this.sceneToFollow = sceneToFollow;
	}
	
	public abstract boolean isTriggered();
}
