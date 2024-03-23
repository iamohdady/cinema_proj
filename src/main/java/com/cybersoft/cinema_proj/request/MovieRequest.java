package com.cybersoft.cinema_proj.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieRequest {

    @JsonProperty("name")
    public String name;
}
