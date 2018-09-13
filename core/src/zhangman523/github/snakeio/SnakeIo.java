package zhangman523.github.snakeio;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SnakeIo extends ApplicationAdapter {
    private WorldController worldController;
    private WorldRenderer worldRenderer;

    @Override
    public void create() {
        worldController = new WorldController();
        worldRenderer = new WorldRenderer(worldController);
    }

    @Override
    public void render() {
        worldController.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldRenderer.render();
    }

    @Override
    public void dispose() {
        worldRenderer.dispose();
        worldController.dispose();
    }
}
