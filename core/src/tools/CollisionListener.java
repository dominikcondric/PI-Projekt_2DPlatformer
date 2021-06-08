package tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import entities.Enemy;
import entities.Entity;

public class CollisionListener implements ContactListener {
	public static final int PLAYER_BIT = (1 << 0);
	public static final int ENEMY_BIT = (1 << 1);
	public static final int SOLID_WALL_BIT = (1 << 2);
	public static final int PLATFORM_BIT = (1 << 3);
	public static final int PROJECTILE_BIT = (1 << 4);
	public static final int LIGHT_BIT = (1 << 5);
	public static final int INTERACTABLE_BIT = (1 << 6);
	public static final int OTHERS_BIT = (1 << 7);
	public static final int FIREBALL_BIT = (1 << 8);
	public static final int LEFT_UPPER_ENEMY_SENSOR_BIT = (1 << 9);
	public static final int RIGHT_UPPER_ENEMY_SENSOR_BIT = (1 << 10);
	public static final int COIN_BIT = (1 << 11);
	public static final int ENEMY_VISION_SENSOR_BIT = (1 << 12);
	public static final int LEFT_BOTTOM_ENEMY_SENSOR_BIT = (1 << 13);
	public static final int RIGHT_BOTTOM_ENEMY_SENSOR_BIT = (1 << 14);
	
	@Override
	public void beginContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if (A.getUserData() instanceof Entity) {
			((Entity)A.getUserData()).resolveCollisionBegin(A, B);
		}
		
		if (B.getUserData() instanceof Entity) {
			((Entity)B.getUserData()).resolveCollisionBegin(B, A);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if ((A.getFilterData().categoryBits & ENEMY_BIT) != 0 || (A.getFilterData().categoryBits & INTERACTABLE_BIT) != 0) {
			((Entity)A.getUserData()).resolveCollisionEnd(A, B);
		}
		
		if ((B.getFilterData().categoryBits & ENEMY_BIT) != 0 || (B.getFilterData().categoryBits & INTERACTABLE_BIT) != 0) {
			((Entity)B.getUserData()).resolveCollisionEnd(B, A);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if(A.getUserData() instanceof Enemy) {
			((Entity)A.getUserData()).resolvePreSolve(A, B);
		}
		
		if(B.getUserData() instanceof Enemy ) {
			((Entity)B.getUserData()).resolvePreSolve(B, A);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}
}
