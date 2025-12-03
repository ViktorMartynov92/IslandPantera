package com.javarush.island.martynov.model;

import com.javarush.island.martynov.config.SimulationConfig;

public class Grass {
    private int height;

    public Grass() {
        this.height = SimulationConfig.RANDOM.nextInt(3);
    }

    public void incrementHeight() {
        if (height < 2) {
            height++;
        }
    }

    public void decrementHeight() {
        if (height > 0) {
            height--;
        }
    }

    public int getHeight() {
        return height;
    }
}
