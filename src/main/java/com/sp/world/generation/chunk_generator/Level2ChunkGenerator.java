package com.sp.world.generation.chunk_generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;

import java.util.Optional;

public final class Level2ChunkGenerator extends BackroomsChunkGenerator {
    public static final Codec<Level2ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
                    )
                    .apply(instance, instance.stable(Level2ChunkGenerator::new))
    );
    private final Holder<NoiseGeneratorSettings> settings;
    RandomSource random = RandomSource.create();
    ImprovedNoise noiseSampler = new ImprovedNoise(random);

    public Level2ChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
    }

    @Override
    public void generate(WorldGenLevel world, ChunkAccess chunk) {
        int x = chunk.getPos().getMinBlockX();
        int z = chunk.getPos().getMinBlockZ();

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        MinecraftServer server = world.getServer();

        StructureTemplateManager structureTemplateManager = world.getServer().getStructureManager();
        Optional<StructureTemplate> optional;

        ResourceLocation roomIdentifier;
        StructurePlaceSettings structurePlacementData = new StructurePlaceSettings();
        structurePlacementData.setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(true);



        if(chunk.getPos().x == 0 && chunk.getPos().z == 0 ){
            roomIdentifier = new ResourceLocation(SPBRevamped.MOD_ID, "level2/stairwell2_2");
            optional = structureTemplateManager.get(roomIdentifier);

            optional.ifPresent(structureTemplate -> structureTemplate.placeInWorld(
                    world,
                    mutable.set(x - 1, 19, z),
                    mutable.set(x - 1, 19, z),
                    structurePlacementData, random, 2));

        } else if (((float)chunk.getPos().x) == 0){
            double noise1 = noiseSampler.noise((x) * 0.02, 0, (z) * 0.02);
            if (server != null) {

                if (noise1 > 0.0) {
                    roomIdentifier = this.getRoom(false);
                }
                else{
                    roomIdentifier = this.getRoom(true);
                }

                optional = structureTemplateManager.get(roomIdentifier);

                optional.ifPresent(structureTemplate -> structureTemplate.placeInWorld(
                        world,
                        mutable.set(x - 1, 19, z),
                        mutable.set(x - 1, 19, z),
                        structurePlacementData, random, 2));
            }
        }
    }


    public ResourceLocation getRoom(boolean dark){
        RandomSource random = RandomSource.create();
        int roomNumber = random.nextIntBetweenInclusive(1,18);
        ResourceLocation identifier;

        if(dark){
            identifier = new ResourceLocation(SPBRevamped.MOD_ID, "level2/dark_room" + roomNumber);
        }else{
            identifier = new ResourceLocation(SPBRevamped.MOD_ID, "level2/room" + roomNumber);
        }
        return identifier;
    }


    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

}

