/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.jsonlib

import com.google.gson.*
import java.io.*
import java.nio.charset.StandardCharsets

class JsonLib private constructor(val jsonElement: JsonElement, private val currentGson: Gson) {

    fun append(property: String, value: String?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Any?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.add(property, this.currentGson.toJsonTree(value))
        return this
    }

    fun append(property: String, value: Number?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Boolean?): JsonLib {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    /**
     * Returns the property found by the specified name
     */
    fun getProperty(name: String): JsonLib? {
        if (jsonElement !is JsonObject)
            return null
        return jsonElement.get(name)?.let { JsonLib(it, currentGson) }
    }

    /**
     * Returns a [JsonLib] found by the specified path
     * The path will be split with .
     */
    fun getPath(path: String): JsonLib? {
        val array = path.split(".")
        var currentJsonLib: JsonLib? = this
        for (property in array) {
            currentJsonLib = currentJsonLib?.getProperty(property)
            if (currentJsonLib == null) {
                return null
            }
        }
        return currentJsonLib
    }

    fun getInt(property: String): Int? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asInt
    }

    fun getLong(property: String): Long? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asLong
    }

    fun getDouble(property: String): Double? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asDouble
    }

    fun getFloat(property: String): Float? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asFloat
    }

    fun getBoolean(property: String): Boolean? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asBoolean
    }

    fun getAsJsonArray(property: String): JsonArray? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asJsonArray
    }

    fun getString(property: String): String? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.getOrNull(property)?.asString
    }


    fun <T> getObject(property: String, clazz: Class<T>): T? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        if (clazz == JsonLib::class.java) {
            return getProperty(property) as T
        }
        return if (!jsonElement.has(property)) null else this.currentGson.fromJson(jsonElement.get(property), clazz)
    }

    fun <T> getObject(clazz: Class<T>): T {
        return this.currentGson.fromJson(getAsJsonString(), clazz)
    }

    fun <T> getObjectOrNull(clazz: Class<T>): T? {
        if (getAsJsonString().isBlank()) return null
        return try {
            this.currentGson.fromJson(getAsJsonString(), clazz)
        } catch (ex: Exception) {
            null
        }

    }


    fun saveAsFile(path: String) {
        saveJsonElementAsFile(File(path))
    }

    fun saveAsFile(file: File) {
        saveJsonElementAsFile(file)
    }

    fun saveJsonElementAsFile(path: String): Boolean {
        return saveJsonElementAsFile(File(path))
    }

    fun saveJsonElementAsFile(file: File): Boolean {
        val dir = file.parentFile
        if (dir != null && !dir.exists()) {
            dir.mkdirs()
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(getJsonStringAsBytes())
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            return false
        }

        return false
    }


    fun getAsJsonString(): String {
        return this.currentGson.toJson(jsonElement)
    }

    fun getJsonStringAsBytes(): ByteArray {
        return getAsJsonString().toByteArray(StandardCharsets.UTF_8)
    }


    companion object {

        var GSON = GsonCreator().excludeAnnotations(JsonLibExclude::class.java).create()
            private set

        fun setDefaultGson(gson: Gson) {
            GSON = gson
        }

        @JvmStatic
        fun empty() = empty(GSON)

        @JvmStatic
        fun empty(gson: Gson) = JsonLib(JsonObject(), gson)

        @JvmStatic
        fun fromJsonElement(jsonElement: JsonElement): JsonLib {
            return JsonLib(jsonElement, GSON)
        }

        @JvmStatic
        fun fromObject(any: Any): JsonLib {
            return fromJsonString(GSON.toJson(any))
        }

        @JvmStatic
        fun fromObject(any: Any, gson: Gson): JsonLib {
            return fromJsonString(gson.toJson(any), gson)
        }

        @JvmStatic
        fun fromJsonFile(path: String): JsonLib? {
            return fromJsonFile(File(path))
        }

        @JvmStatic
        fun fromJsonFile(file: File): JsonLib? {
            if (!file.exists()) return null
            return fromJsonString(loadFile(file))
        }

        @JvmStatic
        fun fromInputStream(inputStream: InputStream): JsonLib {
            return fromJsonString(loadFromInputStream(inputStream))
        }

        @JvmStatic
        fun fromInputStream(inputStream: InputStream, gson: Gson): JsonLib {
            return fromJsonString(loadFromInputStream(inputStream), gson)
        }

        @JvmStatic
        fun fromJsonString(string: String): JsonLib {
            return fromJsonString(string, GSON)
        }

        @JvmStatic
        fun fromJsonString(string: String, gson: Gson): JsonLib {
            return try {
                val jsonObject = gson.fromJson(string, JsonObject::class.java)
                JsonLib(jsonObject, gson)
            } catch (ex: Exception) {
                try {
                    val jsonPrimitive = gson.fromJson(string, JsonArray::class.java)
                    JsonLib(jsonPrimitive, gson)
                } catch (ex: java.lang.Exception) {
                    try {
                        val jsonPrimitive = gson.fromJson(string, JsonPrimitive::class.java)
                        JsonLib(jsonPrimitive, gson)
                    } catch (ex: Exception) {
                        throw IllegalArgumentException("Can't parse string $string", ex)
                    }
                }
            }
        }

        private fun loadFromInputStream(inputStream: InputStream): String {
            try {
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()
                return String(data, Charsets.UTF_8)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ""
        }


        private fun loadFile(file: File): String {
            if (!file.exists())
                return ""
            return loadFromInputStream(FileInputStream(file))
        }
    }

    override fun toString(): String {
        return getAsJsonString()
    }
}
