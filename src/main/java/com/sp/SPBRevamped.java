package com.sp;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.command.EventCommand;
import com.sp.command.GimmeMyInventoryBack;
import com.sp.command.LevelCommand;
import com.sp.command.SkinwalkerCommand;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.custom.SmilerEntity;
import com.sp.entity.ik.model.GeckoLib.MowzieModelFactory;
import com.sp.entity.ik.util.PrAnCommonClass;
import com.sp.init.*;
import com.sp.item.ModItemGroups;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.networking.InitializePackets;
import com.sp.networking.S2C.*;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SPBRevamped implements ModInitializer {
	public static final String MOD_ID = "spb-revamped";
    public static final Logger LOGGER = LoggerFactory.getLogger("spb-revamped");
	public static final int FINAL_MAZE_SIZE = 5;

	public static final AttributeModifier SLOW_SPEED_MODIFIER = new AttributeModifier(
			ResourceLocation.fromNamespaceAndPath("spb-revamped", "slow_walk_speed"), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            PrAnCommonClass.isDev = true;
            PrAnCommonClass.shouldRenderDebugLegs = true;
            PrAnCommonClass.LOGGER.info("Started in a development environment. Debug renderers will be activated by default.");
        }

		BackroomsLevels.init();

		ModItems.registerModItems();
		ModSounds.registerSounds();
		InitializePackets.registerC2SPackets();
		ModItemGroups.registerItemGroups();
		ModBlocks.init();
		ModBlockEntities.registerAllBlockEntities();
		MidnightConfig.init(MOD_ID, ConfigStuff.class);
		ModGamerules.registerGamerules();

		CommandRegistrationCallback.EVENT.register(EventCommand::register);
		CommandRegistrationCallback.EVENT.register(LevelCommand::register);
		CommandRegistrationCallback.EVENT.register(GimmeMyInventoryBack::register);
		CommandRegistrationCallback.EVENT.register(SkinwalkerCommand::register);

		// Thanks Bob Mowzie
		GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
		GeckoLibConstants.init();

		PrAnCommonClass.init();

		FabricDefaultAttributeRegistry.register(ModEntities.SKIN_WALKER_ENTITY, SkinWalkerEntity.createSkinWalkerAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SMILER_ENTITY, SmilerEntity.createSmilerAttributes());

		LOGGER.info("\"WOOOOOOOOOOOOOOOOOOOOOOOooooooooooooooooooooooooo..........\" -He said as he fell into the backrooms, never to be seen again.");

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) -> {
			ServerPlayNetworking.send(player, new ReloadLightsPayload());
		}));

		ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
			if(!BackroomsLevels.isInBackrooms(oldPlayer.level().dimension())) {
				return;
			}

			boolean backupInvulnerable;
			try {
				ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
				PlayerComponent playerComponent = InitializeComponents.PLAYER.get(newPlayer);

				sendBlackScreenPacket(newPlayer, 120, false, true);
				backupInvulnerable = newPlayer.getAbilities().invulnerable;
				newPlayer.getAbilities().invulnerable = true;
				playerComponent.setShouldRender(false);
				playerComponent.sync();
				newPlayer.connection.send(new ClientboundSoundPacket(Holder.direct(ModSounds.NO_ESCAPE), SoundSource.AMBIENT, newPlayer.position().x(), newPlayer.position().y(), newPlayer.position().z(), 100.0f, 1.0f, newPlayer.getRandom().nextLong()));

				//After YOU CAN'T ESCAPE is over
				executorService.schedule(() -> {
					playerComponent.setShouldRender(true);
					playerComponent.setShouldDoStatic(true);
					playerComponent.sync();
					newPlayer.getAbilities().invulnerable = backupInvulnerable;
					executorService.shutdown();
				}, 6000, TimeUnit.MILLISECONDS);

				executorService.schedule(() -> {
					playerComponent.setShouldDoStatic(false);
					playerComponent.sync();
					executorService.shutdown();
				}, 8000, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				LOGGER.error("Error in AFTER_RESPAWN event: {}", String.valueOf(e));
			}
		}));
	}

	public static void sendCameraShakePacket(ServerPlayer player, double speed, double trauma){
		ServerPlayNetworking.send(player, new ScreenShakePayload(speed, trauma));
	}

	public static void sendBlackScreenPacket(ServerPlayer player, int duration, boolean shouldPauseSounds, boolean noEscape){
		ServerPlayNetworking.send(player, new BlackScreenPayload(duration, shouldPauseSounds, noEscape));
	}

	public static void sendPersonalPlaySoundPacket(ServerPlayer player, SoundEvent sound, float volume, float pitch){
		ServerPlayNetworking.send(player, new SoundPayload(Holder.direct(sound), volume, pitch));
	}

	public static void sendLevelTransitionLightsOutPacket(ServerPlayer player, int time) {
		PlayerComponent component = InitializeComponents.PLAYER.get(player);
		component.setTeleporting(true);
		ServerPlayNetworking.send(player, new LevelTransitionLightsOutPayload(time));
	}

    public static int getExitSpawnRadius(Level world) {
        int exitRadius = ConfigStuff.exitSpawnRadius;

        if (world.getServer() != null) {
            if (world.getServer().isDedicatedServer()) {
                exitRadius = ((NewServerProperties) ((DedicatedServer) world.getServer()).getProperties()).getExitSpawnRadius();
            }
        }

        return exitRadius;
    }
}