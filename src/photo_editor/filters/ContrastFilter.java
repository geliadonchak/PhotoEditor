package photo_editor.filters;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ContrastFilter implements FilterInterface {
    @Override
    public Mat filter(Mat image, FilterOptions options) {
        Scalar meanBGR = Core.mean(image);
        double mean = meanBGR.val[0] * 0.114 + meanBGR.val[1] * 0.587 +
                meanBGR.val[2] * 0.299;

        double contrast = options.getOption("value");

        Mat lut = new Mat(1, 256, CvType.CV_8UC1);
        byte[] arr = new byte[256];
        int color = 0;
        for (int i = 0; i < 256; i++) {
            color = (int) (contrast * (i - mean) + mean);
            color = color > 255 ? 255 : (Math.max(color, 0));
            arr[i] = (byte) (color);
        }
        lut.put(0, 0, arr);

        Mat img2 = new Mat();
        Core.LUT(image, lut, img2);
        return img2;
    }
}