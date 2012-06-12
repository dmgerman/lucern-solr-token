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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
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
name|ReferenceManager
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
name|Directory
import|;
end_import
begin_comment
comment|/**  * Utility class to safely share {@link DirectoryReader} instances across  * multiple threads, while periodically reopening. This class ensures each  * reader is closed only once all threads have finished using it.  *   * @see SearcherManager  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|ReaderManager
specifier|public
specifier|final
class|class
name|ReaderManager
extends|extends
name|ReferenceManager
argument_list|<
name|DirectoryReader
argument_list|>
block|{
comment|/**    * Creates and returns a new ReaderManager from the given    * {@link IndexWriter}.    *     * @param writer    *          the IndexWriter to open the IndexReader from.    * @param applyAllDeletes    *          If<code>true</code>, all buffered deletes will be applied (made    *          visible) in the {@link IndexSearcher} / {@link DirectoryReader}.    *          If<code>false</code>, the deletes may or may not be applied, but    *          remain buffered (in IndexWriter) so that they will be applied in    *          the future. Applying deletes can be costly, so if your app can    *          tolerate deleted documents being returned you might gain some    *          performance by passing<code>false</code>. See    *          {@link DirectoryReader#openIfChanged(DirectoryReader, IndexWriter, boolean)}.    *     * @throws IOException    */
DECL|method|ReaderManager
specifier|public
name|ReaderManager
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|boolean
name|applyAllDeletes
parameter_list|)
throws|throws
name|IOException
block|{
name|current
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
name|applyAllDeletes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates and returns a new ReaderManager from the given {@link Directory}.     * @param dir the directory to open the DirectoryReader on.    *            * @throws IOException    */
DECL|method|ReaderManager
specifier|public
name|ReaderManager
parameter_list|(
name|Directory
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|current
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decRef
specifier|protected
name|void
name|decRef
parameter_list|(
name|DirectoryReader
name|reference
parameter_list|)
throws|throws
name|IOException
block|{
name|reference
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|refreshIfNeeded
specifier|protected
name|DirectoryReader
name|refreshIfNeeded
parameter_list|(
name|DirectoryReader
name|referenceToRefresh
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|DirectoryReader
operator|.
name|openIfChanged
argument_list|(
name|referenceToRefresh
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|tryIncRef
specifier|protected
name|boolean
name|tryIncRef
parameter_list|(
name|DirectoryReader
name|reference
parameter_list|)
block|{
return|return
name|reference
operator|.
name|tryIncRef
argument_list|()
return|;
block|}
block|}
end_class
end_unit
