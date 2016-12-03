package com.github.atomicblom.nullplayerexception.configuration;

import com.google.common.collect.ImmutableList;
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
    public ImmutableList<String> getObfuscatedPlayers()
    {
        synchronized (obfuscatedPlayersSetting)
        {
            final String[] players = obfuscatedPlayers.toArray(new String[obfuscatedPlayers.size()]);
            return ImmutableList.copyOf(obfuscatedPlayers);
        }
    }

    public boolean addObfuscatedPlayer(String playerName) {
        synchronized (obfuscatedPlayersSetting)
        {
            if (obfuscatedPlayers.contains(playerName))
            {
                return false;
            }
            obfuscatedPlayers.add(playerName);
            updateConfig();
            return true;
        }
    }

    public boolean removeObfuscatedPlayer(String playerName) {
        synchronized (obfuscatedPlayersSetting)
        {
            final String lowercaseName = playerName.toLowerCase();
            final String[] players = obfuscatedPlayers.toArray(new String[obfuscatedPlayers.size()]);

            boolean madeAChange = false;
            for (final String obfuscatedPlayer : players)
            {
                if (obfuscatedPlayer.toLowerCase().equals(lowercaseName))
                {
                    obfuscatedPlayers.remove(obfuscatedPlayer);
                    madeAChange = true;
                }
            }

            if (madeAChange)
            {
                updateConfig();
            }
            return madeAChange;
        }
    }

    private void updateConfig()
    {
        final String[] players = obfuscatedPlayers.toArray(new String[obfuscatedPlayers.size()]);
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
