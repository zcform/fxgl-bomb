package com.zc.boom;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * 2023-11-16
 * zhangxl
 */
public class BoomApplication extends GameApplication {
    public static void main(String[] args) {
        launch(args);
    }

    int level = 0;

    int max_wlen = BoomConstant.max_size_w.length - 1;
    int max_hlen = BoomConstant.max_size_h.length - 1;

    int w = BoomConstant.min_left * 2 + (BoomConstant.max_size_w[max_wlen]) * BoomConstant.cell_w + BoomConstant.right;
    int h = BoomConstant.top + (BoomConstant.max_size_h[max_hlen]) * BoomConstant.cell_w + BoomConstant.min_bottom;

    int left = (w - BoomConstant.right - (BoomConstant.max_size_w[level]) * BoomConstant.cell_w) / 2;
    int cell_size_w = BoomConstant.max_size_w[level];
    int cell_size_h = BoomConstant.max_size_h[level];
    int cell_w = BoomConstant.cell_w;

    BoomCell[][] cells = new BoomCell[cell_size_w][cell_size_h];
    List<BoomCell> cellList = new ArrayList<>();

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("ZC-boom");
        gameSettings.setVersion("");
        gameSettings.setWidth(w);
        gameSettings.setHeight(h);
        gameSettings.setAppIcon("noom/boom.png");
    }

    @Override
    public void initGame() {
        getGameWorld().addEntityFactory(new BoomFactory());

        spawn("laughP");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        set("isStart", false);
        set("hasBoom", false);
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initPhysics() {

    }

    // 计时
    Text time = new Text();

    @Override
    protected void initUI() {
        time.setText("0 s");

        time.setFont(new Font(20));
        time.setTranslateX(w - 180);
        time.setTranslateY(33);
        time.setOpacity(.7);

        addUINode(time);
    }

    // 是否结束
    void over() {
        spawn("over");

        set("isStart", false);

        showBooom();
    }

    private void isOkOver() {
        boolean isOver = false;
        int boomSize = BoomConstant.max_boom[level];

        long count = cellList.stream().filter(c -> BoomEntityType.NUM == c.getType()).count();
        long bs = cellList.size() - count;

        if (boomSize == bs) {
            isOver = true;
            System.out.println("完成！");
        }

        List<BoomCell> collect = cellList.stream().filter(c -> BoomEntityType.FLAG == c.getType()).collect(Collectors.toList());
        long fct = collect.size();

        if (fct == boomSize) {
            long count1 = collect.stream().filter(c -> -1 == c.getScore()).count();

            if (boomSize == count1) {
                isOver = true;
                System.out.println("完成！");
            }
        }

        if (isOver) {
            showBooom();
        }
    }

    void showBooom() {
        spawn("over");

        for (BoomCell cell : cellList) {
            int score = cell.getScore();

            if (-1 == score) {
                reCellEntity(cell, "boom");
            } else {
                reCellEntity(cell, cell.getScore());
            }
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        if (getb("isStart")) {
            long sTime = geto("sTime");

            long now = System.currentTimeMillis();

            long compare = Math.floorDiv((now - sTime), 1000);

            time.setText(compare + " s");
        }
    }

    void setLevel(int lv) {
        getGameWorld().getSingleton(BoomEntityType.LV).removeFromWorld();

        level = lv;
        left = (w - BoomConstant.right - (BoomConstant.max_size_w[level]) * BoomConstant.cell_w) / 2;
        cell_size_w = BoomConstant.max_size_w[level];
        cell_size_h = BoomConstant.max_size_h[level];
        cells = new BoomCell[cell_size_w][cell_size_h];
        cellList = new ArrayList<>();

        spawn("background");

        refreshPanel();

        set("isStart", true);
        set("sTime", System.currentTimeMillis());
    }

    private void refreshPanel() {
        set("hasBoom", false);

        for (int ic = 0; ic < cells.length; ic++) {
            BoomCell[] cell = cells[ic];
            for (int i = 0; i < cell.length; i++) {
                cell[i] = new BoomCell(ic, i);

                Point2D pxy = new Point2D(cell[i].getX() * cell_w, cell[i].getY() * cell_w);

                Entity spawn = spawn("cellEmpty", new SpawnData(pxy));

                cell[i].setEntity(spawn);
                cell[i].setType(BoomEntityType.EMPTY);
                cell[i].setScore(0);

                cellList.add(cell[i]);
            }
        }
    }

    private void initBoom(Point2D xy) {
        int x0 = (int) xy.getX();
        int y0 = (int) xy.getY();

        int num = BoomConstant.max_boom[level];

        while (num > 0) {
            int x = random(0, cell_size_w - 1);
            int y = random(0, cell_size_h - 1);

            if (x0 != x && y0 != y) {
                BoomCell cell = cells[x][y];

                int score = cell.getScore();

                if (score == 0) {
                    cell.setScore(-1);
                    num--;
                }
            }
        }

        writeNum();
    }

    private void writeNum() {
        for (BoomCell[] cellx : cells) {
            for (BoomCell cell : cellx) {
                if (-1 != cell.getScore()) {
                    List<BoomCell> range = getRange(cell);

                    long count = range.stream().filter(c -> -1 == c.getScore()).count();

                    cell.setScore(count);
                }
            }
        }
    }

    public void mouse_clicked(MouseEvent e, Point2D xy) {
        if (!getb("hasBoom")) {
            initBoom(xy);
            set("hasBoom", true);
        }

        int x = (int) xy.getX();
        int y = (int) xy.getY();

        BoomCell cell = cells[x][y];
        BoomEntityType type = cell.getType();
        int score = cell.getScore();

        if (e.getButton() == MouseButton.PRIMARY) {
            if (score == -1) {
                over();
                reCellEntity(cell, "boomRed");
            } else {
                if (BoomEntityType.EMPTY == type) {
                    openNum(cell);
                }
            }

            //int clickCount = e.getClickCount();
            //System.out.println("???????\t"+clickCount);
        } else if (e.getButton() == MouseButton.SECONDARY) {
            switch (type) {
                case EMPTY -> {
                    cell.setType(BoomEntityType.FLAG);
                    reCellEntity(cell, "flag");
                }
                case FLAG -> {
                    cell.setType(BoomEntityType.WH);
                    reCellEntity(cell, "flagW");
                }
                case WH -> {
                    cell.setType(BoomEntityType.EMPTY);
                    reCellEntity(cell, "cell");
                }
            }
        }

        isOkOver();
    }

    private void openNum(BoomCell cell) {
        cell.setType(BoomEntityType.NUM);
        if (cell.getScore() == 0) {
            openNum0(cell);
        } else {
            reCellEntity(cell, cell.getScore());
        }
    }

    private void openNum0(BoomCell cell) {
        cell.setType(BoomEntityType.NUM);
        reCellEntity(cell, cell.getScore());

        List<BoomCell> range = getRange(cell)
                .stream().filter(c -> BoomEntityType.EMPTY == c.getType())
                .collect(Collectors.toList());

        for (BoomCell boomCell : range) {
            if (boomCell.getScore() == 0) {
                openNum0(boomCell);
            } else {
                if (boomCell.getScore() != -1) {
                    reCellEntity(cell, boomCell.getScore());
                }
            }
        }
    }

    void reCellEntity(BoomCell cell, int fname) {
        reCellEntity(cell, fname + "");
    }

    void reCellEntity(BoomCell cell, String fname) {
        cell.getEntity().removeFromWorld();

        Point2D pxy = new Point2D(cell.getX() * cell_w, cell.getY() * cell_w);

        Entity spawn = spawn("cellType", new SpawnData(pxy).put("num", fname));
        cell.setEntity(spawn);
    }

    private List<BoomCell> getRange(BoomCell cell) {
        List<BoomCell> list = new ArrayList<>();

        int maxX = cells.length;
        int maxY = cells[0].length;

        int x = cell.getX();
        int y = cell.getY();

        int xq1 = x - 1;
        int xj1 = x + 1;

        int yq1 = y - 1;
        int yj1 = y + 1;

        if (yq1 >= 0) {
            if (xq1 >= 0) {
                list.add(cells[xq1][yq1]);
            }

            list.add(cells[x][yq1]);

            if (xj1 < maxX) {
                list.add(cells[xj1][yq1]);
            }
        }

        if (xq1 >= 0) {
            list.add(cells[xq1][y]);
        }

        if (xj1 < maxX) {
            list.add(cells[xj1][y]);
        }

        if (yj1 < maxY) {
            if (xq1 >= 0) {
                list.add(cells[xq1][yj1]);
            }

            list.add(cells[x][yj1]);

            if (xj1 < maxX) {
                list.add(cells[xj1][yj1]);
            }
        }

        return list;
    }
}
