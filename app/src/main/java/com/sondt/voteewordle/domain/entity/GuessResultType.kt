package com.sondt.voteewordle.domain.entity

import com.squareup.moshi.Json

enum class GuessResultType {
    @Json(name = "correct")
    CORRECT,
    @Json(name = "present")
    PRESENT,
    @Json(name = "absent")
    ABSENT;
}
