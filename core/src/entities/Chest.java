package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import scenes.Scene;

public class Chest extends Entity {
	private Item item;
	private Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/chest.wav"));
	
	private boolean openable = false;
	private boolean opened = false;

	public Chest(Vector2 position, Item item) {
		super(position);
		this.item = item;
		sprite.setRegion(new TextureRegion(new Texture(Gdx.files.internal("items/chest.png")), 0, 0, 20, 20));
		sprite.setSize(0.7f, 0.5f);
	}

	@Override
	public void addToWorld(World world) {
		BodyDef bodyDefinition = new BodyDef();
		bodyDefinition.position.set(sprite.getX() + sprite.getWidth() / 2.f, sprite.getY() + sprite.getHeight() / 2.f);
		bodyDefinition.type = BodyDef.BodyType.StaticBody;
		
		this.body = world.createBody(bodyDefinition);
		
		PolygonShape polShape = new PolygonShape();
		polShape.setAsBox(sprite.getWidth() * 2.f, sprite.getHeight() / 2.f);

		FixtureDef fdef = new FixtureDef();
		fdef.shape = polShape;
		fdef.isSensor = true;
		this.body.createFixture(fdef).setUserData(this);
		
		polShape.dispose();
	}

	@Override
	public void update(Scene scene, float deltaTime) {
		super.update(scene, deltaTime);
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && openable && !opened) {
			sound.play();
			sprite.setRegion(20, 0, 20, 20);
			opened = true;
			item.appear();
		}
	}

	@Override
	public void resolveCollision(Fixture self, Fixture other) {
		openable = false;
		if (!opened && other.getUserData() instanceof Player) {
			openable = true;
		}
	}

	@Override
	public void resolveCollisionEnd(Fixture A, Fixture B) {
	}

	@Override
	public void resolvePreSolve(Fixture A, Fixture B) {
		// TODO Auto-generated method stub
		
	}
}
