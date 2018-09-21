package zhangman523.github.snakeio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import zhangman523.github.snakeio.objects.Food;
import zhangman523.github.snakeio.objects.Snake;

public class WorldRenderer implements Disposable {

    private WorldController worldController;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;//绘制地图
    private Skin touchPadSkin;
    public Stage stage;
    private Touchpad touchpad;
    private ImageButton btnSpeed;

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

        stage = new Stage(new ExtendViewport(Constants.VIEWPORT_GUI_WIDTH,
                Constants.VIEWPORT_GUI_HEIGHT));
        touchPadSkin = new Skin(Gdx.files.internal(Constants.SKIN_TOUCHPAD_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_TOUCHPAD_UI));
        stage.addActor(buildSpeed());
        stage.addActor(buildTouchPad());
        worldController.touchProcessor = stage;
    }

    public void render() {
        renderBackground(shapeRenderer);
        renderWorld(spriteBatch);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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
//        for (Snake snake : worldController.enemies) {
//            snake.render(batch);
//        }
        batch.end();
    }

    private ImageButton buildSpeed() {
        Drawable drawable = new TextureRegionDrawable(Assets.instance.ui.lightning);
        btnSpeed = new ImageButton(drawable);
        btnSpeed.setBounds(stage.getWidth() - 150, 50, 100, 100);
        btnSpeed.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                worldController.snake.speedUp();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                worldController.snake.speedDown();
            }
        });
        return btnSpeed;
    }

    private Touchpad buildTouchPad() {
        Skin skin = touchPadSkin;
        touchpad = new Touchpad(20, skin);
        touchpad.setBounds(50, 50, 100, 100);
        touchpad.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                // knob相对touchpad左下角的位置
                float knobX = touchpad.getKnobX();
                float knobY = touchpad.getKnobY();
                // knob依touchpad的中心点向右水平线，
                float knobPercentX = touchpad.getKnobPercentX(); // 手指离开中心向左趋于-1，向右趋于1
                float knobPercentY = touchpad.getKnobPercentY(); // 手指离开中心向下趋于1，向上趋于-1
                if (knobPercentX == 0 && knobPercentY == 0) return;
                double angle = Math.atan2(knobPercentY, knobPercentX);
                if (angle < 0) {
                    angle += Math.PI * 2;
                }
                worldController.snake.setDirection(angle);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);

            }
        });
        return touchpad;
    }

    public void resize(int width, int height) {
        camera.viewportWidth = Constants.VIEWPORT_WIDTH;                 // Viewport of 30 units!
        camera.viewportHeight = Constants.VIEWPORT_HEIGHT * height / width; // Lets keep things in proportion.
        camera.update();
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        stage.dispose();
        touchPadSkin.dispose();
    }
}
