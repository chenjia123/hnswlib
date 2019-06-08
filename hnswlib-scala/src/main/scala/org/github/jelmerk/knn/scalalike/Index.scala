package org.github.jelmerk.knn.scalalike

import java.io.{File, OutputStream}
import java.nio.file.Path

import org.github.jelmerk.knn.{Index => JIndex}

/**
  * K-nearest neighbours search index.
  *
  * @tparam TId type of the external identifier of an item
  * @tparam TVector The type of the vector to perform distance calculation on
  * @tparam TItem The type of items to connect into small world.
  * @tparam TDistance The type of distance between items (expect any numeric type: float, double, int, ..).
  *
  * @see See [[https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm">k-nearest neighbors algorithm]] for more
  * information.
  */
trait Index[TId, TVector, TItem <: Item[TId, TVector], TDistance] extends Serializable {

  /**
    * Called periodically to report on progress during indexing. The first argument is the number of records processed.
    * The second argument is the total number of record to index as part of this operation.
    */
  type ProgressListener = (Int, Int) => Unit

  /**
    * Add a new item to the index.
    *
    * @param item the item to add to the index
    */
  def add(item: TItem): Unit

  /**
    * Add multiple items to the index. Reports progress to the passed in progress listener
    * every progressUpdateInterval elements indexed.

    * @param items the items to add to the index
    * @param numThreads number of threads to use for parallel indexing
    * @param listener listener to report progress to
    * @param progressUpdateInterval after indexing this many items progress will be reported
    */
  def addAll(items: Iterable[TItem],
             numThreads: Int = Runtime.getRuntime.availableProcessors,
             listener: ProgressListener = (_, _) => (),
             progressUpdateInterval: Int = JIndex.DEFAULT_PROGRESS_UPDATE_INTERVAL): Unit

  /**
    * Returns the size of the index.
    *
    * @return size of the index
    */
  def size: Int

  /**
    * Returns an item by its identifier. If the item does not exist in the index a NoSuchElementException is thrown
    * @param id unique identifier of the item to return
    * @return the item
    *
    * @throws NoSuchElementException when the item does not exist
    */
  def apply(id: TId): TItem

  /**
    * Optionally return an item by its identifier
    * @param id unique identifier of the item to return
    *
    * @return the item
    */
  def get(id: TId): Option[TItem]

  /**
    * Find the items closest to the passed in vector.
    *
    * @param vector the vector
    * @param k number of items to return
    * @return the items closest to the passed in vector
    */
  def findNearest(vector: TVector, k: Int): Seq[SearchResult[TItem, TDistance]]

  /**
    * Find the items closest to the item identified by the passed in id. If the id does not match an item an empty
    * list is returned. the element itself is not included in the response.

    * @param id of the item to find the neighbours of
    * @param k number of neighbours to return
    * @return the items closest to the item
    */
  def findNeighbours(id: TId, k: Int): Seq[SearchResult[TItem, TDistance]]

  /**
    * Removes an item from the index.
    * @param id unique identifier or the item to remove
    * @return true if this list contained the specified element
    */
  def remove(id: TId): Boolean

  /**
    * Saves the index to an OutputStream.
    *
    * @param out the output stream to write the index to
    */
  def save(out: OutputStream): Unit

  /**
    * Saves the index to a file.
    *
    * @param file file to write the index to
    */
  def save(file: File): Unit

  /**
    * Saves the index to a path.
    *
    * @param path path to write the index to
    */
  def save(path: Path): Unit

}