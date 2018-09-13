package zhangman523.github.snakeio.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.Constants;

public class Snake extends AbstractGameObject implements Disposable {
    private static final String TAG = Snake.class.getSimpleName();
    TextureRegion eyes;
    TextureRegion body;
    private AssetManager assetManager;

    private int length = 100 * 80;//长度
    private float speed = 8 * 10;
    public Array<Movement> movements;


    public Snake() {
        initRegion();
        init();
    }

    private void init() {
        dimension.set(20, 20);
        origin.set(dimension.x / 2, dimension.y / 2);
        terminalVelocity.set(100, 100);
        position.set(50, 120);
        rotation = -90;
        scale.set(0.7f, 0.7f);
        bounds.set(0, 0, dimension.x, dimension.y);
        movements = new Array<Movement>();
        velocity.set(speed, 0);
    }

    private void initRegion() {
        assetManager = new AssetManager();
        assetManager.setErrorListener(new AssetErrorListener() {
            @Override
            public void error(AssetDescriptor asset, Throwable throwable) {
                Gdx.app.error(TAG, "Couldn't load asset '"
                        + asset.fileName + "'", throwable);
            }
        });
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
        //load sounds
        assetManager.finishLoading();
        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
        // enable texture filtering for pixel smoothing;
        for (Texture texture : atlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
        eyes = atlas.findRegion("eyes");
        body = atlas.findRegion("body");
    }

    @Override
    public void render(SpriteBatch batch) {
        float snakeLength = this.length;
        for (int i = movements.size - 1; i >= 0; i--) {
            Movement movement = movements.get(i);
            float x = movement.position.x;
            float y = movement.position.y;
            if (snakeLength > 0 && snakeLength < movement.speed) {
                if (i != movements.size - 1) {
                    Movement lm = movements.get(i + 1);
                    float ratio = snakeLength / movement.speed;
                    x = lm.position.x - (lm.position.x - x) * ratio;
                    y = lm.position.y - (lm.position.y - y) * ratio;
                }
            } else if (snakeLength < 0) {
                break;
            }
            snakeLength -= movement.speed;
            TextureRegion reg = body;
            batch.setColor(Color.valueOf("#F65454"));//set snake body color
            batch.draw(reg.getTexture(), x,
                    y, origin.x, origin.y, dimension.x,
                    dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
                    reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                    false, false);
        }
        TextureRegion reg = eyes;
        batch.setColor(Color.WHITE.toFloatBits());//reset  color
        batch.draw(reg.getTexture(), position.x,
                position.y, origin.x, origin.y, dimension.x - 0.1f,
                dimension.y - 0.1f, scale.x, scale.y, rotation, reg.getRegionX(),
                reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
    }

    @Override
    public void update(float deltaTime) {
        movements.add(new Movement(new Vector2(position), speed));
        if (movements.size > length) {
            movements.removeValue(movements.first(), true);
        }
        super.update(deltaTime);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    public class Movement {
        public Vector2 position;
        public float speed;

        public Movement(Vector2 position, float speed) {
            this.position = position;
            this.speed = speed;
        }
    }
}
