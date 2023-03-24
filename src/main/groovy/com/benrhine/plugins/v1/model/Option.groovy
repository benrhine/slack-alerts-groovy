package com.benrhine.plugins.v1.model

class Option {

    String value

    Text text

    String dismissText

    void text(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        text = new Text()
        closure.delegate = text
        closure()
    }

}
