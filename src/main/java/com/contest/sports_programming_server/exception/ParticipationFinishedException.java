package com.contest.sports_programming_server.exception;

public class ParticipationFinishedException extends RuntimeException{
    public ParticipationFinishedException(){
        super("Participation is finished for you");
    }
}
