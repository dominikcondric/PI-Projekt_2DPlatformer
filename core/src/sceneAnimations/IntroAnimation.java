package sceneAnimations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

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
			player.moveRight();
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
			player.moveRight();
		}
		if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			thinkingDuration = 0.9f;
			messageCounter = 1;
			
		}
	}
}
