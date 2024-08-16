package io.github.my_first_android_game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/*
UltraInstinctGo is the name of our game
The game class which this class extends, is responsible for handling multiple screens and provides some helper methods for this purpose,
alongside ApplicationListener for us to use. This class UltraInstinctGo will be the entry point to our game.
*/

public class UltraInstinctGo extends Game{
    public SpriteBatch batch; //allows us to render multiple objects to our screen such as textures so that we are not repeating ourselves.
    public BitmapFont font;//Used to render text on the screen

    public void create() {
        batch = new SpriteBatch();//instantiates the batch for sprites
        font = new BitmapFont(); // use libGDX's default Arial font
        this.setScreen(new MainMenuScreen(this));//We set the screen of the game to a MainMenuScreen obkect with a UltraInstinctGo instance as its first and only parameter
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        //Dispose of unused objects
        batch.dispose();
        font.dispose();
    }
}
