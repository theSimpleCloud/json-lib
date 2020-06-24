package eu.thesimplecloud.jsonlib.bukkit

import com.google.gson.*
import eu.thesimplecloud.jsonlib.JsonLib
import org.bukkit.Bukkit
import org.bukkit.Location
import java.lang.reflect.Type

/**
 * Created by IntelliJ IDEA.
 * Date: 24.06.2020
 * Time: 14:13
 * @author Frederick Baier
 */
class LocationSerializer : JsonSerializer<Location>, JsonDeserializer<Location> {

    override fun serialize(src: Location, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val result = JsonObject()
        result.add("x", JsonPrimitive(src.x))
        result.add("y", JsonPrimitive(src.y))
        result.add("z", JsonPrimitive(src.z))
        result.add("yaw", JsonPrimitive(src.yaw))
        result.add("pitch", JsonPrimitive(src.pitch))
        result.add("world", JsonPrimitive(src.world!!.name))

        return result
    }

    override fun deserialize(jsonElement: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Location? {
        val jsonObject: JsonObject = jsonElement.asJsonObject
        val jsonLib = JsonLib.fromJsonElement(jsonObject)
        val worldName: String = jsonLib.getString("world") ?: return null
        val world = Bukkit.getWorld(worldName)

        val x: Double = jsonLib.getDouble("x") ?: return null
        val y: Double = jsonLib.getDouble("y") ?: return null
        val z: Double = jsonLib.getDouble("z") ?: return null
        val yaw: Float = jsonLib.getFloat("yaw") ?: return null
        val pitch: Float = jsonLib.getFloat("pitch") ?: return null

        return Location(world, x, y, z, yaw, pitch)
    }
}