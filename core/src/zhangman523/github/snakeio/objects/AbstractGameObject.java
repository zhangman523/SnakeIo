package zhangman523.github.snakeio.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class AbstractGameObject {
    public Vector2 position;
    public Vector2 dimension;
    public Vector2 origin;
    public Vector2 scale;
    public float rotation;

    public Vector2 velocity;//This is the object's current speed in m/s.
    //This is the object's positive and negative maximum speed in m/s.
    public Vector2 terminalVelocity;
    public Vector2 friction;

    public Vector2 acceleration;//This is the object's constant acceleration in m/s2.
    public Rectangle bounds;

    public Body body;

    public float stateTime;
    public Animation<TextureRegion> animation;

    public AbstractGameObject() {
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1, 1);
        rotation = 0;

        velocity = new Vector2();
        terminalVelocity = new Vector2();
        friction = new Vector2();
        acceleration = new Vector2();
        bounds = new Rectangle();
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (body == null) {
            updateMotionX(deltaTime);
            updateMotionY(deltaTime);
            //move to new position
            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;
        } else {
            position.set(body.getPosition());
            rotation = body.getAngle() * MathUtils.radiansToDegrees;
        }
    }

    protected void updateMotionX(float deltaTime) {
        if (velocity.x != 0) {
            //Apply friction
            if (velocity.x > 0) {
                velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
            } else {
                velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
            }
        }
        //Apply acceleration
        velocity.x += acceleration.x * deltaTime;
        // Make sure the object's velocity does not exceed the
        // positive or negative terminal velocity
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
    }

    protected void updateMotionY(float deltaTime) {
        if (velocity.y != 0) {
            //Apply friction
            if (velocity.y > 0) {
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
            } else {
                velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
            }
        }
        //Apply acceleration
        velocity.y += acceleration.y * deltaTime;
        // Make sure the object's velocity does not exceed the
        // positive or negative terminal velocity
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        stateTime = 0;
    }

    public abstract void render(SpriteBatch batch);

    @Override
    public String toString() {
        return "AbstractGameObject{" +
                "position=" + position.toString() +
                ", dimension=" + dimension.toString() +
                ", origin=" + origin.toString() +
                ", scale=" + scale.toString() +
                ", rotation=" + rotation +
                ", velocity=" + velocity.toString() +
                ", terminalVelocity=" + terminalVelocity.toString() +
                ", friction=" + friction.toString() +
                ", acceleration=" + acceleration.toString() +
                ", bounds=" + bounds +
                ", body=" + body +
                ", stateTime=" + stateTime +
                '}';
    }
}
