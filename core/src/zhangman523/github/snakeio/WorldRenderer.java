package zhangman523.github.snakeio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import zhangman523.github.snakeio.objects.Food;
import zhangman523.github.snakeio.util.AudioManager;
import zhangman523.github.snakeio.util.Constants;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;

public class WorldRenderer implements Disposable {

    private WorldController worldController;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;//绘制地图
    private Skin touchPadSkin;
    public Stage stage;
    private Touchpad touchpad;
    private ImageButton btnSpeed;
    private Skin skinLibgdx;//默认皮肤private Window winGameOver;
    private TextButton btnBack;
    private TextButton btnStart;
    private Window winGameOver;

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
        skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
        stage.addActor(buildSpeed());
        stage.addActor(buildTouchPad());
        stage.addActor(buildGameOverDialog());
        worldController.touchProcessor = stage;
    }

    public void render() {
        renderBackground(shapeRenderer);
        renderWorld(spriteBatch);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        if (worldController.isGameOver()) {
            showOptionsWindow(true, true);
        }
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

    private Table buildGameOverDialog() {
        winGameOver = new Window("GAME OVER", skinLibgdx);
        Table table = new Table();
        table.pad(10, 10, 0, 10);
        table.add(new Label("GAME OVER!", skinLibgdx, "default-font", Color.ORANGE)).colspan(3);
        table.row();
        btnBack = new TextButton("Back", skinLibgdx);
        table.add(btnBack).padRight(30);
        btnBack.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showOptionsWindow(false, true);
                Gdx.app.exit();
            }
        });
        btnStart = new TextButton("ReStart", skinLibgdx);
        table.add(btnStart);
        btnStart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.instance.play(Assets.instance.sounds.bm);
                showOptionsWindow(false, true);
                worldController.init();
            }
        });
        winGameOver.add(table).pad(10, 10, 0, 10);
        winGameOver.setColor(1, 1, 1, 0.8f);
        showOptionsWindow(false, false);
        winGameOver.pack();
        winGameOver.setPosition((Constants.VIEWPORT_GUI_WIDTH - winGameOver.getWidth()) / 2,
                (Constants.VIEWPORT_GUI_HEIGHT - winGameOver.getHeight()) / 2);
        return winGameOver;
    }

    public void showOptionsWindow(boolean visible, boolean animated) {
        float alphaTo = visible ? 0.8f : 0.0f;
        float duration = animated ? 1.0f : 0.0f;
        final Touchable touchEnable = visible ? Touchable.enabled : Touchable.disabled;
        winGameOver.addAction(sequence(touchable(touchEnable), alpha(alphaTo, duration)));
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
