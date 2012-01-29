begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|AtomicIndexReader
operator|.
name|AtomicReaderContext
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|DocumentStoredFieldVisitor
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
name|search
operator|.
name|SearcherManager
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|*
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
name|util
operator|.
name|Bits
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
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|ReaderUtil
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|SetOnce
import|;
end_import
begin_comment
comment|/** IndexReader is an abstract class, providing an interface for accessing an  index.  Search of an index is done entirely through this abstract interface,  so that any subclass which implements it is searchable.<p> Concrete subclasses of IndexReader are usually constructed with a call to  one of the static<code>open()</code> methods, e.g. {@link  #open(Directory)}.<p> For efficiency, in this API documents are often referred to via<i>document numbers</i>, non-negative integers which each name a unique  document in the index.  These document numbers are ephemeral--they may change  as documents are added to and deleted from an index.  Clients should thus not  rely on a given document having the same number between sessions.<p><b>NOTE</b>: for backwards API compatibility, several methods are not listed   as abstract, but have no useful implementations in this base class and   instead always throw UnsupportedOperationException.  Subclasses are   strongly encouraged to override these methods, but in many cases may not   need to.</p><p><a name="thread-safety"></a><p><b>NOTE</b>: {@link  IndexReader} instances are completely thread  safe, meaning multiple threads can call any of its methods,  concurrently.  If your application requires external  synchronization, you should<b>not</b> synchronize on the<code>IndexReader</code> instance; use your own  (non-Lucene) objects instead. */
end_comment
begin_class
DECL|class|CompositeIndexReader
specifier|public
specifier|abstract
class|class
name|CompositeIndexReader
extends|extends
name|IndexReader
block|{
DECL|field|readerContext
specifier|private
name|CompositeReaderContext
name|readerContext
init|=
literal|null
decl_stmt|;
comment|// lazy init
DECL|method|CompositeIndexReader
specifier|protected
name|CompositeIndexReader
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
specifier|final
name|IndexReader
index|[]
name|subReaders
init|=
name|getSequentialSubReaders
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|subReaders
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|subReaders
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|subReaders
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|subReaders
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|subReaders
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTopReaderContext
specifier|public
specifier|final
name|CompositeReaderContext
name|getTopReaderContext
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// lazy init without thread safety for perf resaons: Building the readerContext twice does not hurt!
if|if
condition|(
name|readerContext
operator|==
literal|null
condition|)
block|{
assert|assert
name|getSequentialSubReaders
argument_list|()
operator|!=
literal|null
assert|;
name|readerContext
operator|=
operator|(
name|CompositeReaderContext
operator|)
name|ReaderUtil
operator|.
name|buildReaderContext
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|readerContext
return|;
block|}
comment|/** Expert: returns the sequential sub readers that this    *  reader is logically composed of. It contrast to previous    *  Lucene versions may not return null.    *  If this method returns an empty array, that means this    *  reader is a null reader (for example a MultiReader    *  that has no sub readers).    */
DECL|method|getSequentialSubReaders
specifier|public
specifier|abstract
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
function_decl|;
comment|/**    * {@link ReaderContext} for {@link CompositeIndexReader} instance.    * @lucene.experimental    */
DECL|class|CompositeReaderContext
specifier|public
specifier|static
specifier|final
class|class
name|CompositeReaderContext
extends|extends
name|ReaderContext
block|{
DECL|field|children
specifier|private
specifier|final
name|ReaderContext
index|[]
name|children
decl_stmt|;
DECL|field|leaves
specifier|private
specifier|final
name|AtomicReaderContext
index|[]
name|leaves
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|CompositeIndexReader
name|reader
decl_stmt|;
comment|/**      * Creates a {@link CompositeReaderContext} for intermediate readers that aren't      * not top-level readers in the current context      */
DECL|method|CompositeReaderContext
specifier|public
name|CompositeReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|CompositeIndexReader
name|reader
parameter_list|,
name|int
name|ordInParent
parameter_list|,
name|int
name|docbaseInParent
parameter_list|,
name|ReaderContext
index|[]
name|children
parameter_list|)
block|{
name|this
argument_list|(
name|parent
argument_list|,
name|reader
argument_list|,
name|ordInParent
argument_list|,
name|docbaseInParent
argument_list|,
name|children
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a {@link CompositeReaderContext} for top-level readers with parent set to<code>null</code>      */
DECL|method|CompositeReaderContext
specifier|public
name|CompositeReaderContext
parameter_list|(
name|CompositeIndexReader
name|reader
parameter_list|,
name|ReaderContext
index|[]
name|children
parameter_list|,
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|reader
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|children
argument_list|,
name|leaves
argument_list|)
expr_stmt|;
block|}
DECL|method|CompositeReaderContext
specifier|private
name|CompositeReaderContext
parameter_list|(
name|CompositeReaderContext
name|parent
parameter_list|,
name|CompositeIndexReader
name|reader
parameter_list|,
name|int
name|ordInParent
parameter_list|,
name|int
name|docbaseInParent
parameter_list|,
name|ReaderContext
index|[]
name|children
parameter_list|,
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|ordInParent
argument_list|,
name|docbaseInParent
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
expr_stmt|;
name|this
operator|.
name|leaves
operator|=
name|leaves
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|leaves
specifier|public
name|AtomicReaderContext
index|[]
name|leaves
parameter_list|()
block|{
return|return
name|leaves
return|;
block|}
annotation|@
name|Override
DECL|method|children
specifier|public
name|ReaderContext
index|[]
name|children
parameter_list|()
block|{
return|return
name|children
return|;
block|}
annotation|@
name|Override
DECL|method|reader
specifier|public
name|CompositeIndexReader
name|reader
parameter_list|()
block|{
return|return
name|reader
return|;
block|}
block|}
block|}
end_class
end_unit
