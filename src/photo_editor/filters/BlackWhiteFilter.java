package photo_editor.filters;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BlackWhiteFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Mat temp1 = new Mat();
        Imgproc.cvtColor(image, temp1, Imgproc.COLOR_BGR2GRAY);
        Mat temp2 = new Mat();
        Imgproc.threshold(temp1, temp2, 100, 255,
                Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Mat result = new Mat();
        Imgproc.cvtColor(temp2, result, Imgproc.COLOR_GRAY2BGR);
        return result;
    }
}
