package me.lojosho.hibiscuscommons.config.serializer;

import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import me.lojosho.hibiscuscommons.nms.MinecraftVersion;
import me.lojosho.hibiscuscommons.nms.NMSHandlers;
import me.lojosho.hibiscuscommons.util.*;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class ItemSerializer implements TypeSerializer<ItemStack> {

    public static final ItemSerializer INSTANCE = new ItemSerializer();
    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String NAME = "name";
    private static final String UNBREAKABLE = "unbreakable";
    private static final String GLOWING = "glowing";
    private static final String LORE = "lore";
    private static final String TOOLTIP_STYLE = "tooltip-style";
    private static final String MODEL_DATA = "model-data";
    private static final String MODEL_ID = "model-id";
    private static final String NBT_TAGS = "nbt-tag";
    private static final String REMOVE_EXISTING_ENCHANTS = "remove-enchants";
    private static final String ENCHANTS = "enchants";
    private static final String ITEM_FLAGS = "item-flags";
    private static final String TEXTURE = "texture";
    private static final String OWNER = "owner";
    private static final String COLOR = "color";
    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";

    private ItemSerializer() {
    }

    @Override
    public ItemStack deserialize(final Type type, final ConfigurationNode source)
            throws SerializationException {
        final ConfigurationNode materialNode = source.node(MATERIAL);
        final ConfigurationNode amountNode = source.node(AMOUNT);
        final ConfigurationNode nameNode = source.node(NAME);
        final ConfigurationNode unbreakableNode = source.node(UNBREAKABLE);
        final ConfigurationNode glowingNode = source.node(GLOWING);
        final ConfigurationNode loreNode = source.node(LORE);
        final ConfigurationNode toolTipStyleNode = source.node(TOOLTIP_STYLE);
        final ConfigurationNode modelDataNode = source.node(MODEL_DATA);
        final ConfigurationNode modelIdNode = source.node(MODEL_ID);
        final ConfigurationNode nbtNode = source.node(NBT_TAGS);
        final ConfigurationNode removeEnchantNode = source.node(REMOVE_EXISTING_ENCHANTS);
        final ConfigurationNode enchantsNode = source.node(ENCHANTS);
        final ConfigurationNode itemFlagsNode = source.node(ITEM_FLAGS);
        final ConfigurationNode textureNode = source.node(TEXTURE);
        final ConfigurationNode ownerNode = source.node(OWNER);
        final ConfigurationNode colorNode = source.node(COLOR);
        final ConfigurationNode redNode = colorNode.node(RED);
        final ConfigurationNode greenNode = colorNode.node(GREEN);
        final ConfigurationNode blueNode = colorNode.node(BLUE);

        if (materialNode.virtual()) return null;
        String material = materialNode.getString("");
        ItemStack item = Hooks.getItem(material);
        if (item == null) {
            //HMCCosmeticsPlugin.getInstance().getLogger().severe("Invalid Material -> " + material);
            return new ItemStack(Material.AIR);
        }
        item.setAmount(amountNode.getInt(1));

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return item;

        ItemBuilder itemBuilder = new ItemBuilder(item, itemMeta);

        if (!nameNode.virtual()) {
            itemBuilder.setDisplayName(nameNode.getString(""));
        }
        if (!unbreakableNode.virtual()) {
            itemBuilder.setUnbreakable(unbreakableNode.getBoolean(false));
        }
        if (!glowingNode.virtual()) {
            itemBuilder.setGlowing(true);
        }
        if (!loreNode.virtual()) {
            itemBuilder.setLore(loreNode.getList(String.class, new ArrayList<>()));
        }
        if (!modelDataNode.virtual()) {
            itemBuilder.setCustomModelId(modelDataNode.getInt());
        }
        if (!modelIdNode.virtual()) {
            itemBuilder.setModelItemId(modelIdNode.getString(""));
        }
        if (!toolTipStyleNode.virtual()) {
            itemBuilder.setToolTip(NamespacedKey.fromString(toolTipStyleNode.getString("")));
        }

        if (!nbtNode.virtual()) {
            for (ConfigurationNode nbtNodes : nbtNode.childrenMap().values()) {
                itemMeta.getPersistentDataContainer().set(NamespacedKey.minecraft(nbtNodes.key().toString()), PersistentDataType.STRING, nbtNodes.getString());
            }
        }

        if (!removeEnchantNode.virtual()) {
            boolean shouldRemove = removeEnchantNode.getBoolean(false);
            if (shouldRemove) itemBuilder.removeAllEnchantments();
        }

        if (!enchantsNode.virtual()) {
            for (ConfigurationNode enchantNode : enchantsNode.childrenMap().values()) {
                String enchantName = enchantNode.key().toString().toLowerCase();
                int level = enchantNode.getInt(1);
                itemBuilder.addEnchantment(enchantName, level);
            }
        }

        item = itemBuilder.build();
        itemMeta = item.getItemMeta();
        // A few more specific misc things

        if (!itemFlagsNode.virtual()) {
            if (HibiscusCommonsPlugin.isOnPaper() && NMSHandlers.getVersion().isHigherOrEqual(MinecraftVersion.v1_20_6)) {
                itemMeta.setAttributeModifiers(item.getType().getDefaultAttributeModifiers());
            }
            for (String itemFlag : itemFlagsNode.getList(String.class)) {
                if (!EnumUtils.isValidEnum(ItemFlag.class, itemFlag)) continue;
                //MessagesUtil.sendDebugMessages("Added " + itemFlag + " to the item!");
                itemMeta.addItemFlags(ItemFlag.valueOf(itemFlag));
            }
        }

        if (item.getType() == Material.PLAYER_HEAD) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;
            if (!ownerNode.virtual()) {
                String ownerString = ownerNode.getString();
                if (ownerString.contains("%")) {
                    // This means it has PAPI placeholders in it
                    skullMeta.getPersistentDataContainer().set(InventoryUtils.getSkullOwner(), PersistentDataType.STRING, ownerString);
                }
                OfflinePlayer player = Bukkit.getOfflinePlayer(ownerString);
                skullMeta.setOwningPlayer(player);
            }

            if (!textureNode.virtual()) {
                String textureString = textureNode.getString();
                if (textureString.contains("%")) {
                    // This means it has PAPI placeholders in it
                    skullMeta.getPersistentDataContainer().set(InventoryUtils.getSkullTexture(), PersistentDataType.STRING, textureString);
                }
                // Decodes the texture string and sets the texture url to the skull
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();

                String decoded = new String(Base64.getDecoder().decode(textureString));
                URL url = null;
                try {
                    url = new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (url != null) {
                    textures.setSkin(url);
                    profile.setTextures(textures);
                    skullMeta.setOwnerProfile(profile);
                }
            }
        }

        if (!colorNode.virtual()) {
            if (ColorBuilder.canBeColored(item.getType())) {
                if (!redNode.virtual() && !greenNode.virtual() && !blueNode.virtual()) {
                    itemMeta = ColorBuilder.color(itemMeta, Color.fromRGB(redNode.getInt(0), greenNode.getInt(0), blueNode.getInt(0)));
                } else {
                    itemMeta = ColorBuilder.color(itemMeta, ServerUtils.hex2Rgb(colorNode.getString("#FFFFFF")));
                }
            }
        }

        item.setItemMeta(itemMeta);
        return item;
    }
    @Override
    public void serialize(final Type type, @Nullable final ItemStack obj, final ConfigurationNode node) throws SerializationException {

    }
}

