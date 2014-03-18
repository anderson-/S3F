
package s3f.util;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.ImageObserver;
import java.net.URL;

/**
 * Present a simple graphic to the user upon launch of the application, to
 * provide a faster initial response than is possible with the main window.
 * 
* <P>Adapted from an <a
 * href=http://developer.java.sun.com/developer/qow/archive/24/index.html>item</a>
 * on Sun's Java Developer Connection.
 * 
* <P>This splash screen appears within about 2.5 seconds on a development
 * machine. The main screen takes about 6.0 seconds to load, so use of a splash
 * screen cuts down the initial display delay by about 55 percent.
 */
public final class SplashScreen extends Frame {

	/**
	 * Construct using an image for the splash screen.
	 *
	 * @param aImageId must have content, and is used by
	 * {@link Class#getResource(java.lang.String)} to retrieve the splash screen
	 * image.
	 */
	public SplashScreen(String aImageId) {
		/* 
		 * Implementation Note
		 * Args.checkForContent is not called here, in an attempt to minimize 
		 * class loading.
		 */
		if (aImageId == null || aImageId.trim().length() == 0) {
			throw new IllegalArgumentException("Image Id does not have content.");
		}
		fImageId = aImageId;
	}

	/**
	 * Show the splash screen to the end user.
	 *
	 * <P>Once this method returns, the splash screen is realized, which means
	 * that almost all work on the splash screen should proceed through the
	 * event dispatch thread. In particular, any call to <tt>dispose</tt> for
	 * the splash screen must be performed in the event dispatch thread.
	 */
	public void splash() {
		initImageAndTracker();
		setSize(fImage.getWidth(NO_OBSERVER), fImage.getHeight(NO_OBSERVER));
		center();

		fMediaTracker.addImage(fImage, IMAGE_ID);
		try {
			fMediaTracker.waitForID(IMAGE_ID);
		} catch (InterruptedException ex) {
			System.out.println("Cannot track image load.");
		}

		SplashWindow splashWindow = new SplashWindow(this, fImage);
	}
	// PRIVATE//
	private final String fImageId;
	private MediaTracker fMediaTracker;
	private Image fImage;
	private static final ImageObserver NO_OBSERVER = null;
	private static final int IMAGE_ID = 0;

	private void initImageAndTracker() {
		fMediaTracker = new MediaTracker(this);
		URL imageURL = SplashScreen.class.getResource(fImageId);
		fImage = Toolkit.getDefaultToolkit().getImage(imageURL);
	}

	/**
	 * Centers the frame on the screen.
	 *
	 * <P>This centering service is more or less in
	 * {@link hirondelle.stocks.util.ui.UiUtil}; this duplication is justified
	 * only because the use of {@link hirondelle.stocks.util.ui.UiUtil} would
	 * entail more class loading, which is not desirable for a splash screen.
	 */
	private void center() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle frame = getBounds();
		setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
	}

	private class SplashWindow extends Window {

		SplashWindow(Frame aParent, Image aImage) {
			super(aParent);
			fImage = aImage;
			setSize(fImage.getWidth(NO_OBSERVER), fImage.getHeight(NO_OBSERVER));
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle window = getBounds();
			setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
			setVisible(true);
		}

		@Override
		public void paint(Graphics graphics) {
			if (fImage != null) {
				graphics.drawImage(fImage, 0, 0, this);
			}
		}
		private Image fImage;
	}
}