import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

public class mp4CanalCreator extends MediaToolAdapter {
	private static final Integer WIDTH = 480;
	private static final Integer HEIGHT = 360;

	public mp4CanalCreator(String filename, int port) {
		// create custom listeners
		Resizer resizer = new Resizer(WIDTH, HEIGHT);

		// reader
		IMediaReader reader = ToolFactory.makeReader(filename);
		reader.addListener(resizer);

		// writer
		IMediaWriter writer = ToolFactory.makeWriter("./data/videos/" + port + "/file.mp4", reader);
		resizer.addListener(writer);
		writer.addListener(this);
		
		while (reader.readPacket() == null) {
			// continue coding
		}
	}
	
	public void onAddStream(IAddStreamEvent event) {
		int streamIndex = event.getStreamIndex();
		IStreamCoder streamCoder = event.getSource().getContainer().getStream(streamIndex).getStreamCoder();
		if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
		} else if (streamCoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
			streamCoder.setWidth(WIDTH);
			streamCoder.setHeight(HEIGHT);
		}
		super.onAddStream(event);
	}
}
 
class Resizer extends MediaToolAdapter {
	private Integer width;
	private Integer height;
 
	private IVideoResampler videoResampler = null;
 
	public Resizer(Integer aWidth, Integer aHeight) {
		this.width = aWidth;
		this.height = aHeight;
	}
 
	@Override
	public void onVideoPicture(IVideoPictureEvent event) {
		IVideoPicture pic = event.getPicture();
		if (videoResampler == null) {
			videoResampler = IVideoResampler.make(width, height, pic.getPixelType(), pic.getWidth(), pic
					.getHeight(), pic.getPixelType());
		}
		IVideoPicture out = IVideoPicture.make(pic.getPixelType(), width, height);
		videoResampler.resample(out, pic);
 
		IVideoPictureEvent asc = new VideoPictureEvent(event.getSource(), out, event.getStreamIndex());
		super.onVideoPicture(asc);
		out.delete();
	}
}