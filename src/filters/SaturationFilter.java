package filters;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class SaturationFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Mat imgHSV = new Mat();
        Imgproc.cvtColor(image, imgHSV, Imgproc.COLOR_BGR2HSV);
        Core.add(imgHSV,
            new Scalar(
                options.getOption("val1"),
                options.getOption("val2"),
                options.getOption("val3")
            ), imgHSV);
        Imgproc.cvtColor(imgHSV, image, Imgproc.COLOR_HSV2BGR);
        return image;
    }
}
