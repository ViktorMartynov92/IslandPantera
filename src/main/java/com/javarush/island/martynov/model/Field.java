package com.javarush.island.martynov.model;

import com.javarush.island.martynov.model.animal.Animal;
import com.javarush.island.martynov.model.animal.Carnivore;
import com.javarush.island.martynov.model.animal.Herbivore;
import java.util.ArrayList;
import java.util.List;

public class Field {
    private final ArrayList<Animal> animals = new ArrayList<>();
    private final Grass grass = new Grass();

    public synchronized void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public synchronized void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public List<Animal> getAnimals() {
        // Возвращаем копию, чтобы избежать ConcurrentModificationException
        return new ArrayList<>(animals);
    }

    public Grass getGrass() {
        return grass;
    }

    public String getSymbol() {
        boolean hasCarnivore = false;
        boolean hasHerbivore = false;

        for (Animal animal : animals) {
            if (animal instanceof Carnivore) {
                hasCarnivore = true;
            } else if (animal instanceof Herbivore) {
                hasHerbivore = true;
            }
        }

        if (hasCarnivore && hasHerbivore) {
            return "X";
        } else if (hasCarnivore) {
            return "C";
        } else if (hasHerbivore) {
            return "H";
        } else {
            switch (grass.getHeight()) {
                case 0:
                    return "_";
                case 1:
                    return "=";
                case 2:
                    return "≡";
                default:
                    return " ";
            }
        }
    }
}
