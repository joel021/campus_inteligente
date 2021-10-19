import tensorflow as tf

def check(uri, log_file=None):
    if not log_file == None:
        f = open(log_file, "w")
    else:
        f = None
    for example in tf.compat.v1.io.tf_record_iterator(uri):
        s = tf.train.Example.FromString(example)+""


        if not f is None:
            f.write(s)
        else:
            print(s)
    f.close()
"""
PorRoboflow
image/encoded
image/filename
image/format
image/height
image/object/bbox/xmax
image/object/bbox/xmin
image/object/bbox/ymax
image/object/bbox/ymin
image/object/class/label
image/object/class/text
image/width
image/encoded
image/depth
"""