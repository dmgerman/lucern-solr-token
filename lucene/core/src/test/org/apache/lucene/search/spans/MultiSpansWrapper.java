begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|IndexReader
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
name|LeafReader
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
name|index
operator|.
name|SlowCompositeReaderWrapper
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
name|util
operator|.
name|Bits
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
begin_comment
comment|/**  *   * A wrapper to perform span operations on a non-leaf reader context  *<p>  * NOTE: This should be used for testing purposes only  * @lucene.internal  */
end_comment
begin_class
DECL|class|MultiSpansWrapper
specifier|public
class|class
name|MultiSpansWrapper
block|{
DECL|method|wrap
specifier|public
specifier|static
name|Spans
name|wrap
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|SpanQuery
name|spanQuery
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|wrap
argument_list|(
name|reader
argument_list|,
name|spanQuery
argument_list|,
name|SpanCollector
operator|.
name|NO_OP
argument_list|)
return|;
block|}
DECL|method|wrap
specifier|public
specifier|static
name|Spans
name|wrap
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|SpanQuery
name|spanQuery
parameter_list|,
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|LeafReader
name|lr
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
decl_stmt|;
comment|// slow, but ok for testing
name|LeafReaderContext
name|lrContext
init|=
name|lr
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|lr
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setQueryCache
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|SpanWeight
name|w
init|=
operator|(
name|SpanWeight
operator|)
name|searcher
operator|.
name|createNormalizedWeight
argument_list|(
name|spanQuery
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|w
operator|.
name|getSpans
argument_list|(
name|lrContext
argument_list|,
operator|new
name|Bits
operator|.
name|MatchAllBits
argument_list|(
name|lr
operator|.
name|numDocs
argument_list|()
argument_list|)
argument_list|,
name|collector
argument_list|)
return|;
block|}
block|}
end_class
end_unit
