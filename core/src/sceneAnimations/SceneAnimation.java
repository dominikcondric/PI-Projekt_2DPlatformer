package sceneAnimations;

public abstract class SceneAnimation {
	protected boolean finished = false;
	protected boolean stopScene = false;
	
	public abstract void animate(float deltaTime);
	
	public boolean isFinished() {
		return finished;
	}
	
	public boolean shouldSceneStop() {
		return stopScene;
	}
}
