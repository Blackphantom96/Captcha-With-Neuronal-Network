from PIL import Image
import pytesseract
import argparse
import cv2
import os

for i in range(1,10000):
    print(i,pytesseract.image_to_string(Image.open(str(i)+".jpg") ,config='-c tessedit_char_whitelist=abcdef0123456789') )
