package utility;

import java.util.HashMap;
import java.util.function.BiConsumer;

import com.badlogic.gdx.utils.Disposable;

import entities.Player;
import scenes.Scene;
import scenes.SceneTrigger;

public class SceneManager implements Disposable {
	
	private Scene activeScene = null;
	protected HashMap<String, Scene> sceneMap;
	
	public SceneManager() {
		sceneMap = new HashMap<String, Scene>(2);
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
	
	public void update() {
		for (SceneTrigger trigger : activeScene.getTriggers()) {
			if (trigger.isTriggered()) {
				activeScene.stopMusic(false);
				Player p = activeScene.getPlayer();
				activeScene = trigger.sceneToFollow;
				activeScene.addEntity(p);			
				activeScene.playMusic();
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
