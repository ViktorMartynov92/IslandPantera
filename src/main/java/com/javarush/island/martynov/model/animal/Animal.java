package com.javarush.island.martynov.model.animal;

import com.javarush.island.martynov.config.SimulationConfig;
import com.javarush.island.martynov.model.Position;

public abstract class Animal {
    public Position position;
    protected boolean hasBred = false;
    protected int weight;

    public Animal() {
        this.position = new Position(
                SimulationConfig.RANDOM.nextInt(SimulationConfig.ISLAND_SIZE),
                SimulationConfig.RANDOM.nextInt(SimulationConfig.ISLAND_SIZE)
        );
    }

    public abstract void move();

    public abstract void eat();

    public abstract void breed();

    public void resetState() {
        this.hasBred = false;
    }

    public void setHasBred(boolean hasBred) {
        this.hasBred = hasBred;
    }
}
