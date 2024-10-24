/*
 * History
 *
 * 16.04.2021 - [JR] - creation
 */
package com.sibvisions.visionx;

import java.io.File;

import org.junit.Test;

import com.sibvisions.util.type.ResourceUtil;

/**
 * The <code>TestWebPreview</code> tests functionality of {@link RecorderConverter}.
 * 
 * @author René Jahn
 */
public class TestRecorderConverter
{
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Test methods
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Tests {@link RecorderConverter#convert(String, String, String, boolean)}.
	 */
	@Test
	public void testConvert()
	{
		File fiInput = new File(ResourceUtil.getLocationForClass(ResourceUtil.getFqClassName(getClass())), "../test/testrecording.avi");
		
		RecorderConverter converter = new RecorderConverter();
		converter.convert(fiInput.getAbsolutePath(),
						  new File(fiInput.getParentFile(), "output_testrecording.avi").getAbsolutePath(),
						  RecorderConverter.PROFILE_AVI,
						  false);
		converter.convert(fiInput.getAbsolutePath(),
				  new File(fiInput.getParentFile(), "output_testrecording.mp4").getAbsolutePath(),
				  RecorderConverter.PROFILE_MP4,
				  false);		
		
		fiInput = new File(ResourceUtil.getLocationForClass(ResourceUtil.getFqClassName(getClass())), "../test/testerror_wmv2.avi");
		
		converter = new RecorderConverter();
		converter.convert(fiInput.getAbsolutePath(),
						  new File(fiInput.getParentFile(), "output_error.avi").getAbsolutePath(),
						  RecorderConverter.PROFILE_AVI,
						  false);
		converter.convert(fiInput.getAbsolutePath(),
				  new File(fiInput.getParentFile(), "output_error.mp4").getAbsolutePath(),
				  RecorderConverter.PROFILE_MP4,
				  false);
		
	}
	
}	// TestWebPreview
