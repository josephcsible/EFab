package mcjty.efab.blocks;

import mcjty.efab.EFab;
import mcjty.lib.compat.CompatBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GenericEFabBlock extends CompatBlock {

    public GenericEFabBlock(Material material, String name) {
        super(material);
        setCreativeTab(EFab.tabEFab);
        setUnlocalizedName(EFab.MODID + "." + name);
        setRegistryName(name);
        setHardness(2.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
