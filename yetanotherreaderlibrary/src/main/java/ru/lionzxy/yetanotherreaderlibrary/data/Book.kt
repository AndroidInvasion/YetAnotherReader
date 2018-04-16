package ru.lionzxy.yetanotherreaderlibrary.data

import java.io.Serializable

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 14.04.18
 */

open class Book(val title: String,
                val author: String,
                val bookText: String,
                val imageUrl: String = "",
                val description: String = "") : Serializable