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

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A class representing an entry in a radar list.
 */
public class RadarListEntry {
    @SerializedName("uuid")
    private final UUID uuid;
    @SerializedName("name")
    private final String name;
    @SerializedName("cause")
    private final String cause;
    @SerializedName("entryCreatedAt")
    private final LocalDateTime entryCreationDate;
    @SerializedName("entryUpdatedAt")
    private final LocalDateTime entryUpdateDate;
    @SerializedName("expiryDays")
    private final int expiryDays;

    /**
     * Constructs a {@link RadarListEntry}.
     *
     * @param uuid The player uuid of the entry.
     * @param name The player name of the entry
     * @param cause The cause of the entry.
     * @param entryCreationDate The date when the entry was created the first time.
     */
    public RadarListEntry(final @NotNull UUID uuid, final @NotNull String name, final @NotNull String cause, final @NotNull LocalDateTime entryCreationDate) {
        this.uuid = uuid;
        this.name = name;
        this.cause = cause;
        this.entryCreationDate = entryCreationDate;
        this.entryUpdateDate = entryCreationDate;
        this.expiryDays = -1;
    }

    /**
     * Gets the player uuid of the entry.
     *
     * @return Returns the player uuid of the entry.
     */
    public @NotNull UUID uuid() {
        return uuid;
    }

    /**
     * Gets the player name of the entry.
     *
     * @return Returns the player name of the entry.
     */
    public @NotNull String name() {
        return name;
    }

    /**
     * Gets the cause of the entry.
     *
     * @return Returns the cause of the entry.
     */
    public @NotNull String cause() {
        return cause;
    }

    /**
     * Gets the creation datetime of the entry.
     *
     * @return Returns the creation datetime of the entry.
     */
    public LocalDateTime entryCreationDate() {
        return entryCreationDate;
    }

    /**
     * Gets the update datetime of the entry.
     *
     * @return Returns the update datetime of the entry.
     */
    public LocalDateTime entryUpdateDate() {
        return entryUpdateDate;
    }

    /**
     * Gets the expiry days of the entry.
     *
     * @return Returns the expiry days of the entry.
     */
    @SuppressWarnings("unused") // included - json has this field
    public int expiryDays() {
        return expiryDays;
    }
}