package zhangman523.github.snakeio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.objects.Food;

public class WorldRenderer implements Disposable {

    private WorldController worldController;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;//绘制地图


    public WorldRenderer(WorldController worldController) {
        this.worldController = worldController;
        init();
    }

    private void init() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT * h / w);
        camera.position.set(0, 0, 0);
        camera.update();
        shapeRenderer = new ShapeRenderer();
    }

    public void render() {
        renderBackground(shapeRenderer);
        renderWorld(spriteBatch);
    }

    private void renderBackground(ShapeRenderer shapeRenderer) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(-Constants.MAP_WIDTH / 2, -Constants.MAP_HEIGHT / 2, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        for (int i = -Constants.MAP_WIDTH / 2; i <= Constants.MAP_WIDTH / 2; i += Constants.MAP_RECT_SIZE) {
            for (int j = -Constants.MAP_HEIGHT / 2; j <= Constants.MAP_HEIGHT / 2; j += Constants.MAP_RECT_SIZE) {
                shapeRenderer.rect(i, j, Constants.MAP_RECT_SIZE, Constants.MAP_RECT_SIZE);
            }
        }
        shapeRenderer.end();
    }

    private void renderWorld(SpriteBatch batch) {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldController.snake.render(batch);
        for (Food food : worldController.foods) {
            food.render(batch);
        }
        batch.end();
    }

    public void resize(int width, int height) {
        camera.viewportWidth = Constants.VIEWPORT_WIDTH;                 // Viewport of 30 units!
        camera.viewportHeight = Constants.VIEWPORT_HEIGHT * height / width; // Lets keep things in proportion.
        camera.update();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
