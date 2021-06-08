package utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.platformer.Platformer;

public class OptionsUI {
	private Table optionsTable;
	private Slider musicVolumeSlider;
	private Slider effectsVolumeSlider;
	public TextButton backButton;

	public OptionsUI(BitmapFont font, SpriteBatch batch) {
		optionsTable = new Table();
		optionsTable.setFillParent(true);

		optionsTable.align(Align.center);
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB888);
		pixmap.setColor(Color.BROWN);
		pixmap.fill();
		TextureRegionDrawable buttonBackground = new TextureRegionDrawable(new Texture(pixmap));
		
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = font;
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.overFontColor = new Color(0.415f, 0.14f, 0.087f, 1f);
		textButtonStyle.up = buttonBackground;
		backButton = new TextButton("Back", textButtonStyle);
		float screenHeight = Gdx.graphics.getHeight();
		float screenWidth = Gdx.graphics.getWidth();
		float buttonHeight = screenHeight / 8f;
		backButton.getLabel().setFontScale(buttonHeight / 40.f);
		
		TextureRegionDrawable knob = new TextureRegionDrawable(new Texture("aerosprites/movement_casting_v2.png"));
		knob.getRegion().setRegion(580, 0, 35, 35);
		knob.setMinSize(50.f, 50.f);
		
		SliderStyle sliderStyle = new SliderStyle();
		sliderStyle.knob = knob;
		
		pixmap.setColor(Color.GRAY);
		pixmap.fill();
		TextureRegionDrawable sliderBackground = new TextureRegionDrawable(new Texture(pixmap));
		sliderBackground.setMinSize(30.f, 10.f);
		sliderStyle.background = sliderBackground;
		
		pixmap.setColor(Color.BROWN);
		pixmap.fill();
		TextureRegionDrawable knobBefore = new TextureRegionDrawable(new Texture(pixmap));
		knobBefore.setMinSize(30.f, 10.f);
		sliderStyle.knobBefore = knobBefore;
		
		// Music volume slider
		musicVolumeSlider = new Slider(0.f, 1.f, 0.05f, false, sliderStyle);
		musicVolumeSlider.setValue(Platformer.musicVolume);
		musicVolumeSlider.setSize(20.f, 100.f);
		
		LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
		Label musicVolumeLabel = new Label("Music volume", labelStyle);
		musicVolumeLabel.setSize(50.f, buttonHeight);
		musicVolumeLabel.setAlignment(Align.center);
		musicVolumeLabel.setFontScale(buttonHeight / 40.f);
		
		// Effects volume slider
		effectsVolumeSlider = new Slider(0.f, 1.f, 0.05f, false, sliderStyle);
		effectsVolumeSlider.setValue(Platformer.effectsVolume);
		effectsVolumeSlider.setSize(20.f, 100.f);
		
		Label effectsVolumeLabel = new Label("Effects volume", labelStyle);
		effectsVolumeLabel.setSize(50.f, buttonHeight);
		effectsVolumeLabel.setAlignment(Align.center);
		effectsVolumeLabel.setFontScale(buttonHeight / 45.f);
			
		optionsTable.row();
		optionsTable.add(musicVolumeLabel).width(screenWidth / 6).height(buttonHeight);
		optionsTable.add(musicVolumeSlider).width(screenWidth / 4).height(buttonHeight).padLeft(10.f);
		
		optionsTable.row();
		optionsTable.add(effectsVolumeLabel).width(screenWidth / 6).height(buttonHeight);
		optionsTable.add(effectsVolumeSlider).width(screenWidth / 4).height(buttonHeight).padLeft(10.f);
		
		optionsTable.row().center();
		optionsTable.add(backButton).width(screenHeight / 5).height(buttonHeight / 2).spaceTop(buttonHeight / 10.f).colspan(2);
		
		pixmap.dispose();
		optionsTable.setVisible(false);
	}
	
	public Table getTable() {
		return optionsTable;
	}
	
	public void setBackground(Drawable background) {
		optionsTable.setBackground(background);
	}

	public void update() {
		Platformer.musicVolume = musicVolumeSlider.getValue();
		Platformer.effectsVolume = effectsVolumeSlider.getValue();
	}
}
