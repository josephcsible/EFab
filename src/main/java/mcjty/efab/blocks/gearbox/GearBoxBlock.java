package mcjty.efab.blocks.gearbox;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class GearBoxBlock extends GenericEFabMultiBlockPart<GearBoxTE, EmptyContainer> {

    public GearBoxBlock() {
        super(Material.IRON, GearBoxTE.class, EmptyContainer.class, "gearbox", false);
    }

    @Override
    public boolean hasNoRotation() {
        return true;
    }

    @Override
    public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.clAddInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block adds " + TextFormatting.GREEN + "gearbox"
                + TextFormatting.WHITE + " style crafting to the fabricator");
    }

}
