package org.inspirecenter.amazechallenge.model;

import java.io.Serializable;

public class Health implements Serializable {

    public static int DEFAULT_HEALTH = 100;
    public static int MIN_HEALTH = 0;
    public static int MAX_HEALTH = 300;

    private int health = DEFAULT_HEALTH;

    public int get() { return health; }

    public void setTo(int amount) {
        if (health > MAX_HEALTH) throw new RuntimeException("Health Error: Cannot set health above " + MAX_HEALTH + ". Current value is " + amount);
        else if (health < MIN_HEALTH) throw new RuntimeException("Health Error: Cannot set health below " + MIN_HEALTH + ". Current value is " + amount);
        else health = amount;
    }//end setTo()

    public void changeBy(int amount) {
        if (health + amount > MAX_HEALTH) health = MAX_HEALTH;
        else if (health + amount < MIN_HEALTH) health = MIN_HEALTH;
        else health += amount;
    }

    public void increaseBy(int amount) {
        if (amount < 0) throw new RuntimeException("Health Error: Cannot increase health by a negative number: " + amount);
        if (health + amount > MAX_HEALTH) health = MAX_HEALTH;
        else health += amount;
    }//end increaseBy()

    public void decreaseBy(int amount) {
        if (amount < 0) throw new RuntimeException("Health Error: Cannot decrease health by a negative number: " + amount);
        if (health - amount < MIN_HEALTH) health = MIN_HEALTH;
        else health -= amount;
    }//end decreaseBy()

    public void doubleHealth() {
        if (health * 2 > MAX_HEALTH) health = MAX_HEALTH;
        else health *= 2;
    }//end doubleHealth()

    public void halveHealth() {
        if (health / 2 < MIN_HEALTH) health = MIN_HEALTH; //This should never occur.
        else health /= 2;
    }//end halveHealth()

    public void resetHealth() { health = DEFAULT_HEALTH; }

    public boolean isAtMin() { return (health <= MIN_HEALTH); }

}//end class Health
