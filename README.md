# lattesFlow

Project to view migration flows in lattes

## Video generation

- https://trac.ffmpeg.org/wiki/Create%20a%20video%20slideshow%20from%20images
- ffmpeg -framerate 50 -i %06d.tif -c:v libx264 -r 30 -pix_fmt yuv420p out.mp4
- ffmpeg -framerate 10 -pattern_type glob -i '*.png' -c:v libx264 -r 30 -pix_fmt yuv420p out.mp4
- ffmpeg -framerate 1 -pattern_type glob -i '*.png' -c:v libx264 out.mp4
