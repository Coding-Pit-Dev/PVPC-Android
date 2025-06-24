package com.codingpit.pvpcplanner.data.local.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.codingpit.pvpcplanner.PvpcSettings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object PvpcSettingsSerializer : Serializer<PvpcSettings> {
    override val defaultValue: PvpcSettings = PvpcSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): PvpcSettings {
        try {
            return PvpcSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: PvpcSettings,
        output: OutputStream,
    ) = t.writeTo(output)
}
