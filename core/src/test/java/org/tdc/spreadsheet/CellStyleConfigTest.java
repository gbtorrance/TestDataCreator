package org.tdc.spreadsheet;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.tdc.shared.config.Config;
import org.tdc.shared.config.ConfigImpl;

/**
 * Unit tests for {@link Config} and {@link CellStyle}.
 */
public class CellStyleConfigTest {
	
	private static Config config;
	
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static void setup() {
		ClassLoader classLoader = CellStyleConfigTest.class.getClassLoader();
		Path path = Paths.get(classLoader.getResource(
				"CellStyleConfigTest.xml").getPath().toString());
		config = new ConfigImpl.Builder(path).build();
	}

	@Test
	public void testCellStyleFound() {
		CellStyle style = new CellStyleImpl.Builder().setFromConfig(
				config, "CellStyle", null, false).build();
		assertThat(style.getFontName()).isEqualTo("Calibri");
		assertThat(style.getFontHeight()).isEqualTo(11);
		assertThat(style.getColorRed()).isEqualTo(50);
		assertThat(style.getColorGreen()).isEqualTo(100);
		assertThat(style.getColorBlue()).isEqualTo(150);
		assertThat(style.getItalic()).isEqualTo(true);
	}

	@Test
	public void testCellStyleNotFoundUseDefault() {
		CellStyle defaultStyle = new CellStyleImpl.Builder()
				.setFontName("Arial")
				.setFontHeight(12.0)
				.setColor(new Color(10, 20, 30))
				.setFillColor(new Color(75, 70, 65))
				.setItalic(true)
				.setBold(true)
				.setShrinkToFit(true)
				.setAlignment(CellAlignment.CENTER)
				.setFormat("text")
				.build();
		CellStyle style = new CellStyleImpl.Builder().setFromConfig(
				config, "CellStyle_DoesNotExist", defaultStyle, false).build();
		assertThat(style.getFontName()).isEqualTo("Arial");
		assertThat(style.getFontHeight()).isEqualTo(12);
		assertThat(style.getColorRed()).isEqualTo(10);
		assertThat(style.getColorGreen()).isEqualTo(20);
		assertThat(style.getColorBlue()).isEqualTo(30);
		assertThat(style.getFillColorRed()).isEqualTo(75);
		assertThat(style.getFillColorGreen()).isEqualTo(70);
		assertThat(style.getFillColorBlue()).isEqualTo(65);
		assertThat(style.getItalic()).isEqualTo(true);
		assertThat(style.getBold()).isEqualTo(true);
		assertThat(style.getShrinkToFit()).isEqualTo(true);
		assertThat(style.getAlignment()).isEqualTo(CellAlignment.CENTER);
		assertThat(style.getFormat()).isEqualTo("text");
	}
}
