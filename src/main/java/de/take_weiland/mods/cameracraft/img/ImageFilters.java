package de.take_weiland.mods.cameracraft.img;

import java.awt.image.BufferedImage;

import de.take_weiland.mods.cameracraft.api.img.ImageFilter;
import de.take_weiland.mods.cameracraft.api.img.SimpleRgbFilter;
import de.take_weiland.mods.cameracraft.img.ColorFilter.Channel;

public final class ImageFilters {

	private ImageFilters() { }
	
	public static final ImageFilter NO_FILTER = new ImageFilter() {
		
		@Override
		public ImageFilter combine(ImageFilter other) {
			return other;
		}
		
		@Override
		public BufferedImage apply(BufferedImage image) {
			return image;
		}
	};
	
	public static final ImageFilter RED = fromRgbFilter(new ColorFilter(Channel.RED));
	public static final ImageFilter GREEN = fromRgbFilter(new ColorFilter(Channel.GREEN));
	public static final ImageFilter BLUE = fromRgbFilter(new ColorFilter(Channel.BLUE));
	public static final ImageFilter YELLOW = fromRgbFilter(new YellowFilter());
	
	public static final ImageFilter SEPIA = fromRgbFilter(new SepiaFilter());
	public static final ImageFilter GRAY = fromRgbFilter(new GrayscaleFilter());
	public static final ImageFilter OVEREXPOSE = fromRgbFilter(new OverexposeFilter());
	
	public static BufferedImage apply(BufferedImage src, SimpleRgbFilter filter) {
		int w = src.getWidth();
	    int h = src.getHeight();
		
	    int[] rgbArr = src.getRGB(0, 0, w, h, null, 0, w);
	    
	    int len = rgbArr.length;
	    for (int i = 0; i < len; ++i) {
	    	rgbArr[i] = filter.modifiyRgb(rgbArr[i]);
	    }
	    
	    src.setRGB(0, 0, w, h, rgbArr, 0, w);
	    return src;
	}
	
	public static BufferedImage apply(BufferedImage src, SimpleRgbFilter... filters) {
		int w = src.getWidth();
	    int h = src.getHeight();
		
	    int[] rgbArr = src.getRGB(0, 0, w, h, null, 0, w);
	    
	    int len = rgbArr.length;
	    for (int i = 0; i < len; ++i) {
	    	int rgb = rgbArr[i];
	    	for (SimpleRgbFilter filter : filters) {
	    		rgb = filter.modifiyRgb(rgb);
	    	}
	    	rgbArr[i] = rgb;
	    }
	    
	    src.setRGB(0, 0, w, h, rgbArr, 0, w);
	    return src;
	}
	
	public static ImageFilter fromRgbFilter(final SimpleRgbFilter filter) {
		return new ImageFilterFromRGB(filter);
	}
	
	public static ImageFilter fromRgbFilters(final SimpleRgbFilter... filters) {
		return new ImageFilterFromRGBChained(filters);
	}
	
	public static ImageFilter combine(ImageFilter filter1, ImageFilter filter2) {
		return filter1 == null ? (filter2 == null ? NO_FILTER : filter2) : (filter2 == null ? filter1 : filter1.combine(filter2));
	}
	
	public static ImageFilter combine(ImageFilter... filters) {
		ImageFilter result = null;
		for (int i = 0; i < filters.length; ++i) {
			ImageFilter current = filters[i];
			if (current != null) {
				if (result == null) {
					result = current;
				} else {
					result = result.combine(current);
				}
			}
		}
		return result == null ? NO_FILTER : result;
	}
}
