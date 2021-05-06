package tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.MassData;

import entities.Enemy;
import entities.Player;
import entities.Projectile;



public class CollisionListener implements ContactListener{

	Vector2 position = new Vector2();
	
	@Override
	public void beginContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if(A == null || B == null || A.getClass() == null || B.getClass() == null) return;
		
		

		if((A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) && (A.isSensor() || B.isSensor())) {
			Fixture sensor;
			Fixture object;
			
			
			
			if(!(A.getUserData() instanceof Player || B.getUserData() instanceof Player)) return;

			if(A.getUserData()instanceof Enemy) {
				sensor = A;
				object = B;
			}
			else {
				sensor = B;
				object = A;
			}			
			
			if(object.getUserData() instanceof Player) {
				((Enemy) sensor.getUserData()).activate();
			}
			
		}
		
		
		if(A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) {
			Fixture enemy;
			Fixture object;

			if(A.getUserData() instanceof Enemy) {
				enemy = A ;
				object=B;
			}else {
				enemy = B;
				object = A;
			}
			
			if(object.getFilterData().categoryBits == 2) {
				return;
			}
			
			if(object.getUserData() instanceof Projectile){

				((Projectile) object.getUserData()).onHit();
				((Enemy) enemy.getUserData()).onHit();
			}
			else if(object.getUserData() instanceof Player && !enemy.isSensor() && !object.isSensor()) {
				((Player) object.getUserData()).onHit(((Enemy) enemy.getUserData()).getBody().getPosition().x);
				if( ((Player) object.getUserData()).getHp() <= 0) ((Enemy) enemy.getUserData()).stop();
				((Enemy) enemy.getUserData()).stop();

			}
			
		}
		
		if(A.getUserData() instanceof Projectile && !B.isSensor()) {
			((Projectile) A.getUserData()).onHit();
		
		}
		if(B.getUserData() instanceof Projectile && !A.isSensor()) {
			((Projectile) B.getUserData()).onHit();
			
		}
		
		if((A.getUserData() instanceof Player || B.getUserData() instanceof Player) && (A.isSensor()) || B.isSensor()) {
			//System.out.println(A.getUserData().toString() + " , " + B.getUserData().toString());
			Fixture sensor;
			Fixture object;
			if(A.getUserData()instanceof Player) {
				sensor = A;
				object = B;
			}
			else {
				sensor = B;
				object = A;
			}
			/*if(sensor.getFilterData().categoryBits == 2 && ((Player)sensor.getUserData()).hasAttacked == false) {
				contact.setEnabled(false);
			}else if(sensor.getFilterData().categoryBits == 2 && ((Player)sensor.getUserData()).hasAttacked == true) {
				contact.setEnabled(true);
			}
			*/
			if(object.getUserData() instanceof Enemy && !object.isSensor()) {
				((Enemy) object.getUserData()).onHit();
			}
			
		}
		
		
		return;
		
	}

	@Override
	public void endContact(Contact contact) {
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		/*
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if(A == null || B == null || A.getClass() == null || B.getClass() == null) return;
		
		if((A.getUserData() instanceof Player || B.getUserData() instanceof Player) ) {
			//System.out.println(A.getUserData().toString() + " , " + B.getUserData().toString());
			Fixture sensor;
			Fixture object;
			if(A.getUserData()instanceof Player) {
				sensor = A;
				object = B;
			}
			else {
				sensor = B;
				object = A;
			}
			
			if(sensor.getFilterData().categoryBits == 2) {
				contact.setEnabled(false);

			}
				
			
			if(object.getUserData() instanceof Enemy ) {
				if(sensor.getFilterData().categoryBits == 2 && ((Player)sensor.getUserData()).hasAttacked == true) {
					contact.setEnabled(true);
					((Enemy) object.getUserData()).onHit();
				}
				
				
			}
			
		}
		*/
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
}
