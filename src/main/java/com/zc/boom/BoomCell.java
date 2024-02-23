package com.zc.boom;

import com.almasb.fxgl.core.collection.grid.Cell;
import com.almasb.fxgl.entity.Entity;

/**
 * TODO
 * 2023-11-24
 * zhangxl
 */
public class BoomCell extends Cell {
    private BoomEntityType type;
    private Entity entity;
    private int score = 0;

    public BoomCell(int x, int y) {
        super(x, y);
        this.type = BoomEntityType.NUM;
    }

    public BoomCell(int x, int y, BoomEntityType type) {
        super(x, y);
        this.type = type;
    }


    public String toString() {
        return "Cell(" + getX() + "," + getY() + "," + getType() + ")";
    }

    public BoomEntityType getType() {
        return type;
    }

    public void setType(BoomEntityType type) {
        this.type = type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setScore(long score) {
        this.score = (int) score;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
