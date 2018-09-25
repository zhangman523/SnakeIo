package zhangman523.github.snakeio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SnakeIo extends ApplicationAdapter {
    private WorldController worldController;
    private WorldRenderer worldRenderer;

    @Override
    public void create() {
        GamePreferences.instance.load();
        Assets.instance.init(new AssetManager());
        worldController = new WorldController();
        worldRenderer = new WorldRenderer(worldController);
        Gdx.input.setInputProcessor(worldController);
        AudioManager.instance.play(Assets.instance.sounds.bm);
    }

    @Override
    public void render() {
        worldController.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldRenderer.resize(width, height);
    }

    @Override
    public void dispose() {
        AudioManager.instance.stopMusic();
        worldRenderer.dispose();
        worldController.dispose();
        Assets.instance.dispose();
    }
}
