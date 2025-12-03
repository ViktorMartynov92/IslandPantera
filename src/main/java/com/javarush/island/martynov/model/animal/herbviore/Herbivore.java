package com.javarush.island.martynov.model.animal.herbviore;

import com.javarush.island.martynov.app.Main;
import com.javarush.island.martynov.config.SimulationConfig;
import com.javarush.island.martynov.model.Field;
import com.javarush.island.martynov.model.Position;
import com.javarush.island.martynov.model.animal.Animal;

import java.util.ArrayList;

public class Herbivore extends Animal {
    public Herbivore() {
        this.weight = SimulationConfig.HERBIVORE_WEIGHT;
    }

    @Override
    public void move() {
        // Используем Main.island, чтобы сохранить оригинальную логику прямого доступа
        Field[][] island = Main.island;

        Position currentPosition = this.position;
        ArrayList<Position> validMoves = new ArrayList<>();
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        for (int i = 0; i < 4; i++) {
            int newX = currentPosition.x + dx[i];
            int newY = currentPosition.y + dy[i];

            if (newX >= 0 && newX < SimulationConfig.ISLAND_SIZE && newY >= 0 && newY < SimulationConfig.ISLAND_SIZE) {
                validMoves.add(new Position(newX, newY));
            }
        }

        if (validMoves.isEmpty()) {
            return;
        }

        Position newPosition = validMoves.get(SimulationConfig.RANDOM.nextInt(validMoves.size()));

        island[currentPosition.x][currentPosition.y].removeAnimal(this);
        island[newPosition.x][newPosition.y].addAnimal(this);
        this.position = newPosition;

        System.out.println("Herbivore is moving to [" + position.x + ", " + position.y + "]");
    }

    @Override
    public void eat() {
        Field currentField = Main.island[this.position.x][this.position.y];
        if (currentField.getGrass().getHeight() > 0) {
            currentField.getGrass().decrementHeight();
            this.weight = Math.min(SimulationConfig.HERBIVORE_WEIGHT, (int) (this.weight * 1.1));
            System.out.println("Herbivore is eating grass, weight is now " + this.weight);
        } else {
            this.weight = (int) (this.weight * 0.9);
            System.out.println("Herbivore is starving, weight is now " + this.weight);
            if (this.weight <= (SimulationConfig.HERBIVORE_WEIGHT * 0.5)) {
                currentField.removeAnimal(this);
                System.out.println("A Herbivore has died of starvation.");
            }
        }
    }

    @Override
    public void breed() {
        new Thread(() -> {
            Field currentField = Main.island[this.position.x][this.position.y];
            synchronized (currentField) {
                if (this.hasBred) {
                    return;
                }

                Herbivore partner = null;

                for (Animal potentialPartner : currentField.getAnimals()) {
                    if (potentialPartner instanceof Herbivore && potentialPartner != this && !((Herbivore) potentialPartner).hasBred) {
                        partner = (Herbivore) potentialPartner;
                        break;
                    }
                }

                if (partner != null) {
                    if (SimulationConfig.RANDOM.nextDouble() < SimulationConfig.HERBIVORE_BREED_PROBABILITY) {
                        int babyCount = SimulationConfig.RANDOM.nextInt(SimulationConfig.HERBIVORE_MAX_BREED - SimulationConfig.HERBIVORE_MIN_BREED + 1) + SimulationConfig.HERBIVORE_MIN_BREED;

                        for (int i = 0; i < babyCount; i++) {
                            Herbivore baby = new Herbivore();
                            baby.position = this.position;
                            baby.setHasBred(true);
                            currentField.addAnimal(baby);
                        }

                        this.setHasBred(true);
                        partner.setHasBred(true);

                        System.out.println("Herbivore has bred! " + babyCount + " baby/babies born.");
                    }
                }
            }
        }).start();
    }
}
