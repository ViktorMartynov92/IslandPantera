package com.javarush.island.martynov.app;

import com.javarush.island.martynov.config.SimulationConfig;

import java.util.concurrent.CompletableFuture;

import static com.javarush.island.martynov.app.Island.*;

public class Runner {
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

                CompletableFuture<Void> moveFuture = CompletableFuture.runAsync(Island::moveAllAnimals, SimulationConfig.EXECUTOR);
                CompletableFuture<Void> growFuture = CompletableFuture.runAsync(Island::growGrass, SimulationConfig.EXECUTOR);

                CompletableFuture.allOf(moveFuture, growFuture).thenRunAsync(() -> {
                    performEatingPhase();
                    performBreedingPhase();
                    printIsland();
                }, SimulationConfig.EXECUTOR);
            });
        }
    }
}
