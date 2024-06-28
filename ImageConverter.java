import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ImageConverter {
    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        BufferedImage hugeImage = ImageIO.read(new File(s.next()));
        short[][] image = imageToNumbers(hugeImage);
        System.out.println(cFormatter(image));

    }

    private static String cFormatter(short[][] image) {
        StringBuilder out = new StringBuilder();
        out.append("static const short myImage[").append(image.length).append("][").append(image[0].length).append("] = ");
        out.append("{");
        for(short[] array : image){
            out.append("{");
            for(short number : array){
                out.append(number).append(", ");
            }
            out.append("},\n");
        }
        out.append("};");
        return out.toString();
    }

    private static String asmFormatter(short[][] image){
        StringBuilder out = new StringBuilder();
        out.append("myImage:\n");

        for(short[] array : image){
            for(short number : array){
                out.append(".short ").append(number).append("\n");
            }
        }
       return out.toString();
    }

    public static short[][] imageToNumbers(BufferedImage image){
        short[][] out = new short[image.getWidth()][image.getHeight()];
        for(int i = 0; i < image.getHeight(); i++){
            for(int j = 0; j < image.getWidth(); j++){
                int pixel = image.getRGB(j,i);
                //pixel is R G B A (8 bits each)
                //first 5 is blue
                //next 6 is green
                //next 5 is red

                //pixel is 32 bit int, out = 16 bit
                // | 8 | 8 | 8 | 8 |
                // | R | G | B | A |
                // | B | G | R |
                // | 5 | 6 | 5 |
        //01101011_10101010_0111001
                // first 5 bits of out = 3rd 8 bits of pixel ->> pixel & 0b1111100000000000
                //next 6 bits of out = 2nd 8 bits of pixel --> pixel AND  0b111111000000000000000000
                //next 5 bits of out = 1st 8 bits of pixel  --> pixel AND 0b11111000000000000000000000000000
                int blueOfPixel = (pixel & 0b11111111) ;
                int greenOfPixel = (pixel &  0b1111111100000000) >> 8;
                int redOfPixel = (pixel & 0b111111110000000000000000) >> 16;
                short b = (short) (((short) blueOfPixel & 0b11111000) >> 3) ;
                short bg = (short) (b | (short) ((short) ((short) greenOfPixel & 0b11111100) >> 2) << 5);
                short bgr = (short) (bg | (short) ((short) ((short) redOfPixel &0b11111000) >> 3) << 11);
                out[j][i] = bgr;
            }
        }
        return out;
    }


}
