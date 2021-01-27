package filters;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class SharpnessFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Integer value = 1;
        Mat kernel = new Mat(3, 3, CvType.CV_32FC1);
        kernel.put(0, 0,
                value - 1, -value, value - 1,
                -value, value + 5, -value,
                value - 1, -value, value - 1);
        Core.divide(kernel, new Scalar(value + 1), kernel);
        Mat result = new Mat();
        Imgproc.filter2D(image, result, -1, kernel);
        return result;
    }
}
