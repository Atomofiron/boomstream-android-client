package ru.atomofiron.boomstream.models

abstract class Node {
    var parentCode: String = ""
    abstract fun contains(text: String): Boolean
}