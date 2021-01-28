package photo_editor.filters;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class SepiaFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Mat kernel = new Mat(3, 3, CvType.CV_32FC1);
        kernel.put(0, 0,
                0.131, 0.534, 0.272,
                0.168, 0.686, 0.349,
                0.189, 0.769, 0.393
        );
        Mat sepia = new Mat();
        Core.transform(image, sepia, kernel);
        return sepia;
    }
}
