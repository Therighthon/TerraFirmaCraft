package net.dries007.tfc.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class ModuloLayer implements IC0Transformer
{
    private final int modulus;

    public ModuloLayer(int modulus)
    {
        this.modulus = modulus;
    }

    @Override
    public int apply(INoiseRandom context, int value)
    {
        return value % modulus;
    }
}