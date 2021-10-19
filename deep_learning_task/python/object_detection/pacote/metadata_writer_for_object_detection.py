"""
codigo encontrado em :https://gist.github.com/4PixelsDev/7400df5cd6f004c4d630c849660577d6
python metadata_writer_for_object_detection.py \ --model_file=yolov4-tiny-416-09-04-2021.tflite \ --label_file=labelmap.txt \ --export_directory=com_metadata

"""

# Copyright 2020 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================
from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

"""Writes metadata and label file to the image classifier models."""

""" 
Usage - Terminal command which execute the script accept 3 parameters:
  -- model_file - path to .tflite model without metadata
  -- label_file - path to .txt file with classes (1 class per row)
  -- export_directory - path to generated .tflite model with metadata
"""
# python ./metadata_writer_for_object_detection.py \
# --model_file=./inal_model.tflite \
# --label_file=./labels.txt \
# --export_directory=./model_with_metadata

import os

from absl import app
from absl import flags
import tensorflow as tf

import flatbuffers
# pylint: disable=g-direct-tensorflow-import
from tflite_support import metadata_schema_py_generated as _metadata_fb
from tflite_support import metadata as _metadata
# pylint: enable=g-direct-tensorflow-import

FLAGS = flags.FLAGS

def define_flags():
  flags.DEFINE_string("model_file", "../outputs/ssd_mbnet_v2_320x320-separa-b-07-04-2021/model.tflite",
                      "Path and file name to the TFLite model file.")
  flags.DEFINE_string("label_file", "../outputs/ssd_mbnet_v2_320x320-separa-b-07-04-2021/classes.txt", "Path to the label file.")
  flags.DEFINE_string("export_directory", "../outputs/ssd_mbnet_v2_320x320-separa-b-07-04-2021/com_metadata/",
                      "Path to save the TFLite model files with metadata.")
  flags.mark_flag_as_required("model_file")
  flags.mark_flag_as_required("label_file")
  flags.mark_flag_as_required("export_directory")


class ModelSpecificInfo(object):
  """Holds information that is specificly tied to an image classifier."""

  def __init__(self, name, version, image_width, image_height, image_min,
               image_max, mean, std, num_classes):
    self.name = name
    self.version = version
    self.image_width = image_width
    self.image_height = image_height
    self.image_min = image_min
    self.image_max = image_max
    self.mean = mean
    self.std = std
    self.num_classes = num_classes


_MODEL_INFO = {
    "model.tflite":
        ModelSpecificInfo(
            name="BlOCOS_UFRB",
            version="v1",
            image_width=320,
            image_height=320,
            image_min=0,
            image_max=255,
            mean=[127.5],
            std=[127.5],
            num_classes=1)
}

