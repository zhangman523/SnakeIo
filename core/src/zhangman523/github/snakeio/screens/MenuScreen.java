package zhangman523.github.snakeio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import zhangman523.github.snakeio.util.Constants;
import zhangman523.github.snakeio.util.GamePreferences;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;

public class MenuScreen extends AbstractGameScreen {

    private Stage stage;
    private Skin snakeSkin;
    private Skin defaultSkin;

    private Image imgBackground;//背景
    private Image imgLogo;//logo
    private Button endlessModel;//无尽模式
    private Button teamModel;//团战模式
    private Button challengeModel;//挑战模式
    private Button robcoinModel;//赏金模式
    private Button setting;//设置

    private Window winSetting;//设置windown
    private CheckBox chkSound;
    private CheckBox chkMusic;
    private CheckBox leftOp;
    private CheckBox rightOp;
    private TextButton btnWinSave;
    private TextButton btnWinCancel;

    private boolean debugEnable = false;

    public MenuScreen(DirectedGame game) {
        super(game);
    }

    @Override
    public void show() {
        GamePreferences.instance.load();
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        buildStage();

    }

    private void buildStage() {
        snakeSkin = new Skin(Gdx.files.internal(Constants.SKIN_TOUCHPAD_UI),
                new TextureAtlas(Gdx.files.internal(Constants.TEXTURE_ATLAS_TOUCHPAD_UI)));
        defaultSkin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
                new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
        Table layerBackground = buildBackgroundLayer();
        Table layerLogo = buildLogoLayer();
        Table layerGame = buildGameModelLayer();
        Table layerSetting = buildSettingLayer();
        Table layerSettingWin = buildSettingWindowLayer();

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerLogo);
        stack.add(layerGame);
        stack.add(layerSetting);
        stage.addActor(layerSettingWin);
    }

    /**
     * 背景
     */
    private Table buildBackgroundLayer() {
        Table layer = new Table();
        imgBackground = new Image(snakeSkin, "home_bg");
        layer.add(imgBackground);
        return layer;
    }

    private Table buildLogoLayer() {
        Table layer = new Table();
        layer.center().top();
        imgLogo = new Image(snakeSkin, "snake_name_icon");
        imgLogo.setOrigin(imgLogo.getWidth() / 2, imgLogo.getHeight() / 2);
        imgLogo.setScale(0.5f);
        layer.add(imgLogo);
        if (debugEnable) layer.debug();
        return layer;
    }

    private Table buildGameModelLayer() {
        Table layer = new Table();
        layer.center();
        endlessModel = new Button(snakeSkin, "endless");
        layer.add(endlessModel).width(100).height(140).padLeft(5);
        teamModel = new Button(snakeSkin, "team");
        layer.add(teamModel).width(100).height(140).padLeft(5);
        Table childTable = new Table();
        challengeModel = new Button(snakeSkin, "challenge");
        childTable.add(challengeModel).width(120).height(70);
        childTable.row();
        robcoinModel = new Button(snakeSkin, "robcoin");
        childTable.add(robcoinModel).width(120).height(70);
        layer.add(childTable).padLeft(5);
        if (debugEnable) layer.debug();
        return layer;
    }


    private Table buildSettingLayer() {
        Table layer = new Table();
        layer.top().right();
        setting = new Button(snakeSkin, "setting");
        layer.add(setting).padRight(20).padTop(20);
        return layer;
    }

    private Table buildSettingWindowLayer() {
        winSetting = new Window("Setting", defaultSkin);
        winSetting.center();
        winSetting.add(buildSetWinAudioSettings()).row();
        winSetting.add(buildSetWinOpreateSettings()).row();
        winSetting.add(buildWinBtn());
        showOptionsWindow(false, false);
        winSetting.pack();
        winSetting.setPosition((Constants.VIEWPORT_GUI_WIDTH - winSetting.getWidth()) / 2, (Constants.VIEWPORT_GUI_HEIGHT - winSetting.getHeight()) / 2);
        return winSetting;
    }

    private Table buildSetWinAudioSettings() {
        Table table = new Table();
        table.pad(10, 10, 0, 10);
        table.add(new Label("Audio", snakeSkin, "default-font", Color.RED)).colspan(3);
        table.row();
        chkSound = new CheckBox("", defaultSkin);
        table.add(chkSound).padLeft(10);
        table.add(new Label("Sound", defaultSkin)).padLeft(5);
        chkMusic = new CheckBox("", defaultSkin);
        table.add(chkMusic).padLeft(10);
        table.add(new Label("Music", defaultSkin)).padLeft(5);
        return table;
    }

    private Table buildSetWinOpreateSettings() {
        Table table = new Table();
        table.pad(10, 10, 0, 10);
        table.add(new Label("Operation", snakeSkin, "default-font", Color.RED)).colspan(3);
        table.row();
        leftOp = new CheckBox("", defaultSkin);
        table.add(leftOp).padLeft(10);
        table.add(new Label("Left", defaultSkin)).padLeft(5);
        rightOp = new CheckBox("", defaultSkin);
        table.add(rightOp).padLeft(10);
        table.add(new Label("Right", defaultSkin)).padLeft(5);
        return table;
    }

    private Table buildWinBtn() {
        Table tbl = new Table();
        // + Separator
        Label lbl = null;
        lbl = new Label("", defaultSkin);
        lbl.setColor(0.75f, 0.75f, 0.75f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = defaultSkin.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
        tbl.row();
        lbl = new Label("", defaultSkin);
        lbl.setColor(0.5f, 0.5f, 0.5f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = defaultSkin.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
        tbl.row();
        // + Save Button with event handler
        btnWinSave = new TextButton("Save", defaultSkin);
        tbl.add(btnWinSave).padRight(30);
        btnWinSave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                onSaveClicked();
                showOptionsWindow(false, false);
            }
        });
        // + Cancel Button with even handler
        btnWinCancel = new TextButton("Cancel", defaultSkin);
        tbl.add(btnWinCancel);
        btnWinCancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                onCancelClicked();
                showOptionsWindow(false, false);
            }
        });
        return tbl;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
        snakeSkin.dispose();
        defaultSkin.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    public void showOptionsWindow(boolean visible, boolean animated) {
        float alphaTo = visible ? 0.8f : 0.0f;
        float duration = animated ? 1.0f : 0.0f;
        final Touchable touchEnable = visible ? Touchable.enabled : Touchable.disabled;
        winSetting.addAction(sequence(touchable(touchEnable), alpha(alphaTo, duration)));
    }
}
