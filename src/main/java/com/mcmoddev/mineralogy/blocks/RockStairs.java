package com.mcmoddev.mineralogy.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;

public class RockStairs extends net.minecraft.block.BlockStairs {

	public RockStairs(Block materialBlock, float hardness, float blastResistance, int toolHardnessLevel,
			SoundType sound, CreativeTabs tab) {
		super(materialBlock.getDefaultState());
		this.setSoundType(sound); // sound for stone
		this.setHardness((float) hardness); // dirt is 0.5, grass is 0.6, stone is 1.5,iron ore is 3, obsidian is 50
		this.setResistance((float) blastResistance); // dirt is 0, iron ore is 5, stone is 10, obsidian is 2000
		this.setHarvestLevel("pickaxe", toolHardnessLevel);
		this.useNeighborBrightness = true;
		this.setCreativeTab(tab);
	}
}
