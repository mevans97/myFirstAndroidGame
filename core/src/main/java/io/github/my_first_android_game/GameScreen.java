package io.github.my_first_android_game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {

    final UltraInstinctGo game;

    Texture kiBlastImage;
    Texture gokuImage;
    Sound kiBlastSound;
    Music ultraInstinctMusic;
    OrthographicCamera camera;
    Rectangle goku;
    Array<Rectangle> kiBlastArray;
    long lastBlastTime;
    int dropsGathered;


    public GameScreen(final UltraInstinctGo game){

        this.game = game;

        // load the images for the blast and the goku, 64x64 pixels each
        kiBlastImage = new Texture(Gdx.files.internal("drop.png"));
        gokuImage = new Texture(Gdx.files.internal("gokuWeaving-transformed.png"));

        // load the ki blast sound effect and the ultra Instinct background "music"
        kiBlastSound = Gdx.audio.newSound(Gdx.files.internal("kiblast.mp3"));
        ultraInstinctMusic = Gdx.audio.newMusic(Gdx.files.internal("ultraInstinct.mp3"));
        ultraInstinctMusic.isLooping();//Loops the music during gameplay

        //This camera makes sure that you are always viewing an area of our game world that is 800x480 units wide. Think of this as a virtual window into our world.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        //creating a rectangle to represent our goku
        //We want the goku to be 20 pixels above the bottom edge of the screen, and centered horizontally.
        //Wait, why is goku.y set to 20, shouldn’t it be 480 - 20? By default, all rendering in libGDX (and OpenGL) is performed with the y-axis pointing upwards.
        goku = new Rectangle();
        goku.x = 800 / 2 - 64 / 2;// center the bucket horizontally
        goku.y = 20;// bottom left corner of the bucket is 20 pixels above
                    // the bottom screen edge
        goku.width = 16;
        goku.height = 16;

        //create the kiBlast array and spawn the first KiBlast
        kiBlastArray = new Array<Rectangle>();
        kiBlast();

    }
    // To facilitate the creation of kiBlastArray well create the following method
    private void kiBlast() {
        Rectangle kiBlast = new Rectangle();//To create the kiBlastObject similar to the goku creation
        kiBlast.x = MathUtils.random(0, 800-64);//Allows our kiBlastArray to spawn in random positions at the top of the screen
        kiBlast.y = 480;
        kiBlast.width = 64;
        kiBlast.height = 64;
        kiBlastArray.add(kiBlast); //adds a new kiBlastObject to the kiBlastObject array in the create method
        lastBlastTime = TimeUtils.nanoTime();
    }



    @Override
    public void render(float delta){
        //Rendering Goku and KiBlast

        //This clears the screen with a dark blue color.
        ScreenUtils.clear(0, 0, 0.2f, 1);

        //This tells our camera to make sure its updated. This updates the coordination system using mathmatical matrixes
        camera.update();

        //Tells batch to use coordinate system specifieed by camera(camera.combined field is a projection matrix)
        game.batch.setProjectionMatrix(camera.combined);


        /*
        The SpriteBatch class helps make OpenGL happy. It will record all drawing commands in between SpriteBatch.begin() and SpriteBatch.end().
        Once we call SpriteBatch.end() it will submit all drawing requests we made at once, speeding up rendering quite a bit. This all might look
        cumbersome in the beginning, but it is what makes the difference between rendering 500 sprites at 60 frames per second and rendering 100
         sprites at 20 frames per second.
         */
        game.batch.begin();
        //Render Goku
        game.batch.draw(gokuImage, goku.x, goku.y);
        //Render KiBlasts
        for(Rectangle kiBlast: kiBlastArray) {
            game.batch.draw(kiBlastImage, kiBlast.x, kiBlast.y);
        }
        game.batch.end();

        //Need to check how much time has passed since we loaded a kiBlastObject :
        if(TimeUtils.nanoTime() - lastBlastTime > 100000000) kiBlast();

        //Need to make our kiBlastArray move in the game :
        for (Iterator<Rectangle> iter = kiBlastArray.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 400 * Gdx.graphics.getDeltaTime();// kiBlastArray fall at a speed of 200 pixels per second
            if (raindrop.y + 64 < 0)
                iter.remove();//If kiBlastArray reach the bottom of the screen, They are removed

            //If kiBlastObject hits our goku, we want to play our drop sound and remove the kiBlastObject from the array
            if (raindrop.overlaps(goku)) {
                kiBlastSound.play();
                iter.remove();
            }
        }

        /*
        Time to let the user control the goku. Earlier we said we’d allow the user to drag the goku. Let’s make things a little bit easier.
        If the user touches the screen (or presses a mouse button), we want the goku to center around that position horizontally.
        Adding the following code to the bottom of the render() method will do this:
         */
        if(Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            goku.x = touchPos.x - 64 / 2;
        }

        //We can also allow the user to use the keyboard directional arrows to control the goku on the desktop
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT))
            goku.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT))
            goku.x += 200 * Gdx.graphics.getDeltaTime();

        //we also need to make sure our goku stays within the screen limits :
        if(goku.x < 0) goku.x = 0;
        if(goku.x > 800 - 64) goku.x = 800 - 64;



    }
    //Clean up everything from memory when the user quits the game.
    @Override
    public void dispose() {
        // dispose of all the native resources
        kiBlastImage.dispose();
        gokuImage.dispose();
        kiBlastSound.dispose();
        ultraInstinctMusic.dispose();
        game.batch.dispose();
    }


    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


}
