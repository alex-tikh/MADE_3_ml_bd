package test

import breeze.linalg._
import breeze.stats._
import java.io.File

object Main {
  def main(args: Array[String]): Unit = {

    val matrix = csvread(new File("/home/alexander/MADE/3_ml_bd/hw3/diabetes_dataset_train.csv"),',')
    val target = DenseVector.zeros[Double](matrix.rows)

    target := matrix(::, -1)
    matrix(::, -1) := DenseVector.ones[Double](matrix.rows)

    // Make Validation Split
    val slice: Int = matrix.rows * 2 / 10
    val train = DenseMatrix.zeros[Double](matrix.rows - slice, matrix.cols)
    val validation = DenseMatrix.zeros[Double](slice, matrix.cols)

    train := matrix(0 to -(slice + 1), ::)
    validation := matrix(-slice to -1, ::)

    val target_train = DenseVector.zeros[Double](matrix.rows - slice)
    val target_validation = DenseVector.zeros[Double](slice)
    target_train := target(0 to -(slice + 1))
    target_validation := target(-slice to -1)

    // Compute linear regression direct solution
    val direct_solution = inv(train.t * train) * train.t * target_train

    // predict on validation set
    val preds = validation * direct_solution

    // compute mse
    val diff = preds - target_validation
    val diff_squared = diff.map(x => x * x)
    val mse = mean(diff_squared)
    println(s"MSE on validation is ${mse}")

    // predict on test dataset
    val matrix_test = csvread(new File("/home/alexander/MADE/3_ml_bd/hw3/diabetes_dataset_test.csv"),',')
    matrix_test(::, -1) := DenseVector.ones[Double](matrix_test.rows)

    val preds_test = DenseMatrix.zeros[Double](matrix_test.rows, 1)
    preds_test(::, 0) := matrix_test * direct_solution
    // write results
    csvwrite(new File("/home/alexander/MADE/3_ml_bd/hw3/answer.csv"), preds_test, separator = ',')
  }
}
