/*
 * Copyright (c) 2009. 財團法人資訊工業策進會. All right reserved.
 */
package utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 紀錄經過時間
 *
 * @author 960122 
 *         在 2009/10/15 建立
 */
public class PerformanceTester {

    private class Calculator {

        private String name;

        private long allThroughTime;

        private long startTime;

        private long stopTime;

        public Calculator(String name) {
            this.name = name;
            this.allThroughTime = 0;
            this.startTime = Calendar.getInstance().getTimeInMillis();
            this.stopTime = 0;
        }

        /**
         * @return 傳回 allThroughTime。
         */
        public long getAllThroughTime() {
            return this.allThroughTime;
        }

        /**
         * @param startTime 要設定的 startTime。
         */
        public void setStartTime(long startTime) {
            if (this.startTime != 0) {
                System.out.println("請注意:進入點設了第二次:" + this.name);
            }
            this.startTime = startTime;
        }

        /**
         * @param stopTime 要設定的 stopTime。
         */
        public void setStopTime(long stopTime) {
            if (this.startTime == 0) {
//                throw new EJBException("沒有進入點");
                return;
            }

            this.allThroughTime += stopTime - this.startTime;

            //做完要清空, 防呆
            this.startTime = 0;
            this.stopTime = 0;
        }

        /**
         * @return 傳回 name。
         */
        public String getName() {
            return this.name;
        }

        /**
         * @param name 要設定的 name。
         */
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 存放要測試的方法
     */
    private Map<String, Calculator> goalMap = new HashMap<String, Calculator>();

    public PerformanceTester() {

    }

    /**
     * 重置
     */
    public void reset() {
        this.goalMap.clear();
    }

    /**
     * 測試開始點
     */
    public void startPoint(String name) {
        if (this.goalMap.containsKey(name)) {
            Calculator cal = (Calculator) this.goalMap.get(name);
            cal.setStartTime(Calendar.getInstance().getTimeInMillis());
        } else {
            this.goalMap.put(name, new Calculator(name));
        }
    }

    /**
     * 測試停止點
     */
    public void stopPoint(String name) {
        if (this.goalMap.containsKey(name)) {
            Calculator cal = (Calculator) this.goalMap.get(name);
            cal.setStopTime(Calendar.getInstance().getTimeInMillis());
        } else {
            System.out.println("沒有==" + name + "==的進入點");
        }
    }

    /**
     * 印出紀錄
     */
    public void printResult() {

        System.out.println("共有" + this.goalMap.size() + "筆時間花費紀錄");
        for (Calculator cal : this.goalMap.values()) {
            System.out.println(cal.getName() + ":" + cal.getAllThroughTime() + " msecs");
        }
    }

    /**
     * 排序,消耗時間越多排越前面
     */
    public void sort() {
        Calculator[] cal = (Calculator[]) this.goalMap.values().toArray(new Calculator[0]);
        for (int i = 0; i < cal.length; i++) {
            for (int j = 0; j < i; j++) {
                if (cal[i].getAllThroughTime() > cal[j].getAllThroughTime()) {
                    Calculator tmp = cal[i];
                    cal[i] = cal[j];
                    cal[j] = tmp;
                }
            }
        }
        
        this.goalMap.clear();
        for (int i = 0; i < cal.length; i++) {
            this.goalMap.put(cal[i].getName(), cal[i]);
        }
    }
    //##################################
    //## sub-block : XXXX(可依功能區分) 
    //##################################
    //====
    //== [Method] Block Stop 
    //================================================
    //== [Main Method] Block Start
    //====
    //public static void main(String[] args) {
    //  
    //}
    //====
    //== [Main Method] Block Stop 
    //================================================
}