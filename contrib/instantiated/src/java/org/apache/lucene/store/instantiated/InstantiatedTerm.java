begin_unit
begin_package
DECL|package|org.apache.lucene.store.instantiated
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|instantiated
package|;
end_package
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import
begin_comment
comment|/**  * A term in the inverted index, coupled to the documents it occurs in.  *  * @see org.apache.lucene.index.Term  */
end_comment
begin_class
DECL|class|InstantiatedTerm
specifier|public
class|class
name|InstantiatedTerm
implements|implements
name|Serializable
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1l
decl_stmt|;
DECL|field|comparator
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|InstantiatedTerm
argument_list|>
name|comparator
init|=
operator|new
name|Comparator
argument_list|<
name|InstantiatedTerm
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|InstantiatedTerm
name|instantiatedTerm
parameter_list|,
name|InstantiatedTerm
name|instantiatedTerm1
parameter_list|)
block|{
return|return
name|instantiatedTerm
operator|.
name|getTerm
argument_list|()
operator|.
name|compareTo
argument_list|(
name|instantiatedTerm1
operator|.
name|getTerm
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|termComparator
specifier|public
specifier|static
specifier|final
name|Comparator
name|termComparator
init|=
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
return|return
operator|(
operator|(
name|InstantiatedTerm
operator|)
name|o
operator|)
operator|.
name|getTerm
argument_list|()
operator|.
name|compareTo
argument_list|(
operator|(
name|Term
operator|)
name|o1
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|term
specifier|private
name|Term
name|term
decl_stmt|;
comment|/**    * index of term in InstantiatedIndex    * @see org.apache.lucene.store.instantiated.InstantiatedIndex#getOrderedTerms() */
DECL|field|termIndex
specifier|private
name|int
name|termIndex
decl_stmt|;
comment|/**    * @return Term associated with this entry of the index object graph    */
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
DECL|method|InstantiatedTerm
name|InstantiatedTerm
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
comment|//  this could speed up TermDocs.skipTo even more
comment|//  private Map</** document number*/Integer, /** index in associatedDocuments */Integer> associatedDocumentIndexByDocumentNumber = new HashMap<Integer, Integer>();
comment|//
comment|//  public Map</** document number*/Integer, /** index in associatedDocuments */Integer> getAssociatedDocumentIndexByDocumentNumber() {
comment|//    return associatedDocumentIndexByDocumentNumber;
comment|//  }
comment|/** Ordered by document number */
DECL|field|associatedDocuments
specifier|private
name|InstantiatedTermDocumentInformation
index|[]
name|associatedDocuments
decl_stmt|;
comment|/**    * Meta data per document in which this term is occurring.    * Ordered by document number.    *    * @return Meta data per document in which this term is occurring.    */
DECL|method|getAssociatedDocuments
specifier|public
name|InstantiatedTermDocumentInformation
index|[]
name|getAssociatedDocuments
parameter_list|()
block|{
return|return
name|associatedDocuments
return|;
block|}
comment|/**    * Meta data per document in which this term is occurring.    * Ordered by document number.    *    * @param associatedDocuments meta data per document in which this term is occurring, ordered by document number    */
DECL|method|setAssociatedDocuments
name|void
name|setAssociatedDocuments
parameter_list|(
name|InstantiatedTermDocumentInformation
index|[]
name|associatedDocuments
parameter_list|)
block|{
name|this
operator|.
name|associatedDocuments
operator|=
name|associatedDocuments
expr_stmt|;
block|}
comment|/**    * Finds index to the first beyond the current whose document number is    * greater than or equal to<i>target</i>, -1 if there is no such element.    *    * @param target the document number to match    * @return -1 if there is no such element    */
DECL|method|seekCeilingDocumentInformationIndex
specifier|public
name|int
name|seekCeilingDocumentInformationIndex
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|seekCeilingDocumentInformationIndex
argument_list|(
name|target
argument_list|,
literal|0
argument_list|,
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**    * Finds index to the first beyond the current whose document number is    * greater than or equal to<i>target</i>, -1 if there is no such element.    *    * @param target the document number to match    * @param startOffset associated documents index start offset    * @return -1 if there is no such element    */
DECL|method|seekCeilingDocumentInformationIndex
specifier|public
name|int
name|seekCeilingDocumentInformationIndex
parameter_list|(
name|int
name|target
parameter_list|,
name|int
name|startOffset
parameter_list|)
block|{
return|return
name|seekCeilingDocumentInformationIndex
argument_list|(
name|target
argument_list|,
name|startOffset
argument_list|,
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**    * Finds index to the first beyond the current whose document number is    * greater than or equal to<i>target</i>, -1 if there is no such element.    *    * @param target the document number to match    * @param startOffset associated documents index start offset    * @param endPosition associated documents index end position    * @return -1 if there is no such element    */
DECL|method|seekCeilingDocumentInformationIndex
specifier|public
name|int
name|seekCeilingDocumentInformationIndex
parameter_list|(
name|int
name|target
parameter_list|,
name|int
name|startOffset
parameter_list|,
name|int
name|endPosition
parameter_list|)
block|{
name|int
name|pos
init|=
name|binarySearchAssociatedDocuments
argument_list|(
name|target
argument_list|,
name|startOffset
argument_list|,
name|endPosition
operator|-
name|startOffset
argument_list|)
decl_stmt|;
comment|//    int pos = Arrays.binarySearch(getAssociatedDocuments(), target, InstantiatedTermDocumentInformation.doumentNumberIntegerComparator);
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
name|pos
operator|=
operator|-
literal|1
operator|-
name|pos
expr_stmt|;
block|}
if|if
condition|(
name|getAssociatedDocuments
argument_list|()
operator|.
name|length
operator|<=
name|pos
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|pos
return|;
block|}
block|}
DECL|method|binarySearchAssociatedDocuments
specifier|public
name|int
name|binarySearchAssociatedDocuments
parameter_list|(
name|int
name|target
parameter_list|)
block|{
return|return
name|binarySearchAssociatedDocuments
argument_list|(
name|target
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|binarySearchAssociatedDocuments
specifier|public
name|int
name|binarySearchAssociatedDocuments
parameter_list|(
name|int
name|target
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|binarySearchAssociatedDocuments
argument_list|(
name|target
argument_list|,
name|offset
argument_list|,
name|associatedDocuments
operator|.
name|length
operator|-
name|offset
argument_list|)
return|;
block|}
comment|/**    * @param target value to search for in the array    * @param offset index of the first valid value in the array    * @param length number of valid values in the array    * @return index of an occurrence of key in array, or -(insertionIndex + 1) if key is not contained in array (<i>insertionIndex</i> is then the index at which key could be inserted).    */
DECL|method|binarySearchAssociatedDocuments
specifier|public
name|int
name|binarySearchAssociatedDocuments
parameter_list|(
name|int
name|target
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
comment|// implementation originally from http://ochafik.free.fr/blog/?p=106
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
operator|-
name|offset
return|;
block|}
name|int
name|min
init|=
name|offset
decl_stmt|,
name|max
init|=
name|offset
operator|+
name|length
operator|-
literal|1
decl_stmt|;
name|int
name|minVal
init|=
name|getAssociatedDocuments
argument_list|()
index|[
name|min
index|]
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
decl_stmt|;
name|int
name|maxVal
init|=
name|getAssociatedDocuments
argument_list|()
index|[
name|max
index|]
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
decl_stmt|;
name|int
name|nPreviousSteps
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// be careful not to compute key - minVal, for there might be an integer overflow.
if|if
condition|(
name|target
operator|<=
name|minVal
condition|)
return|return
name|target
operator|==
name|minVal
condition|?
name|min
else|:
operator|-
literal|1
operator|-
name|min
return|;
if|if
condition|(
name|target
operator|>=
name|maxVal
condition|)
return|return
name|target
operator|==
name|maxVal
condition|?
name|max
else|:
operator|-
literal|2
operator|-
name|max
return|;
assert|assert
name|min
operator|!=
name|max
assert|;
name|int
name|pivot
decl_stmt|;
comment|// A typical binarySearch algorithm uses pivot = (min + max) / 2.
comment|// The pivot we use here tries to be smarter and to choose a pivot close to the expectable location of the key.
comment|// This reduces dramatically the number of steps needed to get to the key.
comment|// However, it does not work well with a logarithmic distribution of values, for instance.
comment|// When the key is not found quickly the smart way, we switch to the standard pivot.
if|if
condition|(
name|nPreviousSteps
operator|>
literal|2
condition|)
block|{
name|pivot
operator|=
operator|(
name|min
operator|+
name|max
operator|)
operator|>>
literal|1
expr_stmt|;
comment|// stop increasing nPreviousSteps from now on
block|}
else|else
block|{
comment|// NOTE: We cannot do the following operations in int precision, because there might be overflows.
comment|//       long operations are slower than float operations with the hardware this was tested on (intel core duo 2, JVM 1.6.0).
comment|//       Overall, using float proved to be the safest and fastest approach.
name|pivot
operator|=
name|min
operator|+
call|(
name|int
call|)
argument_list|(
operator|(
name|target
operator|-
operator|(
name|float
operator|)
name|minVal
operator|)
operator|/
operator|(
name|maxVal
operator|-
operator|(
name|float
operator|)
name|minVal
operator|)
operator|*
operator|(
name|max
operator|-
name|min
operator|)
argument_list|)
expr_stmt|;
name|nPreviousSteps
operator|++
expr_stmt|;
block|}
name|int
name|pivotVal
init|=
name|getAssociatedDocuments
argument_list|()
index|[
name|pivot
index|]
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
decl_stmt|;
comment|// NOTE: do not store key - pivotVal because of overflows
if|if
condition|(
name|target
operator|>
name|pivotVal
condition|)
block|{
name|min
operator|=
name|pivot
operator|+
literal|1
expr_stmt|;
name|max
operator|--
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|target
operator|==
name|pivotVal
condition|)
block|{
return|return
name|pivot
return|;
block|}
else|else
block|{
name|min
operator|++
expr_stmt|;
name|max
operator|=
name|pivot
operator|-
literal|1
expr_stmt|;
block|}
name|maxVal
operator|=
name|getAssociatedDocuments
argument_list|()
index|[
name|max
index|]
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
expr_stmt|;
name|minVal
operator|=
name|getAssociatedDocuments
argument_list|()
index|[
name|min
index|]
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentNumber
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Navigates to the view of this occurrences of this term in a specific document.     *    * This method is only used by InstantiatedIndex(IndexReader) and    * should not be optimized for less CPU at the cost of more RAM.    *    * @param documentNumber the n:th document in the index    * @return view of this term from specified document    */
DECL|method|getAssociatedDocument
specifier|public
name|InstantiatedTermDocumentInformation
name|getAssociatedDocument
parameter_list|(
name|int
name|documentNumber
parameter_list|)
block|{
name|int
name|pos
init|=
name|binarySearchAssociatedDocuments
argument_list|(
name|documentNumber
argument_list|)
decl_stmt|;
return|return
name|pos
operator|<
literal|0
condition|?
literal|null
else|:
name|getAssociatedDocuments
argument_list|()
index|[
name|pos
index|]
return|;
block|}
DECL|method|field
specifier|public
specifier|final
name|String
name|field
parameter_list|()
block|{
return|return
name|term
operator|.
name|field
argument_list|()
return|;
block|}
DECL|method|text
specifier|public
specifier|final
name|String
name|text
parameter_list|()
block|{
return|return
name|term
operator|.
name|text
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|term
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getTermIndex
specifier|public
name|int
name|getTermIndex
parameter_list|()
block|{
return|return
name|termIndex
return|;
block|}
DECL|method|setTermIndex
specifier|public
name|void
name|setTermIndex
parameter_list|(
name|int
name|termIndex
parameter_list|)
block|{
name|this
operator|.
name|termIndex
operator|=
name|termIndex
expr_stmt|;
block|}
block|}
end_class
end_unit
