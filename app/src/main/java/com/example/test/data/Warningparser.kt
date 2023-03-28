package com.example.test.data

import com.example.test.model.Warning
import java.util.*

class Warningparser {
    //Work in progress. Må finne ut av hva som er kordinater for warnings slik at vi kan lage geometriske representasjoner av objektene på kartet.
    fun parse(input: String): List<Warning> {
        val liste: MutableList<Warning> = mutableListOf()
        val s: Scanner = Scanner(input)
        while (s.hasNextLine()) {
            if (s.nextLine() == "ZCZC") {
                while ("".equals(s.nextLine().trim())) {
                    var data: String = s.nextLine()
                    data += s.nextLine()
                    data += s.nextLine()
                    liste.add(Warning(data))
                }
            }
        }
        return liste
    }
}