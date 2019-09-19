package com.daiqing.view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;;

public class TakePhoto implements Runnable {

	private boolean save;
	private String studentId;
	private OpenCVFrameGrabber grabber;

	public TakePhoto() {
		super();
		this.save = false;
		this.studentId = null;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}
	
	public static BufferedImage IplImageToBufferedImage(IplImage src) {
	    OpenCVFrameConverter.ToIplImage grabberConverter = new OpenCVFrameConverter.ToIplImage();
	    Java2DFrameConverter paintConverter = new Java2DFrameConverter();
	    Frame frame = grabberConverter.convert(src);
	    return paintConverter.getBufferedImage(frame,1);
	}
	
//	public static BufferedImage iplToBufImgData(IplImage mat) {
//		if (mat.height() > 0 && mat.width() > 0) {
//			BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
//			WritableRaster raster = image.getRaster();
//			DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
//			byte[] data = dataBuffer.getData();
//			BytePointer bytePointer = new BytePointer(data);
//			mat.imageData(bytePointer);
//			return image;
//		}
//		return null;
//	}

	private boolean savePhoto() throws Exception, IOException {
		OpenCVFrameConverter.ToIplImage convert = new OpenCVFrameConverter.ToIplImage();
		grabber.grab();
		IplImage image = null;
		while (image == null) {
			image = convert.convert(grabber.grab());
		}
		
		BufferedImage bufferedImage = IplImageToBufferedImage(image);
		File file = new File(studentId + ".jpg");
		ImageIO.write(bufferedImage, "jpg", file);
		return true;
	}

	@Override
	public void run() {
		grabber = new OpenCVFrameGrabber(0);
		CanvasFrame canvas = new CanvasFrame("摄像头");// 新建一个窗口
		canvas.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		try {
			grabber.start();// 开始获取摄像头数据

			while (true) {
				if (!canvas.isDisplayable()) {// 窗口是否关闭
					grabber.stop();
					System.exit(2);// 退出
				}
				canvas.showImage(grabber.grab());
				if (save) {
					savePhoto();
					save = false;
				}
				Thread.sleep(50);// 50毫秒刷新一次图像
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) throws Exception, InterruptedException {
//		TakePhoto takePhoto = new TakePhoto();
//		Thread t1 = new Thread(takePhoto);
//		t1.run();
//	}

}
