package com.zc.boom;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.shape.Line;

/**
 * TODO
 * 2023-11-24
 * zhangxl
 */
public class BoomGridView extends Parent {
    public BoomGridView(int size_w, int size_h, int cellWidth, int cellHeight) {
        size_w++;
        size_h++;

        Group linesGroup = new Group();
        for (int x = 0; x < size_w; x++) {
            var lineX = new Line(x * cellWidth, 0, x * cellWidth, (size_h - 1) * cellWidth);

            linesGroup.getChildren().add(lineX);
        }

        for (int x = 0; x < size_h; x++) {
            var lineY = new Line(0, x * cellHeight, (size_w - 1) * cellWidth, x * cellHeight);

            linesGroup.getChildren().add(lineY);
        }

        getChildren().addAll(linesGroup);
    }

}
