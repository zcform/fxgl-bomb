package com.zc.boom;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * TODO
 * 2023-10-16
 * zhangxl
 */
public class BoomFactory implements EntityFactory {
    private int getLeft() {
        return FXGL.<BoomApplication>getAppCast().left;
    }

    private int getCellW() {
        return FXGL.<BoomApplication>getAppCast().cell_w;
    }

    @Spawns("background")
    public Entity background(SpawnData data) {
        int w = FXGL.<BoomApplication>getAppCast().w;
        int h = FXGL.<BoomApplication>getAppCast().h;
        int cellSizeW = FXGL.<BoomApplication>getAppCast().cell_size_w;
        int cellSizeH = FXGL.<BoomApplication>getAppCast().cell_size_h;

        BoomGridView chessGridView = new BoomGridView(cellSizeW, cellSizeH, getCellW(), getCellW());

        chessGridView.setTranslateX(getLeft());
        chessGridView.setTranslateY(BoomConstant.top);

        int lw = w - BoomConstant.right;

        return entityBuilder(data)
                .type(BoomEntityType.BACKGROUND)
                .view(new Rectangle(getAppWidth(), getAppHeight(), Color.rgb(162, 162, 162, .5)))
                .view(chessGridView)
                .view(new Line(lw, 0, lw, h))
                .zIndex(-100)
                .neverUpdated()
                .build();
    }

    @Spawns("selLv")
    public Entity selLv(SpawnData data) {
        Button button0 = new Button("简单");
        Button button1 = new Button("一般");
        Button button2 = new Button("困难");

        int w = FXGL.<BoomApplication>getAppCast().w / 2 - 20;

        button0.setTranslateX(w);
        button1.setTranslateX(w);
        button2.setTranslateX(w);
        button0.setTranslateY(BoomConstant.top + 40);
        button1.setTranslateY(BoomConstant.top + 40 * 2);
        button2.setTranslateY(BoomConstant.top + 40 * 3);

        button0.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> FXGL.<BoomApplication>getAppCast().setLevel(0));
        button1.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> FXGL.<BoomApplication>getAppCast().setLevel(1));
        button2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> FXGL.<BoomApplication>getAppCast().setLevel(2));

        return entityBuilder(data)
                .type(BoomEntityType.LV)
                .view(button0)
                .view(button1)
                .view(button2)
                .zIndex(15)
                .neverUpdated()
                .build();
    }

    @Spawns("cellEmpty")
    public Entity cellEmpty(SpawnData data) {
        data.put("num", "cell");

        return cellType(data);
    }

    @Spawns("laughP")
    public Entity laughP(SpawnData data) {
        Texture texture = new Texture(image("boom/微笑.png", 30, 30));

        int w = FXGL.<BoomApplication>getAppCast().w / 2 - 15;

        texture.setTranslateX(w);
        texture.setTranslateY(5);

        texture.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            List<Entity> collect = getGameWorld().getEntities().stream()
                    .filter(Objects::nonNull)
                    .filter(et -> BoomEntityType.RUN != et.getType())
                    .collect(Collectors.toList());

            if (collect != null && collect.size() > 0) {
                for (Entity entity : collect) {
                    entity.removeFromWorld();
                }
            }

            set("isStart", false);
            spawn("selLv");
        });

        return entityBuilder(data)
                .type(BoomEntityType.RUN)
                .view(texture)
                .zIndex(100)
                .neverUpdated()
                .build();
    }

    @Spawns("cellType")
    public Entity cellType(SpawnData data) {
        String num = data.get("num").toString();

        Point2D pxy = new Point2D(data.getX() / getCellW(), data.getY() / getCellW());

        Texture texture = new Texture(image("boom/" + num + ".png", getCellW(), getCellW()));

        texture.setTranslateX(getLeft());
        texture.setTranslateY(BoomConstant.top);

        texture.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> FXGL.<BoomApplication>getAppCast().mouse_clicked(e, pxy));



        return entityBuilder(data)
                .type(BoomEntityType.CELL)
                .view(texture)
                .zIndex(15)
                .neverUpdated()
                .build();
    }

    @Spawns("over")
    public Entity over(SpawnData data) {
        return entityBuilder(data)
                .type(BoomEntityType.OVER)
                .view(new Rectangle(getAppWidth(), getAppHeight(), Color.rgb(162, 162, 162, .3)))
                .zIndex(50)
                .neverUpdated()
                .build();
    }
}
