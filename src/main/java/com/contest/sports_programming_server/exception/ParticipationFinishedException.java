package com.contest.sports_programming_server.exception;

public class ParticipationFinishedException extends RuntimeException{
    public ParticipationFinishedException(){
        super("Участник в соревнованиях для вас окончено");
    }
}
