package photo_editor;

import photo_editor.filters.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import photo_editor.nodes.NodeTypes;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.HashMap;

public class ImageContainer {
    private Mat mainImage = new Mat(1080, 1920, CvType.CV_8UC3, new Scalar(255, 255, 255));

    private final HashMap<NodeTypes, FilterInterface> filtersRegister = new HashMap<>() {{
        put(NodeTypes.SetSepia, new SepiaFilter());
        put(NodeTypes.SetSharpness, new SharpnessFilter());
        put(NodeTypes.SetNegative, new NegativeFilter());
        put(NodeTypes.ChangeSaturation, new SaturationFilter());
        put(NodeTypes.ChangeBrightness, new BrightnessFilter());
        put(NodeTypes.SetGray, new GrayFilter());
        put(NodeTypes.ChangeContrast, new ContrastFilter());
        put(NodeTypes.SetBlackWhite, new BlackWhiteFilter());
    }};

    public void filter(NodeTypes nodeType, FilterOptions options) {
        FilterInterface filter = filtersRegister.get(nodeType);
        mainImage = filter.filter(mainImage, options);
    }

    public WritableImage getWritableImage() {
        return matToImageFX(mainImage);
    }

    public Mat getMainImage() {
        return mainImage;
    }

    public void setMainImage(Mat mainImage) {
        this.mainImage = mainImage;
    }

    // convert Mat to BufferedImage
    private WritableImage matToImageFX(Mat mat) {
        if (mat == null || mat.empty()) {
            return null;
        }

        switch (mat.depth()) {
            case CvType.CV_8U:
                break;
            case CvType.CV_16U:
                Mat m_16 = new Mat();
                mat.convertTo(m_16, CvType.CV_8U, 255.0 / 65535);
                mat = m_16;
                break;
            case CvType.CV_32F:
                Mat m_32 = new Mat();
                mat.convertTo(m_32, CvType.CV_8U, 255);
                mat = m_32;
                break;
            default:
                return null;
        }

        switch (mat.channels()) {
            case 1:
                Mat m_bgra = new Mat();
                Imgproc.cvtColor(mat, m_bgra, Imgproc.COLOR_GRAY2BGRA);
                mat = m_bgra;
                break;
            case 3:
                Mat mm_bgra = new Mat();
                Imgproc.cvtColor(mat, mm_bgra, Imgproc.COLOR_BGR2BGRA);
                mat = mm_bgra;
                break;
            case 4:
                break;
            default:
                return null;
        }

        byte[] buf = new byte[mat.channels() * mat.cols() * mat.rows()];
        mat.get(0, 0, buf);
        WritableImage writableImage = new WritableImage(mat.cols(), mat.rows());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        pixelWriter.setPixels(0, 0, mat.cols(), mat.rows(),
                WritablePixelFormat.getByteBgraInstance(),
                buf, 0, mat.cols() * 4);
        return writableImage;
    }
}
