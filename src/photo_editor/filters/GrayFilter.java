package photo_editor.filters;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class GrayFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Mat temp = new Mat();
        Mat result = new Mat();
        Imgproc.cvtColor(image, temp, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(temp, result, Imgproc.COLOR_GRAY2BGR);
        return result;
    }
}
