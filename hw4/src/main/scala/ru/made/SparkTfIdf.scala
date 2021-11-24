package ru.made

import org.apache.spark.sql._
import org.apache.spark.sql.functions._


object TfIdf{
  def main(args: Array[String]): Unit = {
    // Создает сессию спарка
    val spark = SparkSession.builder()
      // адрес мастера
      .master("local[*]")
      // имя приложения в интерфейсе спарка
      .appName("made-tfidf")
      // взять текущий или создать новый
      .getOrCreate()

    // синтаксический сахар для удобной работы со спарк

    import spark.implicits._

    // прочитаем датасет https://www.kaggle.com/andrewmvd/trip-advisor-hotel-reviews

    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("tripadvisor_hotel_reviews.csv")

    val df_explode = df
      .withColumn("Review", regexp_replace(lower(col("Review")), "[^a-z ]", ""))
      .select(col("Review"))
      .withColumn("Review", split(trim(col("Review")), "\\s+"))
      .withColumn("id", monotonically_increasing_id())
      .withColumn("word", explode(col("Review")))

    // df_explode.show(1, 500, true)

    import org.apache.spark.sql.expressions.Window

    val windowById = Window.partitionBy(col("id"), col("word"))
    val tf = df_explode
      .withColumn("word_cnt", count(col("Review")).over(windowById))
      .withColumn("len", size(col("Review")))
      .withColumn("tf", col("word_cnt") / col("len"))
      .select("id", "tf", "word")

    // df_with_count.show(10)
    val docsNumber = df.count()


    val idf = df_explode
      .groupBy("word")
      .agg(countDistinct("id").as("docfreq"))
      .orderBy(desc("docfreq"))
      .limit(100)
      .withColumn("docsNumber", lit(docsNumber))
      .withColumn("idf", log(col("docsNumber") / col("docfreq")))
      .select("word", "idf")

    //tf.show(2)
    //idf.show(2)

    val tfIdf = tf
      .join(idf, Seq("word"), joinType = "Inner")
      .withColumn("tfidf", col("tf") * col("idf"))

    val tfIdfPivot = tfIdf.groupBy("id").pivot("word").max("tfidf").na.fill(0)

    tfIdfPivot.show(3, 30, true)
  }
}