package zhangman523.github.snakeio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import zhangman523.github.snakeio.util.Constants;

public class Assets implements Disposable, AssetErrorListener {
    private static final String TAG = Assets.class.getName();

    public static final Assets instance = new Assets();

    private AssetManager assetManager;
    public AssetSnake snake;
    public AssetUi ui;
    public AssetSounds sounds;
    private Assets() {
    }

    public void init(AssetManager assetManager) {
        this.assetManager = assetManager;
        assetManager.setErrorListener(this);
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
        //load sounds
        assetManager.load("sounds/bm.mp3", Music.class);
        assetManager.load("sounds/live_lost.wav", Sound.class);
        assetManager.finishLoading();
        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
        // enable texture filtering for pixel smoothing;
        for (Texture texture : atlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        snake = new AssetSnake(atlas);
        ui = new AssetUi(atlas);
        sounds = new AssetSounds(assetManager);
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Couldn't load asset '"
                + asset.fileName + "'", throwable);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    public class AssetSnake {
        public final TextureAtlas.AtlasRegion eyes;
        public final TextureAtlas.AtlasRegion body;

        public AssetSnake(TextureAtlas atlas) {
            eyes = atlas.findRegion("eyes");
            body = atlas.findRegion("body");
        }
    }

    public class AssetUi {
        public final TextureAtlas.AtlasRegion lightning;

        public AssetUi(TextureAtlas atlas) {
            lightning = atlas.findRegion("lightning");
        }
    }
    public class AssetSounds {
        public final Music bm;
        public final Sound liveLost;

        public AssetSounds(AssetManager manager) {
            this.bm = manager.get("sounds/bm.mp3", Music.class);
            this.liveLost = manager.get("sounds/live_lost.wav", Sound.class);
        }
    }
}
