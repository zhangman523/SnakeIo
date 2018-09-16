package zhangman523.github.snakeio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.objects.Food;
import zhangman523.github.snakeio.objects.Snake;

public class WorldController extends InputAdapter implements Disposable {

    Snake snake;
    Array<Food> foods;
    public CameraHelper cameraHelper;
    private Circle c1 = new Circle();//用来检测碰撞
    private Circle c2 = new Circle();

    public WorldController() {
        snake = new Snake();
        cameraHelper = new CameraHelper();
        cameraHelper.setTarget(snake);
        foods = new Array<Food>();
        for (int i = 0; i < 100; i++) {
            foods.add(Food.nextRandomFood());
        }
    }

    public void update(float deltaTime) {
        handleInput(deltaTime);
        snake.update(deltaTime);
        testCollision();
        cameraHelper.update(deltaTime);
        for (Food food : foods) {
            food.update(deltaTime);
        }
    }

    private void testCollision() {
        float radius = snake.bounds.width / 2;
        c1.set(snake.position.x + radius, snake.position.y + radius,
                snake.bounds.width / 2);
        for (Food food : foods) {
            if (food.collected) {
                foods.removeValue(food, false);
                continue;
            }
            c2.set(food.position.x + food.bounds.width / 2, food.position.y + food.bounds.width / 2, food.bounds.width / 2);
            if (!Intersector.overlaps(c1, c2)) continue;
            onCollisionSnakeWithFood(food);
            break;
        }

    }

    private void onCollisionSnakeWithFood(Food food) {
        food.collected = true;
        snake.eatFood(Math.round(food.score));
    }

    private void handleInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;
        int[][] angleArray = new int[][]{
                {-1, 270, 90},
                {180, 225, 135},
                {0, 315, 45}
        };//定义二维数组分别代表 上下左右 点击的角度
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
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                snake.speedUp();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.SPACE:
                snake.speedDown();
                break;
        }
        return true;
    }

    @Override
    public void dispose() {
        snake.dispose();
    }
}
