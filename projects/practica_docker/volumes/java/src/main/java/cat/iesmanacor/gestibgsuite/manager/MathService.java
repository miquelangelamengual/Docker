package cat.iesmanacor.gestibgsuite.manager;

import org.springframework.stereotype.Service;


@Service
public class MathService {

    public double mean(double[] data) {
        // The mean average
        double mean = 0.0;
        for (int i = 0; i < data.length; i++) {
            mean += data[i];
        }
        mean /= data.length;

        return mean;
    }

    public double variance(double[] data){
        double mean = this.mean(data);

        // The variance
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance += Math.pow(data[i] - mean, 2);
        }
        variance /= data.length;

        return variance;
    }

    public double standardDeviation(double[] data){
        double variance = this.variance(data);

        // Standard Deviation
        double std = Math.sqrt(variance);

        return std;
    }

}

