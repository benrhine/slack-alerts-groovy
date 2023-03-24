package com.benrhine.plugins.v1.model

class Element {

    String type

    String actionId

    String initialDate

    String imageUrl

    String altText

    String value

    Text text

    Text placeholder

    def options = []

    void placeholder(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        placeholder = new Text()
        closure.delegate = placeholder
        closure()
    }

    void text(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        text = new Text()
        closure.delegate = text
        closure()
    }

    void option(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        def option = new Option()
        closure.delegate = option
        options << option
        closure()
    }

}
