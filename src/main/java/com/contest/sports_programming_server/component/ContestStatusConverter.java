package com.contest.sports_programming_server.component;

import com.contest.sports_programming_server.dto.ContestStatus;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ContestStatusConverter implements Converter<String, ContestStatus> {

    @Override
    @NonNull
    public ContestStatus convert(String source) {
        return ContestStatus.valueOf(source.toUpperCase().replace("-", "_"));
    }
}
