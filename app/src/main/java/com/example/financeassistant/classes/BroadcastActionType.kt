package com.example.financeassistant.classes

enum class BroadcastActionType(val value: Int){
    Unknown(-1),
    Exchange(0),
    AutoCloseTask(1);

    companion object {
        fun from(findValue: Int): BroadcastActionType? = values().firstOrNull { it.value == findValue }
    }
}