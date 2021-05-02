package tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import entities.Enemy;
import entities.Player;
import entities.Projectile;

public class CollisionListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		Fixture enemy;
		Fixture object;
		
		if(A==null || B==null || A.getClass()==null || B.getClass()==null || A.getUserData() instanceof Player || B.getUserData() instanceof Player ) return;
		
		
		if(A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) {

			if(A.getUserData() instanceof Enemy) {
				enemy = A ;
				object=B;
			}
			else {
				enemy = B ;
				object=A;
			}
			
			if(object.getUserData() instanceof Projectile){

				((Projectile) object.getUserData()).onHit();
				((Enemy) enemy.getUserData()).onHit();
			}	
			return;
		}
		
		if(A.getUserData() instanceof Projectile) {
			((Projectile) A.getUserData()).onHit();
			return;
		}
		if(B.getUserData() instanceof Projectile) {
			((Projectile) B.getUserData()).onHit();
			return;
		}

		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
