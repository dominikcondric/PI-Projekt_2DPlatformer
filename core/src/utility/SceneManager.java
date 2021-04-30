package utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.function.BiConsumer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import scenes.Scene;

public class SceneManager implements Disposable {

	private class Pair<T1, T2> {
		public T1 first;
		public T2 second;
		
		public Pair(T1 first, T2 second) {
			this.first = first;
			this.second = second;
		}
	}
	
	private Pair<Scene, ArrayList<String>> activeScene = null;
	protected HashMap<String, Pair<Scene, ArrayList<String>>> sceneMap;
	
	public SceneManager() {
		sceneMap = new HashMap<String, Pair<Scene, ArrayList<String>>>(2);
	}
	
	/**
	 * Adds *next scene* that could be loaded after *previous scene*
	 * @param previousScene
	 * @param nextScene
	*/
	public void addSceneToFollow(String previousScene, String nextScene) {
		if (previousScene != null && nextScene != null && sceneMap.containsKey(previousScene) && sceneMap.containsKey(nextScene)) {
			sceneMap.get(previousScene).second.add(nextScene);
		} else {
			System.out.println("Scene names either null or doesn't exist!");
		}
	}
	
	/**
	 * Adds scene to scene map and sets it to active if setActive is true, or if it
	 * is the first scene in the map.\n
	 * WARNING!!! - If scene name already exist, new name to fetch scene is changed and shown in console!
	 * @param scene
	 * @param sceneName
	 * @param setActive
	*/
	public void addScene(Scene scene, boolean setActive) {
		if (sceneMap.containsKey(scene.getName())) {
			System.out.println("Scene name already used!");
			return;
		}
		
		Pair<Scene, ArrayList<String>> newScene = new Pair<Scene, ArrayList<String>>(scene, new ArrayList<String>(2));
		sceneMap.put(scene.getName(), newScene);
		
		if (setActive || sceneMap.isEmpty()) {
			activeScene = newScene;
		}
	}
	
	public Scene getActiveScene() {
		return activeScene.first;
	}
	
	public void setActiveScene(String sceneName) {
		if (sceneName != null && sceneMap.containsKey(sceneName)) {
			activeScene = sceneMap.get(sceneName);
		} else {
			System.out.println("Scene doesn't exist!");
		}
	}
	
	public void update() {
		for (String sceneName : activeScene.second) {
			if (activeScene.first.shouldTransit(sceneName)) {
				Pair<Scene, ArrayList<String>> nextScene = sceneMap.get(sceneName);
				nextScene.first.transitFromScene(activeScene.first);
				activeScene = sceneMap.get(sceneName);
			}
		}
	}
	
	@Override
	public void dispose() {
		sceneMap.forEach(new BiConsumer<String, Pair<Scene, ArrayList<String>>>() {

			@Override
			public void accept(String t, Pair<Scene, ArrayList<String>> u) {
				// TODO Auto-generated method stub
				u.first.dispose();
				u.second.clear();
			}
		});
		
		sceneMap.clear();
	}
}
