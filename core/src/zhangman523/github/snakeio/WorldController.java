package zhangman523.github.snakeio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.objects.Snake;

public class WorldController extends InputAdapter implements Disposable {

    Snake snake;

    private int[][] angleArray = new int[][]{
            {-1, 270, 90},
            {180, 225, 135},
            {0, 315, 45}
    };//定义二维数组分别代表 上下左右 点击的角度

    public WorldController() {
        snake = new Snake();
    }

    public void update(float deltaTime) {
        handleInput(deltaTime);
        snake.update(deltaTime);
    }

    private void handleInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;
        int i = 0, j = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            i = 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            i = 2;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            j = 2;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            j = 1;
        }
        double angle = angleArray[i][j];
        if (angle == -1) {
            return;
        }
        snake.setDirection(Math.toRadians(angle));
    }

    @Override
    public void dispose() {
        snake.dispose();
    }
}
