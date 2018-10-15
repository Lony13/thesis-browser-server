package com.koczy.kurek.mizera.thesisbrowser.service;

import javax.servlet.http.HttpServletRequest;

public abstract class DemoServiceResolver<T> {
    T implementation;
    T demoImplementation;

    public DemoServiceResolver(T implementation, T demoImplementation) {
        this.implementation = implementation;
        this.demoImplementation = demoImplementation;
    }

    protected T resolveService(HttpServletRequest request){
        if (request.isUserInRole("ROLE_DEMO")) {
            return demoImplementation;
        } else {
            return implementation;
        }
    }
}
