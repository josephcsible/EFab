package mcjty.efab.blocks.monitor;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.lib.container.EmptyContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class MonitorBlock extends GenericEFabMultiBlockPart<MonitorTE, EmptyContainer> {

    public static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public MonitorBlock() {
        super(Material.IRON, MonitorTE.class, EmptyContainer.class, "monitor", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.clAddInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This block allows you to monitor");
        tooltip.add(TextFormatting.WHITE + "crafting operation status");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(MonitorTE.class, new MonitorRenderer());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return EMPTY;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
}
