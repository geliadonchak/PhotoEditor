package photo_editor.filters;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class BrightnessFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Integer value = options.getOption("value");
        Mat imgHSV = new Mat();
        Imgproc.cvtColor(image, imgHSV, Imgproc.COLOR_BGR2HSV);
        Core.add(imgHSV, new Scalar(0, 0, value), imgHSV);
        Imgproc.cvtColor(imgHSV, image, Imgproc.COLOR_HSV2BGR);
        return image;
    }
}
