package filters;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class NegativeFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Mat m = new Mat(image.rows(), image.cols(), image.type(), new Scalar(255, 255, 255));
        Mat negative = new Mat();
        Core.subtract(m, image, negative);
        return negative;
    }
}
