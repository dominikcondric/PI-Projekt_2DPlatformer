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

		
		if(A==null || B==null || A.getClass()==null || B.getClass()==null) return;
		
		
		
		if((A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) && (A.isSensor() || B.isSensor())) {
			Fixture sensor;
			Fixture object;
			
			
			if(!(A.getUserData() instanceof Player || B.getUserData() instanceof Player)) return;

			if(A.getUserData()instanceof Enemy) {
				sensor = A ;
				object=B;
			}
			else {
				sensor = B ;
				object=A;
			}
			
			
			if(object.getUserData() instanceof Player) {
				((Enemy) sensor.getUserData()).activate();
			}
			return;
		}
		
		
		if(A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) {
			Fixture enemy;
			Fixture object;

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
			if(object.getUserData() instanceof Player) {
				((Player) object.getUserData()).onHit();
				((Enemy) enemy.getUserData()).stop();

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