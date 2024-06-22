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

import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

/**
 * A class representing a message that can be shown to a player.
 */
public class RadarMessage {
    private final String text;

    /**
     * Constructs a {@link RadarMessage}.
     *
     * @param text The text for the message.
     * @param includePrefix Whether a prefix should be included in the message.
     */
    private RadarMessage(final @NotNull String text, final boolean includePrefix) {
        this.text = (includePrefix ? Messages.PREFIX : "") + text;
    }

    /**
     * Converts this class instance to a {@link TextComponentString}.
     *
     * @return Returns the text converted to a {@link TextComponentString}.
     */
    public @NotNull TextComponentString toChatComponentText() {
        return new TextComponentString(this.text);
    }

    /**
     * A class that serves as a builder for the class {@link RadarMessage}.
     */
    public static class RadarMessageBuilder {
        private String text;
        private boolean includePrefix;

        /**
         * Constructs a {@link RadarMessageBuilder}.
         *
         * @param text The text for the builder.
         */
        public RadarMessageBuilder(final @NotNull String text) {
            this.text = text;
            this.includePrefix = true;
        }

        /**
         * Replaces old text with a new one in the text stored in this builder.
         *
         * @param oldText The old text to replace.
         * @param newText The replacement text.
         * @return Returns the builder after replacing the text.
         */
        public @NotNull RadarMessageBuilder replace(final @NotNull String oldText, final @NotNull String newText) {
            this.text = this.text.replace(oldText, newText);
            return this;
        }

        /**
         * Replaces old text with a new one in the text stored in this builder by considering color codes.
         *
         * @param oldText The old text to replace.
         * @param newText The replacement text.
         * @return Returns the builder after replacing the text and color codes.
         */
        public @NotNull RadarMessageBuilder replaceWithColorCodes(final @NotNull String oldText, final @NotNull String newText) {
            this.text = this.text.replace(oldText, newText.replace("&", "§"));
            return this;
        }

        /**
         * Sets the prefix exclude state in the builder.
         *
         * @return Returns the builder after setting the prefix exclude state.
         */
        public @NotNull RadarMessageBuilder excludePrefix() {
            this.includePrefix = false;
            return this;
        }

        /**
         * Builds a {@link RadarMessage} out of the builder.
         *
         * @return Returns the build {@link RadarMessage}.
         */
        public @NotNull RadarMessage build() {
            return new RadarMessage(text, includePrefix);
        }
    }
}
