package com.example.minesweeper_schoolproject.minesweeper;

import java.security.SecureRandom;

public class SecureSeedGenerator {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public static long nextSeed(){
        return secureRandom.nextLong();
    }
}
