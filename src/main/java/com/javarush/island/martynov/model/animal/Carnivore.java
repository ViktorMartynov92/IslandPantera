package com.javarush.island.martynov.model.animal;

import com.javarush.island.martynov.app.Island;
import com.javarush.island.martynov.config.SimulationConfig;
import com.javarush.island.martynov.model.Field;
import com.javarush.island.martynov.model.Position;

import java.util.ArrayList;

public class Carnivore extends Animal {
    public Carnivore() {
        this.weight = SimulationConfig.CARNIVORE_WEIGHT;
    }

    @Override
    public void move() {
        // Используем Main.island, чтобы сохранить оригинальную логику прямого доступа
        Field[][] island = Island.island;

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

        System.out.println("Carnivore is moving to [" + position.x + ", " + position.y + "]");
    }

    @Override
    public void eat() {
        Field currentField = Island.island[this.position.x][this.position.y];
        Herbivore prey = null;

        for (Animal animal : currentField.getAnimals()) {
            if (animal instanceof Herbivore) {
                prey = (Herbivore) animal;
                break;
            }
        }

        if (prey != null && SimulationConfig.RANDOM.nextDouble() < SimulationConfig.CARNIVORE_SUCCESSFUL_HUNT_PROBABILITY) {
            currentField.removeAnimal(prey);
            this.weight = Math.min(SimulationConfig.CARNIVORE_WEIGHT, this.weight + (int) (prey.weight * 0.5));
            System.out.println("Carnivore ate a Herbivore, weight is now " + this.weight);
        } else {
            if (prey != null) {
                System.out.println("Carnivore is unsuccessful");
            } else {
                System.out.println("Carnivore is starving");
            }
            this.weight = (int) (this.weight * 0.9);
            System.out.println("Carnivore weight is now " + this.weight);
            if (this.weight <= (SimulationConfig.CARNIVORE_WEIGHT * 0.5)) {
                currentField.removeAnimal(this);
                System.out.println("A Carnivore has died of starvation.");
            }
        }
    }

    @Override
    public void breed() {
        new Thread(() -> {
            Field currentField = Island.island[this.position.x][this.position.y];
            synchronized (currentField) {
                if (this.hasBred) {
                    return;
                }

                Carnivore partner = null;

                for (Animal potentialPartner : currentField.getAnimals()) {
                    if (potentialPartner instanceof Carnivore && potentialPartner != this && !((Carnivore) potentialPartner).hasBred) {
                        partner = (Carnivore) potentialPartner;
                        break;
                    }
                }

                if (partner != null) {
                    if (SimulationConfig.RANDOM.nextDouble() < SimulationConfig.CARNIVORE_BREED_PROBABILITY) {
                        int babyCount = 1;
                        Carnivore baby1 = new Carnivore();
                        baby1.position = this.position;
                        baby1.setHasBred(true);
                        currentField.addAnimal(baby1);

                        if (SimulationConfig.RANDOM.nextDouble() < SimulationConfig.CARNIVORE_DOUBLE_BREED_PROBABILITY) {
                            Carnivore baby2 = new Carnivore();
                            baby2.position = this.position;
                            baby2.setHasBred(true);
                            currentField.addAnimal(baby2);
                            babyCount = 2;
                        }

                        this.setHasBred(true);
                        partner.setHasBred(true);

                        System.out.println("Carnivore has bred! " + babyCount + " baby/babies born.");
                    }
                }
            }
        }).start();
    }
}
