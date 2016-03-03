package com.github.onsdigital.pootle.json;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    public String message;
    public boolean error;
}
