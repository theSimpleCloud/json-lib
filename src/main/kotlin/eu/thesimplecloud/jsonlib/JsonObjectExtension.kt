package eu.thesimplecloud.jsonlib

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject

/**
 * Created by IntelliJ IDEA.
 * Date: 07.11.2020
 * Time: 13:07
 * @author Frederick Baier
 */
fun JsonObject.getOrNull(property: String): JsonElement? {
    val jsonElement = get(property)
    return if (jsonElement is JsonNull) null else jsonElement
}