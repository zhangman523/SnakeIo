package zhangman523.github.snakeio;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.objects.Snake;

public class WorldController extends InputAdapter implements Disposable {

    Snake snake;

    public WorldController() {
        snake = new Snake();
    }

    public void update(float deltaTime) {
        snake.update(deltaTime);
    }

    @Override
    public void dispose() {
        snake.dispose();
    }
}
