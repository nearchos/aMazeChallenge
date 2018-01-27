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

    APPLE("apple", REWARD, FRUIT, "Apple reward +30 health", 30, 0, 7),
    BANANA("banana", REWARD, FRUIT, "Banana reward +15 health", 15, 0, 10),
    STRAWBERRY("strawberry", REWARD, FRUIT, "Strawberry reward +10 health", 10, 0, 10),
    PEACH("peach", REWARD, FRUIT, "Peach reward +5 health", 5, 0, 10),
    WATERMELON("watermelon", REWARD, FRUIT, "Watermelon reward +20 health", 20, 0, 10),
    GRAPES("grapes", REWARD, FRUIT, "Grapes reward +25 health", 25, 0, 7),
    ORANGE("orange", REWARD, FRUIT, "Orange reward +50 health", 50, 0, 7),

    COIN_5("coin5", REWARD, NONE, "Coin reward +5 points", 0, 5, 10),
    COIN_10("coin10", REWARD, NONE, "Coin reward +10 points", 0, 10, 6),
    COIN_20("coin20", REWARD, NONE, "Coin reward +20 points", 0, 20, 4),
    GIFTBOX("giftbox", REWARD, NONE, "Gift reward +50 points", 0, 50, 3),

    BOMB("bomb", Bias.PENALTY, NONE, "Bomb penalty -50 health", 50, 0, 10), // customize bombs?

    DOUBLE_MOVES("doublemoves", REWARD, NONE, "Player gets to do 2 moves", 0, 0, 2),
    TRAP("trap", PENALTY, NONE, "Player loses 1 move", 0, 0, 2)

    ;

    public enum Bias { REWARD, PENALTY, NONE };
    public enum Category { FRUIT, SNACK, NONE };

    private String imageResourceName;
    private Bias bias;
    private Category category;
    private String description;
    private int absoluteHealth; // a zero or positive integer
    private int absolutePoints; // a zero or positive integer
    private int defaultState; // used for countdowns, e.g. in the bomb

    /*public static PickableType getRandomFruit() {
        return random(REWARD, FRUIT);
    }*/

    public static PickableType getRandomReward() {
        final Vector<PickableType> selected = new Vector<>();
        for (PickableType pickableType : values()) {
            if(pickableType.bias == REWARD) {
                selected.add(pickableType);
            }
        }
        if(selected.isEmpty()) throw new RuntimeException("Invalid selection criteria: " + REWARD.name());
        Collections.shuffle(selected);
        return selected.firstElement();
    }

    public static PickableType getRandomPenalty() {
        return random(PENALTY, NONE);
    }

    public static PickableType random(Bias bias, Category category) {
        final Vector<PickableType> selected = new Vector<>();
        for (PickableType pickableType : values()) {
            if(pickableType.bias == bias && pickableType.category == category) {
                selected.add(pickableType);
            }
        }
        if(selected.isEmpty()) throw new RuntimeException("Invalid selection criteria: " + bias + ", " + category);
        Collections.shuffle(selected);
        return selected.firstElement();
    }

    private PickableType(String imageResourceName, Bias bias, Category category, String description, int absoluteHealth, int absolutePoints, int defaultState) {
        this.imageResourceName = imageResourceName;
        this.bias = bias;
        this.category = category;
        this.description = description;
        this.absoluteHealth = absoluteHealth;
        this.absolutePoints = absolutePoints;
        this.defaultState = defaultState;
    }

    public String getImageResourceName() {
        return imageResourceName;
    }

    public int getPointsChange() {
        return bias == REWARD ? absolutePoints : -absolutePoints;
    }

    public int getHealthChange() {
        return bias == REWARD ? absoluteHealth: -absoluteHealth;
    }

    public int getDefaultState() { return defaultState; }

    public String getDescription() { return description; }

    public Bias getBias() {
        return bias;
    }

    public Category getCategory() {
        return category;
    }

}