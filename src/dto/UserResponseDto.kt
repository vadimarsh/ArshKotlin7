package com.example.dto

import com.example.model.Author


data class UserResponseDto(val id: Long, val username: String) {
    companion object {
        fun fromModel(model: Author) = UserResponseDto(
                id = model.id,
                username = model.username
        )

        fun unknown() = UserResponseDto(
                id = 0,
                username = "unknown"
        )
    }
}
