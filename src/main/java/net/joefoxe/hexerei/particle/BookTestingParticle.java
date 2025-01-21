package net.joefoxe.hexerei.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class BookTestingParticle extends TextureSheetParticle {

    public static final ResourceLocation TEXTURE = HexereiUtil.getResource("textures/particle/cauldron_boil_particle.png");
    public static final Vector3f[] CUBE = {
            // bottom render
            //right
//            new Vector3f(0.1f, -0.1f, -0.1f), // >^
//            new Vector3f(0.1f, -0.1f, 0.1f),  // >V
//            new Vector3f(0.1f, -0.1f, 0.1f), // <V
//            new Vector3f(0.1f, -0.1f, -0.1f),// <^
//            //left
//            new Vector3f(-0.1f, -0.1f, -0.1f), // >^
//            new Vector3f(-0.1f, -0.1f, 0.1f),  // >V
//            new Vector3f(-0.1f, -0.1f, 0.1f), // <V
//            new Vector3f(-0.1f, -0.1f, -0.1f),// <^
//            //front
//            new Vector3f(0.1f, -0.1f, -0.1f), // >^
//            new Vector3f(0.1f, -0.1f, -0.1f),  // >V
//            new Vector3f(-0.1f, -0.1f, -0.1f), // <V
//            new Vector3f(-0.1f, -0.1f, -0.1f),// <^
//            //back
//            new Vector3f(-0.1f, -0.1f, 0.1f), // >^
//            new Vector3f(-0.1f, -0.1f, 0.1f),  // >V
//            new Vector3f(0.1f, -0.1f, 0.1f), // <V
//            new Vector3f(0.1f, -0.1f, 0.1f),// <^


            // top render
//            new Vector3f(0.1f, 0.1f, -0.1f), // >^
//            new Vector3f(0.1f, 0.1f, 0.1f),  // >V
//            new Vector3f(0.1f, 0.1f, 0.1f), // <V
//            new Vector3f(0.1f, 0.1f, -0.1f),// <^
//            //left
//            new Vector3f(-0.1f, 0.1f, -0.1f), // >^
//            new Vector3f(-0.1f, 0.1f, 0.1f),  // >V
//            new Vector3f(-0.1f, 0.1f, 0.1f), // <V
//            new Vector3f(-0.1f, 0.1f, -0.1f),// <^
//            //front
//            new Vector3f(-0.1f, 0.1f, -0.1f), // >^
//            new Vector3f(-0.1f, 0.1f, -0.1f),  // >V
//            new Vector3f(0.1f, 0.1f, -0.1f), // <V
//            new Vector3f(0.1f, 0.1f, -0.1f),// <^
//            //back
//            new Vector3f(0.1f, 0.1f, 0.1f), // >^
//            new Vector3f(0.1f, 0.1f, 0.1f),  // >V
//            new Vector3f(-0.1f, 0.1f, 0.1f), // <V
//            new Vector3f(-0.1f, 0.1f, 0.1f),// <^


            // front render
            new Vector3f(-0.1f, -0.1f, -0.1f),
            new Vector3f(-0.1f, 0.1f, -0.1f),
            new Vector3f(0.1f, 0.1f, -0.1f),
            new Vector3f(0.1f, -0.1f, -0.1f),

            // back render
            new Vector3f(0.1f, -0.1f, 0.1f),
            new Vector3f(0.1f, 0.1f, 0.1f),
            new Vector3f(-0.1f, 0.1f, 0.1f),
            new Vector3f(-0.1f, -0.1f, 0.1f),

            // left render
            new Vector3f(0.1f, -0.1f, -0.1f),
            new Vector3f(0.1f, 0.1f, -0.1f),
            new Vector3f(0.1f, 0.1f, 0.1f),
            new Vector3f(0.1f, -0.1f, 0.1f),

            // right render
            new Vector3f(-0.1f, -0.1f, 0.1f),
            new Vector3f(-0.1f, 0.1f, 0.1f),
            new Vector3f(-0.1f, 0.1f, -0.1f),
            new Vector3f(-0.1f, -0.1f, -0.1f),

            //middle top inside
            new Vector3f(0.1f, -0.1f, -0.1f),
            new Vector3f(0.1f, -0.1f, 0.1f),
            new Vector3f(-0.1f, -0.1f, 0.1f),
            new Vector3f(-0.1f, -0.1f, -0.1f),
            // middle bottom render
            new Vector3f(-0.1f, 0.1f, -0.1f),
            new Vector3f(-0.1f, 0.1f, 0.1f),
            new Vector3f(0.1f, 0.1f, 0.1f),
            new Vector3f(0.1f, 0.1f, -0.1f),
//            //middle top inside
//            new Vector3f(0.01f, -0.01f, -0.01f),
//            new Vector3f(0.01f, -0.01f, 0.01f),
//            new Vector3f(-0.01f, -0.01f, 0.01f),
//            new Vector3f(-0.01f, -0.01f, -0.01f),
//            // middtorlfe bottom render
//            new Vector3f(-0.01f, 0.01f, -0.01f),
//            new Vector3f(-0.01f, 0.01f, 0.01f),
//            new Vector3f(0.01f, 0.01f, 0.01f),
//            new Vector3f(0.01f, 0.01f, -0.01f),


    };

    public static final Vec3[] CUBE_NORMALS = {
            // modified normals for the sides
            new Vec3(0, 0, 0.1f),
            new Vec3(0, 0, 0.1f),
    };

    protected float scale;
    protected float rotationDirection;
    protected float rotation;
    protected float rotationOffsetYaw;
    protected float rotationOffsetPitch;
    protected float rotationOffsetRoll;
    protected float colorOffset;


    public BookTestingParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.rotation = 0;

        this.lifetime = (int) motionX;

        Random random = new Random();

        this.colorOffset = (random.nextFloat() * 0.26f);
        this.rotationOffsetYaw = random.nextFloat();
        this.rotationOffsetPitch = random.nextFloat();
        this.rotationOffsetRoll = random.nextFloat();

        setScale(0.02F);
        setRotationDirection(random.nextFloat() - 0.5f);
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.setSize(scale * 0.5f, scale * 0.5f);
    }

    public void averageAge(int age) {
        Random random = new Random();
        this.lifetime = (int) (age + (random.nextDouble() * 2D - 1D) * 8);
    }

    public void setRotationDirection(float rotationDirection) {
        this.rotationDirection = rotationDirection;
    }


    @Override
    public void tick() {

        this.rotation = (this.rotationDirection * 0.1f) + this.rotation;

        super.tick();
    }

    @Override
    public void render(VertexConsumer builder, Camera renderInfo, float partialTicks) {
        Vec3 projectedView = renderInfo.getPosition();
        float lerpX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - projectedView.x());
        float lerpY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - projectedView.y());
        float lerpZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - projectedView.z());

