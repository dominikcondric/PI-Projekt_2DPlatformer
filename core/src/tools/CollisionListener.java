package tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import entities.Chest;
import entities.Entity;

public class CollisionListener implements ContactListener {
	@Override
	public void beginContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if (A.getUserData() instanceof Entity) {
			((Entity)A.getUserData()).resolveCollision(A, B);
		}
		
		if (B.getUserData() instanceof Entity) {
			((Entity)B.getUserData()).resolveCollision(B, A);
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if (A.getUserData() instanceof Chest) {
			((Entity)A.getUserData()).resolveCollision(A, B);
		}
		
		if (B.getUserData() instanceof Chest) {
			((Entity)A.getUserData()).resolveCollision(B, A);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
