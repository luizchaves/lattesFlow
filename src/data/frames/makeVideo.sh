#!/bin/bash

rm *.png
convert -crop +0-500 '../../../bin/data/frames/*.png' image%05d.png
rm image00000.png image00001.png
ffmpeg -framerate 10 -pattern_type glob -i '*.png' -c:v libx264 -r 30 -pix_fmt yuv420p out.mp4