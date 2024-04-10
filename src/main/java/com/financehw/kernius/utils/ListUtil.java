package com.financehw.kernius.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListUtil {

  /**
   * The `map` function takes a list of items and a mapper function, and returns a new list by
   * applying the mapper function to each item in the original list.
   *
   * @param items A list of items that you want to map to a different type.
   * @param mapper The `mapper` parameter is a `Function` that takes an element of type `From` as
   *     input and returns an element of type `To`.
   * @return The list which contains the mapped items.
   */
  public static <From, To> List<To> map(List<From> items, Function<From, To> mapper) {
    return items.stream().map(mapper).collect(Collectors.toList());
  }
}
