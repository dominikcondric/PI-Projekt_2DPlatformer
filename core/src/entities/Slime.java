package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


import scenes.Scene;

public class Slime extends Enemy {
	
	public Slime(Vector2 position) {
		super(position);		
		atlas = new TextureAtlas(Gdx.files.internal("slimesprites\\idle_slime.atlas"));
		idle = new TextureRegion(atlas.findRegion("idle_slime01"), 0, 0, 19, 18);
		for(int i = 0; i < 6; i++) {
			idleFrames.add(new TextureRegion(atlas.findRegion("idle_slime01"), i * 23+5 , 0, 19, 18 ));
		}
		idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);
		sprite.setRegion(idle);
		sprite.setSize(0.9f, 0.9f);
		sprite.setScale(2f, 2f);
		

		movespeed=0.3f;
		jumpheight=11f;

	}
	
	@Override
	public void update(final Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
	}

	@Override
	public void move(int direction) {
		this.direction = direction;
		if(direction==-1 || direction==2 && this.body.getLinearVelocity().y>=0) {
			moveLeft();
		}
		else if (direction==1 || direction == 4 && this.body.getLinearVelocity().y>=0 ){
			moveRight();
		}
		if(direction>=2 && this.body.getLinearVelocity().y==0) {
			jump();
		}		
	}




}
