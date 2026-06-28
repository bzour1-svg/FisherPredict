package com.fisher.overlay;

import java.util.Random;

public class TrackedFish {

    public static final String[][] FISH_TYPES = {
        {"قرش",              "red",    "fast",      "40"},
        {"سمكة كبيرة شريرة", "red",    "medium",    "30"},
        {"السمكة الشريرة",   "red",    "fast",      "30"},
        {"السمكة الحمراء",   "red",    "medium",    "10"},
        {"قنديل البحر",      "blue",   "slow",      "5"},
        {"تمساح",            "green",  "medium",    "12"},
        {"سلحفاة البحر",     "green",  "very_slow", "8"},
        {"سمك القرش الذهبي", "golden", "fast",      "40"},
        {"الدولفين الذهبي",  "golden", "fast",      "40"},
        {"السلحفاة الذهبية", "golden", "very_slow", "8"},
        {"قنديل البحر الذهبي","golden","slow",      "5"},
        {"ملك التنين",       "purple", "medium",    "40"},
    };

    public float x, y, vx, vy, radius, lifetime;
    public String name, color, speedClass;
    public int value;
    public boolean isCatchable;
    private long catchableChangeTime;
    private static final Random random = new Random();

    public TrackedFish(int screenWidth, int screenHeight) {
        String[] type = FISH_TYPES[random.nextInt(FISH_TYPES.length)];
        this.name = type[0];
