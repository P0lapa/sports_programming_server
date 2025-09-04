package com.contest.sports_programming_server.exception;

public class UnsupportedLanguageException extends RuntimeException {
    public UnsupportedLanguageException() {
        super("Выбранный язык не поддерживается");
    }
}
