package com.example.test.data

import com.example.test.model.Warning
import java.util.*

class Warningparser {
    fun parse(input: String): List<Warning> {
        val liste: MutableList<Warning> = mutableListOf()
        val s: Scanner = Scanner(input)
        while (s.hasNextLine()) {
            if (s.nextLine() == "ZCZC") {
                var data: String = s.nextLine()
                data += s.nextLine()
                data += s.nextLine()
                liste.add(Warning(data))
            }
        }
        return liste
    }
}