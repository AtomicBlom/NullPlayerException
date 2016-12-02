package com.github.atomicblom.nullplayerexception.client;

import com.github.atomicblom.nullplayerexception.client.render.ShaderHelper;
import com.github.atomicblom.nullplayerexception.common.CommonRenderProxy;

@SuppressWarnings("unused")
public class ClientRenderProxy extends CommonRenderProxy
{
    @Override
    public void registerRenderers()
    {
        ShaderHelper.initShaders();
    }
}
