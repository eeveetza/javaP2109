package main;

// Recommendation ITU-R P.2109
// Building Entry Loss
public class P2109 {
    //
    // Class implementation of Recommendation ITU-R P.2109
    //
    //
    // Rev   Date        Author                          Description
    //-------------------------------------------------------------------------------
    // v1    05MAY17     Ivica Stevanovic, OFCOM         Initial implementation in Java
    //
    //  Copyright (c) 2017 , Ivica Stevanovic
    //  All rights reserved.
    //
    // Redistribution and use in source and binary forms, with or without
    // modification, are permitted provided that the following conditions are
    // met:
    //
    //     * Redistributions of source code must retain the above copyright
    //       notice, this list of conditions and the following disclaimer.
    //     * Redistributions in binary form must reproduce the above copyright
    //       notice, this list of conditions and the following disclaimer in
    //       the documentation and/or other materials provided with the distribution
    //
    //
    ////
    // THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    // AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    // IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    // ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    // LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    // CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    // SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    // INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    // CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    // ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    // POSSIBILITY OF SUCH DAMAGE.
    //
    // THE AUTHORS AND OFCOM (CH) DO NOT PROVIDE ANY SUPPORT FOR THIS SOFTWARE
    ////

    public double bel(double f, double p, int cl, double th) {
        //bel building entry loss according to ITU-R P.2109
        //   L = bel(f, p, cl, th)
        //
        //   This function computes the building entry loss not exceeded for the
        //   probability p as defined in ITU-R P.2109
        //
        //     Input parameters:
        //     f       -   Frequency (GHz): 0.08 <= f <= 100
        //     p       -   probability for which the loss is not exceeded (%): 0 < p < 100
        //     cl      -   building class (1 - 'traditional', 2 - 'thermally efficient')
        //     th      -   elevation angle at the building facade (degrees above the horizontal)
        //
        //     Output parameters:
        //     L       -   Building entry loss not exceeded for the probability p
        //
        //     Example:
        //     L = bel(f, p, cl, th)

        //     Rev   Date        Author                          Description
        //     -------------------------------------------------------------------------------
        //     v0    05MAY17     Ivica Stevanovic, OFCOM         Initial version

        //// Read the input arguments and check them

        // Checking passed parameters to the defined limits

        if (th < -90 || th > 90) {
            throw new RuntimeException("Elevation angle is outside the valid domain [-90, 90] degrees");
        }


        if (p <= 0 || p >= 100) {
            throw new RuntimeException("Percentage of locations is outside the valid domain (0, 100)%");
        }


        // traditional building
        double r = 12.64;
        double s = 3.72;
        double t = 0.96;
        double u = 9.6;
        double v = 2.0;
        double w = 9.1;
        double x = -3.0;
        double y = 4.5;
        double z = -2.0;

        if (cl == 2) {
            // thermally efficient building
            r = 28.19;
            s = -3.00;
            t = 8.48;
            u = 13.5;
            v = 3.8;
            w = 27.8;
            x = -2.9;
            y = 9.4;
            z = -2.1;
        }

        double Le = 0.212 * Math.abs(th);  // (10)
        double Lh = r + s * Math.log10(f) + t * Math.pow((Math.log10(f)), 2);  //(9)

        double sigma2 = y + z * Math.log10(f);  //(8)
        double sigma1 = u + v * Math.log10(f);  //(7)

        double mu2 = w + x * Math.log10(f);  //(6)
        double mu1 = Lh + Le;  //(5)
        double C = -3;  // (4)

        double B = norminv(p / 100, mu2, sigma2);
        double A = norminv(p / 100, mu1, sigma1);

        double L = 10 * Math.log10(Math.pow(10, (0.1 * A)) + Math.pow(10, (0.1 * B)) + Math.pow(10, (0.1 * C)));

        return L;
    }


    private double norminv(double p, double mu, double sigma) {
        //   This function computes the inverse of the normal distribution with mean mu and standard deviation sigma
        //
        //     Input parameters:
        //     p       -   percentage of locations (0-1)
        //     mu      -   mean of the normal distribution (dB)
        //     sigma   -   standard deviation of the normal distribution (dB)
        //
        //     Output parameters:
        //     y       -   value for which

        //
        //
        //     Rev   Date        Author                          Description
        //     -------------------------------------------------------------------------------
        //     v0    03MAY17     Ivica Stevanovic, OFCOM         Initial version in Java


        double y;

        y = mu + sigma* Qi(1-p);

        return y;
    }

    private double Qi(double x) {
        //Anex 5, Sec. 16 An approximation to the inverse complementary cumulative normal distribution
        // function
        // Rev     Date    Author                      Description
        // -------------------------------------------------------------------------------
        // v1      1DEC16  Ivica Stevanovic, OFCOM     Initial version

        double out;

        if (x <= .5) {
            out = T(x) - C(x);          //(39 a)
        } else {
            out = -(T(1 - x) - C(1 - x)); //(39 b)
        }

        return out;

    }

    private double T(double y) {
        double outT = Math.sqrt(-2 * Math.log(y));     //(39 c)
        return outT;
    }

    private double C(double z) {
        double C0 = 2.515517;
        double C1 = 0.802853;
        double C2 = 0.010328;
        double D1 = 1.432788;
        double D2 = 0.189269;
        double D3 = 0.001308;
        double outC = (((C2 * T(z) + C1) * T(z)) + C0) / (((D3 * T(z) + D2) * T(z) + D1) * T(z) + 1);//(39d)
        return outC;
    }
}
