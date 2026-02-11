package com.example.minesweeper_schoolproject.minesweeper;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyLogger implements NativeKeyListener {
    
    public KeyLogger() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(this);
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }
    
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Optional: log releases if needed
    }
    
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        // Optional: log typed characters
    }
}
