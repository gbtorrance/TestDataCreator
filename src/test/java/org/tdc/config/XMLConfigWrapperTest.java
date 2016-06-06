package org.tdc.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.io.File;
import java.util.NoSuchElementException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tdc.spreadsheet.CellStyle;
import org.tdc.spreadsheet.CellStyleImpl;

/**
 * Unit tests for {@link XMLConfigWrapper} and its related classes.
 */
public class XMLConfigWrapperTest {
	
	private static XMLConfigWrapper config;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void setup() {
		ClassLoader classLoader = XMLConfigWrapperTest.class.getClassLoader();
		File file = new File(classLoader.getResource("XMLConfigWrapperTest.xml").getFile());
		config = new XMLConfigWrapper(file);
	}

	@Test
	public void
	test_config_xml_does_not_exist_throws_exception() {
		File file = new File("/PathDoesNotExist");
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Configuration file does not exist:");
		new XMLConfigWrapper(file);
	}

	@Test
	public void
	test_invalid_xml_throws_exception() {
		ClassLoader classLoader = XMLConfigWrapperTest.class.getClassLoader();
		File file = new File(classLoader.getResource("XMLConfigWrapperTest_InvalidXML.xml").getFile());
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Unable to read configuration file:");
		new XMLConfigWrapper(file);
	}
	
	@Test
	public void
	test_string_element_found() {
		String found = config.getString("StringElement", null, false);
		assertThat(found).isEqualTo("TestString");
	}
	
	@Test
	public void
	test_string_element_not_found_use_default() {
		String notFound = config.getString("StringElement_NotFound", "Default", false);
		assertThat(notFound).isEqualTo("Default");
	}

	@Test
	public void
	test_string_element_not_found_but_required_throws_exception() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getString("DoesNotExist", null, true);
	}

	@Test
	public void
	test_int_element_found() {
		int found = config.getInt("IntElement", 0, false);
		assertThat(found).isEqualTo(1);
	}
	
	@Test
	public void
	test_int_element_not_found_use_default() {
		int notFound = config.getInt("IntElement_NotFound", 5, false);
		assertThat(notFound).isEqualTo(5);
	}

	@Test
	public void
	test_int_element_required_but_not_found_throws_exception() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getInt("DoesNotExist", 0, true);
	}

	@Test
	public void
	test_double_element_found() {
		double found = config.getDouble("DoubleElement", 0, false);
		assertThat(found).isEqualTo(1.5);
	}
	
	@Test
	public void
	test_double_element_not_found_use_default() {
		double notFound = config.getDouble("DoubleElement_NotFound", 5.5, false);
		assertThat(notFound).isEqualTo(5.5);
	}

	@Test
	public void
	test_double_element_required_but_not_found_throws_exception() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getDouble("DoesNotExist", 0, true);
	}

	@Test
	public void
	test_boolean_element_found() {
		boolean foundTrue = config.getBoolean("BooleanElementTrue", false, false);
		boolean foundYes = config.getBoolean("BooleanElementYes", false, false);
		boolean foundFalse = config.getBoolean("BooleanElementFalse", true, false);
		boolean foundNo = config.getBoolean("BooleanElementNo", true, false);
		assertThat(foundTrue).isEqualTo(true);
		assertThat(foundYes).isEqualTo(true);
		assertThat(foundFalse).isEqualTo(false);
		assertThat(foundNo).isEqualTo(false);
	}
	
	@Test
	public void
	test_boolean_element_not_found_use_default() {
		boolean notFoundTrue = config.getBoolean("BooleanElement_NotFound", true, false);
		boolean notFoundFalse = config.getBoolean("BooleanElement_NotFound", false, false);
		assertThat(notFoundTrue).isEqualTo(true);
		assertThat(notFoundFalse).isEqualTo(false);
	}

	@Test
	public void
	test_boolean_element_required_but_not_found_throws_exception() {
		exception.expect(NoSuchElementException.class);
		exception.expectMessage("Required config item 'DoesNotExist' not found in:");
		config.getInt("DoesNotExist", 0, true);
	}
	
	@Test
	public void
	test_count_of_repeating_elements() {
		assertThat(config.getCount("RepeatingElement")).isEqualTo(3);
	}

	@Test
	public void
	test_count_of_element_that_does_not_exist() {
		assertThat(config.getCount("RepeatingElement_DoesNotExist")).isEqualTo(0);
	}

	@Test
	public void
	test_max_index_of_repeating_elements() {
		assertThat(config.getMaxIndex("RepeatingElement")).isEqualTo(2);
	}

	@Test
	public void
	test_max_index_of_element_that_does_not_exist() {
		assertThat(config.getMaxIndex("RepeatingElement_DoesNotExist")).isEqualTo(-1);
	}
	
	@Test
	public void
	test_has_node_found_empty_node() {
		assertThat(config.hasNode("EmptyElement")).isEqualTo(true);
	}

	@Test
	public void
	test_has_node_found_parent_node() {
		assertThat(config.hasNode("ParentElement")).isEqualTo(true);
	}

	@Test
	public void
	test_has_node_not_found() {
		assertThat(config.hasNode("DoesNotExist")).isEqualTo(false);
	}
	
	@Test
	public void
	test_cell_style_found() {
		CellStyle style = config.getCellStyle("CellStyle", null, false);
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(11);
		assertThat(style.getColorRed()).isEqualTo(50);
		assertThat(style.getColorGreen()).isEqualTo(100);
		assertThat(style.getColorBlue()).isEqualTo(150);
		assertThat(style.getItalic()).isEqualTo(true);
	}

	@Test
	public void
	test_cell_style_not_found_use_default() {
		CellStyle defaultStyle = new CellStyleImpl("Arial", 12, new Color(10, 20, 30), true);
		CellStyle style = config.getCellStyle("CellStyle_DoesNotExist", defaultStyle, false);
		assertThat(style.getFontName()).isEqualTo("Arial");
		assertThat(style.getFontHeight()).isEqualTo(12);
		assertThat(style.getColorRed()).isEqualTo(10);
		assertThat(style.getColorGreen()).isEqualTo(20);
		assertThat(style.getColorBlue()).isEqualTo(30);
		assertThat(style.getItalic()).isEqualTo(true);
	}
}
