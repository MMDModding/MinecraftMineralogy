package cyano.mineralogy;

import org.apache.commons.lang3.text.WordUtils;

// DON'T FORGET TO UPDATE mcmod.info FILE!!!

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cyano.mineralogy.blocks.*;
import cyano.mineralogy.itemblock.BypassItemBlock;
import cyano.mineralogy.items.*;
import cyano.mineralogy.patching.PatchHandler;
import cyano.mineralogy.tileentity.TileEntityRockFurnace;
import cyano.mineralogy.worldgen.OreSpawner;
import cyano.mineralogy.worldgen.StoneReplacer;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent; 
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

@Mod(modid = Mineralogy.MODID, name=Mineralogy.NAME, version = Mineralogy.VERSION,
		acceptedMinecraftVersions = "[1.10.2,)",
		certificateFingerprint = "@FINGERPRINT@")
public class Mineralogy {
	public static final String MODID = "mineralogy";
    public static final String NAME ="Mineralogy";
    public static final String VERSION = "3.3.8";
    
    public static CreativeTabs mineralogyTab = new CreativeTabs("mineralogyTab"){
		@Override
		public Item getTabIconItem(){
			return Item.getItemFromBlock(Blocks.STONE);
		}
		
		public boolean hasSearchBar() {
			return true;
		};
	};
    
    /** stone block replacements that are Sedimentary */
    public static final List<Block> sedimentaryStones = new ArrayList<Block>();
    /** stone block replacements that are Metamorphic */
    public static final List<Block> metamorphicStones = new ArrayList<Block>();
    /** stone block replacements that are Igneous */
    public static final List<Block> igneousStones = new ArrayList<Block>();
	/** all blocks used in this mod (blockID, block)*/
	public static final Map<String,Block> mineralogyBlockRegistry = new HashMap<String, Block>();
	/** all items used in this mod (blockID, block)*/
	public static final Map<String,Item> mineralogyItemRegistry = new HashMap<String, Item>();
    
    /** size of rock layers */
    public static double ROCK_LAYER_NOISE = 32; 
    /** size of mineral biomes */
    public static int GEOME_SIZE = 100; 
    /** thickness of rock layers */
    public static int GEOM_LAYER_THICKNESS = 8;
    /** realistic coal placement */
    public static boolean REALISTIC_COAL_LAYERS = false;
    
	public static boolean SMELTABLE_GRAVEL = true;
	public static boolean DROP_COBBLESTONE = false;
	public static boolean PATCH_UPDATE = true;
    
 //   public static OrePlacer orePlacementGenerator = null;
	public static boolean GENERATE_ROCKSTAIRS = true;
	public static boolean GENERATE_ROCKSLAB = true;
	public static boolean GENERATE_ROCK_WALL = true;
	public static boolean GENERATE_BRICK = true;
	public static boolean GENERATE_BRICKSTAIRS = true;
	public static boolean GENERATE_BRICKSLAB = true;
	public static boolean GENERATE_BRICK_WALL = true;
	public static boolean GENERATE_SMOOTH = true;
	public static boolean GENERATE_SMOOTHSTAIRS = true;
	public static boolean GENERATE_SMOOTHSLAB = true;
	public static boolean GENERATE_SMOOTH_WALL = true;
	public static boolean GENERATE_SMOOTHBRICK = true;
	public static boolean GENERATE_SMOOTHBRICKSTAIRS = true;
	public static boolean GENERATE_SMOOTHBRICKSLAB = true;
	public static boolean GENERATE_SMOOTHBRICK_WALL = true;
	public static boolean GENERATE_ROCKFURNACES = true;
	public static boolean GENERATE_SMOOTHFURNACES = true;
	public static boolean GENERATE_BRICKFURNACES = true;
	public static boolean GENERATE_SMOOTHBRICKFURNACES = true;
	public static boolean GENERATE_RELIEFS = true;
	
	public static Block blockChert;
    public static Block blockGypsum;
    public static Block blockChalk;
    public static Block blockSalt;
    public static Block blockPumice;
    public static Block blockRockSaltLamp;
    public static Block blockRockSaltStreetLamp;
    public static Item gypsumPowder;
    public static Item chalkPowder;
    public static Item saltPowder;
    public static Item rockSaltPowder;
    public static Item sulphurPowder;
    public static Item phosphorousPowder;
    public static Item nitratePowder; // aka "saltpeter"
    public static Item mineralFertilizer;
    
    public static Block[] drywall = new Block[16];

