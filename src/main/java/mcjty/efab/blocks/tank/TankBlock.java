package mcjty.efab.blocks.tank;

import mcjty.efab.blocks.GenericEFabMultiBlockPart;
import mcjty.efab.config.GeneralConfiguration;
import mcjty.efab.tools.InventoryHelper;
import mcjty.lib.container.EmptyContainer;
import mcjty.lib.tools.ChatTools;
import mcjty.lib.tools.FluidTools;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class TankBlock extends GenericEFabMultiBlockPart<TankTE, EmptyContainer> {

    public static final AxisAlignedBB EMPTY = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public static final PropertyEnum<EnumTankState> STATE = PropertyEnum.create("state", EnumTankState.class, EnumTankState.values());

    public TankBlock() {
        super(Material.IRON, TankTE.class, EmptyContainer.class, TankItemBlock.class, "tank", false);
    }

    @Override
    public boolean isHorizRotation() {
        return true;
    }

    @Override
    public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.clAddInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(TextFormatting.WHITE + "This tank can store " + TextFormatting.GREEN + GeneralConfiguration.tankCapacity + TextFormatting.WHITE + " mb");
        tooltip.add(TextFormatting.WHITE + "You can combine tanks by placing them");
        tooltip.add(TextFormatting.WHITE + "on top of each other");
        tooltip.add(TextFormatting.WHITE + "Tanks are used for " + TextFormatting.GREEN + "liquid" + TextFormatting.WHITE + " and "
            + TextFormatting.GREEN + "steam" + TextFormatting.WHITE + " crafting");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initModel() {
        super.initModel();
        ClientRegistry.bindTileEntitySpecialRenderer(TankTE.class, new TankRenderer());
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

    @Override
    protected boolean clOnBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TankTE) {
                TankTE tankTE = (TankTE) te;
                if (ItemStackTools.isValid(heldItem)) {
                    if (FluidTools.isEmptyContainer(heldItem)) {
                        extractIntoContainer(player, tankTE);
                        return true;
                    } else if (FluidTools.isFilledContainer(heldItem)) {
                        fillFromContainer(player, world, tankTE);
                        return true;
                    }
                }
                FluidStack fluid = tankTE.getFluid();
                if (fluid == null || fluid.amount <= 0) {
                    ChatTools.addChatMessage(player, new TextComponentString("Tank is empty"));
                } else {
                    ChatTools.addChatMessage(player, new TextComponentString("Tank contains " + fluid.amount + "mb of " + fluid.getLocalizedName()));
                }
            }
        }
        return true;
    }

    private void fillFromContainer(EntityPlayer player, World world, TankTE tank) {
        ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        if (ItemStackTools.isEmpty(heldItem)) {
            return;
        }
        ItemStack container = heldItem.copy().splitStack(1);
        FluidStack fluidStack = FluidTools.convertBucketToFluid(container);
        if (fluidStack != null) {
            int filled = tank.getHandler().fill(fluidStack, false);
            if (filled == fluidStack.amount) {
                // Success
                tank.getHandler().fill(fluidStack, true);
                if (!player.capabilities.isCreativeMode) {
                    heldItem.splitStack(1);
                    ItemStack emptyContainer = FluidTools.drainContainer(container);
                    mcjty.efab.tools.InventoryHelper.giveItemToPlayer(player, emptyContainer);
                }
            }
        }
    }

    private void extractIntoContainer(EntityPlayer player, TankTE tank) {
        FluidStack drained = tank.getHandler().drain(1, false);
        if (drained != null) {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
            if (ItemStackTools.isEmpty(heldItem)) {
                return;
            }
            ItemStack container = heldItem.copy().splitStack(1);
            int capacity = FluidTools.getCapacity(drained, container);
            if (capacity != 0) {
                drained = tank.getHandler().drain(capacity, false);
                if (drained != null && drained.amount == capacity) {
                    tank.getHandler().drain(capacity, true);
                    ItemStack filledContainer = FluidTools.fillContainer(drained, container);
                    if (ItemStackTools.isValid(filledContainer)) {
                        heldItem.splitStack(1);
                        InventoryHelper.giveItemToPlayer(player, filledContainer);
                    }
                }
            }
            player.openContainer.detectAndSendChanges();
        }
    }

    @Override
    protected void clOnNeighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
        super.clOnNeighborChanged(state, world, pos, blockIn);
        // @todo: on 1.11 we could have used the position from which the update is coming
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
//        boolean half = isNeedToBeHalf(worldIn.getBlockState(pos.down()).getBlock());
        boolean tankUp = worldIn.getBlockState(pos.up()).getBlock() == this;
        boolean tankDown = worldIn.getBlockState(pos.down()).getBlock() == this;
        EnumTankState s;
        if (tankUp && tankDown) {
            s = EnumTankState.MIDDLE;
        } else if (tankUp) {
            s = EnumTankState.BOTTOM;
        } else if (tankDown) {
            s = EnumTankState.TOP;
        } else {
            s = EnumTankState.FULL;
        }
        return state.withProperty(STATE, s);
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING_HORIZ, STATE);
    }
}
