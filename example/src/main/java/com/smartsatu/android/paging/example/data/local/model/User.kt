package com.smartsatu.android.paging.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "users")
data class User(
        @PrimaryKey
        val id: Long,
        val firstName: String,
        val lastName: String
) {
    companion object {

        private val random = Random(100000)

        fun random(id: Long): User {
            return User(
                    id,
                    "First$id",
                    "Last$id"
            )
        }

        fun random(page: Int, count: Int): List<User> {
            val users = mutableListOf<User>()
            for (i in 0 until count) {
                users.add(random(((page - 1) * count + i).toLong()))
            }
            return users
        }
    }
}