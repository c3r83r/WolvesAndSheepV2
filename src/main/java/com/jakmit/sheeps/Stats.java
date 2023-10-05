package com.jakmit.sheeps;

import java.util.ArrayList;

public class Stats extends Thread {
    private ArrayList<Sheep> sheepArrayList = new ArrayList<Sheep>();
    private ArrayList<Wolf> wolfArrayList = new ArrayList<Wolf>();

    public Stats(ArrayList<Sheep> sheepArrayList, ArrayList<Wolf> wolfArrayList) {
        this.sheepArrayList = sheepArrayList;
        this.wolfArrayList = wolfArrayList;
    }

    public void run() {
        while (true) {
            System.out.println("Sheeps:" + sheepArrayList.size() + "; Wolves:" + wolfArrayList.size());
            /*for (Wolf w : wolfArrayList
            ) {
                System.out.println(w.hungerBar);
            }*/
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
