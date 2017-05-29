package ru.atomofiron.boomstream.models

abstract class Node {
    abstract fun contains(text: String): Boolean
}