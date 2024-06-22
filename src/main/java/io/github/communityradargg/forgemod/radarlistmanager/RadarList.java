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
package io.github.communityradargg.forgemod.radarlistmanager;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import io.github.communityradargg.forgemod.CommunityRadarMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A class representing a radar list.
 */
public class RadarList {
    private static final Logger logger = LogManager.getLogger(RadarList.class);
    @SerializedName("VERSION")
    @SuppressWarnings("unused") // needed in future
    private final int version = 1;
    @SerializedName("namespace")
    private final String namespace;
    @SerializedName("playerMap")
    private final Map<UUID, RadarListEntry> playerMap;
    @SerializedName("visibility")
    private final RadarListVisibility visibility;
    @SerializedName("prefix")
    private String prefix;
    private transient String url;

    /**
     * Constructs a {@link RadarList}.
     *
     * @param namespace The namespace for the list.
     * @param prefix The prefix for the list.
     * @param url The url for the list.
     * @param visibility The visibility of the list.
     */
    public RadarList(final @NotNull String namespace, final @NotNull String prefix, final @NotNull String url, final @NotNull RadarListVisibility visibility) {
        this.namespace = namespace;
        this.prefix = prefix;
        this.visibility = visibility;
        this.playerMap = new HashMap<>();
        this.url = url;
        load();
    }

    /**
     * Checks, whether a given uuid is in the list.
     *
     * @param uuid The uuid to check.
     * @return Returns, whether the given uuid is in the list.
     */
    public boolean isInList(final @NotNull UUID uuid) {
        return playerMap.get(uuid) != null;
    }

    /**
     * Gets a radar list entry by a given uuid.
     *
     * @param uuid The uuid to get the entry for.
     * @return Returns an optional with the found entry.
     */
    public @NotNull Optional<RadarListEntry> getRadarListEntry(final @NotNull UUID uuid) {
        return Optional.ofNullable(playerMap.get(uuid));
    }

    /**
     * Gets the namespace of the list.
     *
     * @return Returns the namespace.
     */
    public @NotNull String getNamespace() {
        return namespace;
    }

    /**
     * Gets the prefix of the list.
     *
     * @return Returns the prefix.
     */
    public @NotNull String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix of the list.
     *
     * @param prefix The prefix to set.
     */
    public void setPrefix(final @NotNull String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the visibility of the list.
     *
     * @return Returns the visibility.
     */
    public @NotNull RadarListVisibility getRadarListVisibility() {
        return visibility;
    }

    /**
     * Gets the url of the list.
     *
     * @return Returns the url.
     */
    public @NotNull String getUrl() {
        return url;
    }

    /**
     * Sets the url of the list.
     *
     * @param url The url to set.
     */
    public void setUrl(final @NotNull String url) {
        this.url = url;
    }

    /**
     * Gets the player map of the list.
     *
     * @return Returns the player map.
     */
    public @NotNull Map<UUID, RadarListEntry> getPlayerMap() {
        return playerMap;
    }

    /**
     * Adds a radar list entry to the list if it is private.
     *
     * @param radarListEntry The entry to add.
     */
    public void addRadarListEntry(final @NotNull RadarListEntry radarListEntry) {
        if (visibility == RadarListVisibility.PRIVATE) {
            playerMap.put(radarListEntry.uuid(), radarListEntry);
            saveList();
        }
    }

    /**
     * Loads a radar list entry.
     *
     * @param radarListEntry The entry to load.
     */
    private void loadRadarListEntry(final @NotNull RadarListEntry radarListEntry) {
        playerMap.put(radarListEntry.uuid(), radarListEntry);
    }

    /**
     * Saves a list to the disk if it is private.
     */
    public void saveList() {
        if (visibility == RadarListVisibility.PRIVATE) {
            CommunityRadarMod.getListManager().saveRadarList(this);
        }
    }

    /**
     * Loads a public list.
     */
    private void loadPublicList() {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(this.url).openStream()))) {
            final List<RadarListEntry> players = RadarListManager.getGson()
                    .fromJson(reader, new TypeToken<List<RadarListEntry>>() {}.getType());
            players.forEach(this::loadRadarListEntry);
        } catch (final IOException | JsonIOException | JsonSyntaxException e) {
            logger.error("Could not load public list", e);
        }
    }

    /**
     * Validates the loaded list.
     *
     * @return Returns, whether the list is valid or not.
     */
    public boolean validateList() {
        return !(namespace == null || url == null || playerMap == null || visibility == null || prefix == null);
    }

    /**
     * Loads a list if it is public.
     */
    public void load() {
        if (visibility == RadarListVisibility.PUBLIC) {
            loadPublicList();
        }
    }
}
