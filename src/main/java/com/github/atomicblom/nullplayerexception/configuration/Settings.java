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
    private Configuration config;

    public boolean enableShaders()
    {
        return enableShaders;
    }
    public List<String> getObfuscatedPlayers()
    {
        return obfuscatedPlayers;
    }

    public boolean addObfuscatedPlayer(String playerName) {
        if (obfuscatedPlayers.contains(playerName)) {
            return false;
        }
        obfuscatedPlayers.add(playerName);
        updateConfig();
        return true;
    }

    public boolean removeObfuscatedPlayer(String playerName) {
        if (!obfuscatedPlayers.contains(playerName)) {
            return false;
        }
        obfuscatedPlayers.remove(playerName);
        updateConfig();
        return true;
    }

    private void updateConfig()
    {
        String[] players = obfuscatedPlayers.toArray(new String[obfuscatedPlayers.size()]);
        obfuscatedPlayersSetting.setValues(players);
        config.save();
    }

    public static void syncConfig(Configuration config)
    {
        INSTANCE.config = config;
        INSTANCE.obfuscatedPlayersSetting = config.get("corruptedPlayers", CATEGORY, new String[0], "List of players to corrupt");
        INSTANCE.enableShaders = config.getBoolean("enableShaders", CATEGORY, true, "Render players as corrupted");
        INSTANCE.obfuscatedPlayers = Lists.newArrayList(INSTANCE.obfuscatedPlayersSetting.getStringList());
    }
}
