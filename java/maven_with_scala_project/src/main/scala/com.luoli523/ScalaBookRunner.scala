package com.luoli523

import com.luoli523.book.BookStorage

object ScalaBookRunner extends App {
  implicit val books = (new BookStorage()).getBooks
  BooksProcessor.filterByAuthor("Jack London").foreach(b => println(b))
}
