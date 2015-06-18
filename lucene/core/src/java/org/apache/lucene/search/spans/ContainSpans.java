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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import
begin_class
DECL|class|ContainSpans
specifier|abstract
class|class
name|ContainSpans
extends|extends
name|ConjunctionSpans
block|{
DECL|field|sourceSpans
name|Spans
name|sourceSpans
decl_stmt|;
DECL|field|bigSpans
name|Spans
name|bigSpans
decl_stmt|;
DECL|field|littleSpans
name|Spans
name|littleSpans
decl_stmt|;
DECL|method|ContainSpans
name|ContainSpans
parameter_list|(
name|Spans
name|bigSpans
parameter_list|,
name|Spans
name|littleSpans
parameter_list|,
name|Spans
name|sourceSpans
parameter_list|)
block|{
name|super
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bigSpans
argument_list|,
name|littleSpans
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|bigSpans
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|bigSpans
argument_list|)
expr_stmt|;
name|this
operator|.
name|littleSpans
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|littleSpans
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceSpans
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|sourceSpans
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startPosition
specifier|public
name|int
name|startPosition
parameter_list|()
block|{
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|oneExhaustedInCurrentDoc
condition|?
name|NO_MORE_POSITIONS
else|:
name|sourceSpans
operator|.
name|startPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|endPosition
specifier|public
name|int
name|endPosition
parameter_list|()
block|{
return|return
name|atFirstInCurrentDoc
condition|?
operator|-
literal|1
else|:
name|oneExhaustedInCurrentDoc
condition|?
name|NO_MORE_POSITIONS
else|:
name|sourceSpans
operator|.
name|endPosition
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|width
specifier|public
name|int
name|width
parameter_list|()
block|{
return|return
name|sourceSpans
operator|.
name|width
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|SpanCollector
name|collector
parameter_list|)
throws|throws
name|IOException
block|{
name|sourceSpans
operator|.
name|collect
argument_list|(
name|collector
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
