package eu.thesimplecloud.jsonlib.bukkit

import com.google.gson.*
import eu.thesimplecloud.jsonlib.JsonLib
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type

/**
 * Created by IntelliJ IDEA.
 * Date: 24.06.2020
 * Time: 14:13
 * @author Frederick Baier
 */
class ItemStackSerializer : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    override fun serialize(src: ItemStack, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonLib.fromObject(src.serialize()).jsonElement
    }

    override fun deserialize(jsonElement: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): ItemStack? {
        return ItemStack.deserialize(JsonLib.fromJsonElement(jsonElement).getObject(LinkedHashMap::class.java)
                as MutableMap<String, Any>)

    }
}