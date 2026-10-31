package com.yungnickyoung.minecraft.yungsapi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.adaptations.EnhancedTerrainAdaptation;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierData;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierHelper;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedBeardifierRigid;
import com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.beardifier.EnhancedJigsawJunction;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects behavior required for using {@link EnhancedTerrainAdaptation} with {@link YungJigsawStructure}.
 * Since 26.3 the Beardifier is a DensitySampler: the enhanced contribution is added on top of vanilla's result,
 * both for single samples and for whole volumes.
 */
@Mixin(Beardifier.class)
@NullMarked
public abstract class BeardifierMixin implements EnhancedBeardifierData {
    @Unique
    private @Nullable ObjectList<EnhancedJigsawJunction> enhancedJunctions;

    @Unique
    private @Nullable ObjectList<EnhancedBeardifierRigid> enhancedPieces;

    @WrapMethod(method = "forStructuresInChunk")
    private static Beardifier yungsapi_supportCustomTerrainAdaptations(final StructureManager structureManager, final ChunkPos chunkPos, final Operation<Beardifier> original) {
        return EnhancedBeardifierHelper.forStructuresInChunk(structureManager, chunkPos, original.call(structureManager, chunkPos));
    }

    @Inject(method = "sampleVolume", at = @At("RETURN"))
    private void yungsapi_sampleVolume(final SamplerContext context, final DensityBuffer buffer, final DensityVolume volume, final CallbackInfo ci) {
        if (!this.yungsapi_hasEnhancedData()) return;
        for (int iz = 0; iz < volume.sizeZ(); iz++) {
            int z = volume.blockZ(iz);
            for (int ix = 0; ix < volume.sizeX(); ix++) {
                int x = volume.blockX(ix);
                for (int iy = 0; iy < volume.sizeY(); iy++) {
                    float extra = (float) EnhancedBeardifierHelper.computeDensity(x, volume.blockY(iy), z, 0.0D, this);
                    if (extra != 0.0F) {
                        buffer.addTo(volume.indexUnchecked(ix, iy, iz), extra);
                    }
                }
            }
        }
    }

    @Inject(method = "sampleValue", at = @At("RETURN"), cancellable = true)
    private void yungsapi_sampleValue(final SamplerContext context, final int x, final int y, final int z, final CallbackInfoReturnable<Float> cir) {
        if (!this.yungsapi_hasEnhancedData()) return;
        cir.setReturnValue(cir.getReturnValue() + (float) EnhancedBeardifierHelper.computeDensity(x, y, z, 0.0D, this));
    }

    @Unique
    private boolean yungsapi_hasEnhancedData() {
        return (this.enhancedPieces != null && !this.enhancedPieces.isEmpty())
                || (this.enhancedJunctions != null && !this.enhancedJunctions.isEmpty());
    }

    @Override
    public @Nullable ObjectListIterator<EnhancedBeardifierRigid> yungsapi_getEnhancedPieceIterator() {
        return this.enhancedPieces == null ? null : this.enhancedPieces.iterator();
    }

    @Override
    public void yungsapi_setEnhancedPieces(ObjectList<EnhancedBeardifierRigid> enhancedPieces) {
        this.enhancedPieces = enhancedPieces;
    }

    @Override
    public @Nullable ObjectListIterator<EnhancedJigsawJunction> yungsapi_getEnhancedJunctionIterator() {
        return this.enhancedJunctions == null ? null : this.enhancedJunctions.iterator();
    }

    @Override
    public void yungsapi_setEnhancedJunctions(ObjectList<EnhancedJigsawJunction> enhancedJunctions) {
        this.enhancedJunctions = enhancedJunctions;
    }
}
