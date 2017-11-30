//package org.inspirecenter.amazechallenge.data;
//
//import com.googlecode.objectify.annotation.Entity;
//import com.googlecode.objectify.annotation.Id;
//import com.googlecode.objectify.annotation.Index;
//
//@Entity
//public class Challenge {
//
//    @Id public Long id;
//    @Index public String name;
//    @Index public int apiVersion; // used to determine if the player can 'support' this challenge
//    public String description; // brief description of the challenge
//    public boolean canRepeat; // whether a player can play again and again
//    public boolean canJoinAfterStart; // whether a player can join after the start of the challenge
//    public boolean canStepOnEachOther; // whether players can step on each other
//    @Index public long startTimestamp; // when the challenge starts being available, or zero if available from the beginning of time (timestamp in UTC)
//    public long endTimestamp; // when the challenge ends being available, or zero if available forever (timestamp in UTC)
//    public Grid grid;
//
//    public Challenge() {
//        super();
//    }
//
//    public Challenge(String name, int apiVersion, String description, boolean canRepeat, boolean canJoinAfterStart, boolean canStepOnEachOther, long startTimestamp, long endTimestamp, Grid grid) {
//        this();
//        this.name = name;
//        this.apiVersion = apiVersion;
//        this.description = description;
//        this.canRepeat = canRepeat;
//        this.canJoinAfterStart = canJoinAfterStart;
//        this.canStepOnEachOther = canStepOnEachOther;
//        this.startTimestamp = startTimestamp;
//        this.endTimestamp = endTimestamp;
//        this.grid = grid;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public int getApiVersion() {
//        return apiVersion;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public boolean isCanRepeat() {
//        return canRepeat;
//    }
//
//    public boolean isCanJoinAfterStart() {
//        return canJoinAfterStart;
//    }
//
//    public boolean isCanStepOnEachOther() {
//        return canStepOnEachOther;
//    }
//
//    public long getStartTimestamp() {
//        return startTimestamp;
//    }
//
//    public long getEndTimestamp() {
//        return endTimestamp;
//    }
//
//    public Grid getGrid() {
//        return grid;
//    }
//}