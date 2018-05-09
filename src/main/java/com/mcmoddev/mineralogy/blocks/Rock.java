package com.mcmoddev.mineralogy.blocks;

import com.mcmoddev.mineralogy.MineralogyConfig;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class Rock extends net.minecraft.block.Block {

	public Rock(boolean isStoneEquivalent, float hardness, float blastResistance, int toolHardnessLevel,
			SoundType sound) {
		super(Material.ROCK);
		this.isStoneEquivalent = isStoneEquivalent;
		this.setHardness((float) hardness); // dirt is 0.5, grass is 0.6, stone is 1.5,iron ore is 3, obsidian is 50
		this.setResistance((float) blastResistance); // dirt is 0, iron ore is 5, stone is 10, obsidian is 2000
		this.setSoundType(sound); // sound for stone
		this.setHarvestLevel("pickaxe", toolHardnessLevel);
	}

	public final boolean isStoneEquivalent;

	@Override
	public boolean isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos,
			com.google.common.base.Predicate<IBlockState> target) {
		return isStoneEquivalent;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		
		super.getDrops(drops, world, pos, state, fortune);
		
		if (MineralogyConfig.dropCobblestone()) {
			drops.add(new ItemStack(Blocks.COBBLESTONE));
		} else {
			drops.add(new ItemStack(Item.getItemFromBlock(this)));
		}
	}
}
