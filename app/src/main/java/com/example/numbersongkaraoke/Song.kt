package com.example.numbersongkaraoke

/**
 * @param title    Label shown in the list
 * @param rawCount How many steps (e.g. 10 for counting to 10)
 * @param step     Step size (1 = every number; 2 = evens; etc.)
 * @param level    Level index (starting at 1)
 */
data class Song(
    val title: String,
    val countTo: Int,
    val step: Int = 1,
    val level: Int,
    val pattern: List<Int>? = null,
    val startFrom: Int? = null,
    val imageRes: Int? = null,           // drawable resource for DnD
    val useDragAndDrop: Boolean = false  // flag for DnD mode
)

