package com.example.max.testapp

import android.view.View
import java.util.*

/**
 * Created by max on 18.03.18.
 *
 */
fun Random.intInRangeInclusive(min: Int, max: Int): Int {

    if (min >= max) {
        throw IllegalArgumentException("max must be greater than min")
    }
    return this.nextInt(max - min + 1) + min
}

fun Boolean.toVisible(): Int = if (this) View.VISIBLE else View.INVISIBLE
