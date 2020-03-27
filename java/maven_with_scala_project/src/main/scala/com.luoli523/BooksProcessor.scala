package com.luoli523

import java.util
import scala.collection.JavaConversions._
import com.luoli523.book.Book

object BooksProcessor {
  def filterByAuthor(author: String)(implicit books: util.ArrayList[Book]) = {
    books.filter(book => book.getAuthor == author)
  }
}
