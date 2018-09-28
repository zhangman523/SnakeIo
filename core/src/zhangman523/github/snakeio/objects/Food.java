package zhangman523.github.snakeio.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import zhangman523.github.snakeio.Assets;
import zhangman523.github.snakeio.util.Constants;

public class Food extends AbstractGameObject {

    private TextureRegion food;
    public boolean collected;
    public float score;

    public Food() {
        food = Assets.instance.snake.body;
        dimension.set(1, 1);
        bounds.set(0, 0, dimension.x / 2, dimension.y / 2);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (collected) return;
        TextureRegion reg = food;
        batch.setColor(Color.PINK.toFloatBits());
        batch.draw(reg.getTexture(), position.x,
                position.y, origin.x, origin.y, dimension.x,
                dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
                reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
                false, false);
        batch.setColor(Color.WHITE.toFloatBits());
    }

    public static Food nextRandomFood() {
        Food food = new Food();
        float scale = MathUtils.random(0.1f, 1.0f);
        food.score = scale * 30;
        food.scale.set(scale, scale);
        food.position.set(MathUtils.random(-Constants.MAP_WIDTH / 2 + 1, Constants.MAP_WIDTH / 2 - 1),
                MathUtils.random(-Constants.MAP_HEIGHT / 2 + 1, Constants.MAP_HEIGHT / 2 - 1));
        return food;
    }
}
