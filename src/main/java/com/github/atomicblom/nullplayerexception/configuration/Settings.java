package com.github.atomicblom.nullplayerexception.configuration;

import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.util.List;

@SuppressWarnings({"InnerClassFieldHidesOuterClassField", "BooleanMethodNameMustStartWithQuestion"})
public enum Settings
{
    INSTANCE;

    public static final String CATEGORY = Configuration.CATEGORY_GENERAL;

    private boolean enableShaders = false;
    private List<String> obfuscatedPlayers = Lists.newArrayList();
    private Property obfuscatedPlayersSetting;

    public static boolean enableShaders()
    {
        return INSTANCE.enableShaders;
    }
    public static List<String> getObfuscatedPlayers()
    {
        return INSTANCE.obfuscatedPlayers;
    }

    public static boolean addObfuscatedPlayer(String playerName) {
        if (INSTANCE.obfuscatedPlayers.contains(playerName)) {
            return false;
        }
        INSTANCE.obfuscatedPlayers.add(playerName);
        String[] players = INSTANCE.obfuscatedPlayers.toArray(new String[INSTANCE.obfuscatedPlayers.size()]);
        INSTANCE.obfuscatedPlayersSetting.setValues(players);
        return true;
    }

    public static boolean removeObfuscatedPlayer(String playerName) {
        if (!INSTANCE.obfuscatedPlayers.contains(playerName)) {
            return false;
        }
        INSTANCE.obfuscatedPlayers.remove(playerName);
        String[] players = INSTANCE.obfuscatedPlayers.toArray(new String[INSTANCE.obfuscatedPlayers.size()]);
        INSTANCE.obfuscatedPlayersSetting.setValues(players);
        return true;
    }

    public static void syncConfig(Configuration config)
    {
        INSTANCE.obfuscatedPlayersSetting = config.get("corruptedPlayers", CATEGORY, new String[0], "List of players to corrupt");
        INSTANCE.enableShaders = config.getBoolean("enableShaders", CATEGORY, true, "Render players as corrupted");
        INSTANCE.obfuscatedPlayers = Lists.newArrayList(INSTANCE.obfuscatedPlayersSetting.getStringList());
    }
}
