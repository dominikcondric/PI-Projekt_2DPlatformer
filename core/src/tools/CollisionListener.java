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
import entities.Fireball;



public class CollisionListener implements ContactListener{

	Vector2 position = new Vector2();
	
	@Override
	public void beginContact(Contact contact) {
		Fixture A = contact.getFixtureA();
		Fixture B = contact.getFixtureB();
		
		if(A == null || B == null || A.getClass() == null || B.getClass() == null) return;
		

		if((A.getUserData() instanceof Enemy || B.getUserData() instanceof Enemy) && (A.isSensor() || B.isSensor())) {
			Fixture enemyorsensor;
			Fixture object;
			
			if(A.getUserData() instanceof Enemy) {
				enemyorsensor = A;
				object = B;
			}
			else {
				enemyorsensor = B;
				object = A;
			}			
			
			//kontakt playera i enemy visiona
			//posto ne postoji senzor s player objektom u userdata to znaci da enemy u ovom slucaju mora biti senzor a player mora biti sam hitbox playera
			if(object.getUserData() instanceof Player) {
				((Enemy) enemyorsensor.getUserData()).activate();
				return;
			}

			//kontakt meleenapada i enemya
			if(object.getUserData()=="meleehitbox")	{
				if(enemyorsensor.getUserData() instanceof Enemy && !enemyorsensor.isSensor()) {
					((Enemy) enemyorsensor.getUserData()).onHit();
				}
			}
			return;
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
			
			//kontakt enemy i projectile
			if(object.getUserData() instanceof Fireball){
				((Fireball) object.getUserData()).onHit();
				((Enemy) enemy.getUserData()).onHit();
				return;
			}
			//kontakt player i tijelo enemya, damage playera
			//nema potrebe provjeravati je li enemy senzor jer ako je onda nikada nece doci do ovog if
			if(object.getUserData() instanceof Player) {
				((Player) object.getUserData()).onHit(((Enemy) enemy.getUserData()).getBody().getPosition().x);
				if( ((Player) object.getUserData()).getHp() <= 0) ((Enemy) enemy.getUserData()).stop();
			}
			return;
		}
		
		if(A.getUserData() instanceof Fireball && !B.isSensor()) {
			((Fireball) A.getUserData()).onHit();
			return;
		}
		if(B.getUserData() instanceof Fireball && !A.isSensor()) {
			((Fireball) B.getUserData()).onHit();
			return;
		}
				
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
