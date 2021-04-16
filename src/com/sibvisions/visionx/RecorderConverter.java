/*
 * History
 * 
 * 16.04.2021 - [JR] - creation
 */
package com.sibvisions.visionx;

import java.io.File;

import com.sibvisions.util.log.LoggerFactory;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;
import ws.schild.jave.progress.EncoderProgressListener;

/**
 * The <code>RecorderConverter</code> is a simple video converter. It converts TSCC Codec AVIs to
 * MPEG4 AVIs or H264 MP4 videos. It is a simple utility and not configurable. It is at it is.
 * 
 * @author René Jahn
 */
public class RecorderConverter 
{
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Class members
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/** the name. */
	public static final String NAME = "Video Converter";

	/** the version number. */
	public static final String VERSION = "1.0";
	
	/** the MP4 conversion profile. */
	public static final String PROFILE_MP4 = "profile_mp4";
	
	/** the AVI conversion profile. */
	public static final String PROFILE_AVI = "profile_avi";
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// User-defined methods
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Converts the given input with given profile.
	 * 
	 * @param pInput the input
	 * @param pOutput the output
	 * @param pProfile the profile
	 * @param pResize whether the input should be resized
	 */
	public void convert(final String pInput, final String pOutput, String pProfile, boolean pResize)
	{
		File fiInput = new File(pInput);
		
		if (!fiInput.exists())
		{
			throw new IllegalArgumentException("File not found: " + pInput);
		}
		
		MultimediaObject moInput = new MultimediaObject(fiInput);
		
		VideoAttributes video = new VideoAttributes();
		EncodingAttributes attrs = new EncodingAttributes();
		
		//this can help: http://www.sauronsoftware.it/projects/jave/manual.php#9
		//mp4 is a container and only some codec are possible
		//avi is also a container and not all codecs are possible
		//
		//played around a little bit until found a good quality/size setting
		if (PROFILE_AVI.equals(pProfile))
		{
			attrs.setOutputFormat("avi");

			video.setCodec("mpeg4");	
			
			/*
			 see https://www.ezs3.com/public/What_bitrate_should_I_use_when_encoding_my_video_How_do_I_optimize_my_video_for_the_web.cfm

			Output size	Bitrate	Filesize
			320x240 	400 kbps	3MB / minute
			480x270 	700 kbps	5MB / minute
			1024x576 	1500 kbps	11MB / minute
			1280x720    2500 kbps	19MB / minute
			1920x1080   4000 kbps	30MB / minute
			*/
			
			video.setBitRate(1500000);
			
			// More the frames more quality and size, but keep it low based on devices like mobile
			video.setFrameRate(30);
		}
		else
		{
			attrs.setOutputFormat("mp4");

			video.setCodec("h264");
		}
		
		try
		{
			VideoSize size = moInput.getInfo().getVideo().getSize();
			
			int width = size.getWidth().intValue();
			int height = size.getHeight().intValue();

			if (pResize)
			{
				video.setSize(new VideoSize(width / 3 * 2, height / 3 * 2));
			}
			else
			{
				//only if wmv2
				//video.setSize(size);
				
				video.getPixelFormat();
				
				if (width > 1920)
				{
					if (width % 1920 == 0)
					{
						int factor = width / 1280;
						
						video.setSize(new VideoSize(width / factor * (factor - 1), height / factor * (factor - 1)));
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		//in case of wmv2 -> width has to be a multiple of 2
//		Optional<VideoSize> size = video.getSize();
		
//		if (!size.isEmpty())
//		{
//			if (PROFILE_AVI.equals(pProfile))
//			{
//				//multiple of 2 for WMV2 codec
//				int width = size.get().getWidth();
//				
//				if (width % 2 == 1)
//				{
//					video.setSize(new VideoSize(width - 1, size.get().getHeight() - 1));
//				}
//			}
//		}
		
		attrs.setVideoAttributes(video);
		
		try
		{
			Encoder encoder = new Encoder();
			encoder.encode(moInput, new File(pOutput), attrs, new EncoderProgressListener() 
			{
				@Override
				public void sourceInfo(MultimediaInfo pInfo) 
				{
					LoggerFactory.getInstance(RecorderConverter.class).debug("Start conversion of [", pInput,
																		     "]\n, to [", pOutput,
										                                     "]\n, size = ", pInfo.getVideo().getSize().getWidth(), "x", pInfo.getVideo().getSize().getHeight(),
										                                     "\n, duration = " + (pInfo.getDuration() / 1000), " seconds",
										                                     "\n, bitrate = ", pInfo.getVideo().getBitRate(),
										                                     "\n, framerate = ", pInfo.getVideo().getFrameRate());
				}

				@Override
				public void progress(int pPermille) 
				{
					LoggerFactory.getInstance(RecorderConverter.class).debug("Conversion progress: ", (pPermille / 10), "%");
				}

				@Override
				public void message(String pMessage) 
				{
					LoggerFactory.getInstance(RecorderConverter.class).debug(pMessage);
				}
			});
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
}	// RecorderConverter