	// add other blocks and recipes
	private static final String[] colorSuffixes = { "black", "red", "green", "brown", "blue", "purple", "cyan",
			"silver", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white" };

	String[] colorSuffixesTwo = { "Black", "Red", "Green", "Brown", "Blue", "Purple", "Cyan",
			"LightGray", "Gray", "Pink", "Lime", "Yellow", "LightBlue", "Magenta", "Orange", "White" };

	public static List<String> igneousWhitelist = new ArrayList<String>();
	public static List<String> igneousBlacklist = new ArrayList<String>();
	public static List<String> sedimentaryWhitelist = new ArrayList<String>();
	public static List<String> sedimentaryBlacklist = new ArrayList<String>();
	public static List<String> metamorphicWhitelist = new ArrayList<String>();
	public static List<String> metamorphicBlacklist = new ArrayList<String>();

	private static final String stickWood = "stickWood";
	private static final String cobblestone = "cobblestone";
	private static final String fertilizer = "fertilizer";
	//private static final String salt = "salt";
	private static final String stone = "stone";
	private static final String dustCarbon = "dustCarbon";
	//private static final String blockPhosphorous = "blockPhosphorous";
	private static final String dustPhosphorous = "dustPhosphorous";
	private static final String orePhosphorous = "orePhosphorous";
	//private static final String blockSulfur = "blockSulfur";
	private static final String dustSulfur = "dustSulfur";
	private static final String oreSulfur = "oreSulfur";
	private static final String sulfur = "sulfur";
	private static final String dustSulphur = "dustSulphur";
	private static final String oreSulphur = "oreSulphur";
	private static final String sulphur = "sulphur";
	private static final String dustGypsum = "dustGypsum";
	private static final String dustChalk = "dustChalk";
	//private static final String dustSalt = "dustSalt";
	private static final String dustRocksalt = "dustRocksalt";
	//private static final String blockNitrate = "blockNitrate";
	private static final String dustNitrate = "dustNitrate";
	private static final String oreNitrate = "oreNitrate";

	public static final Logger logger = LogManager.getFormatterLogger(Mineralogy.MODID);

	@EventHandler
	public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		logger.warn("Invalid fingerprint detected!");
	}

	@EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    	// load config
    	Configuration config = new Configuration(event.getSuggestedConfigurationFile());
    	config.load();

		PATCH_UPDATE = config.getBoolean("patch_world", "options", PATCH_UPDATE,
				"If true, then the world will be patched to fix compatibility-breaking " +
				"changes to this mod by adding-back mock-ups of old obsolete blocks and " +
				"then replacing obsolete blocks with newer blocks.");

    	SMELTABLE_GRAVEL = config.getBoolean("SMELTABLE_GRAVEL", "options", SMELTABLE_GRAVEL,
   "If true, then gravel can be smelted into generic stone");
   		DROP_COBBLESTONE = config.getBoolean("DROP_COBBLESTONE", "options", DROP_COBBLESTONE,
   "If true, then rock blocks will drop cobblestone instead of themselves");
    	GEOME_SIZE = config.getInt("GEOME_SIZE", "world-gen", GEOME_SIZE, 4, Short.MAX_VALUE, 
   "Making this value larger increases the size of regions of igneous, \n"
 + "sedimentary, and metamorphic rocks");
    	ROCK_LAYER_NOISE = (double)config.getFloat("ROCK_LAYER_NOISE", "world-gen", (float)ROCK_LAYER_NOISE, 1.0f, (float)Short.MAX_VALUE, 
   "Changing this value will change the 'waviness' of the layers.");
    	GEOM_LAYER_THICKNESS = config.getInt("ROCK_LAYER_THICKNESS", "world-gen",GEOM_LAYER_THICKNESS, 1, 255, 
   "Changing this value will change the height of individual layers.");

    	REALISTIC_COAL_LAYERS = config.getBoolean("REALISTIC_COAL_LAYERS", "world-gen",REALISTIC_COAL_LAYERS, 
    			   "Changing this value will make coal render in realistic sedimentary layers instead of the vanilla placement.");
    	
