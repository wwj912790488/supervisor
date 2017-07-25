package com.arcsoft.supervisor.transcoder.type;


/**
 * Framerate def
 * <p/>
 * Standard:
 * "ntsc_film" 			24000 	1001 	23.976
 * "ntsc_video" 		30000 	1001 	29.970
 * "ntsc_double" 		60000 	1001 	59.940
 * "ntsc_quad" 			120000 	1001 	119.880
 * "ntsc_round_film" 	2997 	125 	23.976
 * "ntsc_round_video" 	2997 	100 	29.97
 * "ntsc_round_double" 	2997 	50 		59.94
 * "ntsc_round_quad" 	2997 	25 		119.88
 * "film" 				24 		1 		24.0
 * "pal_film" 			25 		1 		25.0
 * "pal_video" 			25 		1 		25.0
 * "pal_double" 		50 		1 		50.0
 * "pal_quad" 			100 	1 		100.0
 *
 * @author Bing
 */
public class Framerate {

    public int numerator = 0;
    public int denominator = 0;

    public Framerate(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
     * approximate fps desc
     */
    @Override
    public String toString() {
        String ret = null;
        float r = 0;
        if (this.denominator != 0) {
            r = ((float) numerator) / denominator;
        }
        try {
            ret = String.format("%.3f", r);
            if (ret.endsWith(".000")) {
                int p = ret.indexOf('.');
                ret = ret.substring(0, p);
            }
        } catch (Exception e) {
        }
        return ret == null ? String.valueOf(r) : ret;
    }

}
