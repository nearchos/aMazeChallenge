package org.inspirecenter.amazechallenge.model;

import java.util.Collections;
import java.util.Vector;

import static org.inspirecenter.amazechallenge.model.PickableType.Bias.PENALTY;
import static org.inspirecenter.amazechallenge.model.PickableType.Bias.REWARD;
import static org.inspirecenter.amazechallenge.model.PickableType.Category.FRUIT;
import static org.inspirecenter.amazechallenge.model.PickableType.Category.NONE;

/**
 * 26/01/2018.
 */
public enum PickableType {

    APPLE("apple", REWARD, FRUIT, "Apple reward +50 health", 50, 0, 0),
    BANANA("banana", REWARD, FRUIT, "Banana reward +10 health", 50, 0, 0),
    BOMB("bomb", Bias.PENALTY, NONE, "Bomb stateful penalty -50 health", 50, 0, 3), // customize bombs?
    DOUBLE_MOVES("twoturns", REWARD, NONE, "Player gets to do 2 moves", 0, 0, 0);

    public enum Bias { REWARD, PENALTY, NONE };
    public enum Category { FRUIT, SNACK, NONE };

    private String resourceName;
    private Bias bias;
    private Category category;
    private String description;
    private int absoluteHealth; // a zero or positive integer
    private int absolutePoints; // a zero or positive integer
    private int defaultState; // used for countdowns, e.g. in the bomb

    private PickableType(String resourceName, Bias bias, Category category, String description, int absoluteHealth, int absolutePoints, int defaultState) {
        this.resourceName = resourceName;
        this.bias = bias;
        this.category = category;
        this.description = description;
        this.absoluteHealth = absoluteHealth;
        this.absolutePoints = absolutePoints;
        this.defaultState = defaultState;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getPointsChange() {
        return bias == REWARD ? absolutePoints : -absolutePoints;
    }

    public int getHealthChange() {
        return bias == REWARD ? absoluteHealth: -absoluteHealth;
    }

    static public PickableType randomFruit() {
        return random(REWARD, FRUIT);
    }

    static public PickableType randomPenalty() {
        return random(PENALTY, NONE);
    }

    static public PickableType random(Bias bias, Category category) {
        final Vector<PickableType> selected = new Vector<>();
        for (PickableType pickableType : values()) {
            if(pickableType.bias == REWARD && pickableType.category == category) {
                selected.add(pickableType);
            }
        }
        if(selected.isEmpty()) throw new RuntimeException("Invalid selection criteria: " + bias + ", " + category);
        Collections.shuffle(selected);
        return selected.firstElement();
    }
}