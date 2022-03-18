package mcjty.theoneprobe.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class NetworkTools {

    /// This function supports itemstacks with more then 64 items.
    public static ItemStack readItemStack(FriendlyByteBuf buf) {
        ItemStack stack = buf.readItem();
        stack.setCount(buf.readInt());
        return stack;
    }

    public static FriendlyByteBuf writeItemStack(FriendlyByteBuf buf, ItemStack pStack, boolean limitedTag) {
        if (pStack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            Item item = pStack.getItem();
            buf.writeVarInt(Item.getId(item));
            buf.writeByte(pStack.getCount());
            CompoundTag compoundtag = null;
            if (item.shouldOverrideMultiplayerNbt()) {
                compoundtag = pStack.getTag();
            }

            buf.writeNbt(compoundtag);
        }

        return buf;
    }

    /// This function supports itemstacks with more then 64 items.
    public static void writeItemStack(FriendlyByteBuf buf, ItemStack itemStack) {
        writeItemStack(buf, itemStack, false);
        buf.writeInt(itemStack.getCount());
    }

    public static String readString(FriendlyByteBuf dataIn) {
        int s = dataIn.readInt();
        if (s == -1) {
            return null;
        }
        if (s == 0) {
            return "";
        }
        byte[] dst = new byte[s];
        dataIn.readBytes(dst);
        return new String(dst);
    }

    public static void writeString(FriendlyByteBuf dataOut, String str) {
        if (str == null) {
            dataOut.writeInt(-1);
            return;
        }
        byte[] bytes = str.getBytes();
        dataOut.writeInt(bytes.length);
        if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
        }
    }

    public static String readStringUTF8(FriendlyByteBuf dataIn) {
        int s = dataIn.readInt();
        if (s == -1) {
            return null;
        }
        if (s == 0) {
            return "";
        }
        byte[] dst = new byte[s];
        dataIn.readBytes(dst);
        return new String(dst, java.nio.charset.StandardCharsets.UTF_8);
    }

    public static void writeStringUTF8(FriendlyByteBuf dataOut, String str) {
        if (str == null) {
            dataOut.writeInt(-1);
            return;
        }
        byte[] bytes = str.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        dataOut.writeInt(bytes.length);
        if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
        }
    }

    public static <T extends Enum<T>> void writeEnumCollection(FriendlyByteBuf buf, Collection<T> collection) {
        buf.writeVarInt(collection.size());
        for (T type : collection) {
            buf.writeEnum(type);
        }
    }

    public static <T extends Enum<T>> void readEnumCollection(FriendlyByteBuf buf, Collection<T> collection, Class<T> enumClass) {
        collection.clear();
        int size = buf.readVarInt();
        for (int i = 0 ; i < size ; i++) {
            collection.add(buf.readEnum(enumClass));
        }
    }

    public static void writeFloat(FriendlyByteBuf buf, Float f) {
        if (f != null) {
            buf.writeBoolean(true);
            buf.writeFloat(f);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static Float readFloat(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return buf.readFloat();
        } else {
            return null;
        }
    }
}