    	GENERATE_ROCKSTAIRS = config.getBoolean("GENERATE_ROCKSTAIRS", "options", GENERATE_ROCKSTAIRS, "If true, then rock stairs will be generated");
        GENERATE_ROCKSLAB = config.getBoolean("GENERATE_ROCKSLAB", "options", GENERATE_ROCKSLAB, "If true, then rock slabs will be generated");
        GENERATE_ROCK_WALL = config.getBoolean("GENERATE_ROCK_WALL", "options", GENERATE_ROCK_WALL, "If true, then rock walls will be generated");
        GENERATE_BRICK = config.getBoolean("GENERATE_BRICK", "options", GENERATE_BRICK, "If true, then rock brick blocks will be generated");
        GENERATE_BRICKSTAIRS = config.getBoolean("GENERATE_BRICKSTAIRS", "options", GENERATE_BRICKSTAIRS, "If true, then brick stairs will be generated");
        GENERATE_BRICKSLAB = config.getBoolean("GENERATE_BRICKSLAB", "options", GENERATE_BRICKSLAB, "If true, then brick slabs will be generated");
        GENERATE_BRICK_WALL = config.getBoolean("GENERATE_BRICK_WALL", "options", GENERATE_BRICK_WALL, "If true, then brick walls will be generated");
        GENERATE_SMOOTH = config.getBoolean("GENERATE_SMOOTH", "options", GENERATE_SMOOTH, "If true, then polished rock will be generated");
        GENERATE_SMOOTHSTAIRS = config.getBoolean("GENERATE_SMOOTHSTAIRS", "options", GENERATE_SMOOTHSTAIRS, "If true, then polished rock stairs will be generated");
        GENERATE_SMOOTHSLAB = config.getBoolean("GENERATE_SMOOTHSLAB", "options", GENERATE_SMOOTHSLAB, "If true, then polished rock slabs will be generated");
        GENERATE_SMOOTH_WALL= config.getBoolean("GENERATE_SMOOTH_WALL", "options", GENERATE_SMOOTH_WALL, "If true, then polished rock walls will be generated");
        GENERATE_SMOOTHBRICK = config.getBoolean("GENERATE_SMOOTHBRICK", "options", GENERATE_SMOOTHBRICK, "If true, then polished brick blocks will be generated");
        GENERATE_SMOOTHBRICKSTAIRS = config.getBoolean("GENERATE_SMOOTHBRICKSTAIRS", "options", GENERATE_SMOOTHBRICKSTAIRS, "If true, then polished brick stairs will be generated");
        GENERATE_SMOOTHBRICKSLAB = config.getBoolean("GENERATE_SMOOTHBRICKSLAB", "options", GENERATE_SMOOTHBRICKSLAB, "If true, then polished brick slabs will be generated");
        GENERATE_SMOOTHBRICK_WALL = config.getBoolean("GENERATE_SMOOTHBRICK_WALL", "options", GENERATE_SMOOTHBRICK_WALL, "If true, then polished brick walls will be generated");
    	
        GENERATE_ROCKFURNACES = config.getBoolean("GENERATE_ROCKFURNACES", "options", GENERATE_ROCKFURNACES, "If true, then rock furnaces will be generated");
        GENERATE_SMOOTHFURNACES = config.getBoolean("GENERATE_SMOOTHFURNACES", "options", GENERATE_SMOOTHFURNACES, "If true, then polished rock furnaces will be generated");
        GENERATE_BRICKFURNACES = config.getBoolean("GENERATE_BRICKFURNACES", "options", GENERATE_BRICKFURNACES, "If true, then brick furnaces will be generated");
        GENERATE_SMOOTHBRICKFURNACES = config.getBoolean("GENERATE_SMOOTHBRICKFURNACES", "options", GENERATE_SMOOTHBRICKFURNACES, "If true, then polished brick furnaces will be generated");
        GENERATE_RELIEFS = config.getBoolean("GENERATE_RELIEFS", "options", GENERATE_RELIEFS, "If true, then reliefs will be generated");
        
    	igneousBlacklist.addAll(asList(config.getString("igneous_blacklist", "world-gen", "", "Ban blocks from spawning in rock layers (format is mod:block as a semicolin (;) delimited list)"),";"));
    	sedimentaryBlacklist.addAll(asList(config.getString("sedimentary_blacklist", "world-gen", "", "Ban blocks from spawning in rock layers (format is mod:block as a semicolin (;) delimited list)"),";"));
    	metamorphicBlacklist.addAll(asList(config.getString("metamorphic_blacklist", "world-gen", "", "Ban blocks from spawning in rock layers (format is mod:block as a semicolin (;) delimited list)"),";"));
    	
    	igneousWhitelist.addAll(asList(config.getString("igneous_whitelist", "world-gen", "", "Adds blocks to rock layers (format is mod:block as a semicolin (;) delimited list)"),";"));
    	sedimentaryWhitelist.addAll(asList(config.getString("sedimentary_whitelist", "world-gen", "", "Adds blocks to rock layers (format is mod:block as a semicolin (;) delimited list)"),";"));
    	metamorphicWhitelist.addAll(asList(config.getString("metamorphic_whitelist", "world-gen", "", "Adds blocks to rock layers (format is mod:block as a semicolin (;) delimited list)"),";"));

		// Blocks, Items, World-gen
    	
		// Rocks	
    	addStoneType(RockType.IGNEOUS, "diabase", 5, 100, 2); // new
    	addStoneType(RockType.IGNEOUS, "gabbro", 5, 100, 2); // new
    	addStoneType(RockType.IGNEOUS, "peridotite", 3, 15, 0); // new
    	addStoneType(RockType.IGNEOUS, "basaltic_glass", 3, 15, 0); // new ?
    	addStoneType(RockType.IGNEOUS, "scoria", 1, 7, 0);// new
    	addStoneType(RockType.IGNEOUS, "tuff", 2, 10, 0);// new
    	
		addStoneType(RockType.IGNEOUS, "andesite", 1.5, 10, 0);
		addStoneType(RockType.IGNEOUS, "basalt", 5, 100, 2);
		addStoneType(RockType.IGNEOUS, "diorite", 1.5, 10, 0);
		addStoneType(RockType.IGNEOUS, "granite", 3, 15, 1);
		addStoneType(RockType.IGNEOUS, "rhyolite", 1.5, 10, 0);
		addStoneType(RockType.IGNEOUS, "pegmatite", 1.5, 10, 0);


