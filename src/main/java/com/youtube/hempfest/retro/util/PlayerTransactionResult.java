package com.youtube.hempfest.retro.util;

public enum PlayerTransactionResult {
        SUCCESS(true), FAILED(false);

        public final boolean transactionSuccess;

        PlayerTransactionResult(boolean b) {
            transactionSuccess = b;
        }
    }