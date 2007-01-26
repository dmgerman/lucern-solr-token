begin_unit
begin_package
DECL|package|org.apache.lucene.xmlparser.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|xmlparser
operator|.
name|builders
package|;
end_package
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
name|spans
operator|.
name|SpanFirstQuery
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
name|spans
operator|.
name|SpanQuery
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
name|xmlparser
operator|.
name|DOMUtils
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
name|xmlparser
operator|.
name|ParserException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|SpanFirstBuilder
specifier|public
class|class
name|SpanFirstBuilder
extends|extends
name|SpanBuilderBase
block|{
DECL|field|factory
name|SpanQueryBuilder
name|factory
decl_stmt|;
DECL|method|SpanFirstBuilder
specifier|public
name|SpanFirstBuilder
parameter_list|(
name|SpanQueryBuilder
name|factory
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|getSpanQuery
specifier|public
name|SpanQuery
name|getSpanQuery
parameter_list|(
name|Element
name|e
parameter_list|)
throws|throws
name|ParserException
block|{
name|int
name|end
init|=
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"end"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Element
name|child
init|=
name|DOMUtils
operator|.
name|getFirstChildElement
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
name|factory
operator|.
name|getSpanQuery
argument_list|(
name|child
argument_list|)
decl_stmt|;
name|SpanFirstQuery
name|sfq
init|=
operator|new
name|SpanFirstQuery
argument_list|(
name|q
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|sfq
operator|.
name|setBoost
argument_list|(
name|DOMUtils
operator|.
name|getAttribute
argument_list|(
name|e
argument_list|,
literal|"boost"
argument_list|,
literal|1.0f
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sfq
return|;
block|}
block|}
end_class
end_unit
