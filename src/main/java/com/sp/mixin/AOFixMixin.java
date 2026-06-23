package com.sp.mixin;

import foundry.veil.api.client.render.VeilRenderSystem;
import net.fabricmc.fabric.impl.client.indigo.Indigo;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.BitSet;


/**
 * Veil overrides Fabric's fix for Vanilla lighting which makes it appear blocky <p>
 * This is a combination of both fabric's {@link net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator}
 * and Minecraft's {@link net.minecraft.client.renderer.block.ModelBlockRenderer.AmbientOcclusionFace}
 * to make the lighting smooth again
 */
@SuppressWarnings("UnstableApiUsage")
@Mixin(targets = "net.minecraft.client.renderer.block.ModelBlockRenderer$AmbientOcclusionFace")
public abstract class AOFixMixin {

    @Shadow @Final
    float[] brightness;

    @Shadow @Final
    int[] lightmap;

    @Shadow protected abstract int blend(int i, int j, int k, int l, float f, float g, float h, float m);


    @Inject(method = "calculate", at = @At("HEAD"), cancellable = true)
    private void fix(BlockAndTintGetter world, BlockState state, BlockPos pos, Direction direction, float[] box, BitSet flags, boolean shaded, CallbackInfo ci){
        ci.cancel();

        BlockPos lightPos = flags.get(0) ? pos.relative(direction) : pos;
        ModelBlockRenderer.AdjacencyInfo neighborData = ModelBlockRenderer.AdjacencyInfo.fromFacing(direction);
        BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();
        BlockState searchState;
        ModelBlockRenderer.Cache brightnessCache = ModelBlockRenderer.CACHE.get();


        searchPos.setWithOffset(lightPos, neighborData.corners[0]);
        searchState = world.getBlockState(searchPos);
        int light1 = brightnessCache.getLightColor(searchState, world, searchPos);
        float ao1 = brightnessCache.getShadeBrightness(searchState, world, searchPos);

        boolean bl = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;


        searchPos.setWithOffset(lightPos, neighborData.corners[1]);
        searchState = world.getBlockState(searchPos);
        int light2 = brightnessCache.getLightColor(searchState, world, searchPos);
        float ao2 = brightnessCache.getShadeBrightness(searchState, world, searchPos);

        boolean bl2 = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;


        searchPos.setWithOffset(lightPos, neighborData.corners[2]);
        searchState = world.getBlockState(searchPos);
        int light3 = brightnessCache.getLightColor(searchState, world, searchPos);
        float ao3 = brightnessCache.getShadeBrightness(searchState, world, searchPos);

        boolean bl3 = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;


        searchPos.setWithOffset(lightPos, neighborData.corners[3]);
        searchState = world.getBlockState(searchPos);
        int light4 = brightnessCache.getLightColor(searchState, world, searchPos);
        float ao4 = brightnessCache.getShadeBrightness(searchState, world, searchPos);

        boolean bl4 = !searchState.isViewBlocking(world, searchPos) || searchState.getLightBlock(world, searchPos) == 0;



        float n;
        int o;
        if (!bl3 && !bl) {
            n = ao1;
            o = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.corners[0]).move(neighborData.corners[2]);
            searchState = world.getBlockState(searchPos);
            n = brightnessCache.getShadeBrightness(searchState, world, searchPos);
            o = brightnessCache.getLightColor(searchState, world, searchPos);
        }

