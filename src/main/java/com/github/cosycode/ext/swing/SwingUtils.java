package com.github.cosycode.ext.swing;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

/**
 * <b>Description : </b> Swing 工具类
 * <p>
 * <b>created in </b> 2020/5/12
 *
 * @author CPF
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SwingUtils {

    /**
     * (默认)频率, 每隔多少毫秒移动一次
     */
    public static final int INTERVAL = 5;
    /**
     * (默认)基本移动时间
     */
    public static final int BASE_TIME = 1000;
    /**
     * (默认)一次移动多长距离
     */
    public static final int PX_ONE_TIME = 1;

    /**
     * 在一个panel中平滑将一个组件移动至另一个地点(动画效果)
     * 200px长度, 每5ms移动一次, 一次移动1px, 需要1s
     *
     * @param component 组件
     * @param toPoint   最终位置
     */
    public static void moveComp(Component component, Point toPoint) throws InterruptedException {
        moveComp(component, toPoint, BASE_TIME, INTERVAL, PX_ONE_TIME);
    }

    /**
     * 平滑将一个组件移动至另一个地点
     * 200px长度, 每5ms移动一次, 一次移动1px, 需要1s
     *
     * @param component 组件
     * @param toPoint   最终位置
     * @param baseTime  移动基本时间
     * @param interval  多久移动一次
     * @param pxOneTime 一次移动多少距离
     */
    public static void moveComp(Component component, Point toPoint, int baseTime, int interval, int pxOneTime) throws InterruptedException {
        Point fromPoint = component.getLocation();
        int xSub = toPoint.x - fromPoint.x;
        int ySub = toPoint.y - fromPoint.y;
        // 基本倍率
        double sqrt = Math.sqrt((Math.sqrt(Math.pow(xSub, 2) + Math.pow(ySub, 2)) / pxOneTime * interval) / baseTime);
        // 移动次数
        int times = (int) (baseTime * sqrt / interval);
        moveComp(component, toPoint, times, interval);
    }

    /**
     * @param component 组件
     * @param toPoint   移动位置
     * @param times     需要移动多少次
     * @param interval  每帧时间间隔
     */
    public static void moveComp(Component component, Point toPoint, int times, int interval) throws InterruptedException {
        Point fromPoint = component.getLocation();
        int xSub = toPoint.x - fromPoint.x;
        int ySub = toPoint.y - fromPoint.y;
        for (int i = 0; i < times; i++) {
            double p = (double) i / times;
            component.setLocation((int) (fromPoint.x + xSub * p), (int) (fromPoint.y + ySub * p));
            Thread.sleep(interval);
        }
        component.setLocation(toPoint.x, toPoint.y);
    }

}