		addStoneType(RockType.SEDIMENTARY, "siltstone", 1, 10, 0);// new // TODO it should crush to sand and clay
		
		addStoneType(RockType.SEDIMENTARY, "shale", 1.5, 10, 0);
		addStoneType(RockType.SEDIMENTARY, "conglomerate" ,1.5, 10, 0);
		addStoneType(RockType.SEDIMENTARY, "dolomite", 3, 15, 1);
		addStoneType(RockType.SEDIMENTARY, "limestone", 1.5, 10, 0);

		addStoneType(RockType.METAMORPHIC,"hornfels", 3, 15, 1);// new
		addStoneType(RockType.METAMORPHIC,"quartzite", 4, 15, 1);// new
		addStoneType(RockType.METAMORPHIC,"novaculite", 2.5, 10, 1);// new // TODO: this can be used like flint
		
		addStoneType(RockType.METAMORPHIC,"slate", 1.5, 10, 0);
		addStoneType(RockType.METAMORPHIC,"schist", 3, 15, 1);
		addStoneType(RockType.METAMORPHIC,"gneiss", 3, 15, 1);
		addStoneType(RockType.SEDIMENTARY,"marble", 1.5, 10, 0);
		addStoneType(RockType.METAMORPHIC,"phyllite", 1.5, 10, 0);
		addStoneType(RockType.METAMORPHIC,"amphibolite", 3, 15, 1);

		// add items
		gypsumPowder = addDust("gypsum_dust", "Gypsum");
		chalkPowder = addDust("chalk_dust", "Chalk");
		saltPowder = addDust("salt_dust", "Salt");
		rockSaltPowder = addDust("rock_salt_dust", "Rocksalt");
		
		OreDictionary.registerOre("salt", saltPowder);
		
		sulphurPowder = addDust("sulfur_dust", "Sulfur");
		
		OreDictionary.registerOre(sulfur, sulphurPowder);
		OreDictionary.registerOre(dustSulphur, sulphurPowder);
		OreDictionary.registerOre(sulphur, sulphurPowder);
		
		phosphorousPowder = addDust("phosphorous_dust", "Phosphorous");

		nitratePowder = addDust("nitrate_dust", "Nitrate");

		mineralFertilizer = registerItem(new MineralFertilizer(), "mineral_fertilizer").setUnlocalizedName(Mineralogy.MODID + "." + "mineral_fertilizer").setCreativeTab(CreativeTabs.MATERIALS);
		OreDictionary.registerOre(fertilizer, mineralFertilizer);
		
		// other blocks
		sedimentaryStones.add(Blocks.SANDSTONE);
		
		if (REALISTIC_COAL_LAYERS)
			sedimentaryStones.add(Blocks.COAL_ORE);
		
		blockChert = registerBlock(new Chert(), "chert");
		sedimentaryStones.add(blockChert);
		OreDictionary.registerOre(cobblestone, blockChert);
				
		blockGypsum = registerBlock(new Gypsum(), "gypsum");
		sedimentaryStones.add(blockGypsum);
		
		blockChalk = registerBlock(new Chalk(), "chalk");
		sedimentaryStones.add(blockChalk);
		
		blockSalt = registerBlock(new RockSalt(), "rock_salt");
		
		addStoneType(RockType.SEDIMENTARY, "rock_salt", 1.5, 10, 0, true, blockSalt);// new 
		
		blockRockSaltLamp = registerBlock(new RockSaltLamp(), "rocksaltlamp");
		blockRockSaltStreetLamp = registerBlock(new RockSaltStreetLamp(), "rocksaltstreetlamp", 16);
		