        float p;
        int q;
        if (!bl4 && !bl) {
            p = ao1;
            q = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.corners[0]).move(neighborData.corners[3]);
            searchState = world.getBlockState(searchPos);
            p = brightnessCache.getShadeBrightness(searchState, world, searchPos);
            q = brightnessCache.getLightColor(searchState, world, searchPos);
        }

        float r;
        int s;
        if (!bl3 && !bl2) {
            r = ao1;
            s = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.corners[1]).move(neighborData.corners[2]);
            searchState = world.getBlockState(searchPos);
            r = brightnessCache.getShadeBrightness(searchState, world, searchPos);
            s = brightnessCache.getLightColor(searchState, world, searchPos);
        }

        float t;
        int u;
        if (!bl4 && !bl2) {
            t = ao1;
            u = light1;
        } else {
            searchPos.set(lightPos).move(neighborData.corners[1]).move(neighborData.corners[3]);
            searchState = world.getBlockState(searchPos);
            t = brightnessCache.getShadeBrightness(searchState, world, searchPos);
            u = brightnessCache.getLightColor(searchState, world, searchPos);
        }

        int v = brightnessCache.getLightColor(state, world, pos);
        searchPos.setWithOffset(pos, direction);
        searchState = world.getBlockState(searchPos);


        if (flags.get(0) || !searchState.isSolidRender(world, searchPos)) {
            v = brightnessCache.getLightColor(searchState, world, searchPos);
        }

        float w = flags.get(0)
                ? brightnessCache.getShadeBrightness(world.getBlockState(lightPos), world, lightPos)
                : brightnessCache.getShadeBrightness(world.getBlockState(pos), world, pos);

        ModelBlockRenderer.AmbientVertexRemap translation = ModelBlockRenderer.AmbientVertexRemap.fromFacing(direction);

        float x = (ao4 + ao1 + p + w) * 0.25F;
        float y = (ao3 + ao1 + n + w) * 0.25F;
        float z = (ao3 + ao2 + r + w) * 0.25F;
        float aa = (ao4 + ao2 + t + w) * 0.25F;
        if (flags.get(1) && neighborData.doNonCubicWeight) {
            float ab = box[neighborData.vert0Weights[0].shape] * box[neighborData.vert0Weights[1].shape];
            float ac = box[neighborData.vert0Weights[2].shape] * box[neighborData.vert0Weights[3].shape];
            float ad = box[neighborData.vert0Weights[4].shape] * box[neighborData.vert0Weights[5].shape];
            float ae = box[neighborData.vert0Weights[6].shape] * box[neighborData.vert0Weights[7].shape];
            float af = box[neighborData.vert1Weights[0].shape] * box[neighborData.vert1Weights[1].shape];
            float ag = box[neighborData.vert1Weights[2].shape] * box[neighborData.vert1Weights[3].shape];
            float ah = box[neighborData.vert1Weights[4].shape] * box[neighborData.vert1Weights[5].shape];
            float ai = box[neighborData.vert1Weights[6].shape] * box[neighborData.vert1Weights[7].shape];
            float aj = box[neighborData.vert2Weights[0].shape] * box[neighborData.vert2Weights[1].shape];
            float ak = box[neighborData.vert2Weights[2].shape] * box[neighborData.vert2Weights[3].shape];
            float al = box[neighborData.vert2Weights[4].shape] * box[neighborData.vert2Weights[5].shape];
            float am = box[neighborData.vert2Weights[6].shape] * box[neighborData.vert2Weights[7].shape];
            float an = box[neighborData.vert3Weights[0].shape] * box[neighborData.vert3Weights[1].shape];
            float ao = box[neighborData.vert3Weights[2].shape] * box[neighborData.vert3Weights[3].shape];
            float ap = box[neighborData.vert3Weights[4].shape] * box[neighborData.vert3Weights[5].shape];
            float aq = box[neighborData.vert3Weights[6].shape] * box[neighborData.vert3Weights[7].shape];
            this.brightness[translation.vert0] = x * ab + y * ac + z * ad + aa * ae;
            this.brightness[translation.vert1] = x * af + y * ag + z * ah + aa * ai;
            this.brightness[translation.vert2] = x * aj + y * ak + z * al + aa * am;
            this.brightness[translation.vert3] = x * an + y * ao + z * ap + aa * aq;
            int ar = this.meanBrightness(light4, light1, q, v);
            int as = this.meanBrightness(light3, light1, o, v);
            int at = this.meanBrightness(light3, light2, s, v);
            int au = this.meanBrightness(light4, light2, u, v);
            this.lightmap[translation.vert0] = this.blend(ar, as, at, au, ab, ac, ad, ae);
            this.lightmap[translation.vert0] = this.blend(ar, as, at, au, ab, ac, ad, ae);
            this.lightmap[translation.vert1] = this.blend(ar, as, at, au, af, ag, ah, ai);
            this.lightmap[translation.vert2] = this.blend(ar, as, at, au, aj, ak, al, am);
            this.lightmap[translation.vert3] = this.blend(ar, as, at, au, an, ao, ap, aq);
        } else {
            this.lightmap[translation.vert0] = this.meanBrightness(light4, light1, q, v);
            this.lightmap[translation.vert1] = this.meanBrightness(light3, light1, o, v);
            this.lightmap[translation.vert2] = this.meanBrightness(light3, light2, s, v);
            this.lightmap[translation.vert3] = this.meanBrightness(light4, light2, u, v);
            this.brightness[translation.vert0] = x;
            this.brightness[translation.vert1] = y;
            this.brightness[translation.vert2] = z;
            this.brightness[translation.vert3] = aa;
        }

        //Reinserting veil's "Disable Ambient Occlusion"
        // TODO(UDouble Factory): i need to check this
//        VeilDeferredRenderer deferredRenderer = VeilRenderSystem.renderer().getDeferredRenderer();
//        if (!deferredRenderer.getLightRenderer().isAmbientOcclusionEnabled()) {
//            Arrays.fill(this.brightness, 1.0F);
//        }

    }


    /**These 4 methods are also from
     * {@link net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator}
     */
    @Unique
    private int meanBrightness(int lightA, int lightB, int lightC, int lightD) {
        if (Indigo.FIX_MEAN_LIGHT_CALCULATION) {
            if (lightA == 0 || lightB == 0 || lightC == 0 || lightD == 0) {
                // Normalize values to non-zero minimum
                final int min = nonZeroMin(nonZeroMin(lightA, lightB), nonZeroMin(lightC, lightD));

                lightA = Math.max(lightA, min);
                lightB = Math.max(lightB, min);
                lightC = Math.max(lightC, min);
                lightD = Math.max(lightD, min);
            }

            return meanInnerBrightness(lightA, lightB, lightC, lightD);
        } else {
            return vanillaMeanBrightness(lightA, lightB, lightC, lightD);
        }
    }

    @Unique
    private int meanInnerBrightness(int a, int b, int c, int d) {
        // bitwise divide by 4, clamp to expected (positive) range
        return a + b + c + d >> 2 & 0xFF00FF;
    }

    @Unique
    private int vanillaMeanBrightness(int a, int b, int c, int d) {
        if (a == 0) a = d;
        if (b == 0) b = d;
        if (c == 0) c = d;
        // bitwise divide by 4, clamp to expected (positive) range
        return a + b + c + d >> 2 & 0xFF00FF;
    }

    @Unique
    private int nonZeroMin(int a, int b) {
        if (a == 0) return b;
        if (b == 0) return a;
        return Math.min(a, b);
    }

}
