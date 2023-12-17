package com.harleylizard.revival.gradle;

import lombok.Data;

@Data
public final class Pair<L, R> {
    private final L l;
    private final R r;
}
