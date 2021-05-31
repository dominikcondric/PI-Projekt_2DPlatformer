package sceneAnimations;

import com.badlogic.gdx.utils.StringBuilder;

public abstract class SceneAnimation {
	protected boolean finished = false;
	protected boolean stopScene = false;
	protected float minimumDuration;
	protected StringBuilder dialogueText = new StringBuilder();
	
	public abstract void animate(float deltaTime);
	
	public String getDialogueText() {
		return dialogueText.toString();
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public boolean shouldSceneStop() {
		return stopScene;
	}
}
