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
package io.github.communityradargg.forgemod;

import io.github.communityradargg.forgemod.command.RadarCommand;
import io.github.communityradargg.forgemod.event.ClientChatReceivedListener;
import io.github.communityradargg.forgemod.event.ClientConnectionDisconnectListener;
import io.github.communityradargg.forgemod.event.KeyInputListener;
import io.github.communityradargg.forgemod.event.PlayerNameFormatListener;
import io.github.communityradargg.forgemod.radarlistmanager.RadarListManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Paths;

@Mod(modid = CommunityRadarMod.MODID, name = CommunityRadarMod.NAME, version = CommunityRadarMod.VERSION)
public class CommunityRadarMod {
    /** The id of the mod. */
    public static final String MODID = "communityradar";
    /** The name of the mod. */
    public static final String NAME = "CommunityRadar Mod - Official - 1.12.2";
    /** The version of the mod. */
    public static final String VERSION = "1.0.1-1.12.2-SNAPSHOT";
    private static final Logger logger = LogManager.getLogger(CommunityRadarMod.class);
    private static RadarListManager listManager;
    private boolean onGrieferGames = false;

    /**
     * The listener for the {@link FMLInitializationEvent} event.
     *
     * @param event The event.
     */
    @EventHandler
    @SuppressWarnings("unused") // called by the mod loader
    public void init(FMLInitializationEvent event) {
        logger.info("Starting the mod '" + MODID + "' with the version '" + VERSION + "'!");
        final File directoryPath = Paths.get(new File("")
                        .getAbsolutePath(),"communityradar", "lists")
                .toFile();
        if (!directoryPath.exists() && !directoryPath.mkdirs()) {
            logger.error("Could not create directory: {}", directoryPath);
        }

        listManager = new RadarListManager(directoryPath.getAbsolutePath() + "/");
        registerPublicLists();
        // Needs to be after loading public lists
        listManager.loadPrivateLists();
        registerEvents();
        registerCommands();
        logger.info("Successfully started the mod '" + MODID + "'!");
    }

    /**
     * Registers the events.
     */
    private void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new ClientChatReceivedListener(this));
        MinecraftForge.EVENT_BUS.register(new PlayerNameFormatListener(this));
        MinecraftForge.EVENT_BUS.register(new KeyInputListener(this));
        MinecraftForge.EVENT_BUS.register(new ClientConnectionDisconnectListener(this));
    }

    /**
     * Registers the commands.
     */
    private void registerCommands() {
        ClientCommandHandler.instance.registerCommand(new RadarCommand());
    }

    /**
     * Registers the public lists.
     */
    private void registerPublicLists() {
        if (!listManager.registerPublicList("scammer", "&7[&cScammer&7]", "https://lists.community-radar.de/versions/v1/scammer.json")) {
            logger.error("Could not register public list 'scammers'!");
        }

        if (!listManager.registerPublicList("trusted", "&7[&aTrusted&7]", "https://lists.community-radar.de/versions/v1/trusted.json")) {
            logger.error("Could not register public list 'verbvllert_trusted'!");
        }
    }

    /**
     * Gets the {@link RadarListManager} instance.
     *
     * @return Returns the radar list manager instance.
     */
    public static @NotNull RadarListManager getListManager() {
        return listManager;
    }

    /**
     * Gets the GrieferGames connection state.
     *
     * @return Returns the GrieferGames connection state.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isOnGrieferGames() {
        return onGrieferGames;
    }

    /**
     * Sets the GrieferGames connection state.
     *
     * @param onGrieferGames The GrieferGames connection state to set.
     */
    public void setOnGrieferGames(final boolean onGrieferGames) {
        this.onGrieferGames = onGrieferGames;
    }
}
