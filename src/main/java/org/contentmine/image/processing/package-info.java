package org.contentmine.image.processing;

/**
Well it starts with a for loop that goes through each pixel of the image. A pixel contains three numbers, each of them represents the intensity of the color in the pixel (for red, green, blue). So we get the values for red, blue and green using the color constructor. Then we increase the values of the colors in our table. Finally we add each table to the ArrayList. I admit, this could be done better (not using ArrayLists and using separate Java constructor classes; but for the sake of the example, I’ve simplified things a bit). So, the first step is taken for!

Next we need to calculate the normalized sum. What the hell is that?

Consider a discrete grayscale image {x} and let n_i be the number of occurrences of gray level i. The probability of an occurrence of a pixel of level i in the image is

\ p_{x}(i) = p(x=i) = \frac{n_i}{n},\quad 0 \le i < L

L being the total number of gray levels in the image, n being the total number of pixels in the image, and p_x(i) being in fact the image’s histogram for pixel value i, normalized to [0,1].

Let us also define the cumulative distribution function corresponding to p_{x} as

\ cdf_x(i) = \sum_{j=0}^i p_x(j)

which is also the image’s accumulated normalized histogram.

We would like to create a transformation of the form y = T(x) to produce a new image {y}, such that its CDF will be linearized across the value range, i.e.

\ cdf_y(i) = iK

for some constant K. The properties of the CDF allow us to perform such a transform; it is defined as

\ y = T(x) = cdf_x(x)

Notice that the T maps the levels into the range [0,1]. In order to map the values back into their original range, the following simple transformation needs to be applied on the result:

\ y^\prime = y \cdot(\max\{x\} - \min\{x\}) + \min\{x\}

To put in in plain mans terms; first calculate the scale factor which is:

sf = 255 / (im_{width} \times im_{height})

Then we go through each value in our histogram and calculate the sum; on the i-th position we put:

LUT[i] = sum_{i} \times sf

Enough of the theory, let’s look how this looks in practice:
*/
	
