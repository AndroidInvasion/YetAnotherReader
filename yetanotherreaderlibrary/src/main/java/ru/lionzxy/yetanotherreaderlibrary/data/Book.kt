package ru.lionzxy.yetanotherreaderlibrary.data

import java.io.Serializable

/**
 * @author Nikita Kulikov <nikita@kulikof.ru>
 * @project YetAnotherReader
 * @date 14.04.18
 */

open class Book(public val title: String, public val author: String, public val bookText: String) : Serializable