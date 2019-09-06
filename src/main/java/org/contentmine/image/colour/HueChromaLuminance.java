package org.contentmine.image.colour;

public class HueChromaLuminance {

	static final public double Y0 = 100;
	static final public double GAMMA = 3;
	static final public double AL = 1.4456;
	static final public double ACH_INC = 0.16;
	
	private double hue;
	private double chroma;
	private double luminance;
	
	public HueChromaLuminance(double h, double c, double l) {
		this.hue = h;
		this.chroma = c;
		this.luminance = l;
	}
	
	public static HueChromaLuminance createHCLfromRGB(int ll) {
		return createHCLfromRGB(ll & 0xFF0000, ll & 0x00FF00, ll & 0x0000FF );
	}

	public static HueChromaLuminance createHCLfromRGB(int r, int g, int b) {
		HueChromaLuminance hcl = new HueChromaLuminance(0.0, 0.0, 0.0);
	    double min = Math.min(Math.min(r, g), b);
	    double max = Math.max(Math.max(r, g), b);
	    if (max == 0) {
	        return hcl;
	    }

	    double alpha = (min / max) / Y0;
	    double q = Math.exp(alpha * GAMMA);
	    double rMinusG = r - g;
	    double gMinusB = g - b;
	    double bMinusR = b - r;
	    double luminance = ((q * max) + ((1 - q) * min)) / 2;
	    double chroma = q * (Math.abs(rMinusG) + Math.abs(gMinusB) + Math.abs(bMinusR)) / 3;
	    double hue = Math.toDegrees(Math.atan2(gMinusB, rMinusG));

	    if (rMinusG <  0) {
	        hue = (gMinusB >= 0) ? 90 + hue : hue - 90; 
	    } //works

	    return new HueChromaLuminance(hue,chroma,luminance);
	}

	public double cycldistance(HueChromaLuminance hcl2) {
	    double deltaLuminance = this.getLuminance() - hcl2.getLuminance();
	    double deltaHue = Math.abs(this.getHue() - hcl2.getHue());
	    double chroma1 = this.getChroma();
	    double chroma2 = hcl2.getChroma();
	    // triangle
	    return Math.sqrt(deltaLuminance*deltaLuminance + chroma1*chroma1 + chroma2*chroma2 - 2*chroma1*chroma2*Math.cos(Math.toRadians(deltaHue)));
	}

	private double getLuminance() {
		return luminance;
	}

	public double getHue() {
		return hue;
	}

	public double getChroma() {
		return chroma;
	}

	public double distance_hcl(HueChromaLuminance hcl2) {
	    double c1 = this.getChroma();
	    double c2 = hcl2.getChroma();
	    double deltaHue = Math.abs(this.getHue() - hcl2.getHue());
	    if (deltaHue > 180) deltaHue = 360 - deltaHue;
	    double ach = deltaHue + ACH_INC;
	    double alDeltaL = AL * Math.abs(this.getLuminance() - hcl2.getLuminance());
	    return Math.sqrt(alDeltaL * alDeltaL + (c1 * c1 + c2 * c2 - 2 * c1 * c2 * Math.cos(Math.toRadians(deltaHue))));
	}
	
	@Override
	public String toString() {
		return this.hue +" "+this.chroma+" "+this.luminance;
	}
}
