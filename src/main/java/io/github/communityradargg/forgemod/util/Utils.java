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
package io.github.communityradargg.forgemod.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.communityradargg.forgemod.CommunityRadarMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * A class with some util methods.
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static final String MOJANG_API_NAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/";
    private static final Pattern UUID_MOJANG_API_PATTERN = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    private static final DateTimeFormatter readableDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final Map<String, UUID> uuidNameCache = new HashMap<>();

    /**
     * Tries to get the uuid to the player name from the world.
     *
     * @param playerName The player name to get the corresponding uuid.
     * @return Returns a CompletableFuture with an optional with the player uuid.
     */
    public static @NotNull CompletableFuture<Optional<UUID>> getUUID(final @NotNull String playerName) {
        // user has to be in a world
        if (Minecraft.getMinecraft().world == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        // If the UUID has been cached, returning from the map.
        if (uuidNameCache.containsKey(playerName)) {
            return CompletableFuture.completedFuture(Optional.of(uuidNameCache.get(playerName)));
        }

        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        // Checking if there is a player with same name in the loaded world. If so, returning UUID from EntityPlayer.
        for (final NetworkPlayerInfo networkPlayerInfo : connection.getPlayerInfoMap()) {
            if (networkPlayerInfo.getGameProfile().getName().equalsIgnoreCase(playerName)) {
                uuidNameCache.put(playerName, networkPlayerInfo.getGameProfile().getId());
                return CompletableFuture.completedFuture(Optional.of(networkPlayerInfo.getGameProfile().getId()));
            }
        }

        if (playerName.startsWith("!") || playerName.startsWith("~")) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        // If no player with same name is in the world, try fetching the UUID from the Mojang-API.
        return requestUuidForName(playerName);
    }

    /**
     * Requests an uuid to a player name, from the Mojang API.
     *
     * @param playerName The player name to get the uuid for.
     * @return Returns a CompletableFuture with an optional with the requested uuid, it will be empty if an error occurred on requesting.
     */
    private static @NotNull CompletableFuture<Optional<UUID>> requestUuidForName(final @NotNull String playerName) {
        final String urlText = MOJANG_API_NAME_TO_UUID + playerName;
        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection connection = null;
            try {
                final URL url = new URL(urlText);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", CommunityRadarMod.MODID + "/" + CommunityRadarMod.VERSION);

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    logger.warn("Requesting data from '{}' resulted in following status code: {}", urlText, connection.getResponseCode());
                    return Optional.empty();
                }

                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    final JsonObject json = new Gson().fromJson(reader, JsonObject.class);
                    if (json == null || !json.has("id") || !json.has("name")) {
                        connection.disconnect();
                        return Optional.empty();
                    }

                    final UUID uuid = UUID.fromString(UUID_MOJANG_API_PATTERN.matcher(json.get("id").getAsString()).replaceAll("$1-$2-$3-$4-$5"));
                    uuidNameCache.put(playerName, uuid);
                    connection.disconnect();
                    return Optional.of(uuid);
                }
            } catch (final Exception e) {
                if (connection != null) {
                    connection.disconnect();
                }
                logger.error("Trying to request data from '{}' resulted in an exception", urlText, e);
                return Optional.empty();
            }
        });
    }

    /**
     * Formats a given date time in a human-readable form.
     *
     * @param localDateTime The local date time to format.
     * @return Returns the formatted date time.
     */
    public static @NotNull String formatDateTime(final @NotNull LocalDateTime localDateTime) {
        return localDateTime.format(readableDateTimeFormatter);
    }

    /**
     * Checks if a given hostname is a hostname of GrieferGames.
     * <br><br>
     * Following domains are taken into account:
     * <br>
     * - griefergames.net
     * <br>
     * - griefergames.de
     * <br>
     * - griefergames.live
     *
     * @param hostName The hostname to check.
     * @return Returns, whether the given hostname is one of the GrieferGames hostnames.
     */
    public static boolean isGrieferGamesHostName(final @NotNull String hostName) {
        final String filteredHostName = Optional.of(hostName)
                .filter(host -> host.endsWith("."))
                .map(host -> host.substring(0, host.length() - 1).toLowerCase(Locale.ENGLISH))
                .orElse(hostName.toLowerCase(Locale.ENGLISH));
        return filteredHostName.endsWith("griefergames.net") || filteredHostName.endsWith("griefergames.de") || filteredHostName.endsWith("griefergames.live");
    }

    /**
     * Gets a {@link NetworkPlayerInfo} by the uuid of a player.
     *
     * @param uuid The uuid to get the network player info for.
     * @return Returns an optional with the network player info of an online player to the uuid.
     */
    private static @NotNull Optional<NetworkPlayerInfo> getNetworkPlayerInfoByUuid(final @NotNull UUID uuid) {
        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return Optional.empty();
        }
        return connection.getPlayerInfoMap().stream()
                .filter(player -> uuid.equals(player.getGameProfile().getId()))
                .findFirst();
    }

    /**
     * Gets a {@link EntityPlayer} by the uuid of a player.
     *
     * @param uuid The uuid to get the entity player for.
     * @return Returns an optional with the entity player to the uuid.
     */
    private static @NotNull Optional<EntityPlayer> getEntityPlayerByUuid(final @NotNull UUID uuid) {
        final World world = Minecraft.getMinecraft().world;
        if (world == null) {
            return Optional.empty();
        }

        return world.playerEntities.stream()
                .filter(player -> uuid.equals(player.getGameProfile().getId()))
                .findFirst();
    }

    /**
     * Updates a player display name and name tag by its uuid.
     *
     * @param uuid The uuid to update the corresponding player.
     */
    public static void updatePlayerByUuid(final @NotNull UUID uuid, final @NotNull Set<String> oldPrefixes) {
        getEntityPlayerByUuid(uuid).ifPresent(player -> updatePlayerNameTag(player, oldPrefixes));
        getNetworkPlayerInfoByUuid(uuid).ifPresent(networkPlayerInfo -> updatePlayerPrefix(networkPlayerInfo, oldPrefixes));
    }

    /**
     * Handles updating the name tag of a player entity.
     *
     * @param player The player entity to update the name tag.
     * @param oldPrefixes The old prefixes that need to be removed before adding the new one.
     */
    public static void updatePlayerNameTag(final @NotNull EntityPlayer player, final @NotNull Set<String> oldPrefixes) {
        player.getPrefixes().removeIf(prefix -> oldPrefixes.stream().anyMatch(oldPrefix -> new TextComponentString(oldPrefix.replace("&", "§") + " ").getUnformattedText().equals(prefix.getUnformattedText())));
        final String addonPrefix = CommunityRadarMod.getListManager()
                .getPrefix(player.getGameProfile().getId())
                .replace("&", "§");

        if (!addonPrefix.isEmpty()) {
            player.addPrefix(new TextComponentString(addonPrefix + " "));
        }
    }

    /**
     * Handles updating the player prefixes in the display name.
     *
     * @param oldPrefixes The old prefixes that need to be removed before adding the new one.
     */
    public static void updatePrefixes(final @NotNull Set<String> oldPrefixes) {
        final NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return;
        }
        connection.getPlayerInfoMap().forEach(player -> updatePlayerPrefix(player, oldPrefixes));
    }

    /**
     * Handles updating the player prefix in the display name of a single player.
     *
     * @param player The player to update.
     * @param oldPrefixes The old prefixes that need to be removed before adding the new one.
     */
    private static void updatePlayerPrefix(final @NotNull NetworkPlayerInfo player, final @NotNull Set<String> oldPrefixes) {
        if (player.getGameProfile().getId() == null || player.getDisplayName() == null) {
            return;
        }

        final ITextComponent displayName = player.getDisplayName();
        ITextComponent newDisplayName = displayName;
        for (final String prefix : oldPrefixes) {
            if (!displayName.getUnformattedText().startsWith(new TextComponentString(prefix.replace("&", "§") + " ").getUnformattedText())) {
                continue;
            }
            newDisplayName = displayName.getSiblings().get(displayName.getSiblings().size() - 1);
        }

        final String addonPrefix = CommunityRadarMod.getListManager()
                .getPrefix(player.getGameProfile().getId())
                .replace("&", "§");
        if (!addonPrefix.isEmpty()) {
            newDisplayName = new TextComponentString(addonPrefix.replace("&", "§") + " ").appendSibling(newDisplayName);
        }
        player.setDisplayName(newDisplayName);
    }
}
