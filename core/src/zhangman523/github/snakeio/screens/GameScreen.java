package zhangman523.github.snakeio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import zhangman523.github.snakeio.Assets;
import zhangman523.github.snakeio.WorldController;
import zhangman523.github.snakeio.WorldRenderer;
import zhangman523.github.snakeio.util.AudioManager;

public class GameScreen extends AbstractGameScreen {
    private WorldController worldController;
    private WorldRenderer worldRenderer;

    private boolean paused;

    public GameScreen(DirectedGame game) {
        super(game);
    }

    @Override
    public void show() {
        worldController = new WorldController(game);
        worldRenderer = new WorldRenderer(worldController);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        if (!paused) {
            worldController.update(delta);
        }
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        worldRenderer.resize(width, height);
    }

    @Override
    public void hide() {
        worldController.dispose();
        worldRenderer.dispose();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void pause() {
        paused = true;
        AudioManager.instance.stopMusic();
    }

    @Override
    public void resume() {
        super.resume();
        paused = false;
        AudioManager.instance.play(Assets.instance.sounds.bm);
    }

    @Override
    public InputProcessor getInputProcessor() {
        return worldController;
    }
}
