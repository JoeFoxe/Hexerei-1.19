package net.joefoxe.hexerei.particle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ExtinguishParticle extends TextureSheetParticle {
    protected float scale;
    protected float rotationDir;
    protected float fallingSpeed;
    protected double xdStart;
    protected double ydStart;
    protected double zdStart;
    protected double ydExtra;

    public ExtinguishParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.xdStart = motionX;
        this.ydStart = motionY;
        this.zdStart = motionZ;
        this.ydExtra = new Random().nextFloat() * (motionY/10f);
        this.rotationDir = new Random().nextFloat() - 0.5f;
        this.fallingSpeed = new Random().nextFloat();
        this.lifetime = 50 + (int)(new Random().nextFloat() * 50f);
        this.quadSize = 0.25f + 0.25f * new Random().nextFloat();

        setScale(0.2F);
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    @Override
    public void tick() {

//        this.oRoll = this.roll;
//        if(Math.abs(this.yd) > 0 && this.y != this.yo)
//            this.roll += 0.3f * rotationDir;
        this.xd  = Math.min(1, (this.lifetime - this.age) / (float)this.lifetime) * xdStart;
        this.yd  = Math.min(1, (this.age) / (float)this.lifetime) * ydStart + this.ydExtra;
        this.zd  = Math.min(1, (this.lifetime - this.age) / (float)this.lifetime) * zdStart;
        this.alpha = Math.min(1, (this.lifetime - this.age) / (float)this.lifetime);

        super.tick();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            Random rand = new Random();
            float colorOffset = (rand.nextFloat() * 0.6f);
            ExtinguishParticle extinguishParticle = new ExtinguishParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            extinguishParticle.pickSprite(this.spriteSet);

//            fogParticle.sprite.u1
            extinguishParticle.setColor(0.8f - colorOffset,0.8f - colorOffset,0.8f - colorOffset);
//            if(this.spriteSet.get(0,1).getName().getPath().toString().matches("particle/fog_particle_4") ||
//                  this.spriteSet.get(0,1).getName().getPath().toString().matches("particle/fog_particle_5") ||
//                      this.spriteSet.get(0,1).getName().getPath().toString().matches("particle/fog_particle_6")) {
//                fogParticle.lifetime += fogParticle.lifetime * 3 + 30;
//            }


            return extinguishParticle;
        }
    }
}
