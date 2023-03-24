package com.benrhine.plugins.v1.model

class Accessory {

    String type

    String actionId

    String initialDate

    String imageUrl

    String altText

    Text  placeholder

    void placeholder(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        placeholder = new Text()
        closure.delegate = placeholder
        closure()
    }

}
