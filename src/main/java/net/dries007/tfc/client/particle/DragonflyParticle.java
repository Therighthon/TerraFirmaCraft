package net.dries007.tfc.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class DragonflyParticle extends AnimatedParticle
{
    public DragonflyParticle(ClientLevel level, double x, double y, double z, int textures, SpriteSet sprites)
    {
        super(level, x, y, z, textures, sprites);
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType>
    {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            return new AnimatedParticle(level, x, y, z, 2, sprites);
        }
    }
}
