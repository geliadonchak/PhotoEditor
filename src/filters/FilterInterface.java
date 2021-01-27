package filters;

import org.opencv.core.Mat;

public interface FilterInterface {
    Mat filter(Mat image, FilterOptions options);
}
