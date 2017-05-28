package ru.atomofiron.boomstream.models

import java.util.*

class Node(var title: String, var text: String) {

    var createDate: Date = Date()

    constructor(title: String, text: String, createDate: Date) : this(title, text) {
        this.createDate = createDate
    }

    fun contains(query: String, onlyTitle: Boolean): Boolean =
            title.contains(query, true) || text.contains(query, true) && !onlyTitle
}