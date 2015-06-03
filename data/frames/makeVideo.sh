#!/bin/bash

# adjustment, enhance image
# http://www.imagemagick.org/script/index.php
# http://www.imagemagick.org/Usage/
# http://www.imagemagick.org/Usage/color_mods/
# http://www.howtogeek.com/109369/how-to-quickly-resize-convert-modify-images-from-the-linux-terminal/
# http://superuser.com/questions/370920/auto-image-enhance-for-ubuntu
# convert -enhance -equalize -contrast image.png image_enhanced.png
# convert image.png -enhance image_enhanced.png
# convert image.png -channel rgb -auto-level image_enhanced.png
# convert -despeckle -normalize image.png image_enhanced.png
# convert image.png -contrast -contrast -contrast -contrast image_enhanced.png

echo 'making video...'
rm *.png
convert -crop +1-500 '../../bin/data/frames/*.png' image%05d.png
# convert -crop +0-500 -contrast -contrast '../../../bin/data/frames/*.png' image%05d.png
rm image00000.png image00001.png
cp image00002.png image00000.png
cp image00002.png image00001.png
rm ../../bin/data/frames/*.png
rm out.mp4
ffmpeg -framerate 10 -pattern_type glob -i '*.png' -c:v libx264 -r 30 -pix_fmt yuv420p out.mp4
