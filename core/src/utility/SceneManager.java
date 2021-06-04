package utility;

import java.util.HashMap;
import java.util.function.BiConsumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

import entities.Player;
import scenes.Scene;
import scenes.SceneTrigger;

public class SceneManager implements Disposable {
	private Scene activeScene = null;
	private HashMap<String, Scene> sceneMap;
	private boolean transitionTriggered;
	private float timer = 2.3f;
	private Scene nextScene = null;
	private ShapeRenderer transitionRenderer;
	
	public SceneManager() {
		sceneMap = new HashMap<String, Scene>(2);
		transitionRenderer = new ShapeRenderer();
		transitionRenderer.setColor(new Color(0.f, 0.f, 0.f, 0.f));
	}
	
	/**
	 * Adds scene to scene map and sets it to active if setActive is true, or if it
	 * is the first scene in the map.
	 * @param scene
	 * @param sceneName
	 * @param setActive
	*/
	public void addScene(Scene scene, String sceneName, boolean setActive) {
		if (sceneMap.containsKey(sceneName)) {
			System.out.println("Scene name already used!");
			return;
		}
		
		sceneMap.put(sceneName, scene);
		
		if (setActive || sceneMap.isEmpty()) {
			if (activeScene != null) 
				activeScene.stopMusic(false);
			
			activeScene = scene;
			activeScene.playMusic();
		}
	}
	
	public boolean isTransitionTriggered() {
		return transitionTriggered;
	}
	
	public Scene getActiveScene() {
		return activeScene;
	}
	
	public void setActiveScene(String sceneName) {
		if (sceneName != null && sceneMap.containsKey(sceneName)) {
			activeScene = sceneMap.get(sceneName);
			activeScene.playMusic();
		} else {
			System.out.println("Scene doesn't exist!");
		}
	}
	
	private void fadeInTransition() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		transitionRenderer.updateMatrices();
		transitionRenderer.begin(ShapeType.Filled);
		transitionRenderer.rect(0.f, 0.f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		transitionRenderer.end();
	}
	
	public void update(float deltaTime) {
		if (transitionTriggered) {
			if (timer <= 0.f) {
				transitionTriggered = false;
			} else if (timer <= 2.f) {
				transitionRenderer.getColor().a = timer / 2.f;
				if (nextScene != null) {
					activeScene.stopMusic(false);
					Player p = activeScene.getPlayer();
					activeScene = nextScene;
					activeScene.addEntity(p);
					activeScene.playMusic();
					nextScene = null;
				}
			} else {
				transitionRenderer.getColor().a = 1.f - (timer / 4.f);
			}
			
			timer -= deltaTime;
			fadeInTransition();
		} else {
			for (SceneTrigger trigger : activeScene.getTriggers()) {
				if (trigger.isTriggered()) {
					nextScene = trigger.sceneToFollow;
					transitionTriggered = true;
					timer = 3.f;
					transitionRenderer.getColor().a = 0.f;
				}
			}
		}
	}
	
	@Override
	public void dispose() {
		sceneMap.forEach(new BiConsumer<String, Scene>() {

			@Override
			public void accept(String t, Scene u) {
				u.dispose();
			}
		});
		
		sceneMap.clear();
	}
}