		blockPumice = registerBlock(new Rock(false, 0.5F, 5F, 0, SoundType.STONE), "pumice");
		igneousStones.add(blockPumice);
		OreDictionary.registerOre(cobblestone, blockPumice);
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(gypsumPowder, 4), blockGypsum));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(chalkPowder, 4), blockChalk));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(saltPowder, 4), blockSalt));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(blockRockSaltLamp, 1), blockSalt, Blocks.TORCH, Items.IRON_INGOT));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockGypsum), "xx", "xx", 'x', dustGypsum));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockChalk), "xx", "xx", 'x', dustChalk));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockSalt), "xx", "xx", 'x', dustRocksalt));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockRockSaltStreetLamp), "x", "y", "y", 'x', blockRockSaltLamp, 'y', Items.IRON_INGOT));

		
		// register ores
		Block s = addOre("sulfur_ore", oreSulfur, sulphurPowder,1, 4, 0,
				config.getInt("sulphur_ore.minY", "ores", 16, 1, 255, "Minimum ore spawn height"),
				config.getInt("sulphur_ore.maxY", "ores", 64, 1, 255, "Maximum ore spawn height"),
				config.getFloat("sulphur_ore.frequency", "ores", 1, 0, 63, "Number of ore deposits per chunk"),
				config.getInt("sulphur_ore.quantity", "ores", 16, 0, 63, "Size of ore deposit"));
		OreDictionary.registerOre(oreSulphur, s); // Damn English and its multiple spellings. There better not be people out there spelling is "sulphre"
		
		addOre("phosphorous_ore", orePhosphorous, phosphorousPowder, 1, 4, 0,
				config.getInt("phosphorous_ore.minY", "ores", 16, 1, 255, "Minimum ore spawn height"),
				config.getInt("phosphorous_ore.maxY", "ores", 64, 1, 255, "Maximum ore spawn height"),
				config.getFloat("phosphorous_ore.frequency", "ores", 1, 0, 63, "Number of ore deposits per chunk"),
				config.getInt("phosphorous_ore.quantity", "ores", 16, 0, 63, "Size of ore deposit"));
		
		addOre("nitrate_ore", oreNitrate, nitratePowder, 1, 4, 0,
				config.getInt("nitrate_ore.minY", "ores", 16, 1, 255, "Minimum ore spawn height"),
				config.getInt("nitrate_ore.maxY", "ores", 64, 1, 255, "Maximum ore spawn height"),
				config.getFloat("nitrate_ore.frequency", "ores", 1, 0, 63, "Number of ore deposits per chunk"),
				config.getInt("nitrate_ore.quantity", "ores", 16, 0, 63, "Size of ore deposit"));

		// TODO: Finish This
		addBlock("sulfur_block", "Sulfur", 0);
		addBlock("phosphorous_block", "Phosphorous", 0);
		addBlock("nitrate_block", "Nitrate", 0);

		config.save();

		for(int i = 0; i < 16; i++) {
			drywall[i] = registerBlock(new DryWall(colorSuffixes[i]), "drywall_" + colorSuffixes[i]);
			OreDictionary.registerOre("drywall", drywall[i]);
		}

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(drywall[7], 3), "pgp", "pgp", "pgp", 'p', Items.PAPER, 'g', dustGypsum));
    }
    
    private static List<String> asList(String list, String delimiter) {
    	String[] a = list.split(delimiter);
    	return Arrays.asList(a);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	
    	// recipes
		for(int i = 0; i < 16; i++) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(drywall[ i] , 1), "drywall", "dye" + colorSuffixesTwo[i]));
		}

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.GUNPOWDER, 4), new ItemStack(Items.COAL,1,1), dustNitrate, dustSulfur));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.GUNPOWDER, 4), dustCarbon, dustNitrate, dustSulfur));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.GUNPOWDER, 4), Items.SUGAR, dustNitrate, dustSulfur));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(mineralFertilizer, 1), dustNitrate, dustPhosphorous));

		// recipe modifications
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.STONE_AXE), "xx", "xy", " y", 'x', stone, 'y', stickWood));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.STONE_HOE), "xx", " y", " y", 'x', stone, 'y', stickWood));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.STONE_PICKAXE), "xxx", " y "," y ", 'x', stone, 'y',stickWood));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.STONE_SHOVEL), "x", "y", "y", 'x', stone, 'y', stickWood));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.STONE_SWORD), "x", "x", "y", 'x', stone, 'y', stickWood));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.FURNACE), "xxx", "x x", "xxx", 'x', stone));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.COBBLESTONE, 4), stone, stone, Blocks.GRAVEL, Blocks.GRAVEL));

		if(SMELTABLE_GRAVEL) GameRegistry.addSmelting(Blocks.GRAVEL, new ItemStack(Blocks.STONE), 0.1F);

		// remove default stone slab recipe (interferes with rock slab recipes)
		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
		List<IRecipe> removeList = new ArrayList<>();
		for(IRecipe r : recipes) {
			ItemStack item = r.getRecipeOutput();
			if(item == null || item.getItem() == null) continue;
			if(item.getItem() == Item.getItemFromBlock(Blocks.STONE_SLAB) && item.getItemDamage() == 0) {
				// is recipe for stone slab, bookmark for removal
				removeList.add(r);
			}
		}
		CraftingManager.getInstance().getRecipeList().removeAll(removeList);
		// less generic stone slab recipe
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.STONE_SLAB, 6, 0), "xxx", 'x', Blocks.STONE));

		// initialize legacy updater
		PatchHandler.getInstance().init(PATCH_UPDATE);

		// event registration, tile entities
    	// (none)

    	// register custom chunk generation
    	GameRegistry.registerWorldGenerator(new StoneReplacer(), 10);

    	// register renderers
    	if(event.getSide().isClient()) {
    		registerItemRenders();
    	}
    }
    
    private void registerItemRenders() {

		for(String name : mineralogyItemRegistry.keySet()){
			Item i = Mineralogy.mineralogyItemRegistry.get(name);
    		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
    				.register(i, 0, new ModelResourceLocation(Mineralogy.MODID + ":" + name, "inventory"));
    	}
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
		// addons to other mods
    	
    	// process black-lists and white-lists
    	for(String id : igneousWhitelist) {
    		Block b = Block.getBlockFromName(id);
    		if(b == null) continue;
    		igneousStones.add(b);
    	}
    	for(String id : metamorphicWhitelist) {
    		Block b = Block.getBlockFromName(id);
    		if(b == null) continue;
    		metamorphicStones.add(b);
    	}
    	for(String id : sedimentaryWhitelist) {
    		Block b = Block.getBlockFromName(id);
    		if(b == null) continue;
    		sedimentaryStones.add(b);
    	}
    	for(String id : igneousBlacklist) {
    		Block b = Block.getBlockFromName(id);
    		if(b == null) continue;
    		igneousStones.remove(b);
    	}
    	for(String id : metamorphicBlacklist) {
    		Block b = Block.getBlockFromName(id);
    		if(b == null) continue;
    		metamorphicStones.remove(b);
    	}
    	for(String id : sedimentaryBlacklist) {
    		Block b = Block.getBlockFromName(id);
    		if(b == null) continue;
    		sedimentaryStones.remove(b);
    	}

    	/*
    	System.out.println("Ore Dictionary Registry:");
    	for(String s : OreDictionary.getOreNames()){
    		System.out.print(s+":");
    		for(ItemStack o : OreDictionary.getOres(s)){
    			System.out.print(" "+o.getItem().getUnlocalizedName()+"#"+o.getItemDamage());
    		}
    		System.out.println();
    	}
    	//*/
    }

    private static int oreWeightCount = 20;

    private static Item addDust(String dustName, String oreDictionaryName) {
    	Item item = registerItem(new Item(), dustName).setUnlocalizedName(Mineralogy.MODID + "." + dustName).setCreativeTab(mineralogyTab);
		OreDictionary.registerOre("dust" + oreDictionaryName, item);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(item, 9), "block" + oreDictionaryName));
		return item;
    }

    // TODO: Recipes
    private static Block addBlock(String name, String oreDictionaryName, int pickLevel) {
    	String blockName = Mineralogy.MODID + "." + name;
    	Block block = new Block(Material.ROCK).setUnlocalizedName(blockName).setCreativeTab(mineralogyTab);
    	registerBlock(block, name);
    	OreDictionary.registerOre("block" + oreDictionaryName, block);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(block), "xxx", "xxx", "xxx", 'x', "dust" + oreDictionaryName));
    	
		return block;
    }

    private static Block addOre(String oreName, String oreDictionaryName, Item oreDropItem, int numMin, int numMax, int pickLevel,
    		int minY, int maxY, float spawnFrequency, int spawnQuantity) {
    	String oreBlockName = Mineralogy.MODID + "." + oreName;
    	Block oreBlock = new Ore(oreName, oreDropItem, numMin, numMax, pickLevel).setUnlocalizedName(oreBlockName);
    	registerBlock(oreBlock, oreName);
    	OreDictionary.registerOre(oreDictionaryName, oreBlock);
    	GameRegistry.registerWorldGenerator(new OreSpawner(oreBlock, minY, maxY, spawnFrequency, spawnQuantity, (oreWeightCount * 25214903917L)+11L), oreWeightCount++);
    	return oreBlock;
    }

    private static Block registerBlock(Block b, String name, int maxStackSize, boolean createItem, boolean bypassSneak) {
    	GameRegistry.register(b.setRegistryName(MODID, name));
		b.setUnlocalizedName(MODID + "." + name);
		
		ItemBlock itemBlock;
		
		if (bypassSneak)
			itemBlock = new BypassItemBlock(b);
		else
			itemBlock = new ItemBlock(b);
		
		itemBlock.setMaxStackSize(maxStackSize);
		
		if (createItem)
			registerItem(itemBlock, name);
		
		mineralogyBlockRegistry.put(name, b);
		return b;
    }
    
    private static Block registerBlock(Block b, String name, int maxStackSize) {
		return registerBlock(b, name, maxStackSize, true, false);
	}
    
	private static Block registerBlock(Block b, String name) {
		return registerBlock(b, name, 64);
	}

	private static Item registerItem(Item i, String name) {
		GameRegistry.register(i.setRegistryName(MODID, name));
		mineralogyItemRegistry.put(name, i);
		i.setUnlocalizedName(MODID + "." + name);
		return i;
	}

	private static Block addStoneType(RockType type, String name, double hardness, double blastResistance, int toolHardnessLevel) {
		
		return addStoneType(type, name, hardness, blastResistance, toolHardnessLevel, true, null);
	}
	
	/**
     * 
     * @param type Igneous, sedimentary, or metamorphic
     * @param name id-name of the block
     * @param hardness How hard (time duration) the block is to pick. For reference, dirt is 0.5, stone is 1.5, ores are 3, and obsidian is 50
     * @param blastResistance how resistant the block is to explosions. For reference, dirt is 0, stone is 10, and blast-proof materials are 2000
     * @param toolHardnessLevel 0 for wood tools, 1 for stone, 2 for iron, 3 for diamond
     */
    private static Block addStoneType(RockType type, String name, double hardness, double blastResistance, int toolHardnessLevel, Boolean canBePolished, Block rockOverride) { //, ItemStack drops
		final Block rock;
		final Block rockStairs;
		final Block rockSlab;
		final Block rockWall;
		final Block rockFurnace;
		final Block brick;
		final Block brickStairs;
		final Block brickSlab;
		final Block brickWall;
		final Block brickFurnace;
		final Block smooth;
		final Block smoothStairs;
		final Block smoothSlab;
		final Block smoothWall;
		final Block smoothFurnace;
		final Block smoothBrick;
		final Block smoothBrickStairs;
		final Block smoothBrickSlab;
		final Block smoothBrickWall;
		final Block smoothBrickFurnace;
		
		if (rockOverride != null)
			rock = rockOverride;
		else
			rock = registerBlock(new Rock(true, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name);
    	
		switch(type) {
	    	case IGNEOUS:
	    		igneousStones.add(rock);
	    		break;
	    	case METAMORPHIC:
	    		metamorphicStones.add(rock);
	    		break;
	    	case SEDIMENTARY:
	    		sedimentaryStones.add(rock);
	    		break;
	    	case ANY:
	    		sedimentaryStones.add(rock);
	    		metamorphicStones.add(rock);
	    		igneousStones.add(rock);
	    		break;
    	}
    	
    	String oreDictName = WordUtils.capitalize(name);
    	
    	OreDictionary.registerOre("stone", rock);
    	OreDictionary.registerOre("stone" + oreDictName, rock);
    	
		GameRegistry.addSmelting(rock, new ItemStack(Blocks.STONE), 0.1F);

		if (GENERATE_ROCKSTAIRS) {
			rockStairs = registerBlock(new RockStairs(rock, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_stairs");
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rockStairs, 4), "x  ", "xx ", "xxx", 'x', rock));
		}
		
		if (GENERATE_ROCKSLAB) {
			rockSlab = registerBlock(new RockSlab((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, name + "_double_slab"), name + "_slab");
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rockSlab, 6), "xxx", 'x', rock));
		
			registerBlock(
					new DoubleSlab((float) hardness, (float) blastResistance, toolHardnessLevel, SoundType.STONE, rockSlab),
					name + "_double_slab",  64, false, true);
			
			if (GENERATE_ROCKFURNACES) {
				rockFurnace = registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, false), name + "_furnace");
				registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, true).setLightLevel(0.875F), "lit_" + name + "_furnace");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rockFurnace, 1), "xxx", "xyx", "xxx", 'x', rockSlab, 'y', Blocks.FURNACE));
				GameRegistry.registerTileEntity(TileEntityRockFurnace.class, name + "_furnace");
			}
		}

		if (GENERATE_ROCK_WALL) {
			rockWall = registerBlock(new RockWall(rock, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_wall");
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(rockWall, 6), "xxx", "xxx", 'x', rock));
		}
		
		if (GENERATE_BRICK) {
			brick = registerBlock(new Rock(false, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_brick");
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(brick, 4), "xx", "xx", 'x', rock));
			
			if (GENERATE_BRICKSTAIRS) {
				brickStairs = registerBlock(new RockStairs(rock, (float)hardness, (float)blastResistance,toolHardnessLevel, SoundType.STONE),name + "_brick_stairs");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(brickStairs, 4), "x  ", "xx ", "xxx", 'x', brick));
			}
			
			if (GENERATE_BRICKSLAB) {
				brickSlab = registerBlock(new RockSlab((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, name + "_double_brick_slab"), name + "_brick_slab");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(brickSlab, 6), "xxx", 'x', brick));
			
				if (GENERATE_BRICKFURNACES) {
					brickFurnace = registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, false), name + "_brick_furnace");
					registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, true).setLightLevel(0.875F), "lit_" + name + "_brick_furnace");
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(brickFurnace, 1), "xxx", "xyx", "xxx", 'x', brickSlab, 'y', Blocks.FURNACE));
					GameRegistry.registerTileEntity(TileEntityRockFurnace.class, name + "_brick_furnace");		
				}
			}
			
			if (GENERATE_BRICK_WALL) {
				brickWall = registerBlock(new RockWall(rock, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_brick_wall");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(brickWall, 6), "xxx", "xxx", 'x', brick));
			}
		}

		if (GENERATE_SMOOTH && canBePolished) {
			smooth = registerBlock(new Rock(false, (float)hardness,(float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_smooth");
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(smooth, 1), rock, "sand"));
	
			if(GENERATE_RELIEFS) {
				generateReliefs(name, hardness, blastResistance, toolHardnessLevel, smooth);
			}
//			if (hardness >= 4) {
//				smoothAnvil = registerBlock(new RockAnvil(name + "_smooth_anvil", (float)hardness, (float)blastResistance, toolHardnessLevel), name + "_smooth_anvil");
//				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothAnvil, 1), "xxx", "xxx", "xxx", 'x', smooth));
//			}
			
			if (GENERATE_SMOOTHSTAIRS) {
				smoothStairs = registerBlock(new RockStairs(rock, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE),name + "_smooth_stairs");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothStairs, 4), "x  ","xx ", "xxx", 'x', smooth));
			}
			
			if (GENERATE_SMOOTHSLAB) {
				smoothSlab = registerBlock(new RockSlab((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, name + "_double_smooth_slab"),name + "_smooth_slab");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothSlab, 6), "xxx", 'x', smooth));
			
				if (GENERATE_SMOOTHFURNACES) {
					smoothFurnace = registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, false), name + "_smooth_furnace");
					registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, true).setLightLevel(0.875F), "lit_" + name + "_smooth_furnace");
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothFurnace, 1), "xxx", "xyx", "xxx", 'x', smoothSlab, 'y', Blocks.FURNACE));
					GameRegistry.registerTileEntity(TileEntityRockFurnace.class, name + "_smooth_furnace");
				}
			}
			
			if (GENERATE_SMOOTH_WALL) {
				smoothWall = registerBlock(new RockWall(rock, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_smooth_wall");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothWall, 6), "xxx", "xxx", 'x', smooth));
			}
			
			if (GENERATE_SMOOTHBRICK) {
				smoothBrick = registerBlock(new Rock(false, (float)hardness,(float)blastResistance,toolHardnessLevel, SoundType.STONE), name + "_smooth_brick");
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothBrick, 4), "xx", "xx", 'x', smooth));
			
				if (GENERATE_SMOOTHBRICKSTAIRS) {
					smoothBrickStairs = registerBlock(new RockStairs(rock, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_smooth_brick_stairs");
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothBrickStairs, 4), "x  ","xx ", "xxx", 'x', smoothBrick));
				}
				
				if (GENERATE_SMOOTHBRICKSLAB) {
					smoothBrickSlab = registerBlock(new RockSlab((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, name + "_double_smooth_brick_slab"), name + "_smooth_brick_slab");
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothBrickSlab, 6), "xxx", 'x', smoothBrick));
				
					if (GENERATE_SMOOTHBRICKFURNACES) {
						smoothBrickFurnace = registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, false), name + "_smooth_brick_furnace");
						registerBlock(new RockFurnace((float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE, true).setLightLevel(0.875F), "lit_" + name + "_smooth_brick_furnace");
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothBrickFurnace, 1), "xxx", "xyx", "xxx", 'x', smoothBrickSlab, 'y', Blocks.FURNACE));
						GameRegistry.registerTileEntity(TileEntityRockFurnace.class, name + "_smooth_brick_furnace");
					}
				}
				
				if (GENERATE_SMOOTHBRICK_WALL) {
					smoothBrickWall = registerBlock(new RockWall(rock, (float)hardness, (float)blastResistance, toolHardnessLevel, SoundType.STONE), name + "_smooth_brick_wall");
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(smoothBrickWall, 6), "xxx", "xxx", 'x', smoothBrick));
				}
			}
		}
		
		return rock;
    }

	private static void generateReliefs(String materialName, double hardness, double blastResistance,
			int toolHardnessLevel, final Block rock) {
		final Block blankRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_blank");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blankRelief, 16), "xxx","xxx","xxx",'x', rock));
		
		final Block axeRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_axe");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(axeRelief, 1), blankRelief, Items.STONE_AXE));
		
		final Block crossRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_cross");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(crossRelief, 1), "x x","   ","x x",'x', blankRelief));
		
		final Block hammerRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_hammer");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hammerRelief, 1), " x "," y "," z ",'x', rock,'y', Items.STICK,'z', blankRelief));
		
		final Block hoeRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_hoe");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(hoeRelief, 1), blankRelief, Items.STONE_HOE));
		
		final Block horizontalRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_horizontal");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(horizontalRelief, 1), "xxx",'x', blankRelief));
		
		final Block leftRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_left");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(leftRelief, 1), "x  "," x ","  x",'x', blankRelief));
		
		final Block pickaxeRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_pickaxe");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(pickaxeRelief, 1), blankRelief, Items.STONE_PICKAXE));
		
		final Block plusRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_plus");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(plusRelief, 1), " x ","xxx"," x ",'x', blankRelief));
		
		final Block rightRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_right");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(rightRelief, 2), leftRelief, leftRelief));
		
		final Block swordRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_sword");
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(swordRelief, 1), blankRelief, Items.STONE_SWORD));
		
		final Block iRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_i");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(iRelief, 1), "xxx"," x ","xxx",'x', blankRelief));
		
		final Block verticalRelief = registerBlock(new RockRelief((float)hardness, (float)blastResistance / 2, toolHardnessLevel, SoundType.STONE), materialName + "_relief_vertical");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(verticalRelief, 1), "x","x","x",'x', blankRelief));
	}
}