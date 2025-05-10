package com.example.numbersongkaraoke

import android.content.Context

object Prefs {
    private const val KEY_UNLOCKED = "unlocked_level"
    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences("levels", Context.MODE_PRIVATE)

    /** Highest level the user has unlocked (defaults to 1) */
    var Context.unlockedLevel: Int
        get() = prefs(this).getInt(KEY_UNLOCKED, 1)
        set(value) {
            prefs(this).edit().putInt(KEY_UNLOCKED, value).apply()
        }
}
