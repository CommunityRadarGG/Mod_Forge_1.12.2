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
package io.github.communityradargg.forgemod.event;

import io.github.communityradargg.forgemod.CommunityRadarMod;
import io.github.communityradargg.forgemod.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A class containing a listener for key input.
 */
public class KeyInputListener {
    private final CommunityRadarMod communityRadarMod;

    /**
     * Constructs the class {@link KeyInputListener}.
     *
     * @param communityRadarMod An instance of the {@link CommunityRadarMod} class.
     */
    public KeyInputListener(final @NotNull CommunityRadarMod communityRadarMod) {
        this.communityRadarMod = communityRadarMod;
    }

    /**
     * The listener for the {@link InputEvent.KeyInputEvent} event.
     *
     * @param event The event.
     */
    @SubscribeEvent
    @SuppressWarnings("unused") // called by mod loader on event
    public void onKeyInput(final InputEvent.KeyInputEvent event) {
        if (!communityRadarMod.isOnGrieferGames()) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        if (!mc.gameSettings.keyBindPlayerList.isPressed()) {
            return;
        }

        Utils.updatePrefixes(CommunityRadarMod.getListManager().getExistingPrefixes());
    }
}