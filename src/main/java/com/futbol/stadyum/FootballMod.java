package com.futbol.stadyum;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod(FootballMod.MODID)
public class FootballMod {
    public static final String MODID = "footballstadium";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // ---- BLOKLAR ----
    public static final RegistryObject<Block> PITCH_GRASS = block("pitch_grass",
            () -> new Block(Block.Properties.of().mapColor(MapColor.GRASS).strength(0.6f).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> PITCH_GRASS_LIGHT = block("pitch_grass_light",
            () -> new Block(Block.Properties.of().mapColor(MapColor.GRASS).strength(0.6f).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> PITCH_LINE = block("pitch_line",
            () -> new Block(Block.Properties.of().mapColor(MapColor.SNOW).strength(0.6f).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> STADIUM_SEAT = block("stadium_seat",
            () -> new Block(Block.Properties.of().mapColor(MapColor.COLOR_BLUE).strength(1.5f, 6f).sound(SoundType.STONE)));
    public static final RegistryObject<Block> GOAL_POST = block("goal_post",
            () -> new Block(Block.Properties.of().mapColor(MapColor.SNOW).strength(2.0f, 6f).sound(SoundType.METAL)));
    public static final RegistryObject<Block> GOAL_NET = block("goal_net",
            () -> new Block(Block.Properties.of().mapColor(MapColor.SNOW).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> STADIUM_LIGHT = block("stadium_light",
            () -> new Block(Block.Properties.of().mapColor(MapColor.SAND).strength(1.0f).lightLevel(s -> 15).sound(SoundType.GLASS)));

    // ---- ESYALAR ----
    public static final RegistryObject<Item> FOOTBALL = ITEMS.register("football",
            () -> new Item(new Item.Properties().stacksTo(16)) {
                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    ItemStack stack = player.getItemInHand(hand);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.SLIME_SQUISH_SMALL, SoundSource.PLAYERS, 1.0f, 1.0f);
                    if (!level.isClientSide) {
                        Snowball ball = new Snowball(level, player);
                        ItemStack one = stack.copy();
                        one.setCount(1);
                        ball.setItem(one);
                        ball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 1.5f, 1.0f);
                        level.addFreshEntity(ball);
                    }
                    player.getCooldowns().addCooldown(this, 10);
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
                }
            });

    public static final RegistryObject<Item> WHISTLE = ITEMS.register("whistle",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.5f, 2.0f);
                    player.getCooldowns().addCooldown(this, 20);
                    return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
                }
            });

    public static final RegistryObject<Item> YELLOW_CARD = ITEMS.register("yellow_card",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
                    if (!player.level().isClientSide) {
                        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
                        player.displayClientMessage(Component.literal("Sarı kart: " + target.getName().getString()), true);
                    }
                    return InteractionResult.sidedSuccess(player.level().isClientSide);
                }
            });

    public static final RegistryObject<Item> RED_CARD = ITEMS.register("red_card",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
                    if (!player.level().isClientSide) {
                        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4));
                        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
                        player.displayClientMessage(Component.literal("Kırmızı kart: " + target.getName().getString()), true);
                    }
                    return InteractionResult.sidedSuccess(player.level().isClientSide);
                }
            });

    // ---- YARATICI MOD SEKMESI ----
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("football_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MODID))
                    .icon(() -> new ItemStack(FOOTBALL.get()))
                    .displayItems((params, out) -> ITEMS.getEntries().forEach(i -> out.accept(i.get())))
                    .build());

    private static RegistryObject<Block> block(String name, Supplier<Block> sup) {
        RegistryObject<Block> b = BLOCKS.register(name, sup);
        ITEMS.register(name, () -> new BlockItem(b.get(), new Item.Properties()));
        return b;
    }

    public FootballMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
