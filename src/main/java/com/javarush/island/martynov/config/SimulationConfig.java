package com.javarush.island.martynov.config;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class SimulationConfig {

    public static final int ISLAND_SIZE = 3;

    // Параметры хищников
    public static final double CARNIVORE_SUCCESSFUL_HUNT_PROBABILITY = 0.75;
    public static final double CARNIVORE_BREED_PROBABILITY = 0.50;
    public static final double CARNIVORE_DOUBLE_BREED_PROBABILITY = 0.50;
    public static final int CARNIVORE_WEIGHT = 50;

    // Параметры травоядных
    public static final double HERBIVORE_BREED_PROBABILITY = 0.85;
    public static final int HERBIVORE_MIN_BREED = 1;
    public static final int HERBIVORE_MAX_BREED = 2;
    public static final int HERBIVORE_WEIGHT = 30;

    // Общие ресурсы
    public static final Random RANDOM = new Random();
    public static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(2);

}
