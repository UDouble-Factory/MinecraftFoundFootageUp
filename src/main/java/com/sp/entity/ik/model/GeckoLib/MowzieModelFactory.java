package com.sp.entity.ik.model.GeckoLib;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.loading.json.raw.Cube;
import software.bernie.geckolib.loading.json.raw.ModelProperties;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.BoneStructure;
import software.bernie.geckolib.loading.object.GeometryTree;

import java.util.List;

/**
 * Provided by Bob Mowzie from <a href="https://www.curseforge.com/minecraft/mc-mods/mowzies-mobs">Mowzie's Mobs</a>
 */
public class MowzieModelFactory implements BakedModelFactory {

    @Override
    public BakedGeoModel constructGeoModel(GeometryTree geometryTree) {
        List<GeoBone> bones = new ObjectArrayList<>();

        for (BoneStructure boneStructure : geometryTree.topLevelBones().values()) {
            bones.add(constructBone(boneStructure, geometryTree.properties(), null));
        }

        return new BakedGeoModel(bones, geometryTree.properties());
    }

    @Override
    public GeoBone constructBone(BoneStructure boneStructure, ModelProperties properties, @Nullable GeoBone parent) {
        Bone bone = boneStructure.self();
        MowzieGeoBone newBone = new MowzieGeoBone(parent, bone.name(), bone.mirror(), bone.inflate(), bone.neverRender(), bone.reset());
        Vec3 rotation = bone.rotation() != null ? new Vec3(bone.rotation()[0], bone.rotation()[1], bone.rotation()[2]) : Vec3.ZERO;
        Vec3 pivot = bone.pivot() != null ? new Vec3(bone.pivot()[0], bone.pivot()[1], bone.pivot()[2]) : Vec3.ZERO;

        newBone.updateRotation((float) Math.toRadians(-rotation.x), (float) Math.toRadians(-rotation.y), (float) Math.toRadians(rotation.z));
        newBone.updatePivot((float) -pivot.x, (float) pivot.y, (float) pivot.z);

        for (Cube cube : bone.cubes()) {
            newBone.getCubes().add(constructCube(cube, properties, newBone));
        }

        for (BoneStructure child : boneStructure.children().values()) {
            newBone.getChildBones().add(constructBone(child, properties, newBone));
        }

        return newBone;
    }

    @Override
    public GeoCube constructCube(Cube cube, ModelProperties properties, GeoBone bone) {
        boolean mirror = cube.mirror() == Boolean.TRUE;
        double inflate = cube.inflate() != null ? cube.inflate() / 16f : (bone.getInflate() == null ? 0 : bone.getInflate() / 16f);
        Vec3 size = cube.size() != null ? new Vec3(cube.size()[0], cube.size()[1], cube.size()[2]) : Vec3.ZERO;
        Vec3 origin = cube.origin() != null ? new Vec3(cube.origin()[0], cube.origin()[1], cube.origin()[2]) : Vec3.ZERO;
        Vec3 rotation = cube.rotation() != null ? new Vec3(cube.rotation()[0], cube.rotation()[1], cube.rotation()[2]) : Vec3.ZERO;
        Vec3 pivot = cube.pivot() != null ? new Vec3(cube.pivot()[0], cube.pivot()[1], cube.pivot()[2]) : Vec3.ZERO;
        origin = new Vec3(-(origin.x + size.x) / 16d, origin.y / 16d, origin.z / 16d);
        Vec3 vertexSize = size.multiply(1 / 16d, 1 / 16d, 1 / 16d);

        pivot = pivot.multiply(-1, 1, 1);
        rotation = new Vec3(Math.toRadians(-rotation.x), Math.toRadians(-rotation.y), Math.toRadians(rotation.z));
        GeoQuad[] quads = buildQuads(cube.uv(), new VertexSet(origin, vertexSize, inflate), cube, (float) properties.textureWidth(), (float) properties.textureHeight(), mirror);

        return new GeoCube(quads, pivot, rotation, size, inflate, mirror);
    }
}