//        double ageMultiplier = 1 - Math.pow(Mth.clamp(age + partialTicks, 0, lifetime), 3) / Math.pow(lifetime, 3);

//        RenderSystem._setShaderTexture(0, TEXTURE);

        for (int i = 0; i < CUBE.length / 4; i++) {
            for (int j = 0; j < 4; j++) {



                Vector3f vec3f = CUBE[i * 4 + j];
                Vec3 vec = new Vec3(vec3f.x, vec3f.y, vec3f.z);
                vec = vec
//                        .rotate(quaternionf).mul((float) (scale))
//                        .yRot(qte[0])
//                        .xRot(qte[1])
//                        .zRot(qte[2])
                        .yRot(this.rotation + this.rotationOffsetYaw)
                        .xRot(this.rotation + this.rotationOffsetPitch)
                        .zRot(this.rotation + this.rotationOffsetRoll)
                        .scale(scale)
                        .add(lerpX, lerpY, lerpZ);

                Vec3 normal = CUBE_NORMALS[0];

                builder.addVertex((float)vec.x, (float)vec.y, (float)vec.z)
                        .setUv(0, 0)
                        .setColor(Mth.clamp(rCol, 0, 1.0f), Mth.clamp(gCol, 0, 1.0f), Mth.clamp(bCol, 0, 1.0f), alpha)
                        .setNormal((float) normal.x, (float) normal.y, (float) normal.z)
                        .setLight(LightTexture.FULL_BRIGHT);

            }
        }
    }

    private static final ParticleRenderType renderType = new ParticleRenderType() {
        @Override
        public @Nullable BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.setShaderTexture(0, TEXTURE);

            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
            RenderSystem.disableDepthTest();

            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }
    };


    @Override
    public ParticleRenderType getRenderType() {
        return renderType;
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
            BookTestingParticle cauldronParticle = new BookTestingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            Random random = new Random();

            float colorOffset = (random.nextFloat() * 0.10f);
            cauldronParticle.setColor(0.5f, 0.5f, 1);

            cauldronParticle.setAlpha(1.0f);


            cauldronParticle.pickSprite(this.spriteSet);
            return cauldronParticle;

        }
    }


}
