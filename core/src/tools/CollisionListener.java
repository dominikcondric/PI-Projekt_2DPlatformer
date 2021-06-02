package tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import entities.Chest;
import entities.Enemy;
import entities.Entity;
import entities.Slime;

public class CollisionListener implements ContactListener {
	
	
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
		
		if (A.getUserData() instanceof Chest) {
			((Entity)A.getUserData()).resolveCollisionEnd(A, B);
			}
		
		if (B.getUserData() instanceof Chest) {
			((Entity)A.getUserData()).resolveCollisionEnd(B, A);
		}
		
		if(A.getUserData() instanceof Enemy) {
			((Entity)A.getUserData()).resolveCollisionEnd(A, B);
		}
		
		if(B.getUserData() instanceof Enemy) {
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
