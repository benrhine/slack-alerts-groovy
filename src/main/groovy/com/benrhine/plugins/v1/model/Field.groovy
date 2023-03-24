package com.benrhine.plugins.v1.model

import com.google.gson.annotations.SerializedName


class Field {

    String title

    String value

    @SerializedName('short')
    Boolean shortValue = true

}
