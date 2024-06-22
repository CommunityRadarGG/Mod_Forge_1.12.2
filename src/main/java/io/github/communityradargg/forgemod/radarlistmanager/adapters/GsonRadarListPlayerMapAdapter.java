/*
 * Copyright 2024 - present CommunityRadarGG <https://community-radar.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.communityradargg.forgemod.radarlistmanager.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.communityradargg.forgemod.radarlistmanager.RadarListEntry;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class with an adapter for serialization and deserialization of following structure {@code Map<UUID, RadarListEntry>} for the GSON library.
 */
public class GsonRadarListPlayerMapAdapter implements JsonSerializer<Map<UUID, RadarListEntry>>, JsonDeserializer<Map<UUID, RadarListEntry>> {
    /** {@inheritDoc} */
    @Override
    public Map<UUID, RadarListEntry> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonArray playerMapJsonArray = json.getAsJsonArray();
        final Map<UUID, RadarListEntry> playerMap = new HashMap<>();

        playerMapJsonArray.forEach(jsonElement -> {
            final RadarListEntry entry = context.deserialize(jsonElement, RadarListEntry.class);
            playerMap.put(entry.uuid(), entry);
        });
        return playerMap;
    }

    /** {@inheritDoc} */
    @Override
    public JsonElement serialize(final Map<UUID, RadarListEntry> playerMap, final Type typeOfSrc, final JsonSerializationContext context) {
        return context.serialize(playerMap.values());
    }
}
