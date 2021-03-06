package zhangman523.github.snakeio.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.Assets;

public class Snake extends AbstractGameObject implements Disposable {
    private static final String TAG = Snake.class.getSimpleName();
    TextureRegion eyes;
    TextureRegion body;
    private int length = 100;//长度
    private float speed = 4;
    public Array<Movement> movements;

    public double angle;//蛇当前运动的角度0-360度
    public double toAngle;//将要转向的角度
    private double turnSpeed = Math.toRadians(10);//转弯速度;

    private boolean isSpeedUp; // 加速
    private float oldSpeed;

    public Snake() {
        initRegion();
        init();
    }

    private void init() {
        dimension.set(1, 1);
        origin.set(dimension.x / 2, dimension.y / 2);
        terminalVelocity.set(speed * 2, speed * 2);
        position.set(0, 0);
        rotation = -90;
        scale.set(0.7f, 0.7f);
        bounds.set(0, 0, dimension.x, dimension.y);
        movements = new Array<Movement>();
        angle = 0;//当前角度设置为0度 向左运行
        toAngle = angle;
    }

    private void initRegion() {
        eyes = Assets.instance.snake.eyes;
        body = Assets.instance.snake.body;
    }

    public void setDirection(double angle) {
        angle = angle % (Math.PI * 2);
        toAngle = angle;
    }

    /**
     * 加速
     */
    public void speedUp() {
        if (isSpeedUp) return;
        isSpeedUp = true;
        oldSpeed = speed;
        speed *= 2;
    }

    public void speedDown() {
        if (!isSpeedUp) return;
        isSpeedUp = false;
        this.speed = oldSpeed;
    }

    public void eatFood(int score) {
        length += score;
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
        angle = (Math.PI * 2 + angle) % (Math.PI * 2);//把负度数转为正度数
        if (Math.abs(angle - toAngle) <= turnSpeed) {
            toAngle = angle = toAngle % (Math.PI * 2);
        } else {
            if (Math.abs(angle - toAngle) < Math.PI) { //如果度数差小于 180度
                if (angle < toAngle) { //如果转向度数大于当前度数 则 逆时针旋转 toAngle-angle 度数(如 30-80度
                    angle += turnSpeed;
                } else {
                    angle -= turnSpeed;
                }
            } else {
                if (angle < toAngle) {
                    angle -= turnSpeed;

                } else {
                    angle += turnSpeed;
                }
            }
            System.out.println(" angle = " + Math.toDegrees(angle) + " toAngle = " + Math.toDegrees(toAngle));
        }
        angle = angle % (Math.PI * 2);
        float vx = (float) (speed * Math.cos(angle));
        float vy = (float) (speed * Math.sin(angle));
        velocity.set(vx, vy);
        rotation = (float) (360 + Math.atan2(velocity.y, velocity.x)
                / (Math.PI / 180) - 90);
        super.update(deltaTime);
    }

    @Override
    public void dispose() {
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
