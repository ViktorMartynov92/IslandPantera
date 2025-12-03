package com.javarush.island.martynov.app;

import com.javarush.island.martynov.config.SimulationConfig;
import com.javarush.island.martynov.model.Field;
import com.javarush.island.martynov.model.animal.Animal;
import com.javarush.island.martynov.model.animal.carnivore.Carnivore;
import com.javarush.island.martynov.model.animal.herbviore.Herbivore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Main {
    // Сделано public static для доступа из классов Animal
    public static final Field[][] island = new Field[SimulationConfig.ISLAND_SIZE][SimulationConfig.ISLAND_SIZE];

    public static void main(String[] args) {

        SimulationConfig.EXECUTOR.execute(() -> {
            // Создаем пустой остров
            initializeIsland();
            // Населяем остров животными
            createAnimals();
            System.out.println("--- Initial Island State ---");
            printIsland();
        });

        System.out.println("Iteration started");

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SimulationConfig.EXECUTOR.execute(() -> {
                // Сбрасываем статус hasBred
                resetAnimalStates();

                CompletableFuture<Void> moveFuture = CompletableFuture.runAsync(Main::moveAllAnimals, SimulationConfig.EXECUTOR);
                CompletableFuture<Void> growFuture = CompletableFuture.runAsync(Main::growGrass, SimulationConfig.EXECUTOR);

                CompletableFuture.allOf(moveFuture, growFuture).thenRunAsync(() -> {
                    performEatingPhase();
                    performBreedingPhase();
                    printIsland();
                }, SimulationConfig.EXECUTOR);
            });
        }
    }

    private static void initializeIsland() {
        for (int i = 0; i < SimulationConfig.ISLAND_SIZE; i++) {
            for (int j = 0; j < SimulationConfig.ISLAND_SIZE; j++) {
                island[i][j] = new Field();
            }
        }
    }

    private static void resetAnimalStates() {
        for (int i = 0; i < SimulationConfig.ISLAND_SIZE; i++) {
            for (int j = 0; j < SimulationConfig.ISLAND_SIZE; j++) {
                for (Animal animal : island[i][j].getAnimals()) {
                    animal.resetState();
                }
            }
        }
    }

    private static void createAnimals() {
        // Создаем животных и ставим их в случайные ячейки
        Carnivore carnivore1 = new Carnivore();
        island[carnivore1.position.x][carnivore1.position.y].addAnimal(carnivore1);

        Herbivore herbivore1 = new Herbivore();
        island[herbivore1.position.x][herbivore1.position.y].addAnimal(herbivore1);

        Carnivore carnivore2 = new Carnivore();
        island[carnivore2.position.x][carnivore2.position.y].addAnimal(carnivore2);

        Herbivore herbivore2 = new Herbivore();
        island[herbivore2.position.x][herbivore2.position.y].addAnimal(herbivore2);
    }

    private static void moveAllAnimals() {
        List<Animal> animalsToMove = new ArrayList<>();
        for (int i = 0; i < SimulationConfig.ISLAND_SIZE; i++) {
            for (int j = 0; j < SimulationConfig.ISLAND_SIZE; j++) {
                animalsToMove.addAll(island[i][j].getAnimals());
            }
        }

        for (Animal animal : animalsToMove) {
            animal.move();
        }
    }

    private static void performEatingPhase() {
        List<Animal> allAnimals = new ArrayList<>();
        for (int i = 0; i < SimulationConfig.ISLAND_SIZE; i++) {
            for (int j = 0; j < SimulationConfig.ISLAND_SIZE; j++) {
                allAnimals.addAll(island[i][j].getAnimals());
            }
        }

        for (Animal animal : allAnimals) {
            if (island[animal.position.x][animal.position.y].getAnimals().contains(animal)) {
                animal.eat();
            }
        }
    }

    private static void performBreedingPhase() {
        List<Animal> animalsToBreed = new ArrayList<>();
        for (int i = 0; i < SimulationConfig.ISLAND_SIZE; i++) {
            for (int j = 0; j < SimulationConfig.ISLAND_SIZE; j++) {
                animalsToBreed.addAll(island[i][j].getAnimals());
            }
        }

        for (Animal animal : animalsToBreed) {
            if (island[animal.position.x][animal.position.y].getAnimals().contains(animal)) {
                animal.breed();
            }
        }
    }

    private static void growGrass() {
        for (int i = 0; i < SimulationConfig.ISLAND_SIZE; i++) {
            for (int j = 0; j < SimulationConfig.ISLAND_SIZE; j++) {
                island[i][j].getGrass().incrementHeight();
            }
        }
    }

    private static void printIsland() {
        System.out.println("--- Island View ---");
        StringBuilder islandMap = new StringBuilder();
        for (int i = 0; i < SimulationConfig.ISLAND_SIZE; i++) {
            for (int j = 0; j < SimulationConfig.ISLAND_SIZE; j++) {
                islandMap.append(island[i][j].getSymbol()).append(" ");
            }
            islandMap.append("\n");
        }
        System.out.println(islandMap.toString());
        System.out.println("-------------------");


    }
}
