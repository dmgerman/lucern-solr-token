begin_unit
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|index
operator|.
name|LeafReaderContext
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
name|LeafCollector
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
name|Collector
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
name|FilterLeafCollector
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
name|FilterCollector
import|;
end_import
begin_comment
comment|/**  *<p>  *  A wrapper {@link Collector} that throws {@link EarlyTerminatingCollectorException})  *  once a specified maximum number of documents are collected.  *</p>  */
end_comment
begin_class
DECL|class|EarlyTerminatingCollector
specifier|public
class|class
name|EarlyTerminatingCollector
extends|extends
name|FilterCollector
block|{
DECL|field|maxDocsToCollect
specifier|private
specifier|final
name|int
name|maxDocsToCollect
decl_stmt|;
DECL|field|numCollected
specifier|private
name|int
name|numCollected
init|=
literal|0
decl_stmt|;
DECL|field|prevReaderCumulativeSize
specifier|private
name|int
name|prevReaderCumulativeSize
init|=
literal|0
decl_stmt|;
DECL|field|currentReaderSize
specifier|private
name|int
name|currentReaderSize
init|=
literal|0
decl_stmt|;
comment|/**    *<p>    *  Wraps a {@link Collector}, throwing {@link EarlyTerminatingCollectorException}    *  once the specified maximum is reached.    *</p>    * @param delegate - the Collector to wrap.    * @param maxDocsToCollect - the maximum number of documents to Collect    *    */
DECL|method|EarlyTerminatingCollector
specifier|public
name|EarlyTerminatingCollector
parameter_list|(
name|Collector
name|delegate
parameter_list|,
name|int
name|maxDocsToCollect
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
assert|assert
literal|0
operator|<
name|maxDocsToCollect
assert|;
assert|assert
literal|null
operator|!=
name|delegate
assert|;
name|this
operator|.
name|maxDocsToCollect
operator|=
name|maxDocsToCollect
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|prevReaderCumulativeSize
operator|+=
name|currentReaderSize
expr_stmt|;
comment|// not current any more
name|currentReaderSize
operator|=
name|context
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
expr_stmt|;
return|return
operator|new
name|FilterLeafCollector
argument_list|(
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|numCollected
operator|++
expr_stmt|;
if|if
condition|(
name|maxDocsToCollect
operator|<=
name|numCollected
condition|)
block|{
throw|throw
operator|new
name|EarlyTerminatingCollectorException
argument_list|(
name|numCollected
argument_list|,
name|prevReaderCumulativeSize
operator|+
operator|(
name|doc
operator|+
literal|1
operator|)
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
