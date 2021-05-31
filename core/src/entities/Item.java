package entities;

import com.badlogic.gdx.math.Vector2;

public abstract class Item extends Entity {
	protected boolean triggerPickup = false;
	
	protected Item(Vector2 position) {
		super(position);
	}
	
	public abstract void appear();
}