class MetadataPopulatorForObjectDetection(object):
  """Populates the metadata for an image classifier."""

  def __init__(self, model_file, model_info, label_file_path):
    self.model_file = model_file
    self.model_info = model_info
    self.label_file_path = label_file_path
    self.metadata_buf = None

  def populate(self):
    """Creates metadata and then populates it for an image classifier."""
    self._create_metadata()
    self._populate_metadata()

  def _create_metadata(self):
    """Creates the metadata for an image classifier."""

    # Creates model info.
    model_meta = _metadata_fb.ModelMetadataT()
    model_meta.name = self.model_info.name
    model_meta.description = ("Equipment.")
    model_meta.version = self.model_info.version
    model_meta.author = "Joel"
    model_meta.license = ("Apache License. Version 2.0 "
                          "http://www.apache.org/licenses/LICENSE-2.0.")

    # Creates input info.
    input_meta = _metadata_fb.TensorMetadataT()
    input_meta.name = "image"
    input_meta.description = ("Detecta blocos da UFRB no campus de Cruz das Almas")
    input_meta.content = _metadata_fb.ContentT()
    input_meta.content.contentProperties = _metadata_fb.ImagePropertiesT()
    input_meta.content.contentProperties.colorSpace = (
        _metadata_fb.ColorSpaceType.RGB)
    input_meta.content.contentPropertiesType = (
        _metadata_fb.ContentProperties.ImageProperties)
    input_normalization = _metadata_fb.ProcessUnitT()
    input_normalization.optionsType = (
        _metadata_fb.ProcessUnitOptions.NormalizationOptions)
    input_normalization.options = _metadata_fb.NormalizationOptionsT()
    input_normalization.options.mean = self.model_info.mean
    input_normalization.options.std = self.model_info.std
    input_meta.processUnits = [input_normalization]
    input_stats = _metadata_fb.StatsT()
    input_stats.max = [self.model_info.image_max]
    input_stats.min = [self.model_info.image_min]
    input_meta.stats = input_stats

    # Creates output info.
    output_location_meta = _metadata_fb.TensorMetadataT()
    output_location_meta.name = "location"
    output_location_meta.description = "The locations of the detected boxes."
    output_location_meta.content = _metadata_fb.ContentT()
    output_location_meta.content.contentPropertiesType = (_metadata_fb.ContentProperties.BoundingBoxProperties)
    output_location_meta.content.contentProperties = (_metadata_fb.BoundingBoxPropertiesT())
    output_location_meta.content.contentProperties.index = [1, 0, 3, 2]
    output_location_meta.content.contentProperties.type = (_metadata_fb.BoundingBoxType.BOUNDARIES)
    output_location_meta.content.contentProperties.coordinateType = (_metadata_fb.CoordinateType.RATIO)
    output_location_meta.content.range = _metadata_fb.ValueRangeT()
    output_location_meta.content.range.min = 2
    output_location_meta.content.range.max = 2

    output_class_meta = _metadata_fb.TensorMetadataT()
    output_class_meta.name = "category"
    output_class_meta.description = "The categories of the detected boxes."
    output_class_meta.content = _metadata_fb.ContentT()
    output_class_meta.content.contentPropertiesType = (
        _metadata_fb.ContentProperties.FeatureProperties)
    output_class_meta.content.contentProperties = (
        _metadata_fb.FeaturePropertiesT())
    output_class_meta.content.range = _metadata_fb.ValueRangeT()
    output_class_meta.content.range.min = 2
    output_class_meta.content.range.max = 2
    label_file = _metadata_fb.AssociatedFileT()
    label_file.name = os.path.basename(self.label_file_path)
    label_file.description = "Label of objects that this model can recognize."
    label_file.type = _metadata_fb.AssociatedFileType.TENSOR_VALUE_LABELS
    output_class_meta.associatedFiles = [label_file]

    output_score_meta = _metadata_fb.TensorMetadataT()
    output_score_meta.name = "score"
    output_score_meta.description = "The scores of the detected boxes."
    output_score_meta.content = _metadata_fb.ContentT()
    output_score_meta.content.contentPropertiesType = (
        _metadata_fb.ContentProperties.FeatureProperties)
    output_score_meta.content.contentProperties = (
        _metadata_fb.FeaturePropertiesT())
    output_score_meta.content.range = _metadata_fb.ValueRangeT()
    output_score_meta.content.range.min = 2
    output_score_meta.content.range.max = 2

    output_number_meta = _metadata_fb.TensorMetadataT()
    output_number_meta.name = "number of detections"
    output_number_meta.description = "The number of the detected boxes."
    output_number_meta.content = _metadata_fb.ContentT()
    output_number_meta.content.contentPropertiesType = (
        _metadata_fb.ContentProperties.FeatureProperties)
    output_number_meta.content.contentProperties = (
        _metadata_fb.FeaturePropertiesT())

    # Creates subgraph info.
    group = _metadata_fb.TensorGroupT()
    group.name = "detection result"
    group.tensorNames = [ output_location_meta.name, output_class_meta.name, output_score_meta.name ]

    subgraph = _metadata_fb.SubGraphMetadataT()
    subgraph.inputTensorMetadata = [input_meta]
    subgraph.outputTensorMetadata = [output_location_meta, output_class_meta, output_score_meta,output_number_meta]
    subgraph.outputTensorGroups = [group]
    model_meta.subgraphMetadata = [subgraph]

    b = flatbuffers.Builder(0)
    b.Finish(
        model_meta.Pack(b),
        _metadata.MetadataPopulator.METADATA_FILE_IDENTIFIER)
    self.metadata_buf = b.Output()
    #model_meta.SubgraphMetadata(0).InputTensorMetadataLength()


  def _populate_metadata(self):
    """Populates metadata and label file to the model file."""
    populator = _metadata.MetadataPopulator.with_model_file(self.model_file)
    populator.load_metadata_buffer(self.metadata_buf)
    populator.load_associated_files([self.label_file_path])
    populator.populate()

def main(_):
  model_file = FLAGS.model_file
  model_basename = os.path.basename(model_file)
  if model_basename not in _MODEL_INFO:
    raise ValueError(
        "The model info for, {0}, is not defined yet.".format(model_basename))

  export_model_path = os.path.join(FLAGS.export_directory, model_basename)

  print("copy "+model_file+" to "+export_model_path)

  # Copies model_file to export_path.
  tf.io.gfile.copy(model_file, export_model_path, overwrite=True)

  # Generate the metadata objects and put them in the model file
  populator = MetadataPopulatorForObjectDetection(
      export_model_path, _MODEL_INFO.get(model_basename), FLAGS.label_file)
  populator.populate()

  # Validate the output model file by reading the metadata and produce
  # a json file with the metadata under the export path
  displayer = _metadata.MetadataDisplayer.with_model_file(export_model_path)
  export_json_file = os.path.join(FLAGS.export_directory,
                                  os.path.splitext(model_basename)[0] + ".json")
  json_file = displayer.get_metadata_json()
  with open(export_json_file, "w") as f:
    f.write(json_file)

  print("Finished populating metadata and associated file to the model:")
  print(model_file)
  print("The metadata json file has been saved to:")
  print(export_json_file)
  print("The associated file that has been been packed to the model is:")
  print(displayer.get_packed_associated_file_list())


if __name__ == "__main__":
  define_flags()
  app.run(main)