package com.mcmoddev.lib.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class DynamicTabProviderTests {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

//	@Test
//	void testProviderCanBeInstansiated() {
//		IDynamicTabProvider dynamicTabProvider = new DynamicTabProvider();
//		
//		assertNotNull(dynamicTabProvider);
//	}

//	@Test
//	void testProviderCanAddTabAndItem() {
//		MinIoC IoC = MinIoC.getInstance(false);
//		IDynamicTabProvider dynamicTabProvider = new DynamicTabProvider();
//	
//		dynamicTabProvider.addTab("myNewTab", true, "myNewMod");
//		
//		int tabCount = dynamicTabProvider.getTabs().length;
//		
//		assertEquals(1, tabCount);
//		
//		Item item = mock(Item.class);
//		
//		try {
//			dynamicTabProvider.addToTab(item);
//		} catch (TabNotFoundException | ItemNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
