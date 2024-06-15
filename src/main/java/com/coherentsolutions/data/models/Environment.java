package com.coherentsolutions.data.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Getter;

import java.util.List;

@Getter
public class Environment {
    @JacksonXmlElementWrapper(useWrapping = false)
    private final List<Parameter> parameter;

    public Environment(List<Parameter> parameter) {
        this.parameter = parameter;
    }
}