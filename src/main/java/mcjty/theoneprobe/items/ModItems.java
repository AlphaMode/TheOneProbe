package mcjty.theoneprobe.items;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.compat.BaubleTools;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static CreativeProbe creativeProbe;
    public static Probe probe;
    public static Item diamondHelmetProbe;
    public static Item goldHelmetProbe;
    public static Item ironHelmetProbe;
    public static Item probeGoggles;
    public static ProbeNote probeNote;

    public static String PROBETAG = "theoneprobe";
    public static String PROBETAG_HAND = "theoneprobe_hand";

    public static void init() {
        probe = new Probe();
        Registry.ITEM.register(new Identifier(TheOneProbe.MODID, "probe"), probe);
        creativeProbe = new CreativeProbe();
        Registry.ITEM.register(new Identifier(TheOneProbe.MODID, "creativeprobe"), creativeProbe);

        // @todo fabric
//        ArmorItem.ArmorMaterial materialDiamondHelmet = EnumHelper.addArmorMaterial("diamond_helmet_probe", TheOneProbe.MODID + ":probe_diamond",
//                33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);
//        ArmorItem.ArmorMaterial materialGoldHelmet = EnumHelper.addArmorMaterial("gold_helmet_probe", TheOneProbe.MODID + ":probe_gold",
//                7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F);
//        ArmorItem.ArmorMaterial materialIronHelmet = EnumHelper.addArmorMaterial("iron_helmet_probe", TheOneProbe.MODID + ":probe_iron",
//                15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);

//        diamondHelmetProbe = makeHelmet(materialDiamondHelmet, 3, "diamond_helmet_probe");
//        goldHelmetProbe = makeHelmet(materialGoldHelmet, 4, "gold_helmet_probe");
//        ironHelmetProbe = makeHelmet(materialIronHelmet, 2, "iron_helmet_probe");

        probeNote = new ProbeNote();
        Registry.ITEM.register(new Identifier(TheOneProbe.MODID, "probenote"), probeNote);

        if (TheOneProbe.baubles) {
            probeGoggles = BaubleTools.initProbeGoggle();
        }
    }

//    private static Item makeHelmet(ItemArmor.ArmorMaterial material, int renderIndex, String name) {
//        Item item = new ItemArmor(material, renderIndex, EntityEquipmentSlot.HEAD) {
//            @Override
//            public boolean getHasSubtypes() {
//                return true;
//            }
//
//            @Override
//            public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
//                if (this.isInCreativeTab(tab)) {
//                    ItemStack stack = new ItemStack(this);
//                    CompoundTag tag = new CompoundTag();
//                    tag.setInteger(PROBETAG, 1);
//                    stack.setTagCompound(tag);
//                    subItems.add(stack);
//                }
//            }
//        };
//        item.setUnlocalizedName(TheOneProbe.MODID + "." + name);
//        item.setRegistryName(name);
//        item.setCreativeTab(TheOneProbe.tabProbe);
//        return item;
//    }


    public static boolean isProbeInHand(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() == probe || stack.getItem() == creativeProbe) {
            return true;
        }
        if (stack.getTag() == null) {
            return false;
        }
        return stack.getTag().containsKey(PROBETAG_HAND);
    }

    private static boolean isProbeHelmet(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.getTag() == null) {
            return false;
        }
        return stack.getTag().containsKey(PROBETAG);
    }

    public static boolean hasAProbeSomewhere(PlayerEntity player) {
        return hasProbeInHand(player, Hand.MAIN) || hasProbeInHand(player, Hand.OFF) || hasProbeInHelmet(player)
                || hasProbeInBauble(player);
    }

    private static boolean hasProbeInHand(PlayerEntity player, Hand hand) {
        ItemStack item = player.getStackInHand(hand);
        return isProbeInHand(item);
    }

    private static boolean hasProbeInHelmet(PlayerEntity player) {
        ItemStack helmet = player.inventory.getInvStack(36+3);
//        ItemStack helmet = player.inventory.armorInventory.get(3);
        return isProbeHelmet(helmet);
    }

    private static boolean hasProbeInBauble(PlayerEntity player) {
        if (TheOneProbe.baubles) {
            return BaubleTools.hasProbeGoggle(player);
        } else {
            return false;
        }
    }
}
