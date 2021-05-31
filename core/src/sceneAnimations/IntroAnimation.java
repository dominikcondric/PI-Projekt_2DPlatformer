package sceneAnimations;

import entities.Player;

public class IntroAnimation extends SceneAnimation {
	private Player player;
	private float thinkingDuration = 4.f;
	private int messageCounter = 0;
	
	public IntroAnimation(final Player player) {
		this.player = player;
		this.stopScene = true;
	}

	@Override
	public void animate(float deltaTime) {
		if (player.getPosition().x < 2.5f) {
			player.getBody().setLinearVelocity(3f, 0f);
		} else if (thinkingDuration > 0.f) {
			thinkingDuration -= deltaTime;
			if (thinkingDuration > 3.f && messageCounter == 0) {
				dialogueText.append("This is it.\n");
				++messageCounter;
			} else if (thinkingDuration < 1.f && messageCounter == 1) {
				dialogueText.append("Can't go back now...");
				++messageCounter;
			}
		} else {
			player.getBody().setLinearVelocity(6f, 0f);
		}
	}
}
