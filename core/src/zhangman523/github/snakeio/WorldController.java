package zhangman523.github.snakeio;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.objects.Food;
import zhangman523.github.snakeio.objects.Snake;
import zhangman523.github.snakeio.util.AudioManager;
import zhangman523.github.snakeio.util.CameraHelper;
import zhangman523.github.snakeio.util.Constants;

public class WorldController extends InputAdapter implements Disposable {

    Snake snake;
    Array<Food> foods;
    Array<Snake> enemies;//敌人
    public CameraHelper cameraHelper;
    private Circle c1 = new Circle();//用来检测碰撞
    private Circle c2 = new Circle();
    private float aroundDest = 1f;
    public Stage touchProcessor;

    public boolean gameOver;

    public WorldController() {
        init();
    }

    public void init() {
        gameOver = false;
        snake = new Snake();
        cameraHelper = new CameraHelper();
        cameraHelper.setTarget(snake);
        foods = new Array<Food>();
        for (int i = 0; i < 100; i++) {
            foods.add(Food.nextRandomFood());
        }

        enemies = new Array<Snake>();
        for (int i = 0; i < 10; i++) {
            Snake snake = new Snake();
            snake.angle = MathUtils.random(0f, (float) (Math.PI * 2));
            snake.toAngle = snake.angle;
            snake.position.set(MathUtils.random(-Constants.MAP_WIDTH / 2 + 5, Constants.MAP_WIDTH / 2 - 5), MathUtils.random(-Constants.MAP_HEIGHT / 2 + 5, Constants.MAP_HEIGHT / 2 - 5));
            enemies.add(snake);
        }
    }

    public void update(float deltaTime) {
        if (isGameOver()) return;
        handleInput(deltaTime);
        enemyAi();
        snake.update(deltaTime);
        testCollision();
        cameraHelper.update(deltaTime);
        for (Food food : foods) {
            food.update(deltaTime);
        }
//        for (Snake snake : enemies) {
//            snake.update(deltaTime);
//        }
        checkGameOver();
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

    public void enemyAi() {
        for (int i = 0; i < enemies.size; i++) {
            Snake snake = enemies.get(i);
            aiTurnAround(snake);
        }
    }

    private void aiTurnAround(Snake snake) {
        float radius = snake.bounds.width / 2;
        if ((snake.position.x - radius < -Constants.MAP_WIDTH / 2 + aroundDest
                && snake.toAngle > Math.PI / 2
                && snake.toAngle < Math.PI * 3 / 2) || (snake.position.x + radius > Constants.MAP_WIDTH / 2 - aroundDest
                && (snake.toAngle < Math.PI / 2 || snake.toAngle > Math.PI * 3 / 2))) {//判断是否靠近左右边界，如果靠近 掉头
            Gdx.app.debug("AI", " x reach left or right " + snake.toAngle + " position: " + snake.position.toString());
            snake.setDirection(Math.PI - snake.toAngle);
        } else if ((snake.position.y - radius < -Constants.MAP_HEIGHT / 2 + aroundDest && snake.toAngle > Math.PI)
                || (snake.position.y + radius > Constants.MAP_HEIGHT / 2 - aroundDest && snake.toAngle < Math.PI)) {
            Gdx.app.debug("AI", " y reach bottom or top " + snake.toAngle + " position: " + snake.position.toString());
            snake.setDirection(Math.PI * 2 - snake.toAngle);
        } else {
            collisionDetect(snake, this.snake);//判断是否要和玩家碰撞
            for (Snake s : enemies) {
                if (s == snake) continue;
                collisionDetect(snake, s);
            }
        }
    }

    private void collisionDetect(Snake snake, Snake other) {
//        if (snake.isDead || other.isDead) return;
        for (Snake.Movement movement : other.movements) {
            float dx = movement.position.x - snake.position.x;
            float dy = movement.position.y - snake.position.y;
            if (Math.abs(dx) > snake.dimension.x * 2 || Math.abs(dy) > snake.dimension.y * 2) {
                return;
            }
            double angle;
            if (dx == 0) {
                if (dy > 0) {
                    angle = Math.PI / 2;
                } else {
                    angle = -Math.PI / 2;
                }
            } else {
                angle = Math.atan2(dy, dx);
                if (dx < 0) {
                    angle += Math.PI;
                }
            }
//            angle = angle % (Math.PI * 2);
            Gdx.app.debug("AI", " angle + " + angle + " snake Angleest:" + snake.toAngle);
            if (Math.abs(angle - snake.toAngle) < Math.toRadians(5)) {
                snake.setDirection(snake.toAngle + Math.random() * Math.PI * 3 / 2);
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void checkGameOver() {
        float radius = snake.bounds.width / 2;
        if (snake.position.x + radius >= Constants.MAP_WIDTH / 2
                || snake.position.x <= -Constants.MAP_WIDTH / 2
                || snake.position.y + radius >= Constants.MAP_HEIGHT / 2
                || snake.position.y <= -Constants.MAP_WIDTH / 2) {
            gameOver = true;
            AudioManager.instance.stopMusic();
            AudioManager.instance.play(Assets.instance.sounds.liveLost);
        }
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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return touchProcessor != null && touchProcessor.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return touchProcessor != null && touchProcessor.touchDragged(screenX, screenY, pointer);

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return touchProcessor != null && touchProcessor.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public void dispose() {
        snake.dispose();
    }
}
