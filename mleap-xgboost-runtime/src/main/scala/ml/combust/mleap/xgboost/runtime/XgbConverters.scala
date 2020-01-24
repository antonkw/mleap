package ml.combust.mleap.xgboost.runtime

import biz.k11i.xgboost.util.FVec
import ml.combust.mleap.tensor.{DenseTensor, SparseTensor, Tensor}
import ml.dmlc.xgboost4j.LabeledPoint
import ml.dmlc.xgboost4j.scala.DMatrix
import org.apache.spark.ml.linalg.{DenseVector, SparseVector, Vector}

trait XgbConverters {
  implicit class VectorOps(vector: Vector) {
    def asXGB: DMatrix = {
      vector match {
        case SparseVector(_, indices, values) =>{
          new DMatrix(Iterator(new LabeledPoint(0.0f, indices, values.map(_.toFloat))))
        }
        case DenseVector(values) =>
          new DMatrix(Iterator(new LabeledPoint(0.0f, null, values.map(_.toFloat))))
      }
    }

    def asXGBPredictor: FVec = {
      vector match {
//        case SparseVector(_, indices, values) => {
//          FVec.Transformer.fromMap(
//        }
        case DenseVector(values) => {
          FVec.Transformer.fromArray(values.map(_.toFloat), false)
        }
      }
    }
  }

  implicit class DoubleTensorOps(tensor: Tensor[Double]) {
    def asXGB: DMatrix = {
      tensor match {
        case SparseTensor(indices, values, _) =>{
          new DMatrix(Iterator(new LabeledPoint(0.0f, indices.map(_.head).toArray, values.map(_.toFloat))))
        }
        case DenseTensor(_, _) =>{
          new DMatrix(Iterator(new LabeledPoint(0.0f, null, tensor.toDense.rawValues.map(_.toFloat))))
        }
      }
    }

    def asXGBPredictor: FVec = {
      FVec.Transformer.fromArray(tensor.toArray.map(_.toFloat), false)
    }
  }
}

object XgbConverters extends XgbConverters
