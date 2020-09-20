package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.roundToInt

val input = Scanner(System.`in`)
val imagePath = "C:\\Users\\nesce\\IdeaProjects\\Seam Carving\\Seam Carving\\task\\"



fun main(args: Array<String>) {
    val imagein = imagePath + if (args.contains("-in")) args[1 + args.indexOf("-in")].replace("/", "\\") else "src\\images\\blue.png"
    val out = imagePath + if (args.contains("-out")) args[1 + args.indexOf("-out")].replace("/", "\\") else "src\\images\\blue_intensity.png"
    val intensityImage = ImageIO.read(File(imagein)).getEnergyMap().intensityImage()
    ImageIO.write(intensityImage, "PNG", File(out))

}

fun makeNegative(image: BufferedImage): BufferedImage {
    val newImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (i in 0 until image.width) {
        for (j in 0 until image.height) {
            val rgb = image.getRGB(i, j)
            newImage.setRGB(i, j, rgb.invert())
        }
    }
    return newImage
}

fun Int.invert(): Int {
    val rgb = Color(this)
    val new = Color(255 - rgb.red, 255 - rgb.green, 255 - rgb.blue)
    return rgb.rgb
}

infix fun Color.diffSquare(other: Color): Int {
    return (red - other.red).sq() + (green - other.green).sq() + (blue - other.blue).sq()
}
fun Int.sq():Int {
    return this * this
}
fun Int.sqrt():Float {
    return this.toFloat().pow(0.5f)
}

fun BufferedImage.getEnergyMap(): Array<FloatArray> {
    val width = this.width
    val height = this.height
    val energyMap = Array(height) { FloatArray(width) { 0.0f } }
    for (j in 0 until height) {
        for (i in 0 until width) {
            val xgrad = when (i) {
                0 -> Color(this.getRGB(0, j)) diffSquare Color(this.getRGB(2, j))
                width - 1 -> Color(this.getRGB(i - 2, j)) diffSquare Color(this.getRGB(i, j))
                else -> Color(this.getRGB(i - 1, j)) diffSquare Color(this.getRGB(i + 1, j))
            }
            val ygrad = when (j) {
                0 -> Color(this.getRGB(i, 0)) diffSquare Color(this.getRGB(i, 2))
                height - 1 -> Color(this.getRGB(i, j - 2)) diffSquare Color(this.getRGB(i, j))
                else -> Color(this.getRGB(i, j - 1)) diffSquare Color(this.getRGB(i, j + 1))
            }
            energyMap[j][i] = (xgrad + ygrad).sqrt()
        }
    }
    return energyMap
}
fun Array<FloatArray>.intensityImage(): BufferedImage {
    val maxEnergy = this.maxOf { row -> row.maxOf { it } }
    val height = this.size
    val width = this[0].size
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    for (j in 0 until height) {
        for (i in 0 until width) {
            val intensity = (this[j][i]*255.0/maxEnergy).toInt()
            image.setRGB(i, j, Color(intensity, intensity, intensity).rgb)
        }
    }
    return image
}

fun makeImage(): BufferedImage {
    println("Enter rectangle width:")
    val width = input.nextInt()
    println("Enter rectangle height:")
    val height = input.nextInt()
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g = image.graphics
    g.color = Color.black
    g.fillRect(0,0,width, height)
    g.color = Color.red
    g.drawLine(0, 0, width-1, height-1)
    g.drawLine(0, height-1, width-1, 0)
    return image
}
fun saveImage(image: BufferedImage) {
    println("Enter output image name:")
    val name = input.next()
    ImageIO.write(image, "PNG", File(name))
}