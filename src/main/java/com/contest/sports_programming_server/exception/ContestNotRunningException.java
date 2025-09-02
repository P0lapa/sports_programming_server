package com.contest.sports_programming_server.exception;

public class ContestNotRunningException extends RuntimeException {
    public ContestNotRunningException() {
        super("Contest not running");
    }
}
