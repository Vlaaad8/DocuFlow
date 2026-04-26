package com.example.state;

@FunctionalInterface
public interface RequestStep {
    void execute(RequestContext context, Runnable next);
}
