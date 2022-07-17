package com.mani.lox;

public class errors {

    static void report(int line, String where, String message) {
        System.err.println("[line "+line +"] Error " + where +" : " + message);
    }

}
