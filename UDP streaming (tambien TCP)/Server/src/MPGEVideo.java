import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class MPGEVideo extends MediaListenerAdapter {
	public MPGEVideo(String filename) {
		IMediaReader reader = ToolFactory.makeReader(filename);
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		reader.addListener(this);
	}

	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			BufferedImage a = event.getImage();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(a.getHeight());
			ImageIO.write(a, "jpg", outputStream);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
