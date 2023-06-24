package com.example.testmafooczi.retrofit

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(with = ProfileUserSerializer::class)
data class ProfileUser(
    val profile_data: ProfileData
)

@Serializable(with = ProfileDataSerializer::class)
data class ProfileData(
    val name: String?,
    val username: String?,
    var birthday: String?,
    var city: String?,
    val vk: String?,
    val instagram: String?,
    val status: String?,
    var avatar: String?,
    val id: Int?,
    val last: String?,
    val online: Boolean,
    val created: String?,
    val phone: String?,
    val completed_task: Int?,
    var avatars: Avatars?
)

@Serializable(with = AvatarsSerializer::class)
data class Avatars(
    val avatar: String,
    val bigAvatar: String,
    val miniAvatar: String
)

object ProfileUserSerializer : KSerializer<ProfileUser> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("ProfileUser") {
            element("profile_data", ProfileData.serializer().descriptor)
        }

    override fun deserialize(decoder: Decoder): ProfileUser {
        require(decoder is JsonDecoder)
        val root = decoder.decodeJsonElement()
        val profileData = decoder.json.decodeFromJsonElement(
            ProfileDataSerializer,
            root.jsonObject["profile_data"]!!
        )
        return ProfileUser(
            profileData
        )
    }

    override fun serialize(encoder: Encoder, value: ProfileUser) {
        require(encoder is JsonEncoder)
        val valueProfileData = value.profile_data
        encoder.encodeJsonElement(buildJsonObject {
            put("profile_data", buildJsonObject {
                put("name", valueProfileData.name)
                put("username", valueProfileData.username)
                put("birthday", valueProfileData.birthday)
                put("city", valueProfileData.city)
                put("vk", valueProfileData.vk)
                put("instagram", valueProfileData.instagram)
                put("status", valueProfileData.status)
                put("avatar", valueProfileData.avatar)
                put("id", valueProfileData.id)
                put("last", valueProfileData.last)
                put("online", valueProfileData.online)
                put("created", valueProfileData.created)
                put("phone", valueProfileData.phone)
                put("completed_task", valueProfileData.completed_task)
                put("avatars", buildJsonObject {
                    put("avatar", valueProfileData.avatars?.avatar)
                    put("bigAvatar", valueProfileData.avatars?.bigAvatar)
                    put("miniAvatar", valueProfileData.avatars?.miniAvatar)
                })
            })
        })
    }
}

object ProfileDataSerializer : KSerializer<ProfileData> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ProfileData") {
        element("name", String.serializer().descriptor)
        element("username", String.serializer().descriptor)
        element("birthday", String.serializer().descriptor)
        element("city", String.serializer().descriptor)
        element("vk", String.serializer().descriptor)
        element("instagram", String.serializer().descriptor)
        element("status", String.serializer().descriptor)
        element("avatar", String.serializer().descriptor)
        element("id", Int.serializer().descriptor)
        element("last", String.serializer().descriptor)
        element("online", Boolean.serializer().descriptor)
        element("created", String.serializer().descriptor)
        element("phone", String.serializer().descriptor)
        element("completed_task", Int.serializer().descriptor)
        element("avatars", Avatars.serializer().descriptor)
    }

    override fun deserialize(decoder: Decoder): ProfileData {
        require(decoder is JsonDecoder)
        val root = decoder.decodeJsonElement()
        val avatars =
            decoder.json.decodeFromJsonElement(AvatarsSerializer, root.jsonObject["avatars"]!!)
        return ProfileData(
            name = root.jsonObject["name"]!!.jsonPrimitive.content,
            username = root.jsonObject["username"]!!.jsonPrimitive.content,
            birthday = root.jsonObject["birthday"]!!.jsonPrimitive.content,
            city = root.jsonObject["city"]!!.jsonPrimitive.content,
            vk = root.jsonObject["vk"]!!.jsonPrimitive.content,
            instagram = root.jsonObject["instagram"]!!.jsonPrimitive.content,
            status = root.jsonObject["status"]!!.jsonPrimitive.content,
            avatar = root.jsonObject["avatar"]!!.jsonPrimitive.content,
            id = root.jsonObject["id"]!!.jsonPrimitive.int,
            last = root.jsonObject["last"]!!.jsonPrimitive.content,
            online = root.jsonObject["online"]!!.jsonPrimitive.boolean,
            created = root.jsonObject["created"]!!.jsonPrimitive.content,
            phone = root.jsonObject["phone"]!!.jsonPrimitive.content,
            completed_task = root.jsonObject["completed_task"]!!.jsonPrimitive.int,
            avatars = avatars
        )
    }


    override fun serialize(encoder: Encoder, value: ProfileData) {
        require(encoder is JsonEncoder)
        encoder.encodeJsonElement(buildJsonObject {
            put("name", value.name)
            put("username", value.username)
            put("birthday", value.birthday)
            put("city", value.city)
            put("vk", value.vk)
            put("instagram", value.instagram)
            put("status", value.status)
            put("avatar", value.avatar)
            put("id", value.id)
            put("last", value.last)
            put("online", value.online)
            put("created", value.created)
            put("phone", value.phone)
            put("completed_task", value.completed_task)
            put("avatars", buildJsonObject {
                put("avatar", value.avatars?.avatar)
                put("bigAvatar", value.avatars?.bigAvatar)
                put("miniAvatar", value.avatars?.miniAvatar)
            })
        })
    }
}

object AvatarsSerializer : KSerializer<Avatars> {
    override fun deserialize(decoder: Decoder): Avatars {
        require(decoder is JsonDecoder)
        val root = decoder.decodeJsonElement()
        return Avatars(
            avatar = root.jsonObject["avatar"]!!.jsonPrimitive.content,
            bigAvatar = root.jsonObject["bigAvatar"]!!.jsonPrimitive.content,
            miniAvatar = root.jsonObject["miniAvatar"]!!.jsonPrimitive.content,
        )
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Avatars") {
        element("avatar", String.serializer().descriptor)
        element("bigAvatar", String.serializer().descriptor)
        element("miniAvatar", String.serializer().descriptor)
    }

    override fun serialize(encoder: Encoder, value: Avatars) {
        require(encoder is JsonEncoder)
        encoder.encodeJsonElement(buildJsonObject {
            put("avatar", value.avatar)
            put("bigAvatar", value.bigAvatar)
            put("miniAvatar", value.miniAvatar)
        })
    }

}