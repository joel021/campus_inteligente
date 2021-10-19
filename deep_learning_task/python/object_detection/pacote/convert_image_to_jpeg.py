from PIL import Image
import glob
import os
from pacote.utils import create_path

def convert_image_to_jpeg(path, to_path):
    create_path(to_path)

    types = ['jpeg','gif','png']

    for type in types:
        files = glob.glob(path+"/*."+type)

        for file in files:
            base = os.path.split(file)

            im = Image.open(file)
            rgb_im = im.convert('RGB')
            name = base[1].replace("."+type,"")

            rgb_im.save(to_path+"/"+name+".jpg")

            os.remove(file)
