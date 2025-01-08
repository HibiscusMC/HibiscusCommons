package me.lojosho.hibiscuscommons.config.serializer;

import me.lojosho.hibiscuscommons.HibiscusCommonsPlugin;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import me.lojosho.hibiscuscommons.util.*;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
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
import java.util.stream.Collectors;

public class ItemSerializer implements TypeSerializer<ItemStack> {

    public static final ItemSerializer INSTANCE = new ItemSerializer();
    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String NAME = "name";
    private static final String UNBREAKABLE = "unbreakable";
    private static final String GLOWING = "glowing";
    private static final String LORE = "lore";
    private static final String MODEL_DATA = "model-data";
    private static final String NBT_TAGS = "nbt-tag";
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
        final ConfigurationNode modelDataNode = source.node(MODEL_DATA);
        final ConfigurationNode nbtNode = source.node(NBT_TAGS);
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

        if (!nameNode.virtual()) {
            if (HibiscusCommonsPlugin.isOnPaper()) {
                itemMeta.displayName(AdventureUtils.MINI_MESSAGE.deserialize(nameNode.getString("")));
            } else {
                itemMeta.setDisplayName(StringUtils.parseStringToString(nameNode.getString("")));
            }
        }
        if (!unbreakableNode.virtual()) itemMeta.setUnbreakable(unbreakableNode.getBoolean());
        if (!glowingNode.virtual()) {
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addEnchant(Enchantment.LUCK, 1, true);
        }
        if (!loreNode.virtual()) {
            if (HibiscusCommonsPlugin.isOnPaper()) {
                itemMeta.lore(loreNode.getList(String.class, new ArrayList<>()).
                        stream().map(AdventureUtils.MINI_MESSAGE::deserialize).collect(Collectors.toList()));
            } else {
                itemMeta.setLore(loreNode.getList(String.class, new ArrayList<>()).
                        stream().map(StringUtils::parseStringToString).collect(Collectors.toList()));
            }
        }
        if (!modelDataNode.virtual()) itemMeta.setCustomModelData(modelDataNode.getInt());

        if (!nbtNode.virtual()) {
            for (ConfigurationNode nbtNodes : nbtNode.childrenMap().values()) {
                itemMeta.getPersistentDataContainer().set(NamespacedKey.minecraft(nbtNodes.key().toString()), PersistentDataType.STRING, nbtNodes.getString());
            }
        }

        if (!enchantsNode.virtual()) {
            for (ConfigurationNode enchantNode : enchantsNode.childrenMap().values()) {
                String enchantName = enchantNode.key().toString().toLowerCase();
                NamespacedKey key = NamespacedKey.minecraft(enchantName);
                Enchantment enchant = Enchantment.getByKey(key);
                if (enchant == null) continue;
                itemMeta.addEnchant(enchant, enchantNode.getInt(1), true);
            }
        }

        try {
            if (!itemFlagsNode.virtual()) {
                for (String itemFlag : itemFlagsNode.getList(String.class)) {
                    if (!EnumUtils.isValidEnum(ItemFlag.class, itemFlag)) continue;
                    //MessagesUtil.sendDebugMessages("Added " + itemFlag + " to the item!");
                    itemMeta.addItemFlags(ItemFlag.valueOf(itemFlag));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
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
            itemMeta = skullMeta;
        }

        if (!colorNode.virtual()) {
            if (ColorBuilder.canBeColored(item.getType())) {
                if (!redNode.virtual()) {
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

